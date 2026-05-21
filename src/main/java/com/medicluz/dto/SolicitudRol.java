package com.medicluz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudRol(
    @NotBlank @Size(max = 60) String nombre,
    @NotBlank @Size(max = 100) String nombreVisualizacion,
    @Size(max = 250) String descripcion
) {}
