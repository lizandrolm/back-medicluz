package com.medicluz.rol.dto;

import lombok.Builder;

@Builder
public record RespuestaPermiso(
    Long idRolOpcion,
    Long idOpcion,
    String codigoOpcion,
    String nombreOpcion,
    String iconoOpcion,
    String rutaOpcion,
    boolean puedeVer,
    boolean puedeCrear,
    boolean puedeEditar,
    boolean puedeEliminar
) {}
