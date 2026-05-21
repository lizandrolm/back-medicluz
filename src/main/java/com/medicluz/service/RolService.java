package com.medicluz.service;

import com.medicluz.dto.*;

import java.util.List;

public interface RolService {
    List<RespuestaRol> listar();
    RespuestaRol buscarPorId(Long id);
    RespuestaRol crear(SolicitudRol solicitud);
    RespuestaRol actualizar(Long id, SolicitudRol solicitud);
    RespuestaRol cambiarEstado(Long id);
    void eliminar(Long id);
    List<RespuestaPermiso> obtenerPermisos(Long idRol);
    List<RespuestaPermiso> asignarPermisos(Long idRol, SolicitudAsignarPermisos solicitud);
    List<RespuestaPermiso> obtenerMenuPorRol(Long idRol);
}
