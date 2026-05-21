package com.medicluz.rol.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SolicitudAsignarPermisos(

    @NotEmpty(message = "Se requiere al menos un permiso")
    @Valid
    List<EntradaPermiso> permisos
) {
    public record EntradaPermiso(
        Long idOpcion,
        boolean puedeVer,
        boolean puedeCrear,
        boolean puedeEditar,
        boolean puedeEliminar
    ) {}
}
