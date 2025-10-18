# Providers Backend API

## Descripción

API REST para gestión de proveedores desarrollada con Spring Boot. Esta aplicación forma parte del ecosistema e-commerce de Gapsi y proporciona operaciones CRUD para la gestión de proveedores.

## Tecnologías Utilizadas

- **Java**: 17 (JDK)
- **Spring Boot**: 3.2.2
- **Maven**: Gestión de dependencias y construcción
- **Lombok**: Reducción de código boilerplate
- **Jackson**: Serialización JSON (incluyendo java.time)
- **OpenAPI 3.0**: Documentación de la API

## Características

- API RESTful siguiendo principios REST
- Documentación automática con OpenAPI/Swagger
- Configuración CORS para integración frontend
- Manejo de fechas con `java.time.Instant`
- Arquitectura limpia con separación de capas (Controller, Service, Repository, Model)

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/gapsi/providers/
│   │   ├── ProvidersApplication.java      # Clase principal de Spring Boot
│   │   ├── config/
│   │   │   └── CorsConfig.java           # Configuración CORS
│   │   ├── controller/
│   │   │   └── ProviderController.java   # Controladores REST
│   │   ├── model/
│   │   │   └── Provider.java             # Modelo de datos
│   │   ├── repository/
│   │   │   └── ProviderRepository.java   # Capa de datos
│   │   └── service/
│   │       ├── ProviderService.java      # Interface de servicio
│   │       └── impl/
│   │           └── ProviderServiceImpl.java # Implementación del servicio
│   └── resources/
│       ├── application.properties        # Configuración de la aplicación
│       ├── db.json                      # Base de datos simulada
│       └── openapi.yml                  # Especificación OpenAPI
└── test/
    └── java/com/gapsi/providers/
        └── ProvidersApplicationTests.java # Tests unitarios
```

## Requisitos Previos

- **Java JDK 17** o superior
- **Maven 3.6+**
- **Puerto 8080** disponible

## Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/itocto2021/api-providers-v1.git
```

## Configuración

La aplicación utiliza el archivo `application.properties` para su configuración:

```properties
spring.application.name=providers
server.port=8080
app.version=1.0.0
server.servlet.context-path=/api/v1
```

### Variables de Entorno

Puedes sobrescribir la configuración usando variables de entorno:

- `SERVER_PORT`: Puerto del servidor (default: 8080)
- `SPRING_PROFILES_ACTIVE`: Perfil activo de Spring

## API Endpoints

La API está disponible en: `http://localhost:8080/api/v1`

### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/gapsi/info` | Información de la aplicación |
| GET | `/providers` | Listar todos los proveedores |
| GET | `/providers/{id}` | Obtener proveedor por ID |
| POST | `/providers` | Crear nuevo proveedor |
| PUT | `/providers/{id}` | Actualizar proveedor |
| DELETE | `/providers/{id}` | Eliminar proveedor |

### Ejemplo de Modelo Provider

```json
{
  "id": "1",
  "nombre": "Proveedor Ejemplo",
  "razonSocial": "Proveedor Ejemplo S.A.",
  "direccion": "Calle Principal 123",
  "createdAt": "2025-10-18T10:30:00Z"
}
```

## Documentación de la API

La documentación completa de la API está disponible en:

- **OpenAPI Spec**: `src/main/resources/openapi.yml`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (si está habilitado)

## Desarrollo

### Estructura de Capas

1. **Controller**: Maneja las peticiones HTTP y respuestas
2. **Service**: Contiene la lógica de negocio
3. **Repository**: Maneja el acceso a datos
4. **Model**: Define las entidades del dominio
5. **Config**: Configuraciones de la aplicación

## Testing

El proyecto incluye tests unitarios utilizando:

- JUnit 5
- Spring Boot Test
- MockMVC para tests de integración


## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un Pull Request

## Autor

- **Ilder Tocto** - *Desarrollo inicial* 

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles.

## Notas Adicionales

- La aplicación utiliza una base de datos simulada (`db.json`) para desarrollo
- El puerto por defecto es 8080, configurable via `application.properties`
- CORS está configurado para permitir requests desde el frontend
- Todas las fechas se manejan en formato ISO-8601 (Instant)


## Patrones de Diseño Implementados

### 1. **Layered Architecture (Arquitectura en Capas)**
```
Controller → Service → Repository → Model
```

**Implementación:**
- `ProviderController`: Capa de presentación (REST endpoints)
- `ProviderService/ProviderServiceImpl`: Capa de lógica de negocio
- `ProviderRepository`: Capa de acceso a datos
- `Provider`: Capa de modelo/dominio

**Beneficios:**
- Separación clara de responsabilidades
- Facilita el mantenimiento y testing
- Permite cambios independientes en cada capa

### 2. **Dependency Injection (Inyección de Dependencias)**
```java
// Constructor injection en ProviderController
public ProviderController(ProviderService service) {
    this.service = service;
}

// Constructor injection en ProviderServiceImpl
public ProviderServiceImpl(ProviderRepository repo) {
    this.repo = repo;
}
```

**Implementación:**
- Uso de Spring Framework para IoC (Inversión de Control)
- Inyección por constructor (recomendada)
- Anotaciones `@RestController`, `@Service`, `@Component`

