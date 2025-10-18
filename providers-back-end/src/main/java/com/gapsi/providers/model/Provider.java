package com.gapsi.providers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Modelo de datos que representa un proveedor.
 *
 * @author Ilder Tocto
 * @version 1.0.0
 * @since 2025-10-18
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provider {
    private String id;
    private String nombre;
    private String razonSocial;
    private String direccion;
    private Instant createdAt;
}
