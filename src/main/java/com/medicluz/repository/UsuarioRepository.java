package com.medicluz.repository;

import com.medicluz.entidad.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    long countByRolId(Long idRol);

    @Query("""
        SELECT u FROM Usuario u
        WHERE (:busqueda IS NULL
               OR LOWER(u.primerNombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(u.apellido)     LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(u.correo)       LIKE LOWER(CONCAT('%', :busqueda, '%')))
          AND (:activo IS NULL OR u.activo = :activo)
        """)
    Page<Usuario> buscar(
            @Param("busqueda") String busqueda,
            @Param("activo")   Boolean activo,
            Pageable pageable);
}
