package com.medicluz.comun.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CorreoYaExisteExcepcion extends RuntimeException {

    public CorreoYaExisteExcepcion(String correo) {
        super("El correo ya está registrado: " + correo);
    }
}
