package com.gapsi.providers.controller;

import com.gapsi.providers.Utils.Constants;
import com.gapsi.providers.model.Provider;
import com.gapsi.providers.service.ProviderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operaciones sobre proveedores.
 *
 * <p>Exposición de endpoints bajo el prefijo <code>/gapsi</code> para:
 * listar proveedores paginados, crear nuevos proveedores, eliminar proveedores
 * y obtener información de la aplicación.</p>
 * @author Ilder Tocto
 * @version 1.0.0
 * @since 2025-10-18
 */
@RestController
@RequestMapping(Constants.API_BASE_PATH)
public class ProviderController {

    /**
     * Servicio que encapsula la lógica de negocio para proveedores.
     */
    private final ProviderService service;

    /**
     * Versión de la aplicación inyectada desde propiedades.
     * Valor por defecto: {@code 1.0.0}.
     */
    @Value(Constants.APP_VERSION_PROPERTY)
    private String appVersion;

    /**
     * Constructor del controlador.
     *
     * @param service instancia de {@link ProviderService} (inyección por constructor)
     */
    public ProviderController(ProviderService service) {
        this.service = service;
    }

    /**
     * Endpoint de información básica de la aplicación.
     *
     * @return mapa con las claves:
     *         <ul>
     *           <li>{@code mensaje} - mensaje de bienvenida</li>
     *           <li>{@code version} - versión de la aplicación</li>
     *         </ul>
     */
    @GetMapping(Constants.INFO_ENDPOINT)
    public Map<String, String> info() {
        Map<String, String> m = new HashMap<>();
        m.put(Constants.JSON_KEY_MESSAGE, Constants.WELCOME_MESSAGE);
        m.put(Constants.JSON_KEY_VERSION, appVersion);
        return m;
    }

    /**
     * Lista proveedores de forma paginada.
     *
     * @param page número de página (0-indexado). Valor por defecto: {@code 0}
     * @param size número de elementos por página. Valor por defecto: {@code 10}
     * @return {@link ResponseEntity} con un mapa que contiene:
     *         <ul>
     *           <li>{@code items} - lista de {@link Provider} de la página</li>
     *           <li>{@code page} - página solicitada</li>
     *           <li>{@code size} - tamaño de página</li>
     *           <li>{@code total} - número total de proveedores</li>
     *         </ul>
     */
    @GetMapping(Constants.PROVIDERS_ENDPOINT)
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        List<Provider> items = service.findPage(page, size);
        long total = service.count();
        Map<String, Object> resp = new HashMap<>();
        resp.put(Constants.JSON_KEY_ITEMS, items);
        resp.put(Constants.JSON_KEY_PAGE, page);
        resp.put(Constants.JSON_KEY_SIZE, size);
        resp.put(Constants.JSON_KEY_TOTAL, total);
        return ResponseEntity.ok(resp);
    }

    /**
     * Crea un nuevo proveedor.
     *
     * <p>Respuestas posibles:
     * <ul>
     *   <li>{@code 201 CREATED} - proveedor creado correctamente (devuelve el objeto creado)</li>
     *   <li>{@code 409 CONFLICT} - error de negocio (ej. proveedor duplicado)</li>
     *   <li>{@code 500 INTERNAL_SERVER_ERROR} - error inesperado</li>
     * </ul>
     *
     * @param request objeto {@link Provider} con los datos a crear (viene en el body)
     * @return {@link ResponseEntity} con el proveedor creado o un mapa con la clave {@code error}
     */
    @PostMapping(Constants.PROVIDERS_ENDPOINT)
    public ResponseEntity<?> create(@RequestBody Provider request) {
        try {
            Provider created = service.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(Constants.JSON_KEY_ERROR, e.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(Constants.JSON_KEY_ERROR, ex.getMessage()));
        }
    }

    /**
     * Elimina un proveedor por su identificador.
     *
     * <p>Respuestas posibles:
     * <ul>
     *   <li>{@code 200 OK} - eliminado correctamente (devuelve mensaje de confirmación)</li>
     *   <li>{@code 400 BAD_REQUEST} - id nulo o vacío</li>
     *   <li>{@code 404 NOT_FOUND} - proveedor no encontrado</li>
     *   <li>{@code 500 INTERNAL_SERVER_ERROR} - error inesperado</li>
     * </ul>
     *
     * @param id identificador del proveedor a eliminar (ruta)
     * @return {@link ResponseEntity} con mensaje de éxito o mapa con la clave {@code error}
     */
    @DeleteMapping(Constants.PROVIDERS_DELETE_ENDPOINT)
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of(Constants.JSON_KEY_SUCCESS_MESSAGE, Constants.PROVIDER_DELETED_MESSAGE));
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            boolean isNullEmptyError = false;
            for (String keyword : Constants.NULL_EMPTY_KEYWORDS) {
                if (msg.contains(keyword)) {
                    isNullEmptyError = true;
                    break;
                }
            }
            
            if (isNullEmptyError) {
                return ResponseEntity.badRequest().body(Map.of(Constants.JSON_KEY_ERROR, e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(Constants.JSON_KEY_ERROR, e.getMessage()));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(Constants.JSON_KEY_ERROR, Constants.INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

}
