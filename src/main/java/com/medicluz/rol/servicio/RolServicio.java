package com.medicluz.rol.servicio;

import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.rol.dto.*;
import com.medicluz.rol.entidad.Opcion;
import com.medicluz.rol.entidad.Rol;
import com.medicluz.rol.entidad.RolOpcion;
import com.medicluz.rol.repositorio.OpcionRepositorio;
import com.medicluz.rol.repositorio.RolOpcionRepositorio;
import com.medicluz.rol.repositorio.RolRepositorio;
import com.medicluz.usuario.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServicio {

    private final RolRepositorio rolRepositorio;
    private final OpcionRepositorio opcionRepositorio;
    private final RolOpcionRepositorio rolOpcionRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    // ─── CRUD Rol ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RespuestaRol> listar() {
        return rolRepositorio.findAll().stream()
                .map(r -> aRespuesta(r, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public RespuestaRol buscarPorId(Long id) {
        Rol rol = rolRepositorio.buscarPorIdConOpciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol", id));
        return aRespuesta(rol, true);
    }

    @Transactional
    public RespuestaRol crear(SolicitudRol solicitud) {
        if (rolRepositorio.existsByNombre(solicitud.nombre())) {
            throw new PeticionInvalidaExcepcion("Ya existe un rol con nombre: " + solicitud.nombre());
        }
        Rol rol = Rol.builder()
                .nombre(solicitud.nombre())
                .nombreVisualizacion(solicitud.nombreVisualizacion())
                .descripcion(solicitud.descripcion())
                .build();
        return aRespuesta(rolRepositorio.save(rol), false);
    }

    @Transactional
    public RespuestaRol actualizar(Long id, SolicitudRol solicitud) {
        Rol rol = obtenerRol(id);
        if (!rol.getNombre().equals(solicitud.nombre())
                && rolRepositorio.existsByNombre(solicitud.nombre())) {
            throw new PeticionInvalidaExcepcion("Ya existe un rol con nombre: " + solicitud.nombre());
        }
        rol.setNombre(solicitud.nombre());
        rol.setNombreVisualizacion(solicitud.nombreVisualizacion());
        rol.setDescripcion(solicitud.descripcion());
        return aRespuesta(rolRepositorio.save(rol), false);
    }

    @Transactional
    public RespuestaRol cambiarEstado(Long id) {
        Rol rol = obtenerRol(id);
        rol.setActivo(!rol.isActivo());
        return aRespuesta(rolRepositorio.save(rol), false);
    }

    @Transactional
    public void eliminar(Long id) {
        Rol rol = obtenerRol(id);
        long usuariosConRol = usuarioRepositorio.countByRolId(id);
        if (usuariosConRol > 0) {
            throw new PeticionInvalidaExcepcion(
                    "No se puede eliminar el rol. " + usuariosConRol + " usuario(s) lo tienen asignado.");
        }
        rolRepositorio.delete(rol);
    }

    // ─── Permisos ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RespuestaPermiso> obtenerPermisos(Long idRol) {
        obtenerRol(idRol);
        return rolOpcionRepositorio.findByRolId(idRol).stream()
                .map(this::aRespuestaPermiso).toList();
    }

    @Transactional
    public List<RespuestaPermiso> asignarPermisos(Long idRol, SolicitudAsignarPermisos solicitud) {
        Rol rol = obtenerRol(idRol);
        rolOpcionRepositorio.eliminarTodosPorRol(idRol);

        List<RolOpcion> permisos = solicitud.permisos().stream()
                .filter(SolicitudAsignarPermisos.EntradaPermiso::puedeVer)
                .map(entrada -> {
                    Opcion opcion = opcionRepositorio.findById(entrada.idOpcion())
                            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Opción", entrada.idOpcion()));
                    return RolOpcion.builder()
                            .rol(rol)
                            .opcion(opcion)
                            .puedeVer(entrada.puedeVer())
                            .puedeCrear(entrada.puedeCrear())
                            .puedeEditar(entrada.puedeEditar())
                            .puedeEliminar(entrada.puedeEliminar())
                            .build();
                }).toList();

        rolOpcionRepositorio.saveAll(permisos);
        return rolOpcionRepositorio.findByRolId(idRol).stream()
                .map(this::aRespuestaPermiso).toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaPermiso> obtenerMenuPorRol(Long idRol) {
        return rolOpcionRepositorio.buscarMenuVisiblePorRol(idRol).stream()
                .map(this::aRespuestaPermiso).toList();
    }

    // ─── Conversores ─────────────────────────────────────────────────────────

    private RespuestaRol aRespuesta(Rol rol, boolean conPermisos) {
        return RespuestaRol.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .nombreVisualizacion(rol.getNombreVisualizacion())
                .descripcion(rol.getDescripcion())
                .activo(rol.isActivo())
                .cantidadUsuarios((int) usuarioRepositorio.countByRolId(rol.getId()))
                .permisos(conPermisos
                        ? rol.getRolesOpciones().stream().map(this::aRespuestaPermiso).toList()
                        : List.of())
                .creadoEn(rol.getCreadoEn())
                .modificadoEn(rol.getModificadoEn())
                .build();
    }

    private RespuestaPermiso aRespuestaPermiso(RolOpcion ro) {
        Opcion op = ro.getOpcion();
        return RespuestaPermiso.builder()
                .idRolOpcion(ro.getId())
                .idOpcion(op.getId())
                .codigoOpcion(op.getCodigo())
                .nombreOpcion(op.getNombre())
                .iconoOpcion(op.getIcono())
                .rutaOpcion(op.getRuta())
                .puedeVer(ro.isPuedeVer())
                .puedeCrear(ro.isPuedeCrear())
                .puedeEditar(ro.isPuedeEditar())
                .puedeEliminar(ro.isPuedeEliminar())
                .build();
    }

    private Rol obtenerRol(Long id) {
        return rolRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol", id));
    }
}
