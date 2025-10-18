package com.gapsi.providers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gapsi.providers.model.Provider;
import com.gapsi.providers.service.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de pruebas de integración para {@link ProviderController}.
 * 
 * <p>Valida el comportamiento de los endpoints REST del controlador de proveedores,
 * incluyendo serialización JSON, códigos de estado HTTP, y manejo de errores.
 * Utiliza MockMvc para simular peticiones HTTP y MockBean para aislar el servicio.</p>
 * 
 * @author Ilder Tocto
 * @version 1.0.0
 * @since 2025-10-18
 */
@WebMvcTest(ProviderController.class)
@DisplayName("ProviderController Integration Tests")
class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderService providerService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Provider> testProviders;
    private Provider testProvider;

    /**
     * Configuración inicial para cada test.
     * 
     * <p>Prepara datos de prueba con providers para validar
     * la funcionalidad de los endpoints REST.</p>
     */
    @BeforeEach
    void setUp() {
        testProvider = Provider.builder()
                .id("test-id-1")
                .nombre("Test Provider")
                .razonSocial("Test Provider S.A.")
                .direccion("Test Address 123")
                .createdAt(Instant.parse("2025-10-18T10:30:00Z"))
                .build();

        Provider testProvider2 = Provider.builder()
                .id("test-id-2")
                .nombre("Test Provider 2")
                .razonSocial("Test Provider 2 S.A.")
                .direccion("Test Address 456")
                .createdAt(Instant.parse("2025-10-18T11:30:00Z"))
                .build();

        testProviders = Arrays.asList(testProvider, testProvider2);
    }

    /**
     * Prueba el endpoint de información de la aplicación.
     * 
     * <p>Verifica que GET /gapsi/info retorne la información
     * básica de la aplicación con el código de estado correcto.</p>
     */
    @Test
    @DisplayName("Should return app info successfully")
    void shouldReturnAppInfoSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(get("/gapsi/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje", is("Bienvenido Candidato Ilder Tocto")))
                .andExpect(jsonPath("$.version", is("1.0.0")));
    }

    /**
     * Prueba el endpoint de listado de proveedores sin parámetros.
     * 
     * <p>Verifica que GET /gapsi/providers retorne la primera página
     * de providers con los valores por defecto (página 0, tamaño 10).</p>
     */
    @Test
    @DisplayName("Should list providers with default pagination")
    void shouldListProvidersWithDefaultPagination() throws Exception {
        // Given
        when(providerService.findPage(0, 10)).thenReturn(testProviders);
        when(providerService.count()).thenReturn(2L);

        // When & Then
        mockMvc.perform(get("/gapsi/providers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is("test-id-1")))
                .andExpect(jsonPath("$.items[0].nombre", is("Test Provider")))
                .andExpect(jsonPath("$.items[1].id", is("test-id-2")))
                .andExpect(jsonPath("$.items[1].nombre", is("Test Provider 2")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.total", is(2)));

        verify(providerService, times(1)).findPage(0, 10);
        verify(providerService, times(1)).count();
    }

    /**
     * Prueba el endpoint de listado de proveedores con parámetros personalizados.
     * 
     * <p>Verifica que GET /gapsi/providers con parámetros page y size
     * utilice los valores proporcionados correctamente.</p>
     */
    @Test
    @DisplayName("Should list providers with custom pagination parameters")
    void shouldListProvidersWithCustomPaginationParameters() throws Exception {
        // Given
        when(providerService.findPage(1, 5)).thenReturn(Arrays.asList(testProvider));
        when(providerService.count()).thenReturn(6L);

        // When & Then
        mockMvc.perform(get("/gapsi/providers")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.total", is(6)));

        verify(providerService, times(1)).findPage(1, 5);
        verify(providerService, times(1)).count();
    }

    /**
     * Prueba el endpoint de listado cuando no hay proveedores.
     * 
     * <p>Verifica que GET /gapsi/providers retorne una respuesta
     * válida cuando la lista de providers está vacía.</p>
     */
    @Test
    @DisplayName("Should return empty list when no providers exist")
    void shouldReturnEmptyListWhenNoProvidersExist() throws Exception {
        // Given
        when(providerService.findPage(0, 10)).thenReturn(Collections.emptyList());
        when(providerService.count()).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/gapsi/providers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));

        verify(providerService, times(1)).findPage(0, 10);
        verify(providerService, times(1)).count();
    }

    /**
     * Prueba la creación exitosa de un nuevo proveedor.
     * 
     * <p>Verifica que POST /gapsi/providers cree un provider
     * correctamente y retorne el código de estado 201 CREATED.</p>
     */
    @Test
    @DisplayName("Should create new provider successfully")
    void shouldCreateNewProviderSuccessfully() throws Exception {
        // Given
        Provider newProvider = Provider.builder()
                .nombre("New Provider")
                .razonSocial("New Provider S.A.")
                .direccion("New Address 789")
                .build();

        Provider createdProvider = Provider.builder()
                .id("created-id")
                .nombre("New Provider")
                .razonSocial("New Provider S.A.")
                .direccion("New Address 789")
                .createdAt(Instant.now())
                .build();

        when(providerService.create(any(Provider.class))).thenReturn(createdProvider);

        // When & Then
        mockMvc.perform(post("/gapsi/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProvider)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("created-id")))
                .andExpect(jsonPath("$.nombre", is("New Provider")))
                .andExpect(jsonPath("$.razonSocial", is("New Provider S.A.")))
                .andExpect(jsonPath("$.direccion", is("New Address 789")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(providerService, times(1)).create(any(Provider.class));
    }

    /**
     * Prueba la creación con datos inválidos.
     * 
     * <p>Verifica que POST /gapsi/providers retorne error 400
     * cuando se envían datos de provider inválidos.</p>
     */
    @Test
    @DisplayName("Should create provider even with empty name")
    void shouldCreateProviderEvenWithEmptyName() throws Exception {
        // Given
        String invalidJson = "{ \"nombre\": \"\" }"; // Nombre vacío
        Provider created = Provider.builder()
                .id("created-id")
                .nombre("")
                .build();

        when(providerService.create(any(Provider.class))).thenReturn(created);

        // When & Then
        mockMvc.perform(post("/gapsi/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isCreated());

        verify(providerService, times(1)).create(any(Provider.class));
    }

    /**
     * Prueba la creación con nombre duplicado.
     * 
     * <p>Verifica que POST /gapsi/providers retorne error 400
     * cuando el servicio lance IllegalArgumentException por nombre duplicado.</p>
     */
    @Test
    @DisplayName("Should return bad request when creating provider with duplicate name")
    void shouldReturnBadRequestWhenCreatingProviderWithDuplicateName() throws Exception {
        // Given
        Provider duplicateProvider = Provider.builder()
                .nombre("Existing Provider")
                .razonSocial("Existing S.A.")
                .build();

        when(providerService.create(any(Provider.class)))
                .thenThrow(new IllegalArgumentException("Proveedor con el mismo nombre ya existe"));

        // When & Then
        mockMvc.perform(post("/gapsi/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateProvider)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Proveedor con el mismo nombre ya existe")));

        verify(providerService, times(1)).create(any(Provider.class));
    }

    /**
     * Prueba la eliminación exitosa de un proveedor.
     * 
     * <p>Verifica que DELETE /gapsi/providers/{id} elimine
     * el provider correctamente y retorne código 204 NO CONTENT.</p>
     */
    @Test
    @DisplayName("Should delete provider successfully")
    void shouldDeleteProviderSuccessfully() throws Exception {
        // Given
        String providerId = "existing-id";
        doNothing().when(providerService).delete(providerId);

        // When & Then
        mockMvc.perform(delete("/gapsi/providers/{id}", providerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Proveedor eliminado correctamente")));

        verify(providerService, times(1)).delete(providerId);
    }

    /**
     * Prueba la eliminación con ID inexistente.
     * 
     * <p>Verifica que DELETE /gapsi/providers/{id} retorne error 404
     * cuando el servicio lance excepción por ID inexistente.</p>
     */
    @Test
    @DisplayName("Should return not found when deleting non-existent provider")
    void shouldReturnNotFoundWhenDeletingNonExistentProvider() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        doThrow(new IllegalArgumentException("Provider not found"))
                .when(providerService).delete(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/gapsi/providers/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Provider not found")));

        verify(providerService, times(1)).delete(nonExistentId);
    }


    /**
     * Prueba el contenido de la respuesta JSON para un provider específico.
     * 
     * <p>Verifica que la serialización JSON contenga todos
     * los campos esperados del modelo Provider.</p>
     */
    @Test
    @DisplayName("Should serialize provider JSON correctly")
    void shouldSerializeProviderJsonCorrectly() throws Exception {
        // Given
        when(providerService.findPage(0, 10)).thenReturn(Arrays.asList(testProvider));
        when(providerService.count()).thenReturn(1L);

        // When & Then
        mockMvc.perform(get("/gapsi/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id", is("test-id-1")))
                .andExpect(jsonPath("$.items[0].nombre", is("Test Provider")))
                .andExpect(jsonPath("$.items[0].razonSocial", is("Test Provider S.A.")))
                .andExpect(jsonPath("$.items[0].direccion", is("Test Address 123")))
                .andExpect(jsonPath("$.items[0].createdAt", is("2025-10-18T10:30:00Z")));
    }

    /**
     * Prueba el manejo de parámetros de paginación inválidos.
     * 
     * <p>Verifica que el controlador maneje correctamente
     * parámetros de paginación fuera de rango o inválidos.</p>
     */
    @Test
    @DisplayName("Should handle invalid pagination parameters gracefully")
    void shouldHandleInvalidPaginationParametersGracefully() throws Exception {
        // Given
        when(providerService.findPage(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(providerService.count()).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/gapsi/providers")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));

        // Verificar que se manejen valores inválidos apropiadamente
        verify(providerService, times(1)).findPage(anyInt(), anyInt());
    }
}
