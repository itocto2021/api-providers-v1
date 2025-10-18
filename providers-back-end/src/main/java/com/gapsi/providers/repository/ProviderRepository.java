package com.gapsi.providers.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gapsi.providers.model.Provider;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Repositorio simple que persiste una lista de {@link Provider} en un archivo JSON local.
 *
 * <p>Utiliza Jackson para la (de)serialización y un {@link ReentrantReadWriteLock} para
 * permitir accesos concurrentes seguros de lectura/escritura sobre el fichero.</p>
 * @author Ilder Tocto
 * @version 1.0
 * @since 2025-10-18
 */
@Component
public class ProviderRepository {

    /**
     * Nombre del fichero JSON que actúa como base de datos local.
     */
    private static final String FILENAME = "db.json";

    /**
     * {@link ObjectMapper} configurado para soportar tipos de fecha/hora (java.time).
     */
    private final ObjectMapper mapper;

    /**
     * Fichero físico donde se almacena la colección de proveedores.
     */
    private final File file;

    /**
     * Cerrojo de lectura/escritura para sincronizar operaciones sobre el fichero.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Constructor por defecto.
     *
     * <p>Configura el {@link ObjectMapper}, inicializa la referencia al fichero de datos y
     * crea o copia `db.json` desde recursos si no existe.</p>
     *
     * @throws RuntimeException si no se puede crear o copiar `db.json`
     */
    public ProviderRepository() {
        // configurar ObjectMapper para soportar java.time (Instant)
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.file = Paths.get(System.getProperty("user.dir"), FILENAME).toFile();

        if (!file.exists()) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(FILENAME)) {
                if (is != null) {
                    Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<Provider>());
                }
            } catch (Exception e) {
                throw new RuntimeException("No se pudo crear o copiar " + FILENAME, e);
            }
        }
    }

    /**
     * Lee y devuelve todos los proveedores almacenados.
     *
     * <p>Si el fichero está vacío devuelve una lista vacía.</p>
     *
     * @return lista de {@link Provider} (nunca {@code null})
     * @throws RuntimeException si ocurre un error al leer o parsear `db.json`
     */
    public List<Provider> findAll() {
        lock.readLock().lock();
        try {
            if (file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Provider>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo " + FILENAME, e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Busca un proveedor por su identificador.
     *
     * @param id identificador del proveedor; si es {@code null} devuelve {@link Optional#empty()}
     * @return {@link Optional} con el proveedor si existe, o {@link Optional#empty()} si no
     */
    public Optional<Provider> findById(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst();
    }

    /**
     * Crea o actualiza un proveedor.
     *
     * <p>Si {@code provider.getId()} es {@code null} o está en blanco, se genera un nuevo UUID
     * y se asigna la fecha de creación actual. Si el id existe se actualiza el registro
     * manteniendo la fecha de creación previa cuando esté disponible.</p>
     *
     * @param provider proveedor a guardar (no debe ser {@code null})
     * @return el mismo objeto {@code provider} con el {@code id} y {@code createdAt} asignados
     * @throws RuntimeException si ocurre un error al escribir en `db.json`
     */
    public Provider save(Provider provider) {
        lock.writeLock().lock();
        try {
            List<Provider> all = findAll();
            if (provider.getId() == null || provider.getId().isBlank()) {
                provider.setId(UUID.randomUUID().toString());
                provider.setCreatedAt(Instant.now());
                all.add(provider);
            } else {
                boolean updated = false;
                for (int i = 0; i < all.size(); i++) {
                    Provider p = all.get(i);
                    if (p.getId() != null && p.getId().equals(provider.getId())) {
                        provider.setCreatedAt(p.getCreatedAt() == null ? Instant.now() : p.getCreatedAt());
                        all.set(i, provider);
                        updated = true;
                        break;
                    }
                }
                if (!updated) {
                    if (provider.getCreatedAt() == null) provider.setCreatedAt(Instant.now());
                    all.add(provider);
                }
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, all);
            return provider;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando en " + FILENAME, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Sobrescribe todo el fichero con la lista proporcionada.
     *
     * @param data lista de {@link Provider} que reemplazará al contenido actual
     * @throws RuntimeException si ocurre un error al escribir en `db.json`
     */
    public void saveAll(List<Provider> data) {
        lock.writeLock().lock();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (Exception e) {
            throw new RuntimeException("Error escribiendo " + FILENAME, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Elimina un proveedor por su identificador.
     *
     * @param id identificador del proveedor a eliminar; si es {@code null} no hace nada y devuelve {@code false}
     * @return {@code true} si se eliminó al menos un elemento, {@code false} si no se encontró el id
     * @throws RuntimeException si ocurre un error al escribir en `db.json`
     */
    public boolean deleteById(String id) {
        if (id == null) return false;
        lock.writeLock().lock();
        try {
            List<Provider> all = findAll();
            boolean removed = all.removeIf(p -> p.getId() != null && p.getId().equals(id));
            if (removed) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, all);
            }
            return removed;
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando en " + FILENAME, e);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
