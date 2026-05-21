package com.medicluz.entidad;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "opciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Opcion extends EntidadBase {

    @Column(name = "codigo", nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "icono", length = 60)
    private String icono;

    @Column(name = "ruta", length = 150)
    private String ruta;

    @Column(name = "orden_visualizacion", nullable = false)
    @Builder.Default
    private int ordenVisualizacion = 0;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_padre")
    private Opcion padre;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordenVisualizacion ASC")
    @Builder.Default
    private List<Opcion> hijos = new ArrayList<>();

    @OneToMany(mappedBy = "opcion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RolOpcion> rolesOpciones = new ArrayList<>();

    public boolean esPadre() { return padre == null; }
}
