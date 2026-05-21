package com.medicluz.usuario.servicio;

import com.medicluz.comun.excepcion.CorreoYaExisteExcepcion;
import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.rol.entidad.Rol;
import com.medicluz.rol.repositorio.RolRepositorio;
import com.medicluz.usuario.dto.RespuestaUsuario;
import com.medicluz.usuario.dto.SolicitudUsuario;
import com.medicluz.usuario.entidad.Usuario;
import com.medicluz.usuario.repositorio.TokenRefrescoRepositorio;
import com.medicluz.usuario.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final TokenRefrescoRepositorio tokenRefrescoRepositorio;
    private final PasswordEncoder codificadorContrasena;

    @Transactional(readOnly = true)
    public RespuestaPagina<RespuestaUsuario> listar(String busqueda, Boolean activo, int pagina, int tamano) {
        PageRequest paginacion = PageRequest.of(pagina, tamano, Sort.by("apellido", "primerNombre"));
        return RespuestaPagina.de(
                usuarioRepositorio.buscar(
                        (busqueda == null || busqueda.isBlank()) ? null : busqueda.trim(),
                        activo, paginacion
                ).map(this::aRespuesta)
        );
    }

    @Transactional(readOnly = true)
    public RespuestaUsuario buscarPorId(Long id) {
        return aRespuesta(obtenerUsuario(id));
    }

    @Transactional
    public RespuestaUsuario crear(SolicitudUsuario solicitud) {
        if (usuarioRepositorio.existsByCorreo(solicitud.correo())) {
            throw new CorreoYaExisteExcepcion(solicitud.correo());
        }
        if (solicitud.contrasena() == null || solicitud.contrasena().isBlank()) {
            throw new PeticionInvalidaExcepcion("La contraseña es obligatoria al crear un usuario");
        }

        Rol rol = obtenerRol(solicitud.idRol());

        Usuario usuario = Usuario.builder()
                .primerNombre(solicitud.primerNombre())
                .apellido(solicitud.apellido())
                .correo(solicitud.correo())
                .contrasena(codificadorContrasena.encode(solicitud.contrasena()))
                .rol(rol)
                .build();

        return aRespuesta(usuarioRepositorio.save(usuario));
    }

    @Transactional
    public RespuestaUsuario actualizar(Long id, SolicitudUsuario solicitud) {
        Usuario usuario = obtenerUsuario(id);

        if (!usuario.getCorreo().equals(solicitud.correo())
                && usuarioRepositorio.existsByCorreo(solicitud.correo())) {
            throw new CorreoYaExisteExcepcion(solicitud.correo());
        }

        Rol rol = obtenerRol(solicitud.idRol());

        usuario.setPrimerNombre(solicitud.primerNombre());
        usuario.setApellido(solicitud.apellido());
        usuario.setCorreo(solicitud.correo());
        usuario.setRol(rol);

        if (solicitud.contrasena() != null && !solicitud.contrasena().isBlank()) {
            usuario.setContrasena(codificadorContrasena.encode(solicitud.contrasena()));
            tokenRefrescoRepositorio.revocarTodosPorUsuario(usuario);
        }

        return aRespuesta(usuarioRepositorio.save(usuario));
    }

    @Transactional
    public RespuestaUsuario cambiarEstado(Long id) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setActivo(!usuario.isActivo());
        if (!usuario.isActivo()) {
            tokenRefrescoRepositorio.revocarTodosPorUsuario(usuario);
        }
        return aRespuesta(usuarioRepositorio.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerUsuario(id);
        tokenRefrescoRepositorio.eliminarTodosPorUsuario(usuario);
        usuarioRepositorio.delete(usuario);
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario", id));
    }

    private Rol obtenerRol(Long idRol) {
        return rolRepositorio.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol", idRol));
    }

    private RespuestaUsuario aRespuesta(Usuario u) {
        return RespuestaUsuario.builder()
                .id(u.getId())
                .primerNombre(u.getPrimerNombre())
                .apellido(u.getApellido())
                .nombreCompleto(u.getNombreCompleto())
                .correo(u.getCorreo())
                .idRol(u.getRol().getId())
                .nombreRol(u.getRol().getNombre())
                .nombreVisualizacionRol(u.getRol().getNombreVisualizacion())
                .activo(u.isActivo())
                .creadoEn(u.getCreadoEn())
                .modificadoEn(u.getModificadoEn())
                .build();
    }
}
