package com.gapsi.providers.service.impl;

import com.gapsi.providers.Utils.Constants;
import com.gapsi.providers.model.Provider;
import com.gapsi.providers.repository.ProviderRepository;
import com.gapsi.providers.service.ProviderService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de negocio para operaciones sobre {@link Provider}.
 *
 * <p>Proporciona métodos para obtener páginas de proveedores ordenadas por fecha de creación,
 * crear nuevos proveedores (evitando duplicados por nombre), eliminar proveedores por id
 * y contar el total de proveedores.</p>
 * @author Ilder Tocto
 * @version 1.0
 * @since 2025-10-18
 */
@Service
public class ProviderServiceImpl implements ProviderService {
    /**
     * Repositorio que maneja la persistencia de proveedores.
     */
    private final ProviderRepository repo;

    /**
     * Constructor con inyección del repositorio.
     *
     * @param repo instancia de {@link ProviderRepository} proporcionada por Spring
     */
    public ProviderServiceImpl(ProviderRepository repo) {
        this.repo = repo;
    }

    /**
     * Devuelve una página de proveedores ordenada por {@code createdAt} descendente.
     *
     * <p>La paginación usa índices 0-based: {@code page} 0 es la primera página. Si el rango
     * solicitado no contiene elementos se devuelve una lista vacía.</p>
     *
     * @param page número de página (0-indexado). Si es negativo se considera 0.
     * @param size cantidad de elementos por página. Si es 0 o negativa puede devolver lista vacía.
     * @return lista de {@link Provider} correspondiente a la página solicitada (nunca {@code null})
     */
    @Override
    public List<Provider> findPage(int page, int size) {
        List<Provider> all = repo.findAll().stream()
                .sorted(Comparator.comparing(Provider::getCreatedAt).reversed())
                .collect(Collectors.toList());
        int from = Math.max(0, page * size);
        int to = Math.min(all.size(), from + size);
        if (from >= to) return List.of();
        return all.subList(from, to);
    }

    /**
     * Crea un nuevo proveedor.
     *
     * <p>Valida que no exista otro proveedor con el mismo nombre (ignorando mayúsculas/minúsculas).
     * Si existe, lanza {@link IllegalArgumentException}.</p>
     *
     * @param p objeto {@link Provider} con los datos a crear (se espera al menos {@code nombre})
     * @return el {@link Provider} creado con {@code id} y {@code createdAt} asignados
     * @throws IllegalArgumentException si ya existe un proveedor con el mismo nombre
     */
    @Override
    public Provider create(Provider p) {
        List<Provider> all = repo.findAll();
        boolean exists = all.stream()
                .anyMatch(x -> x.getNombre() != null &&
                        x.getNombre().toLowerCase(Locale.ROOT).equals(p.getNombre().toLowerCase(Locale.ROOT)));
        if (exists) {
            throw new IllegalArgumentException(Constants.DUPLICATE_PROVIDER_ERROR);
        }
        Provider newP = new Provider().builder()
                .id(java.util.UUID.randomUUID().toString())
                .nombre(p.getNombre())
                .razonSocial(p.getRazonSocial())
                .direccion(p.getDireccion())
                .createdAt(java.time.Instant.now())
                .build();

        all.add(newP);
        repo.saveAll(all);
        return newP;
    }

    /**
     * Elimina un proveedor por su identificador.
     *
     * <p>Si {@code id} es {@code null} o está vacío lanza {@link IllegalArgumentException}.
     * Si no existe un proveedor con ese id lanza {@link IllegalArgumentException} indicando
     * que no existe.</p>
     *
     * @param id identificador del proveedor a eliminar
     * @throws IllegalArgumentException si {@code id} es nulo/vacío o si no existe el proveedor
     */
    @Override
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(Constants.INVALID_ID_ERROR);
        }
        List<Provider> all = repo.findAll();
        boolean exists = all.stream().anyMatch(p -> id.equals(p.getId()));
        if (!exists) {
            throw new IllegalArgumentException(String.format(Constants.PROVIDER_NOT_FOUND_ERROR, id));
        }
        List<Provider> filtered = all.stream()
                .filter(p -> !id.equals(p.getId()))
                .collect(Collectors.toList());
        repo.saveAll(filtered);
    }

    /**
     * Devuelve el número total de proveedores almacenados.
     *
     * @return cantidad total de proveedores
     */
    @Override
    public long count() {
        return repo.findAll().size();
    }
}
