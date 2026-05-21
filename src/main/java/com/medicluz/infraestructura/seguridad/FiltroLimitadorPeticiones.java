package com.medicluz.infraestructura.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate-limiter de ventana deslizante simple (en memoria).
 * Para producción con múltiples instancias, reemplazar con Redis + Bucket4j.
 *
 * Límites por IP:
 *   - Rutas de autenticación (/autenticacion/**): 10 req / minuto
 *   - Resto de la API:                           120 req / minuto
 */
@Slf4j
@Component
public class FiltroLimitadorPeticiones extends OncePerRequestFilter {

    @Value("${app.limite.autenticacion:10}")
    private int limiteAutenticacion;

    @Value("${app.limite.general:120}")
    private int limiteGeneral;

    private static final long VENTANA_MS = 60_000L;

    private record Contador(AtomicInteger cuenta, long inicio) {}

    private final ConcurrentHashMap<String, Contador> contadores = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest solicitud,
            @NonNull HttpServletResponse respuesta,
            @NonNull FilterChain cadenaFiltros
    ) throws ServletException, IOException {

        String ip  = obtenerIpCliente(solicitud);
        String uri = solicitud.getRequestURI();
        int limite = uri.contains("/autenticacion/") ? limiteAutenticacion : limiteGeneral;

        if (excedeLimite(ip + ":" + uri.split("/")[2], limite)) {
            respuesta.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
            respuesta.getWriter().write(
                    """
                    {"exito":false,"mensaje":"Demasiadas peticiones. Intente en un momento."}
                    """);
            log.warn("Rate limit alcanzado para IP: {}", ip);
            return;
        }

        cadenaFiltros.doFilter(solicitud, respuesta);
    }

    private boolean excedeLimite(String clave, int limite) {
        long ahora = System.currentTimeMillis();
        contadores.compute(clave, (k, actual) -> {
            if (actual == null || (ahora - actual.inicio()) > VENTANA_MS) {
                return new Contador(new AtomicInteger(1), ahora);
            }
            actual.cuenta().incrementAndGet();
            return actual;
        });
        Contador c = contadores.get(clave);
        return c != null && c.cuenta().get() > limite;
    }

    private String obtenerIpCliente(HttpServletRequest solicitud) {
        String forwarded = solicitud.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return solicitud.getRemoteAddr();
    }
}
