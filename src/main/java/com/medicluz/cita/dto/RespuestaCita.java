package com.medicluz.cita.dto;

import com.medicluz.cita.entidad.EstadoCita;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RespuestaCita(
    Long id,
    Long idPaciente,
    String nombrePaciente,
    String codigoPaciente,
    Long idMedico,
    String nombreMedico,
    LocalDateTime fechaHora,
    String tipo,
    EstadoCita estado,
    String motivo,
    String notas,
    LocalDateTime fechaRegistro,
    LocalDateTime fechaActualiza
) {}
