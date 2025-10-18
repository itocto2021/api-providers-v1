package com.gapsi.providers.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para {@link Provider}.
 * 
 * <p>Valida el correcto funcionamiento del modelo de datos Provider,
 * incluyendo la construcción con Builder pattern, métodos equals/hashCode,
 * y la correcta asignación de propiedades.</p>
 * 
 * @author Ilder Tocto
 * @version 1.0.0
 * @since 2025-10-18
 */
@DisplayName("Provider Model Tests")
class ProviderTest {

    /**
     * Prueba la construcción de un Provider usando el patrón Builder.
     * 
     * <p>Verifica que todas las propiedades se asignen correctamente
     * cuando se utiliza el builder de Lombok.</p>
     */
    @Test
    @DisplayName("Should create Provider using Builder pattern")
    void shouldCreateProviderUsingBuilder() {
        // Given
        String expectedId = "123e4567-e89b-12d3-a456-426614174000";
        String expectedNombre = "Proveedor Test";
        String expectedRazonSocial = "Proveedor Test S.A.";
        String expectedDireccion = "Calle Ficticia 123";
        Instant expectedCreatedAt = Instant.now();

        // When
        Provider provider = Provider.builder()
                .id(expectedId)
                .nombre(expectedNombre)
                .razonSocial(expectedRazonSocial)
                .direccion(expectedDireccion)
                .createdAt(expectedCreatedAt)
                .build();

        // Then
        assertNotNull(provider);
        assertEquals(expectedId, provider.getId());
        assertEquals(expectedNombre, provider.getNombre());
        assertEquals(expectedRazonSocial, provider.getRazonSocial());
        assertEquals(expectedDireccion, provider.getDireccion());
        assertEquals(expectedCreatedAt, provider.getCreatedAt());
    }

    /**
     * Prueba la construcción de un Provider usando el constructor por defecto.
     * 
     * <p>Verifica que se pueda crear una instancia vacía y luego
     * asignar propiedades usando los setters generados por Lombok.</p>
     */
    @Test
    @DisplayName("Should create Provider using default constructor and setters")
    void shouldCreateProviderUsingDefaultConstructor() {
        // Given
        String expectedId = "test-id";
        String expectedNombre = "Test Provider";
        Instant expectedCreatedAt = Instant.parse("2025-10-18T10:30:00Z");

        // When
        Provider provider = new Provider();
        provider.setId(expectedId);
        provider.setNombre(expectedNombre);
        provider.setCreatedAt(expectedCreatedAt);

        // Then
        assertNotNull(provider);
        assertEquals(expectedId, provider.getId());
        assertEquals(expectedNombre, provider.getNombre());
        assertNull(provider.getRazonSocial());
        assertNull(provider.getDireccion());
        assertEquals(expectedCreatedAt, provider.getCreatedAt());
    }

    /**
     * Prueba la construcción de un Provider usando el constructor con todos los argumentos.
     * 
     * <p>Verifica que el constructor generado por @AllArgsConstructor
     * asigne correctamente todas las propiedades.</p>
     */
    @Test
    @DisplayName("Should create Provider using all args constructor")
    void shouldCreateProviderUsingAllArgsConstructor() {
        // Given
        String id = "all-args-id";
        String nombre = "All Args Provider";
        String razonSocial = "All Args Provider S.A.";
        String direccion = "All Args Street 456";
        Instant createdAt = Instant.parse("2025-10-18T15:45:30Z");

        // When
        Provider provider = new Provider(id, nombre, razonSocial, direccion, createdAt);

        // Then
        assertNotNull(provider);
        assertEquals(id, provider.getId());
        assertEquals(nombre, provider.getNombre());
        assertEquals(razonSocial, provider.getRazonSocial());
        assertEquals(direccion, provider.getDireccion());
        assertEquals(createdAt, provider.getCreatedAt());
    }

