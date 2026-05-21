package com.medicluz.service.impl;

import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaCita;
import com.medicluz.dto.SolicitudCita;
import com.medicluz.entidad.Cita;
import com.medicluz.entidad.Paciente;
import com.medicluz.entidad.Usuario;
import com.medicluz.enums.EstadoCita;
import com.medicluz.repository.CitaRepository;
import com.medicluz.repository.PacienteRepository;
import com.medicluz.repository.UsuarioRepository;
import com.medicluz.service.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override @Transactional(readOnly = true)
    public RespuestaPagina<RespuestaCita> listar(Long idPaciente, Long idMedico,
                                                  LocalDate fecha, EstadoCita estado,
                                                  int pagina, int tamano) {
        PageRequest paginacion = PageRequest.of(pagina, tamano, Sort.by("fechaHora").descending());
        return RespuestaPagina.de(
                citaRepository.buscarPorFechaYFiltros(idPaciente, idMedico, fecha, estado, paginacion)
                        .map(this::aRespuesta)
        );
    }

    @Override @Transactional(readOnly = true)
    public List<RespuestaCita> listarPorPaciente(Long idPaciente) {
        return citaRepository.findByPacienteId(idPaciente).stream().map(this::aRespuesta).toList();
    }

    @Override @Transactional(readOnly = true)
    public RespuestaCita buscarPorId(Long id) {
        return aRespuesta(obtenerCita(id));
    }

    @Override @Transactional
    public RespuestaCita crear(SolicitudCita solicitud) {
        Paciente paciente = pacienteRepository.findById(solicitud.idPaciente())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente", solicitud.idPaciente()));
        Usuario medico = usuarioRepository.findById(solicitud.idMedico())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Médico", solicitud.idMedico()));

        return aRespuesta(citaRepository.save(Cita.builder()
                .paciente(paciente).medico(medico)
                .fechaHora(solicitud.fechaHora()).tipo(solicitud.tipo())
                .estado(solicitud.estado() != null ? solicitud.estado() : EstadoCita.PENDIENTE)
                .motivo(solicitud.motivo()).notas(solicitud.notas())
                .build()));
    }

    @Override @Transactional
    public RespuestaCita actualizar(Long id, SolicitudCita solicitud) {
        Cita cita = obtenerCita(id);
        if (cita.getEstado() == EstadoCita.COMPLETADA || cita.getEstado() == EstadoCita.CANCELADA) {
            throw new PeticionInvalidaExcepcion("No se puede modificar una cita completada o cancelada");
        }
        Paciente paciente = pacienteRepository.findById(solicitud.idPaciente())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente", solicitud.idPaciente()));
        Usuario medico = usuarioRepository.findById(solicitud.idMedico())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Médico", solicitud.idMedico()));

        cita.setPaciente(paciente); cita.setMedico(medico);
        cita.setFechaHora(solicitud.fechaHora()); cita.setTipo(solicitud.tipo());
        if (solicitud.estado() != null) cita.setEstado(solicitud.estado());
        cita.setMotivo(solicitud.motivo()); cita.setNotas(solicitud.notas());
        return aRespuesta(citaRepository.save(cita));
    }

    @Override @Transactional
    public RespuestaCita cambiarEstado(Long id, EstadoCita estado) {
        Cita cita = obtenerCita(id);
        cita.setEstado(estado);
        return aRespuesta(citaRepository.save(cita));
    }

    @Override @Transactional
    public void cancelar(Long id, String motivo) {
        Cita cita = obtenerCita(id);
        if (cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new PeticionInvalidaExcepcion("No se puede cancelar una cita ya completada");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        if (motivo != null && !motivo.isBlank()) cita.setNotas(motivo);
        citaRepository.save(cita);
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private Cita obtenerCita(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cita", id));
    }

    private RespuestaCita aRespuesta(Cita c) {
        return RespuestaCita.builder()
                .id(c.getId())
                .idPaciente(c.getPaciente().getId())
                .nombrePaciente(c.getPaciente().getNombreCompleto())
                .codigoPaciente(c.getPaciente().getCodigo())
                .idMedico(c.getMedico().getId())
                .nombreMedico(c.getMedico().getNombreCompleto())
                .fechaHora(c.getFechaHora()).tipo(c.getTipo()).estado(c.getEstado())
                .motivo(c.getMotivo()).notas(c.getNotas())
                .fechaRegistro(c.getFechaRegistro()).fechaActualiza(c.getFechaActualiza())
                .build();
    }
}
