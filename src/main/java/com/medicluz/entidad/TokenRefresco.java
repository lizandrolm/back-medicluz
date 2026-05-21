package com.medicluz.entidad;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
    name = "tokens_refresco",
    indexes = {
        @Index(name = "idx_tokens_refresco_hash",    columnList = "token_hash"),
        @Index(name = "idx_tokens_refresco_usuario", columnList = "id_usuario")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenRefresco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_expiracion", nullable = false)
    private Instant fechaExpiracion;

    @Column(name = "revocado", nullable = false)
    @Builder.Default
    private boolean revocado = false;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Builder.Default
    private Instant fechaRegistro = Instant.now();

    public boolean estaExpirado() { return Instant.now().isAfter(fechaExpiracion); }
    public boolean esValido()     { return !revocado && !estaExpirado(); }
}
