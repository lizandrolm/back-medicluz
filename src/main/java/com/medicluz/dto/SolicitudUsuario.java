package com.medicluz.dto;

import jakarta.validation.constraints.*;

public record SolicitudUsuario(
    @NotBlank @Size(max = 100) String primerNombre,
    @NotBlank @Size(max = 100) String apellido,
    @NotBlank @Email @Size(max = 150) String correo,
    String contrasena,
    @NotNull Long idRol
) {}
