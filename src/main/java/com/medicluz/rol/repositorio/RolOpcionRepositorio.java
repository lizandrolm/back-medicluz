package com.medicluz.rol.repositorio;

import com.medicluz.rol.entidad.RolOpcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolOpcionRepositorio extends JpaRepository<RolOpcion, Long> {

    List<RolOpcion> findByRolId(Long idRol);

    Optional<RolOpcion> findByRolIdAndOpcionId(Long idRol, Long idOpcion);

    @Modifying
    @Query("DELETE FROM RolOpcion ro WHERE ro.rol.id = :idRol")
    void eliminarTodosPorRol(@Param("idRol") Long idRol);

    @Query("""
        SELECT ro FROM RolOpcion ro
        JOIN FETCH ro.opcion o
        WHERE ro.rol.id = :idRol
          AND ro.puedeVer = true
        ORDER BY o.ordenVisualizacion ASC
        """)
    List<RolOpcion> buscarMenuVisiblePorRol(@Param("idRol") Long idRol);
}
