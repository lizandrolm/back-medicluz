package com.medicluz.rol.dto;

import jakarta.validation.constraints.*;

public record SolicitudOpcion(
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 60)
    @Pattern(regexp = "^[A-Z_]+$", message = "Solo letras mayúsculas y guión bajo")
    String codigo,

    @NotBlank(message = "El nombre es obligatorio") @Size(max = 100)
    String nombre,

    @Size(max = 60) String icono,
    @Size(max = 150) String ruta,

    /** null = opción raíz (sección) */
    Long idPadre,

    int ordenVisualizacion
) {}
