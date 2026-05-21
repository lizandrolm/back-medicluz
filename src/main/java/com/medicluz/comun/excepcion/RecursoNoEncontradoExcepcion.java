package com.medicluz.comun.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNoEncontradoExcepcion extends RuntimeException {

    public RecursoNoEncontradoExcepcion(String recurso, Long id) {
        super(recurso + " no encontrado con id: " + id);
    }

    public RecursoNoEncontradoExcepcion(String recurso, String campo, String valor) {
        super(recurso + " no encontrado con " + campo + ": " + valor);
    }
}
