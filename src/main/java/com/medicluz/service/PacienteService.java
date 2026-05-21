package com.medicluz.service;

import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaPaciente;
import com.medicluz.dto.ResumenPaciente;
import com.medicluz.dto.SolicitudPaciente;
import com.medicluz.enums.EstadoPaciente;

public interface PacienteService {
    RespuestaPagina<ResumenPaciente> listar(String busqueda, EstadoPaciente estado, int pagina, int tamano);
    RespuestaPaciente buscarPorId(Long id);
    RespuestaPaciente buscarPorCodigo(String codigo);
    RespuestaPaciente crear(SolicitudPaciente solicitud);
    RespuestaPaciente actualizar(Long id, SolicitudPaciente solicitud);
    RespuestaPaciente cambiarEstado(Long id, EstadoPaciente estado);
    void eliminar(Long id);
}
