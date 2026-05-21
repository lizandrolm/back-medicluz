package com.medicluz.dto;

import jakarta.validation.constraints.*;

public record SolicitudRegistro(
    @NotBlank @Size(max = 100) String primerNombre,
    @NotBlank @Size(max = 100) String apellido,
    @NotBlank @Email @Size(max = 150) String correo,
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
    )
    String contrasena,
    @NotNull Long idRol
) {}
