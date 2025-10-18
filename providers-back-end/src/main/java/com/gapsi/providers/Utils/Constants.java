package com.gapsi.providers.Utils;

/**
 * Constantes centralizadas de la aplicación Gapsi Providers.
 *
 * <p>Esta clase contiene todas las constantes utilizadas en la aplicación
 * para evitar valores hardcodeados dispersos en el código y facilitar
 * el mantenimiento y configuración.</p>
 *
 * @author Ilder Tocto
 * @version 1.0.0
 * @since 2025-10-18
 */
public final class Constants {

    // Constructor privado para prevenir instanciación
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad");
    }

    // =================================================================
    // CONSTANTES DE API Y ENDPOINTS
    // =================================================================

    /**
     * Ruta base del controlador principal.
     */
    public static final String API_BASE_PATH = "/gapsi";

    /**
     * Endpoint para información de la aplicación.
     */
    public static final String INFO_ENDPOINT = "/info";

    /**
     * Endpoint base para operaciones de proveedores.
     */
    public static final String PROVIDERS_ENDPOINT = "/providers";

    /**
     * Endpoint para eliminar proveedores (con parámetro de ruta).
     */
    public static final String PROVIDERS_DELETE_ENDPOINT = "/providers/{id}";

    // =================================================================
    // CONSTANTES DE PAGINACIÓN
    // =================================================================

    /**
     * Página por defecto para paginación (0-indexado).
     */
    public static final String DEFAULT_PAGE = "0";

    /**
     * Tamaño de página por defecto.
     */
    public static final String DEFAULT_PAGE_SIZE = "10";

    // =================================================================
    // CONSTANTES DE MENSAJES
    // =================================================================

    /**
     * Mensaje de bienvenida de la aplicación.
     */
    public static final String WELCOME_MESSAGE = "Bienvenido Candidato Ilder Tocto";

    /**
     * Mensaje de confirmación al eliminar un proveedor.
     */
    public static final String PROVIDER_DELETED_MESSAGE = "Proveedor eliminado correctamente";

    /**
     * Mensaje de error interno del servidor.
     */
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Error interno del servidor";

    // =================================================================
    // CONSTANTES DE VALIDACIÓN Y ERRORES
    // =================================================================

    /**
     * Mensaje de error cuando ya existe un proveedor con el mismo nombre.
     */
    public static final String DUPLICATE_PROVIDER_ERROR = "Proveedor con el mismo nombre ya existe";

    /**
     * Mensaje de error cuando el ID es nulo o vacío.
     */
    public static final String INVALID_ID_ERROR = "El id no puede ser nulo o vacío";

    /**
     * Mensaje de error cuando no se encuentra un proveedor.
     * Usar con String.format(PROVIDER_NOT_FOUND_ERROR, id).
     */
    public static final String PROVIDER_NOT_FOUND_ERROR = "Proveedor con id %s no existe";

    // =================================================================
    // CONSTANTES DE CLAVES DE RESPUESTA JSON
    // =================================================================

    /**
     * Clave para el mensaje en respuestas JSON.
     */
    public static final String JSON_KEY_MESSAGE = "mensaje";

    /**
     * Clave para la versión en respuestas JSON.
     */
    public static final String JSON_KEY_VERSION = "version";

    /**
     * Clave para elementos/items en respuestas de paginación.
     */
    public static final String JSON_KEY_ITEMS = "items";

    /**
     * Clave para página actual en respuestas de paginación.
     */
    public static final String JSON_KEY_PAGE = "page";

    /**
     * Clave para tamaño de página en respuestas de paginación.
     */
    public static final String JSON_KEY_SIZE = "size";

    /**
     * Clave para total de elementos en respuestas de paginación.
     */
    public static final String JSON_KEY_TOTAL = "total";

    /**
     * Clave para errores en respuestas JSON.
     */
    public static final String JSON_KEY_ERROR = "error";

    /**
     * Clave para mensajes de éxito en respuestas JSON.
     */
    public static final String JSON_KEY_SUCCESS_MESSAGE = "message";

    // =================================================================
    // CONSTANTES DE CONFIGURACIÓN CORS
    // =================================================================

    /**
     * Patrón de mapeo para todas las rutas CORS.
     */
    public static final String CORS_MAPPING_PATTERN = "/**";

    /**
     * Origen permitido para desarrollo local (puerto 3000).
     */
    public static final String CORS_ORIGIN_3000 = "http://localhost:3000";

    /**
     * Origen permitido para desarrollo local (puerto 3001).
     */
    public static final String CORS_ORIGIN_3001 = "http://localhost:3001";

    /**
     * Métodos HTTP permitidos para CORS.
     */
    public static final String[] CORS_ALLOWED_METHODS = {
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    };

    /**
     * Headers permitidos para CORS.
     */
    public static final String CORS_ALLOWED_HEADERS = "*";

    /**
     * Tiempo máximo de cache para preflight requests (en segundos).
     */
    public static final long CORS_MAX_AGE = 3600L;

    // =================================================================
    // CONSTANTES DE VALIDACIÓN DE STRINGS
    // =================================================================

    /**
     * Palabras clave para detectar errores de validación de ID nulo/vacío.
     */
    public static final String[] NULL_EMPTY_KEYWORDS = {"nulo", "vacío", "vacio"};

    // =================================================================
    // CONSTANTES DE CONFIGURACIÓN DE APLICACIÓN
    // =================================================================

    /**
     * Clave de propiedad para la versión de la aplicación.
     */
    public static final String APP_VERSION_PROPERTY = "${app.version:1.0.0}";

    /**
     * Versión por defecto de la aplicación.
     */
    public static final String DEFAULT_APP_VERSION = "1.0.0";
}