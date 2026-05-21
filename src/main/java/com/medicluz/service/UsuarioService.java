package com.medicluz.service;

import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaUsuario;
import com.medicluz.dto.SolicitudUsuario;

public interface UsuarioService {
    RespuestaPagina<RespuestaUsuario> listar(String busqueda, Boolean activo, int pagina, int tamano);
    RespuestaUsuario buscarPorId(Long id);
    RespuestaUsuario crear(SolicitudUsuario solicitud);
    RespuestaUsuario actualizar(Long id, SolicitudUsuario solicitud);
    RespuestaUsuario cambiarEstado(Long id);
    void eliminar(Long id);
}
