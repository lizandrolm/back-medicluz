package com.medicluz.service;

import com.medicluz.dto.RespuestaOpcion;
import com.medicluz.dto.SolicitudOpcion;

import java.util.List;

public interface OpcionService {
    List<RespuestaOpcion> obtenerArbol();
    List<RespuestaOpcion> listar();
    RespuestaOpcion buscarPorId(Long id);
    RespuestaOpcion crear(SolicitudOpcion solicitud);
    RespuestaOpcion actualizar(Long id, SolicitudOpcion solicitud);
    RespuestaOpcion cambiarEstado(Long id);
    void eliminar(Long id);
}
