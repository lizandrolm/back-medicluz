package com.medicluz.infraestructura.configuracion;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "MedicLuz API",
        version     = "1.0.0",
        description = "Sistema de Gestión Médica — API REST",
        contact     = @Contact(name  = "Equipo MedicLuz", email = "dev@medicluz.com"),
        license     = @License(name  = "Propietario")
    ),
    servers  = { @Server(url = "/api", description = "Servidor local") },
    security = @SecurityRequirement(name = "autorizacionBearer")
)
@SecurityScheme(
    name         = "autorizacionBearer",
    description  = "Token JWT. Obtener desde POST /autenticacion/ingresar",
    scheme       = "bearer",
    type         = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in           = SecuritySchemeIn.HEADER
)
public class ConfiguracionOpenApi {
}
