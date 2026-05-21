package com.medicluz.comun.excepcion;

import com.medicluz.comun.respuesta.RespuestaApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    public ResponseEntity<RespuestaApi<Void>> manejarNoEncontrado(RecursoNoEncontradoExcepcion ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RespuestaApi.error(ex.getMessage()));
    }

    @ExceptionHandler(PeticionInvalidaExcepcion.class)
    public ResponseEntity<RespuestaApi<Void>> manejarPeticionInvalida(PeticionInvalidaExcepcion ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespuestaApi.error(ex.getMessage()));
    }

    @ExceptionHandler(CorreoYaExisteExcepcion.class)
    public ResponseEntity<RespuestaApi<Void>> manejarCorreoExistente(CorreoYaExisteExcepcion ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RespuestaApi.error(ex.getMessage()));
    }

    @ExceptionHandler(CuentaBloqueadaExcepcion.class)
    public ResponseEntity<RespuestaApi<Void>> manejarCuentaBloqueada(CuentaBloqueadaExcepcion ex) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(RespuestaApi.error(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RespuestaApi<Void>> manejarCredencialesInvalidas(BadCredentialsException ex) {
        // No revelamos si el usuario existe o no
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RespuestaApi.error("Credenciales inválidas"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<RespuestaApi<Void>> manejarCuentaDeshabilitada(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaApi.error("Cuenta deshabilitada. Contacte al administrador."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespuestaApi<Void>> manejarAccesoDenegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaApi.error("No tiene permisos para realizar esta acción"));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<RespuestaApi<Void>> manejarConflictoVersiones(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RespuestaApi.error("El registro fue modificado por otro proceso. Recargue e intente de nuevo."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaApi<Void>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCampo = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            erroresCampo.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespuestaApi.error("Error de validación", erroresCampo));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaApi<Void>> manejarGenerico(Exception ex) {
        log.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaApi.error("Error interno del servidor. Intente más tarde."));
    }
}
