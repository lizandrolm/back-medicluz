package com.medicluz.patient.dto;

import com.medicluz.patient.entity.Gender;
import com.medicluz.patient.entity.PatientStatus;
import lombok.Builder;

@Builder
public record PatientSummary(
    Long id,
    String code,
    String fullName,
    Integer age,
    Gender gender,
    String bloodType,
    String phonePrimary,
    String email,
    PatientStatus status
) {}
