package com.medicluz.rol.entidad;

import com.medicluz.comun.entidad.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol extends EntidadBase {

    /** Nombre técnico en mayúsculas: ADMINISTRADOR, MEDICO, RECEPCIONISTA */
    @Column(name = "nombre", nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(name = "nombre_visualizacion", nullable = false, length = 100)
    private String nombreVisualizacion;

    @Column(name = "descripcion", length = 250)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RolOpcion> rolesOpciones = new ArrayList<>();
}
