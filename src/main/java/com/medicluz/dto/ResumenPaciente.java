package com.medicluz.dto;

import com.medicluz.enums.EstadoPaciente;
import com.medicluz.enums.Genero;
import lombok.Builder;

@Builder
public record ResumenPaciente(
    Long id, String codigo, String nombreCompleto,
    Integer edad, Genero genero, String grupoSanguineo,
    String telefonoPrincipal, String correo, EstadoPaciente estado
) {}
