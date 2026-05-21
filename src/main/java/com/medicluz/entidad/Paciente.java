package com.medicluz.entidad;

import com.medicluz.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "pacientes",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_paciente_documento",
        columnNames = {"tipo_documento", "numero_documento"}
    ),
    indexes = {
        @Index(name = "idx_paciente_codigo",    columnList = "codigo"),
        @Index(name = "idx_paciente_nombre",    columnList = "apellido, primer_nombre"),
        @Index(name = "idx_paciente_estado",    columnList = "estado"),
        @Index(name = "idx_paciente_documento", columnList = "tipo_documento, numero_documento")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Paciente extends EntidadBase {

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "primer_nombre", nullable = false, length = 100)
    private String primerNombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", nullable = false, length = 15)
    private Genero genero;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_civil", length = 20)
    private EstadoCivil estadoCivil;

    @Column(name = "grupo_sanguineo", length = 5)
    private String grupoSanguineo;

    @Column(name = "nacionalidad", length = 100)
    private String nacionalidad;

    @Column(name = "ocupacion", length = 100)
    private String ocupacion;

    @Column(name = "telefono_principal", length = 20)
    private String telefonoPrincipal;

    @Column(name = "telefono_secundario", length = 20)
    private String telefonoSecundario;

    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "provincia", length = 100)
    private String provincia;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "enfermedades_cronicas", columnDefinition = "TEXT")
    private String enfermedadesCronicas;

    @Column(name = "medicamentos_actuales", columnDefinition = "TEXT")
    private String medicamentosActuales;

    @Column(name = "historial_quirurgico", columnDefinition = "TEXT")
    private String historialQuirurgico;

    @Column(name = "antecedentes_familiares", columnDefinition = "TEXT")
    private String antecedentesFamiliares;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "nombre_emergencia", length = 100)
    private String nombreEmergencia;

    @Column(name = "telefono_emergencia", length = 100)
    private String telefonoEmergencia;

    @Column(name = "parentesco_emergencia", length = 50)
    private String parentescoEmergencia;

    @Column(name = "tiene_seguro", nullable = false)
    @Builder.Default
    private boolean tieneSeguro = false;

    @Column(name = "empresa_seguro", length = 100)
    private String empresaSeguro;

    @Column(name = "numero_poliza", length = 50)
    private String numeroPoliza;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoPaciente estado = EstadoPaciente.ACTIVO;

    public String getNombreCompleto() { return primerNombre + " " + apellido; }
}
