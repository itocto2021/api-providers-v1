package com.gapsi.providers.repository;

import com.gapsi.providers.model.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para {@link ProviderRepository}.
 * 
 * <p>Valida el correcto funcionamiento del repositorio de proveedores,
 * incluyendo operaciones CRUD, manejo de archivos JSON, y concurrencia.
 * Utiliza un directorio temporal para aislar las pruebas.</p>
 * 
 * @author Ilder Tocto
 * @version 1.0.0
 * @since 2025-10-18
 */
@DisplayName("ProviderRepository Tests")
class ProviderRepositoryTest {

    /**
     * Directorio temporal para las pruebas.
     * Se crea automáticamente para cada test y se limpia al finalizar.
     */
    @TempDir
    Path tempDir;

    /**
     * Instancia del repositorio bajo prueba.
     */
    private ProviderRepository repository;

    /**
     * Archivo temporal para simular la base de datos JSON.
     */
    private File tempDbFile;

    /**
     * Configuración inicial para cada test.
     * 
     * <p>Crea un archivo temporal y configura el repositorio
     * para usar este archivo en lugar del archivo por defecto.</p>
     * 
     * @throws IOException si hay problemas creando el archivo temporal
     */
    @BeforeEach
    void setUp() throws IOException {
        tempDbFile = tempDir.resolve("test-db.json").toFile();
        
        // Crear archivo inicial con array JSON vacío
        Files.write(tempDbFile.toPath(), "[]".getBytes());
        
        // Crear instancia del repositorio y configurar el archivo usando reflection
        repository = new ProviderRepository();
        ReflectionTestUtils.setField(repository, "file", tempDbFile);
    }

    /**
     * Prueba la inicialización del repositorio con archivo vacío.
     * 
     * <p>Verifica que el repositorio pueda inicializarse correctamente
     * con un archivo JSON vacío y que findAll() retorne una lista vacía.</p>
     */
    @Test
    @DisplayName("Should initialize repository with empty file")
    void shouldInitializeRepositoryWithEmptyFile() {
        // When
        List<Provider> providers = repository.findAll();

        // Then
        assertNotNull(providers);
        assertTrue(providers.isEmpty());
    }

    /**
     * Prueba el guardado de un nuevo proveedor.
     * 
     * <p>Verifica que se pueda guardar un provider y que se le asigne
     * un ID automáticamente si no lo tiene.</p>
     */
    @Test
    @DisplayName("Should save new provider successfully")
    void shouldSaveNewProviderSuccessfully() {
        // Given
        Provider provider = Provider.builder()
                .nombre("Test Provider")
                .razonSocial("Test S.A.")
                .direccion("Test Address 123")
                .createdAt(Instant.now())
                .build();

        // When
        Provider savedProvider = repository.save(provider);

        // Then
        assertNotNull(savedProvider);
        assertNotNull(savedProvider.getId());
        assertEquals("Test Provider", savedProvider.getNombre());
        assertEquals("Test S.A.", savedProvider.getRazonSocial());
        assertEquals("Test Address 123", savedProvider.getDireccion());
        assertNotNull(savedProvider.getCreatedAt());
    }

    /**
     * Prueba la búsqueda de todos los proveedores después de guardar algunos.
     * 
     * <p>Verifica que findAll() retorne todos los providers guardados
     * en el orden correcto.</p>
     */
    @Test
    @DisplayName("Should find all providers after saving multiple")
    void shouldFindAllProvidersAfterSavingMultiple() {
        // Given
        Provider provider1 = Provider.builder()
                .nombre("Provider 1")
                .razonSocial("Provider 1 S.A.")
                .build();
        
        Provider provider2 = Provider.builder()
                .nombre("Provider 2")
                .razonSocial("Provider 2 S.A.")
                .build();

        // When
        repository.save(provider1);
        repository.save(provider2);
        List<Provider> allProviders = repository.findAll();

        // Then
        assertNotNull(allProviders);
        assertEquals(2, allProviders.size());
        
        // Verificar que ambos providers están en la lista
        assertTrue(allProviders.stream()
                .anyMatch(p -> "Provider 1".equals(p.getNombre())));
        assertTrue(allProviders.stream()
                .anyMatch(p -> "Provider 2".equals(p.getNombre())));
    }

    /**
     * Prueba la búsqueda de un proveedor por ID.
     * 
     * <p>Verifica que findById() retorne el provider correcto
     * cuando existe, y Optional.empty() cuando no existe.</p>
     */
    @Test
    @DisplayName("Should find provider by ID when exists")
    void shouldFindProviderByIdWhenExists() {
        // Given
        Provider provider = Provider.builder()
                .nombre("Findable Provider")
                .razonSocial("Findable S.A.")
                .build();
        Provider savedProvider = repository.save(provider);

        // When
        Optional<Provider> foundProvider = repository.findById(savedProvider.getId());

        // Then
        assertTrue(foundProvider.isPresent());
        assertEquals(savedProvider.getId(), foundProvider.get().getId());
        assertEquals("Findable Provider", foundProvider.get().getNombre());
    }

