package com.medicluz.patient.dto;

import com.medicluz.patient.entity.DocumentType;
import com.medicluz.patient.entity.Gender;
import com.medicluz.patient.entity.MaritalStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientRequest(

    // ─── Personal ──────────────────────────────────────────────────────────

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    String lastName,

    @NotNull(message = "Document type is required")
    DocumentType documentType,

    @NotBlank(message = "Document number is required")
    @Size(max = 20)
    String documentNumber,

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    @NotNull(message = "Gender is required")
    Gender gender,

    MaritalStatus maritalStatus,

    @Size(max = 5)
    String bloodType,

    @Size(max = 100)
    String nationality,

    @Size(max = 100)
    String occupation,

    // ─── Contact ───────────────────────────────────────────────────────────

    @NotBlank(message = "Primary phone is required")
    @Size(max = 20)
    String phonePrimary,

    @Size(max = 20)
    String phoneSecondary,

    @Email(message = "Invalid email format")
    @Size(max = 150)
    String email,

    @Size(max = 200)
    String address,

    @Size(max = 100)
    String city,

    @Size(max = 100)
    String province,

    @Size(max = 10)
    String postalCode,

    // ─── Medical history ───────────────────────────────────────────────────

    String allergies,
    String chronicDiseases,
    String currentMedications,
    String surgicalHistory,
    String familyHistory,
    String observations,

    // ─── Emergency contact ─────────────────────────────────────────────────

    @NotBlank(message = "Emergency contact name is required")
    @Size(max = 100)
    String emergencyContactName,

    @NotBlank(message = "Emergency contact phone is required")
    @Size(max = 100)
    String emergencyContactPhone,

    @Size(max = 50)
    String emergencyContactRelationship,

    // ─── Insurance ─────────────────────────────────────────────────────────

    boolean hasInsurance,

    @Size(max = 100)
    String insuranceCompany,

    @Size(max = 50)
    String insurancePolicyNumber
) {}
