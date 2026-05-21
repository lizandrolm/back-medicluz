package com.medicluz.paciente.dto;

import com.medicluz.paciente.entidad.EstadoPaciente;
import com.medicluz.paciente.entidad.Genero;
import lombok.Builder;

@Builder
public record ResumenPaciente(
    Long id, String codigo, String nombreCompleto,
    Integer edad, Genero genero, String grupoSanguineo,
    String telefonoPrincipal, String correo, EstadoPaciente estado
) {}
