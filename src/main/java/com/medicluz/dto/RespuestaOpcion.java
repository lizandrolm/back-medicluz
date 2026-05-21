package com.medicluz.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record RespuestaOpcion(
    Long id, String codigo, String nombre,
    String icono, String ruta,
    int ordenVisualizacion, boolean activo,
    Long idPadre, String nombrePadre,
    List<RespuestaOpcion> hijos
) {}
