package com.gapsi.providers.service.impl;

import com.gapsi.providers.model.Provider;
import com.gapsi.providers.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link ProviderServiceImpl}.
 * 
 * <p>Valida la lógica de negocio del servicio de proveedores,
 * incluyendo paginación, validaciones de duplicados, creación y eliminación.
 * Utiliza Mockito para aislar las pruebas del repositorio.</p>
 * 
 * @author Ilder Tocto
 * @version 1.0.0
 * @since 2025-10-18
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProviderService Implementation Tests")
class ProviderServiceImplTest {

    /**
     * Mock del repositorio de proveedores.
     * Se utiliza para aislar las pruebas de la capa de datos.
     */
    @Mock
    private ProviderRepository repository;

    /**
     * Instancia del servicio bajo prueba con dependencias inyectadas.
     */
    @InjectMocks
    private ProviderServiceImpl service;

    /**
     * Lista de providers de prueba para usar en los tests.
     */
    private List<Provider> testProviders;

    /**
     * Configuración inicial para cada test.
     * 
     * <p>Prepara datos de prueba con providers ordenados por fecha
     * de creación para validar la funcionalidad de paginación.</p>
     */
    @BeforeEach
    void setUp() {
        // Crear providers con fechas diferentes para probar ordenamiento
        Provider provider1 = Provider.builder()
                .id("1")
                .nombre("Provider Oldest")
                .razonSocial("Oldest S.A.")
                .createdAt(Instant.parse("2025-10-16T10:00:00Z"))
                .build();

        Provider provider2 = Provider.builder()
                .id("2")
                .nombre("Provider Middle")
                .razonSocial("Middle S.A.")
                .createdAt(Instant.parse("2025-10-17T10:00:00Z"))
                .build();

        Provider provider3 = Provider.builder()
                .id("3")
                .nombre("Provider Newest")
                .razonSocial("Newest S.A.")
                .createdAt(Instant.parse("2025-10-18T10:00:00Z"))
                .build();

        testProviders = Arrays.asList(provider1, provider2, provider3);
    }

