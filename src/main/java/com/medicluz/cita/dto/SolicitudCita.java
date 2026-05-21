package com.medicluz.cita.dto;

import com.medicluz.cita.entidad.EstadoCita;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SolicitudCita(

    @NotNull(message = "El paciente es obligatorio")
    Long idPaciente,

    @NotNull(message = "El médico es obligatorio")
    Long idMedico,

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La cita debe ser en el futuro")
    LocalDateTime fechaHora,

    @NotBlank(message = "El tipo de cita es obligatorio") @Size(max = 100)
    String tipo,

    EstadoCita estado,

    @Size(max = 200) String motivo,
    String notas
) {}
