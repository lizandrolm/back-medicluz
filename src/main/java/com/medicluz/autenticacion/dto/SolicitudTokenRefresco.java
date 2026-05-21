package com.medicluz.autenticacion.dto;

import jakarta.validation.constraints.NotBlank;

public record SolicitudTokenRefresco(
    @NotBlank(message = "El token de refresco es obligatorio")
    String tokenRefresco
) {}
