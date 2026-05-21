package com.medicluz.dto;

import com.medicluz.enums.EstadoCita;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SolicitudCita(
    @NotNull Long idPaciente,
    @NotNull Long idMedico,
    @NotNull @Future LocalDateTime fechaHora,
    @NotBlank @Size(max = 100) String tipo,
    EstadoCita estado,
    @Size(max = 200) String motivo,
    String notas
) {}
