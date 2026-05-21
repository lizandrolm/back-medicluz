package com.medicluz.rol.dto;

import jakarta.validation.constraints.*;

public record SolicitudRol(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60)
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ_]+$", message = "Solo letras mayúsculas y guión bajo")
    String nombre,

    @NotBlank(message = "El nombre de visualización es obligatorio")
    @Size(max = 100)
    String nombreVisualizacion,

    @Size(max = 250)
    String descripcion
) {}
