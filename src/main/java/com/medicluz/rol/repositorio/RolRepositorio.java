package com.medicluz.rol.repositorio;

import com.medicluz.rol.entidad.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepositorio extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Rol> findByActivoTrue();

    @Query("SELECT r FROM Rol r LEFT JOIN FETCH r.rolesOpciones ro LEFT JOIN FETCH ro.opcion WHERE r.id = :id")
    Optional<Rol> buscarPorIdConOpciones(@Param("id") Long id);
}
