package com.medicluz.patient.repository;

import com.medicluz.patient.entity.Patient;
import com.medicluz.patient.entity.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByCode(String code);

    boolean existsByDocumentTypeAndDocumentNumber(
            com.medicluz.patient.entity.DocumentType documentType,
            String documentNumber);

    @Query("""
        SELECT p FROM Patient p
        WHERE (:search IS NULL OR
               LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(p.lastName)  LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(p.code)      LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(p.documentNumber) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR p.status = :status)
        """)
    Page<Patient> search(
            @Param("search") String search,
            @Param("status") PatientStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.status = :status")
    long countByStatus(@Param("status") PatientStatus status);
}
