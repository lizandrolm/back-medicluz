package com.medicluz.infraestructura.auditoria;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provee el usuario actual al mecanismo de auditoría de Spring Data JPA.
 * Rellena automáticamente los campos "creado_por" y "modificado_por" en EntidadBase.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacion == null || !autenticacion.isAuthenticated()
                || "anonymousUser".equals(autenticacion.getPrincipal())) {
            return Optional.of("sistema");
        }

        return Optional.of(autenticacion.getName());
    }
}
