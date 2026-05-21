package com.medicluz.controller;

import com.medicluz.comun.respuesta.RespuestaApi;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaPaciente;
import com.medicluz.dto.ResumenPaciente;
import com.medicluz.dto.SolicitudPaciente;
import com.medicluz.enums.EstadoPaciente;
import com.medicluz.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pacientes", description = "Gestión de pacientes del sistema")
@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @Operation(summary = "Listar pacientes con paginación y filtros")
    @GetMapping
    public ResponseEntity<RespuestaApi<RespuestaPagina<ResumenPaciente>>> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) EstadoPaciente estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {
        return ResponseEntity.ok(RespuestaApi.exitoso(pacienteService.listar(busqueda, estado, pagina, tamano)));
    }

    @Operation(summary = "Obtener paciente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaApi<RespuestaPaciente>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(pacienteService.buscarPorId(id)));
    }

    @Operation(summary = "Obtener paciente por código")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<RespuestaApi<RespuestaPaciente>> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(RespuestaApi.exitoso(pacienteService.buscarPorCodigo(codigo)));
    }

    @Operation(summary = "Crear nuevo paciente")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
    public ResponseEntity<RespuestaApi<RespuestaPaciente>> crear(@Valid @RequestBody SolicitudPaciente solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Paciente registrado", pacienteService.crear(solicitud)));
    }

    @Operation(summary = "Actualizar paciente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
    public ResponseEntity<RespuestaApi<RespuestaPaciente>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudPaciente solicitud) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Paciente actualizado", pacienteService.actualizar(id, solicitud)));
    }

    @Operation(summary = "Cambiar estado del paciente")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO')")
    public ResponseEntity<RespuestaApi<RespuestaPaciente>> cambiarEstado(
            @PathVariable Long id, @RequestParam EstadoPaciente estado) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Estado actualizado", pacienteService.cambiarEstado(id, estado)));
    }

    @Operation(summary = "Eliminar paciente")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaApi<Void>> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.ok(RespuestaApi.exitoso("Paciente eliminado", null));
    }
}
