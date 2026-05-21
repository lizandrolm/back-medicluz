package com.medicluz.paciente.dto;

import com.medicluz.paciente.entidad.EstadoCivil;
import com.medicluz.paciente.entidad.Genero;
import com.medicluz.paciente.entidad.TipoDocumento;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SolicitudPaciente(

    @NotBlank(message = "El nombre es obligatorio") @Size(max = 100)
    String primerNombre,

    @NotBlank(message = "El apellido es obligatorio") @Size(max = 100)
    String apellido,

    @NotNull(message = "El tipo de documento es obligatorio")
    TipoDocumento tipoDocumento,

    @NotBlank(message = "El número de documento es obligatorio") @Size(max = 20)
    String numeroDocumento,

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    LocalDate fechaNacimiento,

    @NotNull(message = "El género es obligatorio")
    Genero genero,

    EstadoCivil estadoCivil,
    @Size(max = 5) String grupoSanguineo,
    @Size(max = 100) String nacionalidad,
    @Size(max = 100) String ocupacion,

    @NotBlank(message = "El teléfono principal es obligatorio") @Size(max = 20)
    String telefonoPrincipal,

    @Size(max = 20) String telefonoSecundario,
    @Email(message = "Formato de correo inválido") @Size(max = 150) String correo,
    @Size(max = 200) String direccion,
    @Size(max = 100) String ciudad,
    @Size(max = 100) String provincia,
    @Size(max = 10)  String codigoPostal,

    String alergias,
    String enfermedadesCronicas,
    String medicamentosActuales,
    String historialQuirurgico,
    String antecedentesFamiliares,
    String observaciones,

    @NotBlank(message = "El contacto de emergencia es obligatorio") @Size(max = 100)
    String nombreEmergencia,

    @NotBlank(message = "El teléfono de emergencia es obligatorio") @Size(max = 100)
    String telefonoEmergencia,

    @Size(max = 50) String parentescoEmergencia,

    boolean tieneSeguro,
    @Size(max = 100) String empresaSeguro,
    @Size(max = 50)  String numeroPoliza
) {}
