package com.medicluz.appointment.dto;

import com.medicluz.appointment.entity.AppointmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppointmentResponse(
    Long id,
    Long patientId,
    String patientFullName,
    String patientCode,
    Long doctorId,
    String doctorFullName,
    LocalDateTime appointmentDateTime,
    String type,
    AppointmentStatus status,
    String reason,
    String notes,
    LocalDateTime createdAt
) {}
