# 🚀 Gapsi Providers - Full Stack Docker

Stack completo de la aplicación Gapsi Providers con Frontend React + Backend Spring Boot orquestado con Docker Compose.

## 📋 Arquitectura del Stack

```
┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │
│   React + Vite  │────│  Spring Boot    │
│   Port: 3000    │    │   Port: 8080    │
│   Nginx         │    │   Java 17       │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────────────────────┘
                     │
              ┌─────────────┐
              │   Docker    │
              │   Network   │
              └─────────────┘
```

## 🎯 URLs de la Aplicación

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Frontend** | http://localhost:3000 | Interfaz de usuario React |
| **Backend** | http://localhost:8080/api/v1/gapsi/info | API REST Spring Boot |
| **Frontend Dev** | http://localhost:3001 | Modo desarrollo con hot-reload |

## 🚀 Inicio Rápido

### Prerrequisitos

- Docker Desktop instalado
- Docker Compose (incluido con Docker Desktop)
- PowerShell (para Windows) o Bash (para Linux/Mac)

### Opción 1: Script de Gestión (Recomendado)

```bash
# Construir e iniciar todo
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener todo
docker-compose down
```

## 📁 Estructura del Proyecto

```
api-providers-v1/
├── docker-compose.yml              # Orquestación del stack
├── docker-stack.ps1               # Script de gestión
│
├── providers-back-end/
│   ├── Dockerfile                 # Backend container
│   ├── src/                       # Código Spring Boot
│   ├── pom.xml                    # Dependencias Maven
│   └── ...
│
└── providers-front-end/
    ├── Dockerfile                 # Frontend production
    ├── Dockerfile.dev             # Frontend development
    ├── src/                       # Código React
    ├── package.json               # Dependencias NPM
    └── ...
```

## 🔧 Comandos Disponibles
### Docker Compose

| Comando | Descripción |
|---------|-------------|
| `docker-compose up -d` | Iniciar stack en background |
| `docker-compose down` | Detener y remover stack |
| `docker-compose build` | Construir imágenes |
| `docker-compose logs -f [servicio]` | Ver logs |
| `docker-compose restart [servicio]` | Reiniciar servicio |
| `docker-compose ps` | Ver estado de servicios |

## 🛠️ Configuración

### Variables de Entorno

#### Backend
- `SERVER_PORT`: Puerto del servidor (default: 8080)
- `APP_VERSION`: Versión de la aplicación (default: 1.0.0)
- `JAVA_OPTS`: Opciones JVM (default: optimizado para container)
- `SPRING_PROFILES_ACTIVE`: Perfil Spring (default: docker)

#### Frontend
- `REACT_APP_API_URL`: URL de la API backend
- `NODE_ENV`: Entorno de Node.js (production/development)

### Volúmenes Persistentes

| Volumen | Propósito | Ruta en Container |
|---------|-----------|-------------------|
| `providers-data` | Datos del backend (JSON) | `/app/data` |
| `providers-logs` | Logs del backend | `/app/logs` |

### Red

- **Nombre**: `gapsi-providers-network`
- **Tipo**: Bridge
- **Propósito**: Comunicación entre frontend y backend

## 🔄 Flujos de Trabajo

### Desarrollo

```powershell
# Iniciar en modo desarrollo
.\docker-stack.ps1 dev

# Esto inicia:
# - Backend en puerto 8080
# - Frontend dev en puerto 3001 con hot-reload
```

## 🔍 Health Checks

### Backend
- **Endpoint**: `http://localhost:8080/api/v1/gapsi/info`
- **Intervalo**: 30s
- **Timeout**: 10s
- **Reintentos**: 3

### Frontend
- **Endpoint**: `http://localhost:3000/`
- **Intervalo**: 30s
- **Timeout**: 10s
- **Reintentos**: 3
