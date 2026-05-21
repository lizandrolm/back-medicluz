package com.medicluz.controller;

import com.medicluz.comun.respuesta.RespuestaApi;
import com.medicluz.dto.RespuestaOpcion;
import com.medicluz.dto.SolicitudOpcion;
import com.medicluz.service.OpcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Opciones", description = "Gestión de opciones del menú de navegación")
@RestController
@RequestMapping("/opciones")
@RequiredArgsConstructor
public class OpcionController {

    private final OpcionService opcionService;

    @Operation(summary = "Árbol completo de opciones")
    @GetMapping("/arbol")
    public ResponseEntity<RespuestaApi<List<RespuestaOpcion>>> obtenerArbol() {
        return ResponseEntity.ok(RespuestaApi.exitoso(opcionService.obtenerArbol()));
    }

    @Operation(summary = "Lista plana de opciones")
    @GetMapping
    public ResponseEntity<RespuestaApi<List<RespuestaOpcion>>> listar() {
        return ResponseEntity.ok(RespuestaApi.exitoso(opcionService.listar()));
    }

    @Operation(summary = "Obtener opción por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaApi<RespuestaOpcion>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(opcionService.buscarPorId(id)));
    }

    @Operation(summary = "Crear nueva opción")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaOpcion>> crear(@Valid @RequestBody SolicitudOpcion solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Opción creada", opcionService.crear(solicitud)));
    }

    @Operation(summary = "Actualizar opción")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaOpcion>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudOpcion solicitud) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Opción actualizada", opcionService.actualizar(id, solicitud)));
    }

    @Operation(summary = "Activar o desactivar opción")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaOpcion>> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Estado actualizado", opcionService.cambiarEstado(id)));
    }

    @Operation(summary = "Eliminar opción")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<Void>> eliminar(@PathVariable Long id) {
        opcionService.eliminar(id);
        return ResponseEntity.ok(RespuestaApi.exitoso("Opción eliminada", null));
    }
}
