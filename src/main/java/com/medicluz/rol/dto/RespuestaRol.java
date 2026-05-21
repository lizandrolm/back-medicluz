package com.medicluz.rol.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RespuestaRol(
    Long id,
    String nombre,
    String nombreVisualizacion,
    String descripcion,
    boolean activo,
    int cantidadUsuarios,
    List<RespuestaPermiso> permisos,
    LocalDateTime fechaRegistro,
    LocalDateTime fechaActualiza
) {}
