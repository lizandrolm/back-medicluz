package com.medicluz.cita.entidad;

import com.medicluz.comun.entidad.EntidadBase;
import com.medicluz.paciente.entidad.Paciente;
import com.medicluz.usuario.entidad.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "citas",
    indexes = {
        @Index(name = "idx_cita_paciente",  columnList = "id_paciente"),
        @Index(name = "idx_cita_medico",    columnList = "id_medico"),
        @Index(name = "idx_cita_fecha",     columnList = "fecha_hora"),
        @Index(name = "idx_cita_estado",    columnList = "estado")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medico", nullable = false)
    private Usuario medico;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(name = "motivo", length = 200)
    private String motivo;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;
}
