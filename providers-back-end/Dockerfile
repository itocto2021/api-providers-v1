# =========================================
# Dockerfile para Gapsi Providers API
# =========================================
# 
# Multi-stage build para optimizar el tamaño de la imagen final
# - Stage 1: Build - Compila la aplicación usando Maven
# - Stage 2: Runtime - Ejecuta la aplicación con JRE mínimo
#
# Uso:
#   docker build -t gapsi-providers .
#   docker run -p 8080:8080 gapsi-providers
#
# Variables de entorno disponibles:
#   - SERVER_PORT: Puerto del servidor (default: 8080)
#   - APP_VERSION: Versión de la aplicación (default: 1.0.0)
#   - JAVA_OPTS: Opciones adicionales de JVM
#
# @author Ilder Tocto
# @version 1.0.0
# @since 2025-10-18

# =========================================
# STAGE 1: Build Stage
# =========================================
FROM maven:3.9.8-eclipse-temurin-17 AS builder

# Etiquetas de metadatos
LABEL stage=builder
LABEL description="Build stage for Gapsi Providers API"

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (optimización de cache)
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación (skip tests en build para agilizar)
RUN mvn clean package -DskipTests -B

# =========================================
# STAGE 2: Runtime Stage
# =========================================
FROM eclipse-temurin:17-jre-alpine AS runtime

# Etiquetas de metadatos
LABEL maintainer="toctocruz@gmail.com"
LABEL description="Gapsi e-Commerce - Providers Maintenance API"
LABEL version="1.0.0"
LABEL org.opencontainers.image.title="Gapsi Providers API"
LABEL org.opencontainers.image.description="REST API para mantenimiento de proveedores"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.source="https://github.com/itocto2021/api-providers-v1"

# Crear usuario no-root por seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Instalar herramientas adicionales si es necesario
RUN apk add --no-cache \
    curl \
    ca-certificates \
    tzdata && \
    rm -rf /var/cache/apk/*

# Establecer zona horaria
ENV TZ=America/Mexico_City

# Crear directorios necesarios
RUN mkdir -p /app/data /app/logs && \
    chown -R appuser:appgroup /app

# Establecer directorio de trabajo
WORKDIR /app

# Copiar JAR desde el stage de build
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

# Copiar archivos de datos si existen
COPY --chown=appuser:appgroup src/main/resources/db.json ./data/db.json

# Variables de entorno
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -XX:+ExitOnOutOfMemoryError"
ENV SERVER_PORT=8080
ENV APP_VERSION=1.0.0
ENV SPRING_PROFILES_ACTIVE=docker

# Exponer puerto
EXPOSE 8080

# Cambiar a usuario no-root
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/v1/gapsi/info || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# Volúmenes para persistencia
VOLUME ["/app/data", "/app/logs"]