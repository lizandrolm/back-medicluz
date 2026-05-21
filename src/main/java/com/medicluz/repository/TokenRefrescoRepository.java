package com.medicluz.repository;

import com.medicluz.entidad.TokenRefresco;
import com.medicluz.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRefrescoRepository extends JpaRepository<TokenRefresco, Long> {

    Optional<TokenRefresco> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE TokenRefresco t SET t.revocado = true WHERE t.usuario = :usuario AND t.revocado = false")
    void revocarTodosPorUsuario(@Param("usuario") Usuario usuario);

    @Modifying
    @Query("DELETE FROM TokenRefresco t WHERE t.usuario = :usuario")
    void eliminarTodosPorUsuario(@Param("usuario") Usuario usuario);
}
