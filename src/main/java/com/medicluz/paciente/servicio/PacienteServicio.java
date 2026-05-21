package com.medicluz.paciente.servicio;

import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.paciente.dto.RespuestaPaciente;
import com.medicluz.paciente.dto.ResumenPaciente;
import com.medicluz.paciente.dto.SolicitudPaciente;
import com.medicluz.paciente.entidad.EstadoPaciente;
import com.medicluz.paciente.entidad.Paciente;
import com.medicluz.paciente.repositorio.PacienteRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PacienteServicio {

    private final PacienteRepositorio pacienteRepositorio;

    @Transactional(readOnly = true)
    public RespuestaPagina<ResumenPaciente> listar(String busqueda, EstadoPaciente estado,
                                                    int pagina, int tamano) {
        PageRequest paginacion = PageRequest.of(pagina, tamano, Sort.by("apellido", "primerNombre"));
        return RespuestaPagina.de(
                pacienteRepositorio.buscar(
                        (busqueda == null || busqueda.isBlank()) ? null : busqueda.trim(),
                        estado, paginacion
                ).map(this::aResumen)
        );
    }

    @Transactional(readOnly = true)
    public RespuestaPaciente buscarPorId(Long id) {
        return aRespuesta(obtenerPaciente(id));
    }

    @Transactional(readOnly = true)
    public RespuestaPaciente buscarPorCodigo(String codigo) {
        return aRespuesta(pacienteRepositorio.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente", "código", codigo)));
    }

    @Transactional
    public RespuestaPaciente crear(SolicitudPaciente solicitud) {
        if (pacienteRepositorio.existsByTipoDocumentoAndNumeroDocumento(
                solicitud.tipoDocumento(), solicitud.numeroDocumento())) {
            throw new PeticionInvalidaExcepcion("Ya existe un paciente con ese documento");
        }
        Paciente paciente = mapearDesdeSolicitud(new Paciente(), solicitud);
        paciente.setCodigo(generarCodigo());
        return aRespuesta(pacienteRepositorio.save(paciente));
    }

    @Transactional
    public RespuestaPaciente actualizar(Long id, SolicitudPaciente solicitud) {
        Paciente paciente = obtenerPaciente(id);
        if (!paciente.getNumeroDocumento().equals(solicitud.numeroDocumento())
                || paciente.getTipoDocumento() != solicitud.tipoDocumento()) {
            if (pacienteRepositorio.existsByTipoDocumentoAndNumeroDocumento(
                    solicitud.tipoDocumento(), solicitud.numeroDocumento())) {
                throw new PeticionInvalidaExcepcion("Otro paciente ya tiene ese documento registrado");
            }
        }
        return aRespuesta(pacienteRepositorio.save(mapearDesdeSolicitud(paciente, solicitud)));
    }

    @Transactional
    public RespuestaPaciente cambiarEstado(Long id, EstadoPaciente estado) {
        Paciente paciente = obtenerPaciente(id);
        paciente.setEstado(estado);
        return aRespuesta(pacienteRepositorio.save(paciente));
    }

    @Transactional
    public void eliminar(Long id) {
        pacienteRepositorio.delete(obtenerPaciente(id));
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private Paciente obtenerPaciente(Long id) {
        return pacienteRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente", id));
    }

    private Paciente mapearDesdeSolicitud(Paciente p, SolicitudPaciente s) {
        p.setPrimerNombre(s.primerNombre()); p.setApellido(s.apellido());
        p.setTipoDocumento(s.tipoDocumento()); p.setNumeroDocumento(s.numeroDocumento());
        p.setFechaNacimiento(s.fechaNacimiento()); p.setGenero(s.genero());
        p.setEstadoCivil(s.estadoCivil()); p.setGrupoSanguineo(s.grupoSanguineo());
        p.setNacionalidad(s.nacionalidad()); p.setOcupacion(s.ocupacion());
        p.setTelefonoPrincipal(s.telefonoPrincipal()); p.setTelefonoSecundario(s.telefonoSecundario());
        p.setCorreo(s.correo()); p.setDireccion(s.direccion());
        p.setCiudad(s.ciudad()); p.setProvincia(s.provincia()); p.setCodigoPostal(s.codigoPostal());
        p.setAlergias(s.alergias()); p.setEnfermedadesCronicas(s.enfermedadesCronicas());
        p.setMedicamentosActuales(s.medicamentosActuales()); p.setHistorialQuirurgico(s.historialQuirurgico());
        p.setAntecedentesFamiliares(s.antecedentesFamiliares()); p.setObservaciones(s.observaciones());
        p.setNombreEmergencia(s.nombreEmergencia()); p.setTelefonoEmergencia(s.telefonoEmergencia());
        p.setParentescoEmergencia(s.parentescoEmergencia());
        p.setTieneSeguro(s.tieneSeguro()); p.setEmpresaSeguro(s.empresaSeguro());
        p.setNumeroPoliza(s.numeroPoliza());
        return p;
    }

    private String generarCodigo() {
        int anio = Year.now().getValue();
        String codigo;
        do {
            int aleatorio = ThreadLocalRandom.current().nextInt(1000, 9999);
            codigo = "PAC-" + anio + "-" + aleatorio;
        } while (pacienteRepositorio.findByCodigo(codigo).isPresent());
        return codigo;
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    private RespuestaPaciente aRespuesta(Paciente p) {
        return RespuestaPaciente.builder()
                .id(p.getId()).codigo(p.getCodigo())
                .primerNombre(p.getPrimerNombre()).apellido(p.getApellido())
                .nombreCompleto(p.getNombreCompleto())
                .tipoDocumento(p.getTipoDocumento()).numeroDocumento(p.getNumeroDocumento())
                .fechaNacimiento(p.getFechaNacimiento()).edad(calcularEdad(p.getFechaNacimiento()))
                .genero(p.getGenero()).estadoCivil(p.getEstadoCivil())
                .grupoSanguineo(p.getGrupoSanguineo()).nacionalidad(p.getNacionalidad())
                .ocupacion(p.getOcupacion()).telefonoPrincipal(p.getTelefonoPrincipal())
                .telefonoSecundario(p.getTelefonoSecundario()).correo(p.getCorreo())
                .direccion(p.getDireccion()).ciudad(p.getCiudad())
                .provincia(p.getProvincia()).codigoPostal(p.getCodigoPostal())
                .alergias(p.getAlergias()).enfermedadesCronicas(p.getEnfermedadesCronicas())
                .medicamentosActuales(p.getMedicamentosActuales())
                .historialQuirurgico(p.getHistorialQuirurgico())
                .antecedentesFamiliares(p.getAntecedentesFamiliares())
                .observaciones(p.getObservaciones())
                .nombreEmergencia(p.getNombreEmergencia())
                .telefonoEmergencia(p.getTelefonoEmergencia())
                .parentescoEmergencia(p.getParentescoEmergencia())
                .tieneSeguro(p.isTieneSeguro()).empresaSeguro(p.getEmpresaSeguro())
                .numeroPoliza(p.getNumeroPoliza()).estado(p.getEstado())
                .creadoEn(p.getCreadoEn()).modificadoEn(p.getModificadoEn())
                .build();
    }

    private ResumenPaciente aResumen(Paciente p) {
        return ResumenPaciente.builder()
                .id(p.getId()).codigo(p.getCodigo()).nombreCompleto(p.getNombreCompleto())
                .edad(calcularEdad(p.getFechaNacimiento())).genero(p.getGenero())
                .grupoSanguineo(p.getGrupoSanguineo()).telefonoPrincipal(p.getTelefonoPrincipal())
                .correo(p.getCorreo()).estado(p.getEstado())
                .build();
    }
}
