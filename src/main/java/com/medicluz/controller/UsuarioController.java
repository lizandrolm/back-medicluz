package com.medicluz.controller;

import com.medicluz.comun.respuesta.RespuestaApi;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaUsuario;
import com.medicluz.dto.SolicitudUsuario;
import com.medicluz.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar usuarios con búsqueda y filtro de estado")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaPagina<RespuestaUsuario>>> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {
        return ResponseEntity.ok(RespuestaApi.exitoso(usuarioService.listar(busqueda, activo, pagina, tamano)));
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaUsuario>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(usuarioService.buscarPorId(id)));
    }

    @Operation(summary = "Crear nuevo usuario")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaUsuario>> crear(@Valid @RequestBody SolicitudUsuario solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Usuario creado", usuarioService.crear(solicitud)));
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaUsuario>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudUsuario solicitud) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Usuario actualizado", usuarioService.actualizar(id, solicitud)));
    }

    @Operation(summary = "Activar o desactivar usuario")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<RespuestaUsuario>> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Estado actualizado", usuarioService.cambiarEstado(id)));
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(RespuestaApi.exitoso("Usuario eliminado", null));
    }
}
