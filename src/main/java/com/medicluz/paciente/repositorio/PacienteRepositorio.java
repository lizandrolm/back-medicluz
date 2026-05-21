package com.medicluz.paciente.repositorio;

import com.medicluz.paciente.entidad.EstadoPaciente;
import com.medicluz.paciente.entidad.Paciente;
import com.medicluz.paciente.entidad.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepositorio extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByCodigo(String codigo);

    boolean existsByTipoDocumentoAndNumeroDocumento(TipoDocumento tipo, String numero);

    @Query("""
        SELECT p FROM Paciente p
        WHERE (:busqueda IS NULL
               OR LOWER(p.primerNombre)    LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(p.apellido)        LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(p.codigo)          LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(p.numeroDocumento) LIKE LOWER(CONCAT('%', :busqueda, '%')))
          AND (:estado IS NULL OR p.estado = :estado)
        """)
    Page<Paciente> buscar(
            @Param("busqueda") String busqueda,
            @Param("estado")   EstadoPaciente estado,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.estado = :estado")
    long contarPorEstado(@Param("estado") EstadoPaciente estado);
}
