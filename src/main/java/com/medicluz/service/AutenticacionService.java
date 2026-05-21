package com.medicluz.service;

import com.medicluz.dto.RespuestaAutenticacion;
import com.medicluz.dto.SolicitudLogin;
import com.medicluz.dto.SolicitudRegistro;
import com.medicluz.dto.SolicitudTokenRefresco;

public interface AutenticacionService {
    RespuestaAutenticacion registrar(SolicitudRegistro solicitud);
    RespuestaAutenticacion ingresar(SolicitudLogin solicitud);
    RespuestaAutenticacion renovarToken(SolicitudTokenRefresco solicitud);
    void salir(String correo);
}
