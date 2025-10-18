# =========================================
# Script de PowerShell para Docker
# =========================================
# 
# Script de PowerShell para facilitar la construcción 
# y ejecución de la aplicación Gapsi Providers en Docker.
#
# Uso:
#   .\docker-run.ps1 build   # Construir imagen
#   .\docker-run.ps1 start   # Iniciar contenedor
#   .\docker-run.ps1 stop    # Detener contenedor
#   .\docker-run.ps1 restart # Reiniciar contenedor
#   .\docker-run.ps1 logs    # Ver logs
#   .\docker-run.ps1 clean   # Limpiar contenedores e imágenes
#
# @author Ilder Tocto
# @version 1.0.0
# @since 2025-10-18

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("build", "start", "stop", "restart", "logs", "shell", "clean", "status", "test", "help")]
    [string]$Command,
    
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$AdditionalArgs
)

# Variables de configuración
$ImageName = "gapsi-providers"
$ContainerName = "gapsi-providers-container"
$Port = "8080"
$Version = "1.0.0"

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
    Write-ColorMessage "Gapsi Providers Docker Management Script" "Blue"
    Write-Host ""
    Write-Host "Uso: .\docker-run.ps1 [COMANDO]"
    Write-Host ""
    Write-Host "Comandos disponibles:"
    Write-ColorMessage "  build     " "Green" -NoNewline; Write-Host "Construir la imagen Docker"
    Write-ColorMessage "  start     " "Green" -NoNewline; Write-Host "Iniciar el contenedor"
    Write-ColorMessage "  stop      " "Green" -NoNewline; Write-Host "Detener el contenedor"
    Write-ColorMessage "  restart   " "Green" -NoNewline; Write-Host "Reiniciar el contenedor"
    Write-ColorMessage "  logs      " "Green" -NoNewline; Write-Host "Ver logs del contenedor"
    Write-ColorMessage "  shell     " "Green" -NoNewline; Write-Host "Acceder al shell del contenedor"
    Write-ColorMessage "  clean     " "Green" -NoNewline; Write-Host "Limpiar contenedores e imágenes"
    Write-ColorMessage "  status    " "Green" -NoNewline; Write-Host "Ver estado del contenedor"
    Write-ColorMessage "  test      " "Green" -NoNewline; Write-Host "Ejecutar tests de la API"
    Write-ColorMessage "  help      " "Green" -NoNewline; Write-Host "Mostrar esta ayuda"
    Write-Host ""
    Write-Host "Ejemplos:"
    Write-Host "  .\docker-run.ps1 build"
    Write-Host "  .\docker-run.ps1 start"
    Write-Host "  .\docker-run.ps1 logs"
}

# Función para verificar si Docker está disponible
function Test-DockerAvailable {
    try {
        docker --version | Out-Null
        return $true
    }
    catch {
        Write-ColorMessage "Error: Docker no está instalado o no está disponible" "Red"
        return $false
    }
}

# Función para construir la imagen
function Build-Image {
    Write-ColorMessage "Construyendo imagen Docker..." "Blue"
    
    try {
        docker build -t "${ImageName}:${Version}" -t "${ImageName}:latest" .
        Write-ColorMessage "✓ Imagen construida exitosamente" "Green"
    }
    catch {
        Write-ColorMessage "✗ Error al construir la imagen" "Red"
        throw
    }
}

# Función para iniciar el contenedor
function Start-Container {
    Write-ColorMessage "Iniciando contenedor..." "Blue"
    
    try {
        # Detener contenedor existente si está corriendo
        $runningContainer = docker ps -q -f "name=$ContainerName"
        if ($runningContainer) {
            Write-ColorMessage "Deteniendo contenedor existente..." "Yellow"
            docker stop $ContainerName
        }
        
        # Remover contenedor existente si existe
        $existingContainer = docker ps -aq -f "name=$ContainerName"
        if ($existingContainer) {
            docker rm $ContainerName
        }
        
        # Crear y ejecutar nuevo contenedor
        docker run -d `
            --name $ContainerName `
            -p "${Port}:8080" `
            -v "gapsi-providers-data:/app/data" `
            -v "gapsi-providers-logs:/app/logs" `
            -e "JAVA_OPTS=-Xmx512m -Xms256m" `
            "${ImageName}:latest"
        
        Write-ColorMessage "✓ Contenedor iniciado exitosamente" "Green"
        Write-ColorMessage "URL de la aplicación: http://localhost:${Port}/api/v1/gapsi/info" "Blue"
    }
    catch {
        Write-ColorMessage "✗ Error al iniciar el contenedor" "Red"
        throw
    }
}

