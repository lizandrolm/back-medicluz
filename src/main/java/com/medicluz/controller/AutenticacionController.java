package com.medicluz.controller;

import com.medicluz.comun.respuesta.RespuestaApi;
import com.medicluz.dto.*;
import com.medicluz.service.AutenticacionService;
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
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    @Operation(summary = "Registrar nuevo usuario")
    @PostMapping("/registrar")
    public ResponseEntity<RespuestaApi<RespuestaAutenticacion>> registrar(
            @Valid @RequestBody SolicitudRegistro solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Usuario registrado exitosamente",
                        autenticacionService.registrar(solicitud)));
    }

    @Operation(summary = "Ingresar al sistema")
    @PostMapping("/ingresar")
    public ResponseEntity<RespuestaApi<RespuestaAutenticacion>> ingresar(
            @Valid @RequestBody SolicitudLogin solicitud) {
        return ResponseEntity.ok(
                RespuestaApi.exitoso("Ingreso exitoso", autenticacionService.ingresar(solicitud)));
    }

    @Operation(summary = "Renovar token de acceso")
    @PostMapping("/renovar-token")
    public ResponseEntity<RespuestaApi<RespuestaAutenticacion>> renovarToken(
            @Valid @RequestBody SolicitudTokenRefresco solicitud) {
        return ResponseEntity.ok(
                RespuestaApi.exitoso("Token renovado", autenticacionService.renovarToken(solicitud)));
    }

    @Operation(summary = "Cerrar sesión")
    @PostMapping("/salir")
    public ResponseEntity<RespuestaApi<Void>> salir(
            @AuthenticationPrincipal UserDetails detallesUsuario) {
        autenticacionService.salir(detallesUsuario.getUsername());
        return ResponseEntity.ok(RespuestaApi.exitoso("Sesión cerrada exitosamente", null));
    }
}
