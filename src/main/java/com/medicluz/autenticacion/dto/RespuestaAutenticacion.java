package com.medicluz.autenticacion.dto;

import lombok.Builder;

@Builder
public record RespuestaAutenticacion(
    String tokenAcceso,
    String tokenRefresco,
    String tipoBearerToken,
    long expiraEn,
    Long idUsuario,
    String nombreCompleto,
    String correo,
    Long idRol,
    String nombreRol,
    String nombreVisualizacionRol
) {
    public static RespuestaAutenticacionBuilder porDefecto() {
        return RespuestaAutenticacion.builder().tipoBearerToken("Bearer");
    }
}
