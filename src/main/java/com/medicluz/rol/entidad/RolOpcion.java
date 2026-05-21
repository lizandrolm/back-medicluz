package com.medicluz.rol.entidad;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla de permisos: qué puede hacer cada rol sobre cada opción del menú.
 * La granularidad CRUD permite control fino por módulo.
 */
@Entity
@Table(
    name = "roles_opciones",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_rol_opcion",
        columnNames = {"id_rol", "id_opcion"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolOpcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_opcion", nullable = false)
    private Opcion opcion;

    @Column(name = "puede_ver",      nullable = false) @Builder.Default private boolean puedeVer      = true;
    @Column(name = "puede_crear",    nullable = false) @Builder.Default private boolean puedeCrear    = false;
    @Column(name = "puede_editar",   nullable = false) @Builder.Default private boolean puedeEditar   = false;
    @Column(name = "puede_eliminar", nullable = false) @Builder.Default private boolean puedeEliminar = false;
}
