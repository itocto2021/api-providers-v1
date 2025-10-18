# =========================================
# Script de PowerShell para Docker Stack
# =========================================
# 
# Script de PowerShell para facilitar la gestión completa
# del stack Gapsi Providers (Frontend + Backend).
#
# Uso:
#   .\docker-stack.ps1 build      # Construir ambas imágenes
#   .\docker-stack.ps1 start      # Iniciar stack completo
#   .\docker-stack.ps1 stop       # Detener stack
#   .\docker-stack.ps1 restart    # Reiniciar stack
#   .\docker-stack.ps1 logs       # Ver logs
#   .\docker-stack.ps1 frontend   # Solo frontend
#   .\docker-stack.ps1 backend    # Solo backend
#
# @author Ilder Tocto
# @version 1.0.0
# @since 2025-10-18

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("build", "start", "stop", "restart", "logs", "frontend", "backend", "dev", "clean", "status", "test", "help")]
    [string]$Command,
    
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$AdditionalArgs
)

# Variables de configuración
$StackName = "gapsi-providers"
$FrontendPort = "3000"
$BackendPort = "8080"
$DevPort = "3001"

# Función para mostrar mensajes con colores
function Write-ColorMessage {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

# Función para mostrar ayuda
function Show-Help {
    Write-ColorMessage "Gapsi Providers Docker Stack Management Script" "Blue"
    Write-Host ""
    Write-Host "Uso: .\docker-stack.ps1 [COMANDO]"
    Write-Host ""
    Write-Host "Comandos disponibles:"
    Write-ColorMessage "  build     " "Green" -NoNewline; Write-Host "Construir todas las imágenes del stack"
    Write-ColorMessage "  start     " "Green" -NoNewline; Write-Host "Iniciar stack completo (frontend + backend)"
    Write-ColorMessage "  stop      " "Green" -NoNewline; Write-Host "Detener stack completo"
    Write-ColorMessage "  restart   " "Green" -NoNewline; Write-Host "Reiniciar stack completo"
    Write-ColorMessage "  logs      " "Green" -NoNewline; Write-Host "Ver logs de todos los servicios"
    Write-ColorMessage "  frontend  " "Green" -NoNewline; Write-Host "Gestionar solo el frontend"
    Write-ColorMessage "  backend   " "Green" -NoNewline; Write-Host "Gestionar solo el backend"
    Write-ColorMessage "  dev       " "Green" -NoNewline; Write-Host "Iniciar en modo desarrollo"
    Write-ColorMessage "  clean     " "Green" -NoNewline; Write-Host "Limpiar contenedores e imágenes"
    Write-ColorMessage "  status    " "Green" -NoNewline; Write-Host "Ver estado del stack"
    Write-ColorMessage "  test      " "Green" -NoNewline; Write-Host "Ejecutar tests del stack"
    Write-ColorMessage "  help      " "Green" -NoNewline; Write-Host "Mostrar esta ayuda"
    Write-Host ""
    Write-Host "URLs de la aplicación:"
    Write-ColorMessage "  Frontend:  " "Cyan" -NoNewline; Write-Host "http://localhost:$FrontendPort"
    Write-ColorMessage "  Backend:   " "Cyan" -NoNewline; Write-Host "http://localhost:$BackendPort/api/v1/gapsi/info"
    Write-ColorMessage "  Frontend Dev: " "Cyan" -NoNewline; Write-Host "http://localhost:$DevPort (solo modo dev)"
    Write-Host ""
    Write-Host "Ejemplos:"
    Write-Host "  .\docker-stack.ps1 build && .\docker-stack.ps1 start"
    Write-Host "  .\docker-stack.ps1 logs backend"
    Write-Host "  .\docker-stack.ps1 dev"
}

# Función para verificar si Docker está disponible
function Test-DockerAvailable {
    try {
        docker --version | Out-Null
        docker-compose --version | Out-Null
        return $true
    }
    catch {
        Write-ColorMessage "Error: Docker o Docker Compose no están instalados o no están disponibles" "Red"
        return $false
    }
}

# Función para construir todas las imágenes
function Build-Stack {
    Write-ColorMessage "Construyendo stack completo..." "Blue"
    
    try {
        docker-compose build
        Write-ColorMessage "✓ Stack construido exitosamente" "Green"
    }
    catch {
        Write-ColorMessage "✗ Error al construir el stack" "Red"
        throw
    }
}

# Función para iniciar stack completo
function Start-Stack {
    Write-ColorMessage "Iniciando stack completo..." "Blue"
    
    try {
        docker-compose up -d
        Write-ColorMessage "✓ Stack iniciado exitosamente" "Green"
        Write-Host ""
        Write-ColorMessage "URLs de la aplicación:" "Blue"
        Write-ColorMessage "  Frontend:  http://localhost:$FrontendPort" "Cyan"
        Write-ColorMessage "  Backend:   http://localhost:$BackendPort/api/v1/gapsi/info" "Cyan"
    }
    catch {
        Write-ColorMessage "✗ Error al iniciar el stack" "Red"
        throw
    }
}

# Función para detener stack
function Stop-Stack {
    Write-ColorMessage "Deteniendo stack..." "Blue"
    
    try {
        docker-compose down
        Write-ColorMessage "✓ Stack detenido" "Green"
    }
    catch {
        Write-ColorMessage "✗ Error al detener el stack" "Red"
        throw
    }
}

# Función para reiniciar stack
function Restart-Stack {
    Write-ColorMessage "Reiniciando stack..." "Blue"
    Stop-Stack
    Start-Stack
}

# Función para ver logs
function Show-Logs {
    Write-ColorMessage "Mostrando logs del stack..." "Blue"
    
    try {
        if ($AdditionalArgs) {
            docker-compose logs -f $AdditionalArgs
        }
        else {
            docker-compose logs -f
        }
    }
    catch {
        Write-ColorMessage "✗ Error al mostrar logs" "Red"
        throw
    }
}

# Función para gestionar frontend
function Manage-Frontend {
    Write-ColorMessage "Gestionando frontend..." "Blue"
    
    $action = if ($AdditionalArgs) { $AdditionalArgs[0] } else { "start" }
    
    try {
        switch ($action) {
            "start" { docker-compose up -d frontend }
            "stop" { docker-compose stop frontend }
            "restart" { docker-compose restart frontend }
            "logs" { docker-compose logs -f frontend }
            "build" { docker-compose build frontend }
            default { docker-compose up -d frontend }
        }
        Write-ColorMessage "✓ Operación de frontend completada" "Green"
    }
    catch {
        Write-ColorMessage "✗ Error en operación de frontend" "Red"
        throw
    }
}

# Función para gestionar backend
function Manage-Backend {
    Write-ColorMessage "Gestionando backend..." "Blue"
    
    $action = if ($AdditionalArgs) { $AdditionalArgs[0] } else { "start" }
    
    try {
        switch ($action) {
            "start" { docker-compose up -d backend }
            "stop" { docker-compose stop backend }
            "restart" { docker-compose restart backend }
            "logs" { docker-compose logs -f backend }
            "build" { docker-compose build backend }
            default { docker-compose up -d backend }
        }
        Write-ColorMessage "✓ Operación de backend completada" "Green"
    }
    catch {
        Write-ColorMessage "✗ Error en operación de backend" "Red"
        throw
    }
}

# Función para modo desarrollo
function Start-DevMode {
    Write-ColorMessage "Iniciando modo desarrollo..." "Blue"
    
    try {
        # Iniciar backend primero
        docker-compose up -d backend
        
        # Iniciar frontend en modo desarrollo
        docker-compose --profile dev up -d frontend-dev
        
        Write-ColorMessage "✓ Modo desarrollo iniciado" "Green"
        Write-Host ""
        Write-ColorMessage "URLs de desarrollo:" "Blue"
        Write-ColorMessage "  Frontend Dev: http://localhost:$DevPort" "Cyan"
        Write-ColorMessage "  Backend:      http://localhost:$BackendPort/api/v1/gapsi/info" "Cyan"
    }
    catch {
        Write-ColorMessage "✗ Error al iniciar modo desarrollo" "Red"
        throw
    }
}

# Función para limpiar
function Clean-Stack {
    Write-ColorMessage "Limpiando stack..." "Blue"
    
    try {
        # Detener y remover contenedores
        docker-compose down --volumes --remove-orphans
        
        # Remover imágenes del stack
        docker image prune -f
        
        Write-ColorMessage "✓ Limpieza completada" "Green"
    }
    catch {
        Write-ColorMessage "✗ Error durante la limpieza" "Red"
        throw
    }
}

# Función para ver estado
function Show-Status {
    Write-ColorMessage "Estado del stack:" "Blue"
    docker-compose ps
    
    Write-Host ""
    Write-ColorMessage "Imágenes del stack:" "Blue"
    docker images | Select-String "gapsi-providers"
    
    Write-Host ""
    Write-ColorMessage "Volúmenes:" "Blue"
    docker volume ls | Select-String "providers"
}

# Función para ejecutar tests
function Run-Tests {
    Write-ColorMessage "Ejecutando tests del stack..." "Blue"
    
    # Verificar que los servicios estén corriendo
    $runningServices = docker-compose ps --services --filter "status=running"
    
    if ($runningServices -notcontains "backend") {
        Write-ColorMessage "✗ El backend no está corriendo. Inicia el stack primero." "Red"
        return
    }
    
    # Esperar a que los servicios estén listos
    Write-ColorMessage "Esperando a que los servicios estén listos..." "Yellow"
    Start-Sleep -Seconds 15
    
    # Test del backend
    Write-ColorMessage "Testing backend endpoint..." "Blue"
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$BackendPort/api/v1/gapsi/info" -Method Get
        if ($response.StatusCode -eq 200) {
            Write-ColorMessage "✓ Backend health check exitoso" "Green"
        }
    }
    catch {
        Write-ColorMessage "✗ Backend health check falló" "Red"
    }
    
    # Test del frontend (si está corriendo)
    if ($runningServices -contains "frontend") {
        Write-ColorMessage "Testing frontend..." "Blue"
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$FrontendPort" -Method Get
            if ($response.StatusCode -eq 200) {
                Write-ColorMessage "✓ Frontend health check exitoso" "Green"
            }
        }
        catch {
            Write-ColorMessage "✗ Frontend health check falló" "Red"
        }
    }
    
    Write-ColorMessage "✓ Tests completados" "Green"
}

# Función principal
function Main {
    # Verificar que Docker esté disponible
    if (-not (Test-DockerAvailable)) {
        exit 1
    }
    
    switch ($Command) {
        "build" { Build-Stack }
        "start" { Start-Stack }
        "stop" { Stop-Stack }
        "restart" { Restart-Stack }
        "logs" { Show-Logs }
        "frontend" { Manage-Frontend }
        "backend" { Manage-Backend }
        "dev" { Start-DevMode }
        "clean" { Clean-Stack }
        "status" { Show-Status }
        "test" { Run-Tests }
        "help" { Show-Help }
        default { 
            Write-ColorMessage "Error: Comando desconocido '$Command'" "Red"
            Show-Help
            exit 1
        }
    }
}

# Ejecutar función principal
try {
    Main
}
catch {
    Write-ColorMessage "Error: $($_.Exception.Message)" "Red"
    exit 1
}