package com.medicluz.patient.dto;

import com.medicluz.patient.entity.DocumentType;
import com.medicluz.patient.entity.Gender;
import com.medicluz.patient.entity.MaritalStatus;
import com.medicluz.patient.entity.PatientStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PatientResponse(
    Long id,
    String code,
    String firstName,
    String lastName,
    String fullName,
    DocumentType documentType,
    String documentNumber,
    LocalDate dateOfBirth,
    Integer age,
    Gender gender,
    MaritalStatus maritalStatus,
    String bloodType,
    String nationality,
    String occupation,
    String phonePrimary,
    String phoneSecondary,
    String email,
    String address,
    String city,
    String province,
    String postalCode,
    String allergies,
    String chronicDiseases,
    String currentMedications,
    String surgicalHistory,
    String familyHistory,
    String observations,
    String emergencyContactName,
    String emergencyContactPhone,
    String emergencyContactRelationship,
    boolean hasInsurance,
    String insuranceCompany,
    String insurancePolicyNumber,
    PatientStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