**Beneficios:**
- Bajo acoplamiento entre componentes
- Facilita el testing con mocks
- Gestión automática del ciclo de vida de objetos

### 3. **Strategy Pattern (Patrón Estrategia)**
```java
// Interface que define la estrategia
public interface ProviderService {
    List<Provider> findPage(int page, int size);
    Provider create(Provider p);
    void delete(String id);
    int countTotal();
}

// Implementación concreta de la estrategia
@Service
public class ProviderServiceImpl implements ProviderService {
    // Implementación específica
}
```

**Implementación:**
- Interface `ProviderService` define el contrato
- `ProviderServiceImpl` proporciona implementación concreta
- Permite múltiples implementaciones (ej: base de datos, memoria, cache)

**Beneficios:**
- Flexibilidad para cambiar algoritmos
- Fácil extensión con nuevas implementaciones
- Respeta el principio Open/Closed

### 4. **Builder Pattern (Patrón Constructor)**
```java
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provider {
    private String id;
    private String nombre;
    private String razonSocial;
    private String direccion;
    private Instant createdAt;
}

// Uso del builder
Provider newProvider = Provider.builder()
    .id(UUID.randomUUID().toString())
    .nombre(p.getNombre())
    .razonSocial(p.getRazonSocial())
    .direccion(p.getDireccion())
    .createdAt(Instant.now())
    .build();
```

**Implementación:**
- Uso de Lombok `@Builder` para generar el patrón automáticamente
- Construcción fluida y legible de objetos
- Inmutabilidad opcional de objetos

**Beneficios:**
- Construcción clara y legible de objetos complejos
- Validación en tiempo de construcción
- Inmutabilidad de objetos

### 5. **Data Transfer Object (DTO) / POJO Pattern**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provider {
    // Encapsulación de datos con getters/setters automáticos
}
```

**Implementación:**
- Uso de Lombok para reducir boilerplate
- `@Data` genera getters, setters, equals, hashCode, toString
- Objetos simples para transferencia de datos

**Beneficios:**
- Menos código boilerplate
- Consistencia en implementación
- Fácil mantenimiento

### 6. **Repository Pattern (Patrón Repositorio)**
```java
@Component
public class ProviderRepository {
    public List<Provider> findAll() { /* implementación */ }
    public Optional<Provider> findById(String id) { /* implementación */ }
    public Provider save(Provider provider) { /* implementación */ }
    public void deleteById(String id) { /* implementación */ }
}
```

**Implementación:**
- Encapsula la lógica de acceso a datos
- Abstrae el mecanismo de persistencia (JSON file en este caso)
- Operaciones CRUD centralizadas

**Beneficios:**
- Abstracción de la capa de datos
- Facilita cambio de tecnología de persistencia
- Testing simplificado con repositorios mock

### 7. **Template Method Pattern (en Spring Framework)**
```java
@RestController
@RequestMapping("/gapsi")
public class ProviderController {
    // Spring maneja el ciclo de vida HTTP request/response
}
```

**Implementación:**
- Spring Boot define el template para manejo HTTP
- Los controllers implementan los pasos específicos
- Manejo automático de serialización/deserialización

**Beneficios:**
- Estructura consistente en controllers
- Manejo automático de aspectos técnicos
- Enfoque en lógica de negocio

### 8. **Facade Pattern (Patrón Fachada)**
```java
@RestController
public class ProviderController {
    // Proporciona una interfaz simplificada para operaciones complejas
    private final ProviderService service;
}
```

**Implementación:**
- Controller actúa como fachada para operaciones del sistema
- Simplifica la interacción cliente-servidor
- Oculta complejidad interna del sistema

**Beneficios:**
- API simplificada para clientes externos
- Desacopla clientes de implementación interna
- Punto único de entrada para funcionalidades

### 9. **Singleton Pattern (Implícito con Spring)**
```java
@Service
@Component
@RestController
```

**Implementación:**
- Spring maneja beans como singletons por defecto
- Una sola instancia por contexto de aplicación
- Gestión automática del ciclo de vida

**Beneficios:**
- Uso eficiente de memoria
- Estado consistente en toda la aplicación
- Gestión centralizada de configuración

### 10. **Configuration Pattern**
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Configuración específica
            }
        };
    }
}
```

**Implementación:**
- Clases de configuración separadas
- Configuración declarativa con anotaciones
- Separación de concerns de configuración

**Beneficios:**
- Configuración centralizada
- Fácil modificación sin cambiar lógica
- Reutilización de configuraciones

## Principios SOLID Aplicados

### Single Responsibility Principle (SRP)
- Cada clase tiene una responsabilidad específica
- `ProviderController`: Manejo HTTP
- `ProviderService`: Lógica de negocio
- `ProviderRepository`: Acceso a datos

### Open/Closed Principle (OCP)
- Interface `ProviderService` permite extensión sin modificación
- Nuevas implementaciones sin cambiar código existente

### Liskov Substitution Principle (LSP)
- Cualquier implementación de `ProviderService` puede sustituir a otra
- Contratos bien definidos en interfaces

### Interface Segregation Principle (ISP)
- Interfaces específicas y cohesivas
- `ProviderService` solo expone operaciones necesarias

### Dependency Inversion Principle (DIP)
- Dependencias hacia abstracciones (interfaces)
- `ProviderController` depende de `ProviderService`, no de implementación concreta
