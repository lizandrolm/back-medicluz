package com.medicluz.autenticacion.controlador;

import com.medicluz.autenticacion.dto.*;
import com.medicluz.autenticacion.servicio.AutenticacionServicio;
import com.medicluz.comun.respuesta.RespuestaApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Registro, ingreso, renovación de token y cierre de sesión")
@RestController
@RequestMapping("/autenticacion")
@RequiredArgsConstructor
public class AutenticacionControlador {

    private final AutenticacionServicio autenticacionServicio;

    @Operation(summary = "Registrar nuevo usuario")
    @PostMapping("/registrar")
    public ResponseEntity<RespuestaApi<RespuestaAutenticacion>> registrar(
            @Valid @RequestBody SolicitudRegistro solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Usuario registrado exitosamente",
                        autenticacionServicio.registrar(solicitud)));
    }

    @Operation(summary = "Ingresar al sistema — devuelve tokens JWT")
    @PostMapping("/ingresar")
    public ResponseEntity<RespuestaApi<RespuestaAutenticacion>> ingresar(
            @Valid @RequestBody SolicitudLogin solicitud) {
        return ResponseEntity.ok(
                RespuestaApi.exitoso("Ingreso exitoso", autenticacionServicio.ingresar(solicitud)));
    }

    @Operation(summary = "Renovar token de acceso usando token de refresco")
    @PostMapping("/renovar-token")
    public ResponseEntity<RespuestaApi<RespuestaAutenticacion>> renovarToken(
            @Valid @RequestBody SolicitudTokenRefresco solicitud) {
        return ResponseEntity.ok(
                RespuestaApi.exitoso("Token renovado", autenticacionServicio.renovarToken(solicitud)));
    }

    @Operation(summary = "Cerrar sesión — revocar todos los tokens de refresco")
    @PostMapping("/salir")
    public ResponseEntity<RespuestaApi<Void>> salir(
            @AuthenticationPrincipal UserDetails detallesUsuario) {
        autenticacionServicio.salir(detallesUsuario.getUsername());
        return ResponseEntity.ok(RespuestaApi.exitoso("Sesión cerrada exitosamente", null));
    }
}