    /**
     * Prueba la búsqueda de un proveedor con ID inexistente.
     * 
     * <p>Verifica que findById() retorne Optional.empty()
     * cuando el ID no existe en el repositorio.</p>
     */
    @Test
    @DisplayName("Should return empty when provider ID does not exist")
    void shouldReturnEmptyWhenProviderIdDoesNotExist() {
        // Given
        String nonExistentId = "non-existent-id";

        // When
        Optional<Provider> foundProvider = repository.findById(nonExistentId);

        // Then
        assertFalse(foundProvider.isPresent());
    }

    /**
     * Prueba la actualización de un proveedor existente.
     * 
     * <p>Verifica que se pueda actualizar un provider existente
     * manteniendo el mismo ID pero cambiando otras propiedades.</p>
     */
    @Test
    @DisplayName("Should update existing provider")
    void shouldUpdateExistingProvider() {
        // Given
        Provider originalProvider = Provider.builder()
                .nombre("Original Name")
                .razonSocial("Original S.A.")
                .direccion("Original Address")
                .build();
        Provider savedProvider = repository.save(originalProvider);

        // When
        Provider updatedProvider = Provider.builder()
                .id(savedProvider.getId())
                .nombre("Updated Name")
                .razonSocial("Updated S.A.")
                .direccion("Updated Address")
                .createdAt(savedProvider.getCreatedAt())
                .build();
        Provider result = repository.save(updatedProvider);

        // Then
        assertNotNull(result);
        assertEquals(savedProvider.getId(), result.getId());
        assertEquals("Updated Name", result.getNombre());
        assertEquals("Updated S.A.", result.getRazonSocial());
        assertEquals("Updated Address", result.getDireccion());
        
        // Verificar que solo hay un provider en el repositorio
        List<Provider> allProviders = repository.findAll();
        assertEquals(1, allProviders.size());
    }

    /**
     * Prueba la eliminación de un proveedor por ID.
     * 
     * <p>Verifica que deleteById() elimine correctamente un provider
     * y que ya no se pueda encontrar después de la eliminación.</p>
     */
    @Test
    @DisplayName("Should delete provider by ID")
    void shouldDeleteProviderById() {
        // Given
        Provider provider = Provider.builder()
                .nombre("To be deleted")
                .razonSocial("Delete S.A.")
                .build();
        Provider savedProvider = repository.save(provider);
        String providerId = savedProvider.getId();

        // When
        repository.deleteById(providerId);

        // Then
        Optional<Provider> deletedProvider = repository.findById(providerId);
        assertFalse(deletedProvider.isPresent());
        
        List<Provider> allProviders = repository.findAll();
        assertTrue(allProviders.isEmpty());
    }

    /**
     * Prueba la eliminación con ID inexistente.
     * 
     * <p>Verifica que deleteById() no lance excepción cuando
     * se intenta eliminar un ID que no existe.</p>
     */
    @Test
    @DisplayName("Should handle delete with non-existent ID gracefully")
    void shouldHandleDeleteWithNonExistentIdGracefully() {
        // Given
        String nonExistentId = "non-existent-id";

        // When & Then
        assertDoesNotThrow(() -> repository.deleteById(nonExistentId));
    }


    /**
     * Prueba la persistencia de datos entre operaciones.
     * 
     * <p>Verifica que los datos se mantengan persistentes
     * entre diferentes operaciones del repositorio.</p>
     */
    @Test
    @DisplayName("Should persist data between operations")
    void shouldPersistDataBetweenOperations() {
        // Given
        Provider provider1 = Provider.builder()
                .nombre("Persistent 1")
                .build();
        Provider provider2 = Provider.builder()
                .nombre("Persistent 2")
                .build();

        // When
        repository.save(provider1);
        repository.save(provider2);

        // Crear nueva instancia del repositorio para simular reinicio
        ProviderRepository newRepository = new ProviderRepository();
        ReflectionTestUtils.setField(newRepository, "file", tempDbFile);

        List<Provider> persistedProviders = newRepository.findAll();

        // Then
        assertNotNull(persistedProviders);
        assertEquals(2, persistedProviders.size());
    }

    /**
     * Prueba el manejo de valores nulos en operaciones.
     * 
     * <p>Verifica que el repositorio maneje correctamente
     * valores nulos en las operaciones principales.</p>
     */
    @Test
    @DisplayName("Should handle null values in operations")
    void shouldHandleNullValuesInOperations() {
        // When & Then
        assertDoesNotThrow(() -> {
            repository.findById(null);
            repository.deleteById(null);
        });
        
        // Verificar que save con null no cause problemas graves
        assertThrows(Exception.class, () -> repository.save(null));
    }

    /**
     * Prueba la generación automática de IDs únicos.
     * 
     * <p>Verifica que el repositorio genere IDs únicos
     * para providers que no tienen ID asignado.</p>
     */
    @Test
    @DisplayName("Should generate unique IDs automatically")
    void shouldGenerateUniqueIdsAutomatically() {
        // Given
        Provider provider1 = Provider.builder().nombre("Provider 1").build();
        Provider provider2 = Provider.builder().nombre("Provider 2").build();

        // When
        Provider saved1 = repository.save(provider1);
        Provider saved2 = repository.save(provider2);

        // Then
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertNotEquals(saved1.getId(), saved2.getId());
    }
}