package com.medicluz.rol.controlador;

import com.medicluz.comun.respuesta.RespuestaApi;
import com.medicluz.rol.dto.RespuestaOpcion;
import com.medicluz.rol.dto.SolicitudOpcion;
import com.medicluz.rol.servicio.OpcionServicio;
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
public class OpcionControlador {

    private final OpcionServicio opcionServicio;

    @Operation(summary = "Árbol completo de opciones (secciones + hijos)")
    @GetMapping("/arbol")
    public ResponseEntity<RespuestaApi<List<RespuestaOpcion>>> obtenerArbol() {
        return ResponseEntity.ok(RespuestaApi.exitoso(opcionServicio.obtenerArbol()));
    }

    @Operation(summary = "Lista plana de todas las opciones")
    @GetMapping
    public ResponseEntity<RespuestaApi<List<RespuestaOpcion>>> listar() {
        return ResponseEntity.ok(RespuestaApi.exitoso(opcionServicio.listar()));
    }

    @Operation(summary = "Obtener opción por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaApi<RespuestaOpcion>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(opcionServicio.buscarPorId(id)));
    }

    @Operation(summary = "Crear nueva opción")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaOpcion>> crear(@Valid @RequestBody SolicitudOpcion solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Opción creada", opcionServicio.crear(solicitud)));
    }

    @Operation(summary = "Actualizar opción")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaOpcion>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudOpcion solicitud) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Opción actualizada", opcionServicio.actualizar(id, solicitud)));
    }

    @Operation(summary = "Activar o desactivar opción (y sus hijos)")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaOpcion>> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Estado actualizado", opcionServicio.cambiarEstado(id)));
    }

    @Operation(summary = "Eliminar opción")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<Void>> eliminar(@PathVariable Long id) {
        opcionServicio.eliminar(id);
        return ResponseEntity.ok(RespuestaApi.exitoso("Opción eliminada", null));
    }
}
