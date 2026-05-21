package com.medicluz.service.impl;

import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaPaciente;
import com.medicluz.dto.ResumenPaciente;
import com.medicluz.dto.SolicitudPaciente;
import com.medicluz.entidad.Paciente;
import com.medicluz.enums.EstadoPaciente;
import com.medicluz.repository.PacienteRepository;
import com.medicluz.service.PacienteService;
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
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    @Override @Transactional(readOnly = true)
    public RespuestaPagina<ResumenPaciente> listar(String busqueda, EstadoPaciente estado, int pagina, int tamano) {
        PageRequest paginacion = PageRequest.of(pagina, tamano, Sort.by("apellido", "primerNombre"));
        return RespuestaPagina.de(
                pacienteRepository.buscar(
                        (busqueda == null || busqueda.isBlank()) ? null : busqueda.trim(),
                        estado, paginacion
                ).map(this::aResumen)
        );
    }

    @Override @Transactional(readOnly = true)
    public RespuestaPaciente buscarPorId(Long id) {
        return aRespuesta(obtenerPaciente(id));
    }

    @Override @Transactional(readOnly = true)
    public RespuestaPaciente buscarPorCodigo(String codigo) {
        return aRespuesta(pacienteRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente", "código", codigo)));
    }

    @Override @Transactional
    public RespuestaPaciente crear(SolicitudPaciente solicitud) {
        if (pacienteRepository.existsByTipoDocumentoAndNumeroDocumento(
                solicitud.tipoDocumento(), solicitud.numeroDocumento())) {
            throw new PeticionInvalidaExcepcion("Ya existe un paciente con ese documento");
        }
        Paciente paciente = mapear(new Paciente(), solicitud);
        paciente.setCodigo(generarCodigo());
        return aRespuesta(pacienteRepository.save(paciente));
    }

    @Override @Transactional
    public RespuestaPaciente actualizar(Long id, SolicitudPaciente solicitud) {
        Paciente paciente = obtenerPaciente(id);
        if (!paciente.getNumeroDocumento().equals(solicitud.numeroDocumento())
                || paciente.getTipoDocumento() != solicitud.tipoDocumento()) {
            if (pacienteRepository.existsByTipoDocumentoAndNumeroDocumento(
                    solicitud.tipoDocumento(), solicitud.numeroDocumento())) {
                throw new PeticionInvalidaExcepcion("Otro paciente ya tiene ese documento registrado");
            }
        }
        return aRespuesta(pacienteRepository.save(mapear(paciente, solicitud)));
    }

    @Override @Transactional
    public RespuestaPaciente cambiarEstado(Long id, EstadoPaciente estado) {
        Paciente paciente = obtenerPaciente(id);
        paciente.setEstado(estado);
        return aRespuesta(pacienteRepository.save(paciente));
    }

    @Override @Transactional
    public void eliminar(Long id) {
        pacienteRepository.delete(obtenerPaciente(id));
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private Paciente obtenerPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Paciente", id));
    }

    private Paciente mapear(Paciente p, SolicitudPaciente s) {
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
        } while (pacienteRepository.findByCodigo(codigo).isPresent());
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
                .fechaRegistro(p.getFechaRegistro()).fechaActualiza(p.getFechaActualiza())
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
