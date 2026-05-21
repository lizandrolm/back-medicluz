package com.medicluz.comun.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Clase base para todas las entidades. Provee:
 * - PK auto-incremental
 * - Auditoría automática (creado/modificado por quién y cuándo)
 * - Bloqueo optimista con @Version para evitar actualizaciones perdidas
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @CreatedDate
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @LastModifiedDate
    @Column(name = "modificado_en", nullable = false)
    private LocalDateTime modificadoEn;

    @CreatedBy
    @Column(name = "creado_por", updatable = false, length = 150)
    private String creadoPor;

    @LastModifiedBy
    @Column(name = "modificado_por", length = 150)
    private String modificadoPor;
}
