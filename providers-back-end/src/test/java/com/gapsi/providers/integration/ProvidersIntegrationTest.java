package com.gapsi.providers.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gapsi.providers.model.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de pruebas de integración completa para la aplicación de proveedores.
 * 
 * <p>Valida el funcionamiento end-to-end de la aplicación, incluyendo
 * la integración entre todas las capas (Controller, Service, Repository).
 * Utiliza el contexto completo de Spring Boot para pruebas realistas.</p>
 * 
 * @author Ilder Tocto
 * @version 1.0.0
 * @since 2025-10-18
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Providers Application Integration Tests")
class ProvidersIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Configuración inicial para cada test de integración.
     * 
     * <p>Configura MockMvc con el contexto completo de la aplicación
     * y resetea el estado para pruebas independientes.</p>
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }


    /**
     * Prueba el flujo completo de validación de nombres duplicados.
     * 
     * <p>Verifica que la validación de nombres duplicados funcione
     * correctamente en un escenario de integración real.</p>
     */
    @Test
    @DisplayName("Should prevent duplicate provider names in integration flow")
    void shouldPreventDuplicateProviderNamesInIntegrationFlow() throws Exception {
        // Given - Crear primer provider
        String uniqueName = "Unique Integration Provider " + System.currentTimeMillis();
        Provider firstProvider = Provider.builder()
                .nombre(uniqueName)
                .razonSocial("Unique S.A.")
                .build();

        mockMvc.perform(post("/gapsi/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstProvider)))
                .andExpect(status().isCreated());

        // When - Intentar crear provider con mismo nombre
        Provider duplicateProvider = Provider.builder()
                .nombre(uniqueName) // Mismo nombre
                .razonSocial("Different S.A.")
                .build();

        // Then - Debe fallar con error 409 (Conflict)
        mockMvc.perform(post("/gapsi/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateProvider)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("mismo nombre ya existe")));
    }

    /**
     * Prueba el flujo completo de eliminación de proveedores.
     * 
     * <p>Verifica que se puedan crear, encontrar y eliminar providers
     * en un flujo end-to-end completo.</p>
     */
    @Test
    @DisplayName("Should create, find, and delete provider in complete flow")
    void shouldCreateFindAndDeleteProviderInCompleteFlow() throws Exception {
        // Given - Crear provider para eliminar
        String uniqueName = "Provider To Delete " + System.currentTimeMillis();
        Provider providerToDelete = Provider.builder()
                .nombre(uniqueName)
                .razonSocial("Delete Me S.A.")
                .build();

        String createdProviderJson = mockMvc.perform(post("/gapsi/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(providerToDelete)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Provider createdProvider = objectMapper.readValue(createdProviderJson, Provider.class);

        // When - Eliminar el provider
        mockMvc.perform(delete("/gapsi/providers/{id}", createdProvider.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Proveedor eliminado correctamente")));

        // Then - Verificar que ya no aparece en la lista
        mockMvc.perform(get("/gapsi/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.id == '" + createdProvider.getId() + "')]", 
                          hasSize(0)));
    }


    /**
     * Prueba el manejo de errores en escenarios de integración.
     * 
     * <p>Verifica que los errores se manejen correctamente
     * en el contexto completo de la aplicación.</p>
     */
    @Test
    @DisplayName("Should handle errors gracefully in integration context")
    void shouldHandleErrorsGracefullyInIntegrationContext() throws Exception {
        // When & Then - Probar eliminación con ID inexistente
        mockMvc.perform(delete("/gapsi/providers/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", notNullValue()));

        // Probar creación con JSON malformado
        mockMvc.perform(post("/gapsi/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Prueba la persistencia de datos entre requests.
     * 
     * <p>Verifica que los datos se mantengan persistentes
     * entre diferentes peticiones HTTP en la misma sesión de test.</p>
     */
    @Test
    @DisplayName("Should persist data between requests")
    void shouldPersistDataBetweenRequests() throws Exception {
        // Given - Crear provider
        String uniqueName = "Persistent Test Provider " + System.currentTimeMillis();
        Provider persistentProvider = Provider.builder()
                .nombre(uniqueName)
                .razonSocial("Persistent S.A.")
                .build();

        String createdProviderJson = mockMvc.perform(post("/gapsi/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(persistentProvider)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Provider createdProvider = objectMapper.readValue(createdProviderJson, Provider.class);

        // When & Then - Verificar que persiste en múltiples requests
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/gapsi/providers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[?(@.id == '" + createdProvider.getId() + "')].nombre", 
                              contains(uniqueName)));
        }
    }

    /**
     * Prueba la configuración CORS en el contexto de integración.
     * 
     * <p>Verifica que las configuraciones CORS estén aplicadas
     * correctamente en el contexto completo de la aplicación.</p>
     */
    @Test
    @DisplayName("Should apply CORS configuration correctly")
    void shouldApplyCorsConfigurationCorrectly() throws Exception {
        // When & Then
        mockMvc.perform(options("/gapsi/providers")
                        .header("Origin", "http://localhost:3001")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3001"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")));
    }
}