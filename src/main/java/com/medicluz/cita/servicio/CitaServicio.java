package com.medicluz.cita.servicio;

import com.medicluz.cita.dto.RespuestaCita;
import com.medicluz.cita.dto.SolicitudCita;
import com.medicluz.cita.entidad.Cita;
import com.medicluz.cita.entidad.EstadoCita;
import com.medicluz.cita.repositorio.CitaRepositorio;
import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.paciente.entidad.Paciente;
import com.medicluz.paciente.repositorio.PacienteRepositorio;
import com.medicluz.usuario.entidad.Usuario;
import com.medicluz.usuario.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaServicio {

    private final CitaRepositorio citaRepositorio;
    private final PacienteRepositorio pacienteRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    @Transactional(readOnly = true)
    public RespuestaPagina<RespuestaCita> listar(Long idPaciente, Long idMedico,
                                                  LocalDate fecha, EstadoCita estado,
                                                  int pagina, int tamano) {
        PageRequest paginacion = PageRequest.of(pagina, tamano, Sort.by("fechaHora").descending());
        return RespuestaPagina.de(
                citaRepositorio.buscarPorFechaYFiltros(idPaciente, idMedico, fecha, estado, paginacion)
                        .map(this::aRespuesta)
        );
    }

    @Transactional(readOnly = true)
    public List<RespuestaCita> listarPorPaciente(Long idPaciente) {
        return citaRepositorio.findByPacienteId(idPaciente).stream()
                .map(this::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public RespuestaCita buscarPorId(Long id) {
        return aRespuesta(obtenerCita(id));
    }

    @Transactional
    public RespuestaCita crear(SolicitudCita solicitud) {
        Paciente paciente = pacienteRepositorio.findById(solicitud.idPaciente())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente", solicitud.idPaciente()));
        Usuario medico = usuarioRepositorio.findById(solicitud.idMedico())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Médico", solicitud.idMedico()));

        Cita cita = Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .fechaHora(solicitud.fechaHora())
                .tipo(solicitud.tipo())
                .estado(solicitud.estado() != null ? solicitud.estado() : EstadoCita.PENDIENTE)
                .motivo(solicitud.motivo())
                .notas(solicitud.notas())
                .build();
        return aRespuesta(citaRepositorio.save(cita));
    }

    @Transactional
    public RespuestaCita actualizar(Long id, SolicitudCita solicitud) {
        Cita cita = obtenerCita(id);
        if (cita.getEstado() == EstadoCita.COMPLETADA || cita.getEstado() == EstadoCita.CANCELADA) {
            throw new PeticionInvalidaExcepcion("No se puede modificar una cita completada o cancelada");
        }

        Paciente paciente = pacienteRepositorio.findById(solicitud.idPaciente())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente", solicitud.idPaciente()));
        Usuario medico = usuarioRepositorio.findById(solicitud.idMedico())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Médico", solicitud.idMedico()));

        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setFechaHora(solicitud.fechaHora());
        cita.setTipo(solicitud.tipo());
        if (solicitud.estado() != null) cita.setEstado(solicitud.estado());
        cita.setMotivo(solicitud.motivo());
        cita.setNotas(solicitud.notas());
        return aRespuesta(citaRepositorio.save(cita));
    }

    @Transactional
    public RespuestaCita cambiarEstado(Long id, EstadoCita estado) {
        Cita cita = obtenerCita(id);
        cita.setEstado(estado);
        return aRespuesta(citaRepositorio.save(cita));
    }

    @Transactional
    public void cancelar(Long id, String motivo) {
        Cita cita = obtenerCita(id);
        if (cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new PeticionInvalidaExcepcion("No se puede cancelar una cita ya completada");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        if (motivo != null && !motivo.isBlank()) cita.setNotas(motivo);
        citaRepositorio.save(cita);
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private Cita obtenerCita(Long id) {
        return citaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Cita", id));
    }

    private RespuestaCita aRespuesta(Cita c) {
        return RespuestaCita.builder()
                .id(c.getId())
                .idPaciente(c.getPaciente().getId())
                .nombrePaciente(c.getPaciente().getNombreCompleto())
                .codigoPaciente(c.getPaciente().getCodigo())
                .idMedico(c.getMedico().getId())
                .nombreMedico(c.getMedico().getPrimerNombre() + " " + c.getMedico().getApellido())
                .fechaHora(c.getFechaHora())
                .tipo(c.getTipo())
                .estado(c.getEstado())
                .motivo(c.getMotivo())
                .notas(c.getNotas())
                .creadoEn(c.getCreadoEn())
                .modificadoEn(c.getModificadoEn())
                .build();
    }
}
