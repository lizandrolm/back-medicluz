package com.medicluz.patient.service;

import com.medicluz.common.exception.BadRequestException;
import com.medicluz.common.exception.ResourceNotFoundException;
import com.medicluz.common.response.PageResponse;
import com.medicluz.patient.dto.PatientRequest;
import com.medicluz.patient.dto.PatientResponse;
import com.medicluz.patient.dto.PatientSummary;
import com.medicluz.patient.entity.Patient;
import com.medicluz.patient.entity.PatientStatus;
import com.medicluz.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    // ─── Create ──────────────────────────────────────────────────────────────

    @Transactional
    public PatientResponse create(PatientRequest request) {
        if (patientRepository.existsByDocumentTypeAndDocumentNumber(
                request.documentType(), request.documentNumber())) {
            throw new BadRequestException("Patient with this document already exists");
        }

        Patient patient = Patient.builder()
                .code(generateCode())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .documentType(request.documentType())
                .documentNumber(request.documentNumber())
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .maritalStatus(request.maritalStatus())
                .bloodType(request.bloodType())
                .nationality(request.nationality())
                .occupation(request.occupation())
                .phonePrimary(request.phonePrimary())
                .phoneSecondary(request.phoneSecondary())
                .email(request.email())
                .address(request.address())
                .city(request.city())
                .province(request.province())
                .postalCode(request.postalCode())
                .allergies(request.allergies())
                .chronicDiseases(request.chronicDiseases())
                .currentMedications(request.currentMedications())
                .surgicalHistory(request.surgicalHistory())
                .familyHistory(request.familyHistory())
                .observations(request.observations())
                .emergencyContactName(request.emergencyContactName())
                .emergencyContactPhone(request.emergencyContactPhone())
                .emergencyContactRelationship(request.emergencyContactRelationship())
                .hasInsurance(request.hasInsurance())
                .insuranceCompany(request.insuranceCompany())
                .insurancePolicyNumber(request.insurancePolicyNumber())
                .build();

        return toResponse(patientRepository.save(patient));
    }

    // ─── Read ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<PatientSummary> findAll(String search, PatientStatus status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("lastName", "firstName"));
        Page<Patient> patients = patientRepository.search(
                (search == null || search.isBlank()) ? null : search.trim(),
                status,
                pageRequest
        );
        return PageResponse.of(patients.map(this::toSummary));
    }

    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        return toResponse(findPatientById(id));
    }

    @Transactional(readOnly = true)
    public PatientResponse findByCode(String code) {
        Patient patient = patientRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "code", code));
        return toResponse(patient);
    }

    // ─── Update ──────────────────────────────────────────────────────────────

    @Transactional
    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = findPatientById(id);

        // Check document uniqueness only if changed
        if (!patient.getDocumentNumber().equals(request.documentNumber()) ||
            patient.getDocumentType() != request.documentType()) {
            if (patientRepository.existsByDocumentTypeAndDocumentNumber(
                    request.documentType(), request.documentNumber())) {
                throw new BadRequestException("Another patient already has this document");
            }
        }

        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setDocumentType(request.documentType());
        patient.setDocumentNumber(request.documentNumber());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setMaritalStatus(request.maritalStatus());
        patient.setBloodType(request.bloodType());
        patient.setNationality(request.nationality());
        patient.setOccupation(request.occupation());
        patient.setPhonePrimary(request.phonePrimary());
        patient.setPhoneSecondary(request.phoneSecondary());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setCity(request.city());
        patient.setProvince(request.province());
        patient.setPostalCode(request.postalCode());
        patient.setAllergies(request.allergies());
        patient.setChronicDiseases(request.chronicDiseases());
        patient.setCurrentMedications(request.currentMedications());
        patient.setSurgicalHistory(request.surgicalHistory());
        patient.setFamilyHistory(request.familyHistory());
        patient.setObservations(request.observations());
        patient.setEmergencyContactName(request.emergencyContactName());
        patient.setEmergencyContactPhone(request.emergencyContactPhone());
        patient.setEmergencyContactRelationship(request.emergencyContactRelationship());
        patient.setHasInsurance(request.hasInsurance());
        patient.setInsuranceCompany(request.insuranceCompany());
        patient.setInsurancePolicyNumber(request.insurancePolicyNumber());

        return toResponse(patientRepository.save(patient));
    }

    // ─── Status ──────────────────────────────────────────────────────────────

    @Transactional
    public PatientResponse changeStatus(Long id, PatientStatus status) {
        Patient patient = findPatientById(id);
        patient.setStatus(status);
        return toResponse(patientRepository.save(patient));
    }

    // ─── Delete ──────────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long id) {
        Patient patient = findPatientById(id);
        patientRepository.delete(patient);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    private String generateCode() {
        int year = Year.now().getValue();
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        String code = "PAC-" + year + "-" + random;
        // Ensure uniqueness (rare collision guard)
        while (patientRepository.findByCode(code).isPresent()) {
            random = ThreadLocalRandom.current().nextInt(1000, 9999);
            code = "PAC-" + year + "-" + random;
        }
        return code;
    }

    private int calculateAge(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    private PatientResponse toResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .fullName(p.getFullName())
                .documentType(p.getDocumentType())
                .documentNumber(p.getDocumentNumber())
                .dateOfBirth(p.getDateOfBirth())
                .age(calculateAge(p.getDateOfBirth()))
                .gender(p.getGender())
                .maritalStatus(p.getMaritalStatus())
                .bloodType(p.getBloodType())
                .nationality(p.getNationality())
                .occupation(p.getOccupation())
                .phonePrimary(p.getPhonePrimary())
                .phoneSecondary(p.getPhoneSecondary())
                .email(p.getEmail())
                .address(p.getAddress())
                .city(p.getCity())
                .province(p.getProvince())
                .postalCode(p.getPostalCode())
                .allergies(p.getAllergies())
                .chronicDiseases(p.getChronicDiseases())
                .currentMedications(p.getCurrentMedications())
                .surgicalHistory(p.getSurgicalHistory())
                .familyHistory(p.getFamilyHistory())
                .observations(p.getObservations())
                .emergencyContactName(p.getEmergencyContactName())
                .emergencyContactPhone(p.getEmergencyContactPhone())
                .emergencyContactRelationship(p.getEmergencyContactRelationship())
                .hasInsurance(p.isHasInsurance())
                .insuranceCompany(p.getInsuranceCompany())
                .insurancePolicyNumber(p.getInsurancePolicyNumber())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private PatientSummary toSummary(Patient p) {
        return PatientSummary.builder()
                .id(p.getId())
                .code(p.getCode())
                .fullName(p.getFullName())
                .age(calculateAge(p.getDateOfBirth()))
                .gender(p.getGender())
                .bloodType(p.getBloodType())
                .phonePrimary(p.getPhonePrimary())
                .email(p.getEmail())
                .status(p.getStatus())
                .build();
    }
}
