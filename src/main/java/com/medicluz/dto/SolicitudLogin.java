package com.medicluz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudLogin(
    @NotBlank @Email String correo,
    @NotBlank String contrasena
) {}
