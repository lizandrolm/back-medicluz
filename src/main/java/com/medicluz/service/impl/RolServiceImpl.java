package com.medicluz.service.impl;

import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.dto.*;
import com.medicluz.entidad.Opcion;
import com.medicluz.entidad.Rol;
import com.medicluz.entidad.RolOpcion;
import com.medicluz.repository.OpcionRepository;
import com.medicluz.repository.RolOpcionRepository;
import com.medicluz.repository.RolRepository;
import com.medicluz.repository.UsuarioRepository;
import com.medicluz.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final OpcionRepository opcionRepository;
    private final RolOpcionRepository rolOpcionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override @Transactional(readOnly = true)
    public List<RespuestaRol> listar() {
        return rolRepository.findAll().stream().map(r -> aRespuesta(r, false)).toList();
    }

    @Override @Transactional(readOnly = true)
    public RespuestaRol buscarPorId(Long id) {
        Rol rol = rolRepository.buscarPorIdConOpciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol", id));
        return aRespuesta(rol, true);
    }

    @Override @Transactional
    public RespuestaRol crear(SolicitudRol solicitud) {
        if (rolRepository.existsByNombre(solicitud.nombre())) {
            throw new PeticionInvalidaExcepcion("Ya existe un rol con nombre: " + solicitud.nombre());
        }
        return aRespuesta(rolRepository.save(Rol.builder()
                .nombre(solicitud.nombre())
                .nombreVisualizacion(solicitud.nombreVisualizacion())
                .descripcion(solicitud.descripcion())
                .build()), false);
    }

    @Override @Transactional
    public RespuestaRol actualizar(Long id, SolicitudRol solicitud) {
        Rol rol = obtenerRol(id);
        if (!rol.getNombre().equals(solicitud.nombre()) && rolRepository.existsByNombre(solicitud.nombre())) {
            throw new PeticionInvalidaExcepcion("Ya existe un rol con nombre: " + solicitud.nombre());
        }
        rol.setNombre(solicitud.nombre());
        rol.setNombreVisualizacion(solicitud.nombreVisualizacion());
        rol.setDescripcion(solicitud.descripcion());
        return aRespuesta(rolRepository.save(rol), false);
    }

    @Override @Transactional
    public RespuestaRol cambiarEstado(Long id) {
        Rol rol = obtenerRol(id);
        rol.setActivo(!rol.isActivo());
        return aRespuesta(rolRepository.save(rol), false);
    }

    @Override @Transactional
    public void eliminar(Long id) {
        Rol rol = obtenerRol(id);
        long usuarios = usuarioRepository.countByRolId(id);
        if (usuarios > 0) {
            throw new PeticionInvalidaExcepcion(
                "No se puede eliminar el rol. " + usuarios + " usuario(s) lo tienen asignado.");
        }
        rolRepository.delete(rol);
    }

    @Override @Transactional(readOnly = true)
    public List<RespuestaPermiso> obtenerPermisos(Long idRol) {
        obtenerRol(idRol);
        return rolOpcionRepository.findByRolId(idRol).stream().map(this::aRespuestaPermiso).toList();
    }

    @Override @Transactional
    public List<RespuestaPermiso> asignarPermisos(Long idRol, SolicitudAsignarPermisos solicitud) {
        Rol rol = obtenerRol(idRol);
        rolOpcionRepository.eliminarTodosPorRol(idRol);
        List<RolOpcion> permisos = solicitud.permisos().stream()
                .filter(SolicitudAsignarPermisos.EntradaPermiso::puedeVer)
                .map(entrada -> {
                    Opcion opcion = opcionRepository.findById(entrada.idOpcion())
                            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Opción", entrada.idOpcion()));
                    return RolOpcion.builder()
                            .rol(rol).opcion(opcion)
                            .puedeVer(entrada.puedeVer()).puedeCrear(entrada.puedeCrear())
                            .puedeEditar(entrada.puedeEditar()).puedeEliminar(entrada.puedeEliminar())
                            .build();
                }).toList();
        rolOpcionRepository.saveAll(permisos);
        return rolOpcionRepository.findByRolId(idRol).stream().map(this::aRespuestaPermiso).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<RespuestaPermiso> obtenerMenuPorRol(Long idRol) {
        return rolOpcionRepository.buscarMenuVisiblePorRol(idRol).stream().map(this::aRespuestaPermiso).toList();
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private RespuestaRol aRespuesta(Rol rol, boolean conPermisos) {
        return RespuestaRol.builder()
                .id(rol.getId()).nombre(rol.getNombre())
                .nombreVisualizacion(rol.getNombreVisualizacion())
                .descripcion(rol.getDescripcion()).activo(rol.isActivo())
                .cantidadUsuarios((int) usuarioRepository.countByRolId(rol.getId()))
                .permisos(conPermisos
                        ? rol.getRolesOpciones().stream().map(this::aRespuestaPermiso).toList()
                        : List.of())
                .fechaRegistro(rol.getFechaRegistro()).fechaActualiza(rol.getFechaActualiza())
                .build();
    }

    private RespuestaPermiso aRespuestaPermiso(RolOpcion ro) {
        Opcion op = ro.getOpcion();
        return RespuestaPermiso.builder()
                .idRolOpcion(ro.getId()).idOpcion(op.getId())
                .codigoOpcion(op.getCodigo()).nombreOpcion(op.getNombre())
                .iconoOpcion(op.getIcono()).rutaOpcion(op.getRuta())
                .puedeVer(ro.isPuedeVer()).puedeCrear(ro.isPuedeCrear())
                .puedeEditar(ro.isPuedeEditar()).puedeEliminar(ro.isPuedeEliminar())
                .build();
    }

    private Rol obtenerRol(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol", id));
    }
}
