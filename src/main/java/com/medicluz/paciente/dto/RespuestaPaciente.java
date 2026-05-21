package com.medicluz.paciente.dto;

import com.medicluz.paciente.entidad.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record RespuestaPaciente(
    Long id, String codigo, String primerNombre, String apellido, String nombreCompleto,
    TipoDocumento tipoDocumento, String numeroDocumento,
    LocalDate fechaNacimiento, Integer edad, Genero genero,
    EstadoCivil estadoCivil, String grupoSanguineo, String nacionalidad, String ocupacion,
    String telefonoPrincipal, String telefonoSecundario, String correo,
    String direccion, String ciudad, String provincia, String codigoPostal,
    String alergias, String enfermedadesCronicas, String medicamentosActuales,
    String historialQuirurgico, String antecedentesFamiliares, String observaciones,
    String nombreEmergencia, String telefonoEmergencia, String parentescoEmergencia,
    boolean tieneSeguro, String empresaSeguro, String numeroPoliza,
    EstadoPaciente estado, LocalDateTime fechaRegistro, LocalDateTime fechaActualiza
) {}