    /**
     * Prueba el método equals generado por Lombok.
     * 
     * <p>Verifica que dos providers con las mismas propiedades
     * sean considerados iguales.</p>
     */
    @Test
    @DisplayName("Should test equals method correctly")
    void shouldTestEqualsMethodCorrectly() {
        // Given
        Instant now = Instant.now();
        Provider provider1 = Provider.builder()
                .id("same-id")
                .nombre("Same Name")
                .razonSocial("Same Razon Social")
                .direccion("Same Address")
                .createdAt(now)
                .build();

        Provider provider2 = Provider.builder()
                .id("same-id")
                .nombre("Same Name")
                .razonSocial("Same Razon Social")
                .direccion("Same Address")
                .createdAt(now)
                .build();

        Provider provider3 = Provider.builder()
                .id("different-id")
                .nombre("Different Name")
                .build();

        // When & Then
        assertEquals(provider1, provider2);
        assertNotEquals(provider1, provider3);
        assertNotEquals(provider1, null);
        assertNotEquals(provider1, "not a provider");
    }

    /**
     * Prueba el método hashCode generado por Lombok.
     * 
     * <p>Verifica que providers iguales tengan el mismo hashCode
     * y que providers diferentes tengan hashCodes diferentes.</p>
     */
    @Test
    @DisplayName("Should test hashCode method correctly")
    void shouldTestHashCodeMethodCorrectly() {
        // Given
        Instant now = Instant.now();
        Provider provider1 = Provider.builder()
                .id("hash-test")
                .nombre("Hash Test")
                .createdAt(now)
                .build();

        Provider provider2 = Provider.builder()
                .id("hash-test")
                .nombre("Hash Test")
                .createdAt(now)
                .build();

        Provider provider3 = Provider.builder()
                .id("different-hash")
                .nombre("Different Hash")
                .build();

        // When & Then
        assertEquals(provider1.hashCode(), provider2.hashCode());
        assertNotEquals(provider1.hashCode(), provider3.hashCode());
    }

    /**
     * Prueba el método toString generado por Lombok.
     * 
     * <p>Verifica que el método toString contenga información
     * relevante del objeto Provider.</p>
     */
    @Test
    @DisplayName("Should test toString method contains relevant information")
    void shouldTestToStringMethodContainsRelevantInformation() {
        // Given
        Provider provider = Provider.builder()
                .id("toString-test")
                .nombre("ToString Provider")
                .razonSocial("ToString S.A.")
                .build();

        // When
        String result = provider.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Provider"));
        assertTrue(result.contains("toString-test"));
        assertTrue(result.contains("ToString Provider"));
        assertTrue(result.contains("ToString S.A."));
    }

    /**
     * Prueba el comportamiento con valores nulos.
     * 
     * <p>Verifica que el Provider pueda manejar correctamente
     * valores nulos en sus propiedades.</p>
     */
    @Test
    @DisplayName("Should handle null values correctly")
    void shouldHandleNullValuesCorrectly() {
        // Given & When
        Provider provider = Provider.builder()
                .id(null)
                .nombre(null)
                .razonSocial(null)
                .direccion(null)
                .createdAt(null)
                .build();

        // Then
        assertNotNull(provider);
        assertNull(provider.getId());
        assertNull(provider.getNombre());
        assertNull(provider.getRazonSocial());
        assertNull(provider.getDireccion());
        assertNull(provider.getCreatedAt());
    }

    /**
     * Prueba la inmutabilidad después de la construcción con Builder.
     * 
     * <p>Verifica que después de crear un Provider con Builder,
     * se puedan modificar las propiedades usando setters (ya que no es inmutable).</p>
     */
    @Test
    @DisplayName("Should allow modification after builder construction")
    void shouldAllowModificationAfterBuilderConstruction() {
        // Given
        Provider provider = Provider.builder()
                .id("original-id")
                .nombre("Original Name")
                .build();

        // When
        provider.setId("modified-id");
        provider.setNombre("Modified Name");

        // Then
        assertEquals("modified-id", provider.getId());
        assertEquals("Modified Name", provider.getNombre());
    }
}