package com.medicluz.patient.entity;

import com.medicluz.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentType documentType;

    @Column(nullable = false, length = 20)
    private String documentNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MaritalStatus maritalStatus;

    @Column(length = 5)
    private String bloodType;

    @Column(length = 100)
    private String nationality;

    @Column(length = 100)
    private String occupation;

    // ─── Contact ──────────────────────────────────────────────────────────────

    @Column(length = 20)
    private String phonePrimary;

    @Column(length = 20)
    private String phoneSecondary;

    @Column(length = 150)
    private String email;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String province;

    @Column(length = 10)
    private String postalCode;

    // ─── Medical history ──────────────────────────────────────────────────────

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(columnDefinition = "TEXT")
    private String chronicDiseases;

    @Column(columnDefinition = "TEXT")
    private String currentMedications;

    @Column(columnDefinition = "TEXT")
    private String surgicalHistory;

    @Column(columnDefinition = "TEXT")
    private String familyHistory;

    @Column(columnDefinition = "TEXT")
    private String observations;

    // ─── Emergency contact ────────────────────────────────────────────────────

    @Column(length = 100)
    private String emergencyContactName;

    @Column(length = 100)
    private String emergencyContactPhone;

    @Column(length = 50)
    private String emergencyContactRelationship;

    // ─── Insurance ────────────────────────────────────────────────────────────

    @Column(nullable = false)
    @Builder.Default
    private boolean hasInsurance = false;

    @Column(length = 100)
    private String insuranceCompany;

    @Column(length = 50)
    private String insurancePolicyNumber;

    // ─── Status ───────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PatientStatus status = PatientStatus.ACTIVE;

    // ─── Helpers ──────────────────────────────────────────────────────────────

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
