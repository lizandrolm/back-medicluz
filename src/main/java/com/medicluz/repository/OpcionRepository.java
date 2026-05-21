package com.medicluz.repository;

import com.medicluz.entidad.Opcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Long> {

    Optional<Opcion> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    @Query("""
        SELECT DISTINCT o FROM Opcion o
        LEFT JOIN FETCH o.hijos h
        WHERE o.padre IS NULL
        ORDER BY o.ordenVisualizacion ASC
        """)
    List<Opcion> buscarArbol();

    List<Opcion> findByPadreIsNullOrderByOrdenVisualizacionAsc();
    List<Opcion> findByPadreIdOrderByOrdenVisualizacionAsc(Long idPadre);
}
