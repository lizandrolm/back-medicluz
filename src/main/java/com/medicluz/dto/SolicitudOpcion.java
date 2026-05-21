package com.medicluz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudOpcion(
    @NotBlank @Size(max = 60) String codigo,
    @NotBlank @Size(max = 100) String nombre,
    @Size(max = 60) String icono,
    @Size(max = 150) String ruta,
    Long idPadre,
    int ordenVisualizacion
) {}
