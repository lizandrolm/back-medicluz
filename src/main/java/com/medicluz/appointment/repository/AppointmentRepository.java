package com.medicluz.appointment.repository;

import com.medicluz.appointment.entity.Appointment;
import com.medicluz.appointment.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorIdAndAppointmentDateTimeBetween(
            Long doctorId, LocalDateTime from, LocalDateTime to);

    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

    @Query("""
        SELECT a FROM Appointment a
        WHERE (:doctorId IS NULL OR a.doctor.id = :doctorId)
          AND (:status   IS NULL OR a.status = :status)
          AND CAST(a.appointmentDateTime AS date) = :date
        ORDER BY a.appointmentDateTime
        """)
    List<Appointment> findByDateAndFilters(
            @Param("date")     LocalDate date,
            @Param("doctorId") Long doctorId,
            @Param("status")   AppointmentStatus status);

    long countByStatusAndAppointmentDateTimeBetween(
            AppointmentStatus status, LocalDateTime from, LocalDateTime to);
}
