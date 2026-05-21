package com.medicluz.service.impl;

import com.medicluz.comun.excepcion.CorreoYaExisteExcepcion;
import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.comun.respuesta.RespuestaPagina;
import com.medicluz.dto.RespuestaUsuario;
import com.medicluz.dto.SolicitudUsuario;
import com.medicluz.entidad.Rol;
import com.medicluz.entidad.Usuario;
import com.medicluz.repository.RolRepository;
import com.medicluz.repository.TokenRefrescoRepository;
import com.medicluz.repository.UsuarioRepository;
import com.medicluz.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TokenRefrescoRepository tokenRefrescoRepository;
    private final PasswordEncoder codificadorContrasena;

    @Override @Transactional(readOnly = true)
    public RespuestaPagina<RespuestaUsuario> listar(String busqueda, Boolean activo, int pagina, int tamano) {
        PageRequest paginacion = PageRequest.of(pagina, tamano, Sort.by("apellido", "primerNombre"));
        return RespuestaPagina.de(
                usuarioRepository.buscar(
                        (busqueda == null || busqueda.isBlank()) ? null : busqueda.trim(),
                        activo, paginacion
                ).map(this::aRespuesta)
        );
    }

    @Override @Transactional(readOnly = true)
    public RespuestaUsuario buscarPorId(Long id) {
        return aRespuesta(obtenerUsuario(id));
    }

    @Override @Transactional
    public RespuestaUsuario crear(SolicitudUsuario solicitud) {
        if (usuarioRepository.existsByCorreo(solicitud.correo())) {
            throw new CorreoYaExisteExcepcion(solicitud.correo());
        }
        if (solicitud.contrasena() == null || solicitud.contrasena().isBlank()) {
            throw new PeticionInvalidaExcepcion("La contraseña es obligatoria al crear un usuario");
        }
        return aRespuesta(usuarioRepository.save(Usuario.builder()
                .primerNombre(solicitud.primerNombre())
                .apellido(solicitud.apellido())
                .correo(solicitud.correo())
                .contrasena(codificadorContrasena.encode(solicitud.contrasena()))
                .rol(obtenerRol(solicitud.idRol()))
                .build()));
    }

    @Override @Transactional
    public RespuestaUsuario actualizar(Long id, SolicitudUsuario solicitud) {
        Usuario usuario = obtenerUsuario(id);
        if (!usuario.getCorreo().equals(solicitud.correo())
                && usuarioRepository.existsByCorreo(solicitud.correo())) {
            throw new CorreoYaExisteExcepcion(solicitud.correo());
        }
        usuario.setPrimerNombre(solicitud.primerNombre());
        usuario.setApellido(solicitud.apellido());
        usuario.setCorreo(solicitud.correo());
        usuario.setRol(obtenerRol(solicitud.idRol()));
        if (solicitud.contrasena() != null && !solicitud.contrasena().isBlank()) {
            usuario.setContrasena(codificadorContrasena.encode(solicitud.contrasena()));
            tokenRefrescoRepository.revocarTodosPorUsuario(usuario);
        }
        return aRespuesta(usuarioRepository.save(usuario));
    }

    @Override @Transactional
    public RespuestaUsuario cambiarEstado(Long id) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setActivo(!usuario.isActivo());
        if (!usuario.isActivo()) tokenRefrescoRepository.revocarTodosPorUsuario(usuario);
        return aRespuesta(usuarioRepository.save(usuario));
    }

    @Override @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerUsuario(id);
        tokenRefrescoRepository.eliminarTodosPorUsuario(usuario);
        usuarioRepository.delete(usuario);
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario", id));
    }

    private Rol obtenerRol(Long idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol", idRol));
    }

    private RespuestaUsuario aRespuesta(Usuario u) {
        return RespuestaUsuario.builder()
                .id(u.getId()).primerNombre(u.getPrimerNombre()).apellido(u.getApellido())
                .nombreCompleto(u.getNombreCompleto()).correo(u.getCorreo())
                .idRol(u.getRol().getId()).nombreRol(u.getRol().getNombre())
                .nombreVisualizacionRol(u.getRol().getNombreVisualizacion())
                .activo(u.isActivo())
                .fechaRegistro(u.getFechaRegistro()).fechaActualiza(u.getFechaActualiza())
                .build();
    }
}
