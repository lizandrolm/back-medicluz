package com.medicluz.appointment.service;

import com.medicluz.appointment.dto.AppointmentRequest;
import com.medicluz.appointment.dto.AppointmentResponse;
import com.medicluz.appointment.entity.Appointment;
import com.medicluz.appointment.entity.AppointmentStatus;
import com.medicluz.appointment.repository.AppointmentRepository;
import com.medicluz.common.exception.BadRequestException;
import com.medicluz.common.exception.ResourceNotFoundException;
import com.medicluz.common.response.PageResponse;
import com.medicluz.patient.entity.Patient;
import com.medicluz.patient.repository.PatientRepository;
import com.medicluz.user.entity.User;
import com.medicluz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.patientId()));
        User doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.doctorId()));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDateTime(request.appointmentDateTime())
                .type(request.type())
                .reason(request.reason())
                .notes(request.notes())
                .build();

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        return toResponse(findAppointmentById(id));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByDate(LocalDate date, Long doctorId, AppointmentStatus status) {
        return appointmentRepository.findByDateAndFilters(date, doctorId, status)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> findByPatient(Long patientId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("appointmentDateTime").descending());
        return PageResponse.of(
                appointmentRepository.findByPatientId(patientId, pageRequest).map(this::toResponse)
        );
    }

    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = findAppointmentById(id);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED ||
            appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Cannot update a completed or cancelled appointment");
        }

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.patientId()));
        User doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.doctorId()));

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(request.appointmentDateTime());
        appointment.setType(request.type());
        appointment.setReason(request.reason());
        appointment.setNotes(request.notes());

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse changeStatus(Long id, AppointmentStatus status) {
        Appointment appointment = findAppointmentById(id);
        appointment.setStatus(status);
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public void delete(Long id) {
        appointmentRepository.delete(findAppointmentById(id));
    }

    private Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientFullName(a.getPatient().getFullName())
                .patientCode(a.getPatient().getCode())
                .doctorId(a.getDoctor().getId())
                .doctorFullName(a.getDoctor().getFullName())
                .appointmentDateTime(a.getAppointmentDateTime())
                .type(a.getType())
                .status(a.getStatus())
                .reason(a.getReason())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
