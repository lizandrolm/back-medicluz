package com.medicluz.dto;

import jakarta.validation.constraints.NotBlank;

public record SolicitudTokenRefresco(@NotBlank String tokenRefresco) {}