# Función para detener el contenedor
function Stop-Container {
    Write-ColorMessage "Deteniendo contenedor..." "Blue"
    
    try {
        docker stop $ContainerName 2>$null
        Write-ColorMessage "✓ Contenedor detenido" "Green"
    }
    catch {
        Write-ColorMessage "Contenedor no estaba corriendo o no existe" "Yellow"
    }
}

# Función para reiniciar el contenedor
function Restart-Container {
    Write-ColorMessage "Reiniciando contenedor..." "Blue"
    Stop-Container
    Start-Container
}

# Función para ver logs
function Show-Logs {
    Write-ColorMessage "Mostrando logs del contenedor..." "Blue"
    
    try {
        if ($AdditionalArgs) {
            docker logs $ContainerName $AdditionalArgs
        }
        else {
            docker logs $ContainerName
        }
    }
    catch {
        Write-ColorMessage "✗ Error al mostrar logs" "Red"
        throw
    }
}

# Función para acceder al shell
function Access-Shell {
    Write-ColorMessage "Accediendo al shell del contenedor..." "Blue"
    
    try {
        docker exec -it $ContainerName /bin/sh
    }
    catch {
        Write-ColorMessage "✗ Error al acceder al shell" "Red"
        throw
    }
}

# Función para limpiar
function Clean-Up {
    Write-ColorMessage "Limpiando contenedores e imágenes..." "Blue"
    
    try {
        # Detener y remover contenedor
        docker stop $ContainerName 2>$null
        docker rm $ContainerName 2>$null
        
        # Remover imágenes
        docker rmi "${ImageName}:latest" "${ImageName}:${Version}" 2>$null
        
        # Limpiar imágenes huérfanas
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
    Write-ColorMessage "Estado del contenedor:" "Blue"
    docker ps -a -f "name=$ContainerName" --format "table {{.Names}}`t{{.Status}}`t{{.Ports}}"
    
    Write-Host ""
    Write-ColorMessage "Imágenes disponibles:" "Blue"
    docker images $ImageName --format "table {{.Repository}}`t{{.Tag}}`t{{.Size}}`t{{.CreatedAt}}"
}

# Función para ejecutar tests
function Run-Tests {
    Write-ColorMessage "Ejecutando tests de la API..." "Blue"
    
    # Verificar que el contenedor esté corriendo
    $runningContainer = docker ps -q -f "name=$ContainerName"
    if (-not $runningContainer) {
        Write-ColorMessage "✗ El contenedor no está corriendo. Inicia el contenedor primero." "Red"
        return
    }
    
    # Esperar a que la aplicación esté lista
    Write-ColorMessage "Esperando a que la aplicación esté lista..." "Yellow"
    Start-Sleep -Seconds 10
    
    # Test básico de health check
    Write-ColorMessage "Testing health endpoint..." "Blue"
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:${Port}/api/v1/gapsi/info" -Method Get
        if ($response.StatusCode -eq 200) {
            Write-ColorMessage "✓ Health check exitoso" "Green"
        }
    }
    catch {
        Write-ColorMessage "✗ Health check falló" "Red"
    }
    
    Write-ColorMessage "✓ Tests básicos completados" "Green"
}

# Función principal
function Main {
    # Verificar que Docker esté disponible
    if (-not (Test-DockerAvailable)) {
        exit 1
    }
    
    switch ($Command) {
        "build" { Build-Image }
        "start" { Start-Container }
        "stop" { Stop-Container }
        "restart" { Restart-Container }
        "logs" { Show-Logs }
        "shell" { Access-Shell }
        "clean" { Clean-Up }
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