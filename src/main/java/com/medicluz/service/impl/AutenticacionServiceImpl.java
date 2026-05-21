package com.medicluz.service.impl;

import com.medicluz.comun.excepcion.*;
import com.medicluz.dto.*;
import com.medicluz.entidad.Rol;
import com.medicluz.entidad.TokenRefresco;
import com.medicluz.entidad.Usuario;
import com.medicluz.infraestructura.seguridad.JwtService;
import com.medicluz.repository.RolRepository;
import com.medicluz.repository.TokenRefrescoRepository;
import com.medicluz.repository.UsuarioRepository;
import com.medicluz.service.AutenticacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutenticacionServiceImpl implements AutenticacionService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRefrescoRepository tokenRefrescoRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder codificadorContrasena;
    private final JwtService jwtService;
    private final AuthenticationManager gestorAutenticacion;

    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final int MINUTOS_BLOQUEO = 15;

    @Value("${app.jwt.expiracion-acceso-ms}")  private long expiracionAccesoMs;
    @Value("${app.jwt.expiracion-refresco-ms}") private long expiracionRefrescoMs;

    @Override
    @Transactional
    public RespuestaAutenticacion registrar(SolicitudRegistro solicitud) {
        if (usuarioRepository.existsByCorreo(solicitud.correo())) {
            throw new CorreoYaExisteExcepcion(solicitud.correo());
        }
        Rol rol = rolRepository.findById(solicitud.idRol())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol", solicitud.idRol()));

        Usuario usuario = Usuario.builder()
                .primerNombre(solicitud.primerNombre())
                .apellido(solicitud.apellido())
                .correo(solicitud.correo())
                .contrasena(codificadorContrasena.encode(solicitud.contrasena()))
                .rol(rol)
                .build();
        usuarioRepository.save(usuario);
        log.info("Nuevo usuario registrado: {}", solicitud.correo());
        return construirRespuesta(usuario);
    }

    @Override
    @Transactional
    public RespuestaAutenticacion ingresar(SolicitudLogin solicitud) {
        Usuario usuario = usuarioRepository.findByCorreo(solicitud.correo())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (usuario.estaBloqueado()) {
            throw new CuentaBloqueadaExcepcion(
                "Cuenta bloqueada hasta " + usuario.getBloqueadoHasta() + ". Demasiados intentos fallidos.");
        }

        try {
            gestorAutenticacion.authenticate(
                    new UsernamePasswordAuthenticationToken(solicitud.correo(), solicitud.contrasena()));
        } catch (BadCredentialsException ex) {
            registrarIntentoFallido(usuario);
            throw ex;
        }

        if (usuario.getIntentosFallidos() > 0) {
            usuario.setIntentosFallidos(0);
            usuario.setBloqueadoHasta(null);
            usuarioRepository.save(usuario);
        }

        tokenRefrescoRepository.revocarTodosPorUsuario(usuario);
        log.info("Ingreso exitoso: {}", solicitud.correo());
        return construirRespuesta(usuario);
    }

    @Override
    @Transactional
    public RespuestaAutenticacion renovarToken(SolicitudTokenRefresco solicitud) {
        String hashEntrante = calcularHash(solicitud.tokenRefresco());
        TokenRefresco almacenado = tokenRefrescoRepository.findByTokenHash(hashEntrante)
                .orElseThrow(() -> new PeticionInvalidaExcepcion("Token de refresco no encontrado"));

        if (!almacenado.esValido()) {
            if (almacenado.isRevocado()) {
                tokenRefrescoRepository.revocarTodosPorUsuario(almacenado.getUsuario());
                log.warn("Posible robo de token para usuario: {}", almacenado.getUsuario().getCorreo());
                throw new PeticionInvalidaExcepcion("Token revocado. Todos los tokens han sido invalidados por seguridad.");
            }
            throw new PeticionInvalidaExcepcion("Token de refresco expirado. Inicie sesión de nuevo.");
        }

        almacenado.setRevocado(true);
        tokenRefrescoRepository.save(almacenado);
        return construirRespuesta(almacenado.getUsuario());
    }

    @Override
    @Transactional
    public void salir(String correo) {
        usuarioRepository.findByCorreo(correo)
                .ifPresent(tokenRefrescoRepository::revocarTodosPorUsuario);
        log.info("Sesión cerrada: {}", correo);
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private RespuestaAutenticacion construirRespuesta(Usuario usuario) {
        Rol rol = usuario.getRol();
        return RespuestaAutenticacion.porDefecto()
                .tokenAcceso(jwtService.generarTokenAcceso(usuario))
                .tokenRefresco(crearTokenRefresco(usuario))
                .expiraEn(expiracionAccesoMs / 1000)
                .idUsuario(usuario.getId())
                .nombreCompleto(usuario.getNombreCompleto())
                .correo(usuario.getCorreo())
                .idRol(rol.getId())
                .nombreRol(rol.getNombre())
                .nombreVisualizacionRol(rol.getNombreVisualizacion())
                .build();
    }

    private String crearTokenRefresco(Usuario usuario) {
        String tokenRaw  = UUID.randomUUID().toString();
        tokenRefrescoRepository.save(TokenRefresco.builder()
                .tokenHash(calcularHash(tokenRaw))
                .usuario(usuario)
                .fechaExpiracion(Instant.now().plusMillis(expiracionRefrescoMs))
                .build());
        return tokenRaw;
    }

    private void registrarIntentoFallido(Usuario usuario) {
        int intentos = usuario.getIntentosFallidos() + 1;
        usuario.setIntentosFallidos(intentos);
        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
            log.warn("Cuenta bloqueada {} min: {}", MINUTOS_BLOQUEO, usuario.getCorreo());
        }
        usuarioRepository.save(usuario);
    }

    private String calcularHash(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible", ex);
        }
    }
}
