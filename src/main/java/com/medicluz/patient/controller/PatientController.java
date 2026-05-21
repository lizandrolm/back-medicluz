package com.medicluz.patient.controller;

import com.medicluz.common.response.ApiResponse;
import com.medicluz.common.response.PageResponse;
import com.medicluz.patient.dto.PatientRequest;
import com.medicluz.patient.dto.PatientResponse;
import com.medicluz.patient.dto.PatientSummary;
import com.medicluz.patient.entity.PatientStatus;
import com.medicluz.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patients", description = "Patient management")
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "List patients with search and status filter")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PatientSummary>>> findAll(
            @Parameter(description = "Search by name, code or document")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter by status: ACTIVE, INACTIVE, CRITICAL")
            @RequestParam(required = false) PatientStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.findAll(search, status, page, size)));
    }

    @Operation(summary = "Get patient by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.findById(id)));
    }

    @Operation(summary = "Get patient by code (e.g. PAC-2025-1234)")
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<PatientResponse>> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.findByCode(code)));
    }

    @Operation(summary = "Create a new patient")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> create(
            @Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Patient created successfully", response));
    }

    @Operation(summary = "Update a patient")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Patient updated", patientService.update(id, request)));
    }

    @Operation(summary = "Change patient status")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<PatientResponse>> changeStatus(
            @PathVariable Long id,
            @RequestParam PatientStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", patientService.changeStatus(id, status)));
    }

    @Operation(summary = "Delete a patient")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Patient deleted", null));
    }
}
