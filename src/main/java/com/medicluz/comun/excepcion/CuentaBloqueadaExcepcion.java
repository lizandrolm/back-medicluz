package com.medicluz.comun.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class CuentaBloqueadaExcepcion extends RuntimeException {

    public CuentaBloqueadaExcepcion(String mensaje) {
        super(mensaje);
    }
}
