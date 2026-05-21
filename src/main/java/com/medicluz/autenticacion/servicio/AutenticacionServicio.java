package com.medicluz.autenticacion.servicio;

import com.medicluz.autenticacion.dto.*;
import com.medicluz.comun.excepcion.*;
import com.medicluz.infraestructura.seguridad.ServicioJwt;
import com.medicluz.rol.entidad.Rol;
import com.medicluz.rol.repositorio.RolRepositorio;
import com.medicluz.usuario.entidad.TokenRefresco;
import com.medicluz.usuario.entidad.Usuario;
import com.medicluz.usuario.repositorio.TokenRefrescoRepositorio;
import com.medicluz.usuario.repositorio.UsuarioRepositorio;
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
public class AutenticacionServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final TokenRefrescoRepositorio tokenRefrescoRepositorio;
    private final RolRepositorio rolRepositorio;
    private final PasswordEncoder codificadorContrasena;
    private final ServicioJwt servicioJwt;
    private final AuthenticationManager gestorAutenticacion;

    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final int MINUTOS_BLOQUEO = 15;

    @Value("${app.jwt.expiracion-acceso-ms}")
    private long expiracionAccesoMs;

    @Value("${app.jwt.expiracion-refresco-ms}")
    private long expiracionRefrescoMs;

    // ─── Registro ────────────────────────────────────────────────────────────

    @Transactional
    public RespuestaAutenticacion registrar(SolicitudRegistro solicitud) {
        if (usuarioRepositorio.existsByCorreo(solicitud.correo())) {
            throw new CorreoYaExisteExcepcion(solicitud.correo());
        }

        Rol rol = rolRepositorio.findById(solicitud.idRol())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Rol", solicitud.idRol()));

        Usuario usuario = Usuario.builder()
                .primerNombre(solicitud.primerNombre())
                .apellido(solicitud.apellido())
                .correo(solicitud.correo())
                .contrasena(codificadorContrasena.encode(solicitud.contrasena()))
                .rol(rol)
                .build();

        usuarioRepositorio.save(usuario);
        log.info("Nuevo usuario registrado: {}", solicitud.correo());
        return construirRespuestaAutenticacion(usuario);
    }

    // ─── Ingreso ─────────────────────────────────────────────────────────────

    @Transactional
    public RespuestaAutenticacion ingresar(SolicitudLogin solicitud) {
        Usuario usuario = usuarioRepositorio.findByCorreo(solicitud.correo())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        // Verificar bloqueo ANTES de intentar autenticar
        if (usuario.estaBloqueado()) {
            throw new CuentaBloqueadaExcepcion(
                "Cuenta bloqueada hasta " + usuario.getBloqueadoHasta() +
                ". Demasiados intentos fallidos.");
        }

        try {
            gestorAutenticacion.authenticate(
                    new UsernamePasswordAuthenticationToken(solicitud.correo(), solicitud.contrasena())
            );
        } catch (BadCredentialsException ex) {
            registrarIntentoFallido(usuario);
            throw ex;
        }

        // Ingreso exitoso: limpiar contador
        if (usuario.getIntentosFallidos() > 0) {
            usuario.setIntentosFallidos(0);
            usuario.setBloqueadoHasta(null);
            usuarioRepositorio.save(usuario);
        }

        tokenRefrescoRepositorio.revocarTodosPorUsuario(usuario);
        log.info("Ingreso exitoso: {}", solicitud.correo());
        return construirRespuestaAutenticacion(usuario);
    }

    // ─── Renovar token ───────────────────────────────────────────────────────

    @Transactional
    public RespuestaAutenticacion renovarToken(SolicitudTokenRefresco solicitud) {
        String hashEntrante = calcularHash(solicitud.tokenRefresco());

        TokenRefresco almacenado = tokenRefrescoRepositorio.findByTokenHash(hashEntrante)
                .orElseThrow(() -> new PeticionInvalidaExcepcion("Token de refresco no encontrado"));

        if (!almacenado.esValido()) {
            if (almacenado.isRevocado()) {
                // Posible robo de token — revocar TODOS los del usuario
                tokenRefrescoRepositorio.revocarTodosPorUsuario(almacenado.getUsuario());
                log.warn("Posible robo de token para usuario: {}", almacenado.getUsuario().getCorreo());
                throw new PeticionInvalidaExcepcion("Token revocado. Todos los tokens han sido invalidados por seguridad.");
            }
            throw new PeticionInvalidaExcepcion("Token de refresco expirado. Inicie sesión de nuevo.");
        }

        // Rotación: revocar el actual y emitir nuevos tokens
        almacenado.setRevocado(true);
        tokenRefrescoRepositorio.save(almacenado);

        return construirRespuestaAutenticacion(almacenado.getUsuario());
    }

    // ─── Salir ───────────────────────────────────────────────────────────────

    @Transactional
    public void salir(String correo) {
        usuarioRepositorio.findByCorreo(correo)
                .ifPresent(tokenRefrescoRepositorio::revocarTodosPorUsuario);
        log.info("Sesión cerrada: {}", correo);
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private RespuestaAutenticacion construirRespuestaAutenticacion(Usuario usuario) {
        String tokenAcceso   = servicioJwt.generarTokenAcceso(usuario);
        String tokenRefresco = crearTokenRefresco(usuario);
        Rol rol = usuario.getRol();

        return RespuestaAutenticacion.porDefecto()
                .tokenAcceso(tokenAcceso)
                .tokenRefresco(tokenRefresco)
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
        String tokenHash = calcularHash(tokenRaw);

        tokenRefrescoRepositorio.save(TokenRefresco.builder()
                .tokenHash(tokenHash)
                .usuario(usuario)
                .fechaExpiracion(Instant.now().plusMillis(expiracionRefrescoMs))
                .build());

        return tokenRaw;   // Solo el raw se envía al cliente, NUNCA el hash
    }

    private void registrarIntentoFallido(Usuario usuario) {
        int intentos = usuario.getIntentosFallidos() + 1;
        usuario.setIntentosFallidos(intentos);

        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
            log.warn("Cuenta bloqueada por {} minutos: {}", MINUTOS_BLOQUEO, usuario.getCorreo());
        }

        usuarioRepositorio.save(usuario);
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
