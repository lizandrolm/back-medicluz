package com.medicluz.infraestructura.seguridad;

import com.medicluz.entidad.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${app.jwt.secreto}")
    private String secreto;

    @Value("${app.jwt.expiracion-acceso-ms}")
    private long expiracionAccesoMs;

    public String generarTokenAcceso(UserDetails detallesUsuario) {
        Usuario usuario = (Usuario) detallesUsuario;
        return construirToken(
                Map.of("rol", usuario.getRol().getNombre(),
                       "idRol", usuario.getRol().getId(),
                       "nombre", usuario.getNombreCompleto()),
                detallesUsuario.getUsername(),
                expiracionAccesoMs
        );
    }

    private String construirToken(Map<String, Object> reclamacionesExtra, String sujeto, long expiracionMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .claims(reclamacionesExtra)
                .subject(sujeto)
                .id(UUID.randomUUID().toString())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(obtenerClaveSecreta())
                .compact();
    }

    public boolean esTokenValido(String token, UserDetails detallesUsuario) {
        try {
            return extraerCorreo(token).equals(detallesUsuario.getUsername()) && !estaExpirado(token);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Token JWT inválido: {}", ex.getMessage());
            return false;
        }
    }

    public boolean estaExpirado(String token)                              { return extraerExpiracion(token).before(new Date()); }
    public String  extraerCorreo(String token)                             { return extraerReclamacion(token, Claims::getSubject); }
    public Date    extraerExpiracion(String token)                         { return extraerReclamacion(token, Claims::getExpiration); }

    public <T> T extraerReclamacion(String token, Function<Claims, T> resolutor) {
        return resolutor.apply(extraerTodasReclamaciones(token));
    }

    private Claims extraerTodasReclamaciones(String token) {
        return Jwts.parser().verifyWith(obtenerClaveSecreta()).build()
                .parseSignedClaims(token).getPayload();
    }

    private SecretKey obtenerClaveSecreta() {
        return Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }
}
