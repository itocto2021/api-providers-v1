#!/bin/bash

# =========================================
# Script de construcción y ejecución Docker
# =========================================
# 
# Script para facilitar la construcción y ejecución
# de la aplicación Gapsi Providers en Docker.
#
# Uso:
#   ./docker-run.sh build   # Construir imagen
#   ./docker-run.sh start   # Iniciar contenedor
#   ./docker-run.sh stop    # Detener contenedor
#   ./docker-run.sh restart # Reiniciar contenedor
#   ./docker-run.sh logs    # Ver logs
#   ./docker-run.sh clean   # Limpiar contenedores e imágenes
#
# @author Ilder Tocto
# @version 1.0.0
# @since 2025-10-18

set -e

# Variables de configuración
IMAGE_NAME="gapsi-providers"
CONTAINER_NAME="gapsi-providers-container"
PORT="8080"
VERSION="1.0.0"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para mostrar ayuda
show_help() {
    echo -e "${BLUE}Gapsi Providers Docker Management Script${NC}"
    echo ""
    echo "Uso: $0 [COMANDO]"
    echo ""
    echo "Comandos disponibles:"
    echo -e "  ${GREEN}build${NC}     Construir la imagen Docker"
    echo -e "  ${GREEN}start${NC}     Iniciar el contenedor"
    echo -e "  ${GREEN}stop${NC}      Detener el contenedor"
    echo -e "  ${GREEN}restart${NC}   Reiniciar el contenedor"
    echo -e "  ${GREEN}logs${NC}      Ver logs del contenedor"
    echo -e "  ${GREEN}shell${NC}     Acceder al shell del contenedor"
    echo -e "  ${GREEN}clean${NC}     Limpiar contenedores e imágenes"
    echo -e "  ${GREEN}status${NC}    Ver estado del contenedor"
    echo -e "  ${GREEN}test${NC}      Ejecutar tests de la API"
    echo -e "  ${GREEN}help${NC}      Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0 build && $0 start    # Construir e iniciar"
    echo "  $0 logs -f               # Seguir logs en tiempo real"
}

# Función para construir la imagen
build_image() {
    echo -e "${BLUE}Construyendo imagen Docker...${NC}"
    docker build -t ${IMAGE_NAME}:${VERSION} -t ${IMAGE_NAME}:latest .
    echo -e "${GREEN}✓ Imagen construida exitosamente${NC}"
}

# Función para iniciar el contenedor
start_container() {
    echo -e "${BLUE}Iniciando contenedor...${NC}"
    
    # Detener contenedor existente si está corriendo
    if docker ps -q -f name=${CONTAINER_NAME} | grep -q .; then
        echo -e "${YELLOW}Deteniendo contenedor existente...${NC}"
        docker stop ${CONTAINER_NAME}
    fi
    
    # Remover contenedor existente si existe
    if docker ps -aq -f name=${CONTAINER_NAME} | grep -q .; then
        docker rm ${CONTAINER_NAME}
    fi
    
    # Crear y ejecutar nuevo contenedor
    docker run -d \
        --name ${CONTAINER_NAME} \
        -p ${PORT}:8080 \
        -v gapsi-providers-data:/app/data \
        -v gapsi-providers-logs:/app/logs \
        -e JAVA_OPTS="-Xmx512m -Xms256m" \
        ${IMAGE_NAME}:latest
    
    echo -e "${GREEN}✓ Contenedor iniciado exitosamente${NC}"
    echo -e "${BLUE}URL de la aplicación: http://localhost:${PORT}/api/v1/gapsi/info${NC}"
}

# Función para detener el contenedor
stop_container() {
    echo -e "${BLUE}Deteniendo contenedor...${NC}"
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    echo -e "${GREEN}✓ Contenedor detenido${NC}"
}

# Función para reiniciar el contenedor
restart_container() {
    echo -e "${BLUE}Reiniciando contenedor...${NC}"
    stop_container
    start_container
}

# Función para ver logs
show_logs() {
    echo -e "${BLUE}Mostrando logs del contenedor...${NC}"
    docker logs ${CONTAINER_NAME} "$@"
}

# Función para acceder al shell
access_shell() {
    echo -e "${BLUE}Accediendo al shell del contenedor...${NC}"
    docker exec -it ${CONTAINER_NAME} /bin/sh
}

# Función para limpiar
clean_up() {
    echo -e "${BLUE}Limpiando contenedores e imágenes...${NC}"
    
    # Detener y remover contenedor
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    docker rm ${CONTAINER_NAME} 2>/dev/null || true
    
    # Remover imágenes
    docker rmi ${IMAGE_NAME}:latest ${IMAGE_NAME}:${VERSION} 2>/dev/null || true
    
    # Limpiar imágenes huérfanas
    docker image prune -f
    
    echo -e "${GREEN}✓ Limpieza completada${NC}"
}

# Función para ver estado
show_status() {
    echo -e "${BLUE}Estado del contenedor:${NC}"
    docker ps -a -f name=${CONTAINER_NAME} --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    echo -e "\n${BLUE}Imágenes disponibles:${NC}"
    docker images ${IMAGE_NAME} --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
}

# Función para ejecutar tests
run_tests() {
    echo -e "${BLUE}Ejecutando tests de la API...${NC}"
    
    # Verificar que el contenedor esté corriendo
    if ! docker ps -q -f name=${CONTAINER_NAME} | grep -q .; then
        echo -e "${RED}✗ El contenedor no está corriendo. Inicia el contenedor primero.${NC}"
        exit 1
    fi
    
    # Esperar a que la aplicación esté lista
    echo -e "${YELLOW}Esperando a que la aplicación esté lista...${NC}"
    sleep 10
    
    # Test básico de health check
    echo -e "${BLUE}Testing health endpoint...${NC}"
    curl -f http://localhost:${PORT}/api/v1/gapsi/info || echo -e "${RED}✗ Health check failed${NC}"
    
    echo -e "${GREEN}✓ Tests básicos completados${NC}"
}

# Función principal
main() {
    case "$1" in
        "build")
            build_image
            ;;
        "start")
            start_container
            ;;
        "stop")
            stop_container
            ;;
        "restart")
            restart_container
            ;;
        "logs")
            shift
            show_logs "$@"
            ;;
        "shell")
            access_shell
            ;;
        "clean")
            clean_up
            ;;
        "status")
            show_status
            ;;
        "test")
            run_tests
            ;;
        "help"|"--help"|"-h")
            show_help
            ;;
        "")
            echo -e "${RED}Error: No se especificó comando${NC}"
            show_help
            exit 1
            ;;
        *)
            echo -e "${RED}Error: Comando desconocido '$1'${NC}"
            show_help
            exit 1
            ;;
    esac
}

# Verificar que Docker esté instalado
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker no está instalado o no está en el PATH${NC}"
    exit 1
fi

# Ejecutar función principal con todos los argumentos
main "$@"