    /**
     * Prueba la paginación con la primera página.
     * 
     * <p>Verifica que findPage() retorne los providers más recientes
     * primero y respete el tamaño de página solicitado.</p>
     */
    @Test
    @DisplayName("Should return first page of providers ordered by creation date desc")
    void shouldReturnFirstPageOfProvidersOrderedByCreationDateDesc() {
        // Given
        when(repository.findAll()).thenReturn(testProviders);
        int page = 0;
        int size = 2;

        // When
        List<Provider> result = service.findPage(page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verificar orden descendente por fecha de creación
        assertEquals("Provider Newest", result.get(0).getNombre());
        assertEquals("Provider Middle", result.get(1).getNombre());
        
        verify(repository, times(1)).findAll();
    }

    /**
     * Prueba la paginación con la segunda página.
     * 
     * <p>Verifica que findPage() retorne los elementos correctos
     * para páginas subsecuentes.</p>
     */
    @Test
    @DisplayName("Should return second page of providers")
    void shouldReturnSecondPageOfProviders() {
        // Given
        when(repository.findAll()).thenReturn(testProviders);
        int page = 1;
        int size = 2;

        // When
        List<Provider> result = service.findPage(page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Provider Oldest", result.get(0).getNombre());
        
        verify(repository, times(1)).findAll();
    }

    /**
     * Prueba la paginación con página fuera de rango.
     * 
     * <p>Verifica que findPage() retorne una lista vacía
     * cuando se solicita una página que no contiene elementos.</p>
     */
    @Test
    @DisplayName("Should return empty list when page is out of range")
    void shouldReturnEmptyListWhenPageIsOutOfRange() {
        // Given
        when(repository.findAll()).thenReturn(testProviders);
        int page = 5;
        int size = 2;

        // When
        List<Provider> result = service.findPage(page, size);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(repository, times(1)).findAll();
    }

    /**
     * Prueba la paginación con página negativa.
     * 
     * <p>Verifica que findPage() maneje correctamente
     * valores negativos de página tratándolos como página 0.</p>
     */
    @Test
    @DisplayName("Should handle negative page number as page 0")
    void shouldHandleNegativePageNumberAsPage0() {
        // Given
        when(repository.findAll()).thenReturn(testProviders);
        int page = -1;
        int size = 2;

        // When
        List<Provider> result = service.findPage(page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Provider Newest", result.get(0).getNombre());
        
        verify(repository, times(1)).findAll();
    }

    /**
     * Prueba la paginación con tamaño de página cero.
     * 
     * <p>Verifica que findPage() retorne una lista vacía
     * cuando el tamaño de página es cero.</p>
     */
    @Test
    @DisplayName("Should return empty list when page size is zero")
    void shouldReturnEmptyListWhenPageSizeIsZero() {
        // Given
        when(repository.findAll()).thenReturn(testProviders);
        int page = 0;
        int size = 0;

        // When
        List<Provider> result = service.findPage(page, size);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(repository, times(1)).findAll();
    }


    /**
     * Prueba la validación de nombres duplicados en creación.
     * 
     * <p>Verifica que create() lance IllegalArgumentException
     * cuando se intenta crear un provider con un nombre que ya existe,
     * ignorando diferencias de mayúsculas/minúsculas.</p>
     */
    @Test
    @DisplayName("Should throw exception when creating provider with duplicate name")
    void shouldThrowExceptionWhenCreatingProviderWithDuplicateName() {
        // Given
        Provider duplicateProvider = Provider.builder()
                .nombre("PROVIDER NEWEST") // Mismo nombre pero en mayúsculas
                .razonSocial("Duplicate S.A.")
                .build();

        when(repository.findAll()).thenReturn(testProviders);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(duplicateProvider)
        );

        assertEquals("Proveedor con el mismo nombre ya existe", exception.getMessage());
        
        verify(repository, times(1)).findAll();
        verify(repository, never()).saveAll(anyList());
    }

    /**
     * Prueba la validación de nombres duplicados con diferentes casos.
     * 
     * <p>Verifica que la validación de duplicados sea insensible
     * a mayúsculas y minúsculas.</p>
     */
    @Test
    @DisplayName("Should detect duplicate names case insensitively")
    void shouldDetectDuplicateNamesCaseInsensitively() {
        // Given
        Provider duplicateProvider = Provider.builder()
                .nombre("provider newest") // Mismo nombre en minúsculas
                .build();

        when(repository.findAll()).thenReturn(testProviders);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicateProvider));
        
        verify(repository, times(1)).findAll();
        verify(repository, never()).saveAll(anyList());
    }


    /**
     * Prueba el conteo total de proveedores.
     * 
     * <p>Verifica que count() retorne el número correcto
     * de providers en el repositorio.</p>
     */
    @Test
    @DisplayName("Should count total providers correctly")
    void shouldCountTotalProvidersCorrectly() {
        // Given
        when(repository.findAll()).thenReturn(testProviders);

        // When
        long result = service.count();

        // Then
        assertEquals(3, result);
        verify(repository, times(1)).findAll();
    }

    /**
     * Prueba el conteo con repositorio vacío.
     * 
     * <p>Verifica que count() retorne cero
     * cuando no hay providers en el repositorio.</p>
     */
    @Test
    @DisplayName("Should return zero count when repository is empty")
    void shouldReturnZeroCountWhenRepositoryIsEmpty() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        long result = service.count();

        // Then
        assertEquals(0, result);
        verify(repository, times(1)).findAll();
    }


    /**
     * Prueba la creación con provider que tiene nombre nulo.
     * 
     * <p>Verifica que create() maneje correctamente casos
     * donde el provider tiene nombre nulo.</p>
     */
    @Test
    @DisplayName("Should handle provider with null name in creation")
    void shouldHandleProviderWithNullNameInCreation() {
        // Given
        Provider providerWithNullName = Provider.builder()
                .nombre(null)
                .razonSocial("Null Name S.A.")
                .build();

        when(repository.findAll()).thenReturn(testProviders);

        // When & Then
        assertThrows(Exception.class, () -> service.create(providerWithNullName));

        verify(repository, times(1)).findAll();
    }

}