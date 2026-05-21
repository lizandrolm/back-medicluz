package com.medicluz.controller;

import com.medicluz.comun.respuesta.RespuestaApi;
import com.medicluz.dto.*;
import com.medicluz.entidad.Usuario;
import com.medicluz.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Roles", description = "Gestión de roles y asignación de permisos")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @Operation(summary = "Listar todos los roles")
    @GetMapping
    public ResponseEntity<RespuestaApi<List<RespuestaRol>>> listar() {
        return ResponseEntity.ok(RespuestaApi.exitoso(rolService.listar()));
    }

    @Operation(summary = "Obtener rol con permisos detallados")
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaApi<RespuestaRol>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(rolService.buscarPorId(id)));
    }

    @Operation(summary = "Crear nuevo rol")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaRol>> crear(@Valid @RequestBody SolicitudRol solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Rol creado", rolService.crear(solicitud)));
    }

    @Operation(summary = "Actualizar rol")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaRol>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudRol solicitud) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Rol actualizado", rolService.actualizar(id, solicitud)));
    }

    @Operation(summary = "Activar o desactivar rol")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaRol>> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Estado actualizado", rolService.cambiarEstado(id)));
    }

    @Operation(summary = "Eliminar rol")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<Void>> eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
        return ResponseEntity.ok(RespuestaApi.exitoso("Rol eliminado", null));
    }

    @Operation(summary = "Obtener permisos de un rol")
    @GetMapping("/{id}/permisos")
    public ResponseEntity<RespuestaApi<List<RespuestaPermiso>>> obtenerPermisos(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(rolService.obtenerPermisos(id)));
    }

    @Operation(summary = "Reemplazar todos los permisos de un rol")
    @PutMapping("/{id}/permisos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<List<RespuestaPermiso>>> asignarPermisos(
            @PathVariable Long id, @Valid @RequestBody SolicitudAsignarPermisos solicitud) {
        return ResponseEntity.ok(
                RespuestaApi.exitoso("Permisos actualizados", rolService.asignarPermisos(id, solicitud)));
    }

    @Operation(summary = "Menú visible según el rol del usuario autenticado")
    @GetMapping("/mi-menu")
    public ResponseEntity<RespuestaApi<List<RespuestaPermiso>>> obtenerMiMenu(
            @AuthenticationPrincipal UserDetails detallesUsuario) {
        Long idRol = ((Usuario) detallesUsuario).getRol().getId();
        return ResponseEntity.ok(RespuestaApi.exitoso(rolService.obtenerMenuPorRol(idRol)));
    }
}
