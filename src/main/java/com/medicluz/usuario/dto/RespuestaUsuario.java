package com.medicluz.usuario.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RespuestaUsuario(
    Long id,
    String primerNombre,
    String apellido,
    String nombreCompleto,
    String correo,
    Long idRol,
    String nombreRol,
    String nombreVisualizacionRol,
    boolean activo,
    LocalDateTime creadoEn,
    LocalDateTime modificadoEn
) {}
