package com.medicluz.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(

    @NotNull(message = "Patient ID is required")
    Long patientId,

    @NotNull(message = "Doctor ID is required")
    Long doctorId,

    @NotNull(message = "Appointment date/time is required")
    @Future(message = "Appointment must be in the future")
    LocalDateTime appointmentDateTime,

    @NotBlank(message = "Appointment type is required")
    @Size(max = 100)
    String type,

    @Size(max = 200)
    String reason,

    String notes
) {}
