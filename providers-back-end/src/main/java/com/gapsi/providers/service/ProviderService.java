// language: java
package com.gapsi.providers.service;

import com.gapsi.providers.model.Provider;

import java.util.List;

/**
 * Contrato del servicio de negocio para operaciones sobre {@link Provider}.
 *
 * <p>Define las operaciones básicas esperadas por la capa de servicio:
 * paginación, creación, eliminación y conteo de proveedores.</p>
 * @author Ilder Tocto
 * @version 1.0
 * @since 2025-10-18
 */
public interface ProviderService {

    /**
     * Devuelve una página de proveedores.
     *
     * <p>La paginación utiliza índices 0-based: {@code page} = 0 corresponde a la primera página.
     * Si {@code page} o {@code size} producen un rango fuera de los elementos disponibles,
     * debe devolverse una lista vacía.</p>
     *
     * @param page índice de página (0-based)
     * @param size cantidad de elementos por página
     * @return lista de {@link Provider} correspondiente a la página solicitada (nunca {@code null})
     */
    List<Provider> findPage(int page, int size);

    /**
     * Crea un nuevo proveedor.
     *
     * <p>Se espera que la implementación valide la entrada y asigne identificador y fecha de creación
     * cuando corresponda. Si la creación no es posible por regla de negocio (ej. nombre duplicado),
     * la implementación puede lanzar una excepción adecuada (por ejemplo {@link IllegalArgumentException}).</p>
     *
     * @param p objeto {@link Provider} con los datos a crear
     * @return el {@link Provider} creado (con id y otros campos asignados)
     */
    Provider create(Provider p);

    /**
     * Elimina un proveedor por su identificador.
     *
     * <p>La implementación debe validar el identificador y lanzar una excepción si el id es inválido
     * o si no existe el proveedor.</p>
     *
     * @param id identificador del proveedor a eliminar
     */
    void delete(String id);

    /**
     * Devuelve el número total de proveedores almacenados.
     *
     * @return cantidad total de proveedores
     */
    long count();
}
