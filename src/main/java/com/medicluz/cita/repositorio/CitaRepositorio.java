package com.medicluz.cita.repositorio;

import com.medicluz.cita.entidad.Cita;
import com.medicluz.cita.entidad.EstadoCita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepositorio extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteId(Long idPaciente);

    @Query("""
        SELECT c FROM Cita c
        WHERE (:idPaciente IS NULL OR c.paciente.id = :idPaciente)
          AND (:idMedico   IS NULL OR c.medico.id   = :idMedico)
          AND (:fecha      IS NULL OR CAST(c.fechaHora AS date) = :fecha)
          AND (:estado     IS NULL OR c.estado = :estado)
        """)
    Page<Cita> buscarPorFechaYFiltros(
            @Param("idPaciente") Long idPaciente,
            @Param("idMedico")   Long idMedico,
            @Param("fecha")      LocalDate fecha,
            @Param("estado")     EstadoCita estado,
            Pageable pageable);
}
