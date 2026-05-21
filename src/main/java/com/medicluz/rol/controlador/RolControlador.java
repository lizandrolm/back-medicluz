package com.medicluz.rol.controlador;

import com.medicluz.comun.respuesta.RespuestaApi;
import com.medicluz.rol.dto.*;
import com.medicluz.rol.servicio.RolServicio;
import com.medicluz.usuario.entidad.Usuario;
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
public class RolControlador {

    private final RolServicio rolServicio;

    @Operation(summary = "Listar todos los roles")
    @GetMapping
    public ResponseEntity<RespuestaApi<List<RespuestaRol>>> listar() {
        return ResponseEntity.ok(RespuestaApi.exitoso(rolServicio.listar()));
    }

    @Operation(summary = "Obtener rol con permisos detallados")
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaApi<RespuestaRol>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(rolServicio.buscarPorId(id)));
    }

    @Operation(summary = "Crear nuevo rol")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaRol>> crear(@Valid @RequestBody SolicitudRol solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Rol creado", rolServicio.crear(solicitud)));
    }

    @Operation(summary = "Actualizar rol")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaRol>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudRol solicitud) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Rol actualizado", rolServicio.actualizar(id, solicitud)));
    }

    @Operation(summary = "Activar o desactivar rol")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaRol>> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Estado actualizado", rolServicio.cambiarEstado(id)));
    }

    @Operation(summary = "Eliminar rol (solo si no tiene usuarios asignados)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<Void>> eliminar(@PathVariable Long id) {
        rolServicio.eliminar(id);
        return ResponseEntity.ok(RespuestaApi.exitoso("Rol eliminado", null));
    }

    // ─── Permisos ─────────────────────────────────────────────────────────────

    @Operation(summary = "Obtener permisos asignados a un rol")
    @GetMapping("/{id}/permisos")
    public ResponseEntity<RespuestaApi<List<RespuestaPermiso>>> obtenerPermisos(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(rolServicio.obtenerPermisos(id)));
    }

    @Operation(summary = "Reemplazar todos los permisos de un rol")
    @PutMapping("/{id}/permisos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<List<RespuestaPermiso>>> asignarPermisos(
            @PathVariable Long id, @Valid @RequestBody SolicitudAsignarPermisos solicitud) {
        return ResponseEntity.ok(
                RespuestaApi.exitoso("Permisos actualizados", rolServicio.asignarPermisos(id, solicitud)));
    }

    @Operation(summary = "Menú visible para el usuario autenticado según su rol")
    @GetMapping("/mi-menu")
    public ResponseEntity<RespuestaApi<List<RespuestaPermiso>>> obtenerMiMenu(
            @AuthenticationPrincipal UserDetails detallesUsuario) {
        Long idRol = ((Usuario) detallesUsuario).getRol().getId();
        return ResponseEntity.ok(RespuestaApi.exitoso(rolServicio.obtenerMenuPorRol(idRol)));
    }
}
