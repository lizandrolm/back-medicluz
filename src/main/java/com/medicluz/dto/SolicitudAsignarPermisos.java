package com.medicluz.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SolicitudAsignarPermisos(@NotEmpty List<EntradaPermiso> permisos) {

    public record EntradaPermiso(
        Long idOpcion,
        boolean puedeVer,
        boolean puedeCrear,
        boolean puedeEditar,
        boolean puedeEliminar
    ) {}
}
