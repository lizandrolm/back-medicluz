package com.medicluz.controller;

import com.medicluz.comun.respuesta.RespuestaApi;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaCita;
import com.medicluz.dto.SolicitudCita;
import com.medicluz.enums.EstadoCita;
import com.medicluz.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Citas", description = "Gestión de citas médicas")
@RestController
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @Operation(summary = "Listar citas con filtros y paginación")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RespuestaApi<RespuestaPagina<RespuestaCita>>> listar(
            @RequestParam(required = false) Long idPaciente,
            @RequestParam(required = false) Long idMedico,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) EstadoCita estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {
        return ResponseEntity.ok(RespuestaApi.exitoso(
                citaService.listar(idPaciente, idMedico, fecha, estado, pagina, tamano)));
    }

    @Operation(summary = "Listar citas de un paciente")
    @GetMapping("/paciente/{idPaciente}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RespuestaApi<List<RespuestaCita>>> listarPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(RespuestaApi.exitoso(citaService.listarPorPaciente(idPaciente)));
    }

    @Operation(summary = "Obtener cita por ID")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RespuestaApi<RespuestaCita>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespuestaApi.exitoso(citaService.buscarPorId(id)));
    }

    @Operation(summary = "Agendar nueva cita")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'MEDICO')")
    public ResponseEntity<RespuestaApi<RespuestaCita>> crear(@Valid @RequestBody SolicitudCita solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.creado("Cita agendada", citaService.crear(solicitud)));
    }

    @Operation(summary = "Actualizar datos de la cita")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'MEDICO')")
    public ResponseEntity<RespuestaApi<RespuestaCita>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudCita solicitud) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Cita actualizada", citaService.actualizar(id, solicitud)));
    }

    @Operation(summary = "Cambiar estado de la cita")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO')")
    public ResponseEntity<RespuestaApi<RespuestaCita>> cambiarEstado(
            @PathVariable Long id, @RequestParam EstadoCita estado) {
        return ResponseEntity.ok(RespuestaApi.exitoso("Estado actualizado", citaService.cambiarEstado(id, estado)));
    }

    @Operation(summary = "Cancelar cita")
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'MEDICO')")
    public ResponseEntity<RespuestaApi<Void>> cancelar(
            @PathVariable Long id, @RequestParam(required = false) String motivo) {
        citaService.cancelar(id, motivo);
        return ResponseEntity.ok(RespuestaApi.exitoso("Cita cancelada", null));
    }
}
