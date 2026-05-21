package com.medicluz.dto;

import com.medicluz.enums.EstadoCivil;
import com.medicluz.enums.Genero;
import com.medicluz.enums.TipoDocumento;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SolicitudPaciente(
    @NotBlank @Size(max = 100) String primerNombre,
    @NotBlank @Size(max = 100) String apellido,
    @NotNull TipoDocumento tipoDocumento,
    @NotBlank @Size(max = 20) String numeroDocumento,
    @NotNull @Past LocalDate fechaNacimiento,
    @NotNull Genero genero,
    EstadoCivil estadoCivil,
    @Size(max = 5) String grupoSanguineo,
    @Size(max = 100) String nacionalidad,
    @Size(max = 100) String ocupacion,
    @NotBlank @Size(max = 20) String telefonoPrincipal,
    @Size(max = 20) String telefonoSecundario,
    @Email @Size(max = 150) String correo,
    @Size(max = 200) String direccion,
    @Size(max = 100) String ciudad,
    @Size(max = 100) String provincia,
    @Size(max = 10) String codigoPostal,
    String alergias,
    String enfermedadesCronicas,
    String medicamentosActuales,
    String historialQuirurgico,
    String antecedentesFamiliares,
    String observaciones,
    @NotBlank @Size(max = 100) String nombreEmergencia,
    @NotBlank @Size(max = 100) String telefonoEmergencia,
    @Size(max = 50) String parentescoEmergencia,
    boolean tieneSeguro,
    @Size(max = 100) String empresaSeguro,
    @Size(max = 50) String numeroPoliza
) {}
