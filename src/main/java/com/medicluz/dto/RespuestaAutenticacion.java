package com.medicluz.dto;

import lombok.Builder;

@Builder(builderMethodName = "porDefecto")
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
        return new RespuestaAutenticacionBuilder().tipoBearerToken("Bearer");
    }
}
