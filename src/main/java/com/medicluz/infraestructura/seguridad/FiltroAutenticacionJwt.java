package com.medicluz.infraestructura.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FiltroAutenticacionJwt extends OncePerRequestFilter {

    private final ServicioJwt servicioJwt;
    private final UserDetailsService servicioDetallesUsuario;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest solicitud,
            @NonNull HttpServletResponse respuesta,
            @NonNull FilterChain cadenaFiltros
    ) throws ServletException, IOException {

        final String encabezadoAutorizacion = solicitud.getHeader("Authorization");

        if (encabezadoAutorizacion == null || !encabezadoAutorizacion.startsWith("Bearer ")) {
            cadenaFiltros.doFilter(solicitud, respuesta);
            return;
        }

        final String jwt = encabezadoAutorizacion.substring(7);

        try {
            final String correo = servicioJwt.extraerCorreo(jwt);

            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails detallesUsuario = servicioDetallesUsuario.loadUserByUsername(correo);

                if (servicioJwt.esTokenValido(jwt, detallesUsuario)) {
                    UsernamePasswordAuthenticationToken tokenAutenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    detallesUsuario, null, detallesUsuario.getAuthorities()
                            );
                    tokenAutenticacion.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(solicitud)
                    );
                    SecurityContextHolder.getContext().setAuthentication(tokenAutenticacion);
                }
            }
        } catch (Exception ex) {
            log.warn("No se pudo establecer autenticación de usuario: {}", ex.getMessage());
        }

        cadenaFiltros.doFilter(solicitud, respuesta);
    }
}
