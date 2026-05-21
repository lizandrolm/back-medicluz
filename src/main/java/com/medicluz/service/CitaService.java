package com.medicluz.service;

import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaCita;
import com.medicluz.dto.SolicitudCita;
import com.medicluz.enums.EstadoCita;

import java.time.LocalDate;
import java.util.List;

public interface CitaService {
    RespuestaPagina<RespuestaCita> listar(Long idPaciente, Long idMedico, LocalDate fecha, EstadoCita estado, int pagina, int tamano);
    List<RespuestaCita> listarPorPaciente(Long idPaciente);
    RespuestaCita buscarPorId(Long id);
    RespuestaCita crear(SolicitudCita solicitud);
    RespuestaCita actualizar(Long id, SolicitudCita solicitud);
    RespuestaCita cambiarEstado(Long id, EstadoCita estado);
    void cancelar(Long id, String motivo);
}
