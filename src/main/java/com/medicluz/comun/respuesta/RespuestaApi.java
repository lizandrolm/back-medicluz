package com.medicluz.comun.respuesta;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespuestaApi<T> {

    private final boolean exito;
    private final String mensaje;
    private final T datos;
    private final Object errores;

    @Builder.Default
    private final LocalDateTime marca = LocalDateTime.now();

    // ─── Fábrica estática ────────────────────────────────────────────────────

    public static <T> RespuestaApi<T> exitoso(T datos) {
        return RespuestaApi.<T>builder().exito(true).datos(datos).build();
    }

    public static <T> RespuestaApi<T> exitoso(String mensaje, T datos) {
        return RespuestaApi.<T>builder().exito(true).mensaje(mensaje).datos(datos).build();
    }

    public static <T> RespuestaApi<T> creado(String mensaje, T datos) {
        return RespuestaApi.<T>builder().exito(true).mensaje(mensaje).datos(datos).build();
    }

    public static <T> RespuestaApi<T> error(String mensaje) {
        return RespuestaApi.<T>builder().exito(false).mensaje(mensaje).build();
    }

    public static <T> RespuestaApi<T> error(String mensaje, Object errores) {
        return RespuestaApi.<T>builder().exito(false).mensaje(mensaje).errores(errores).build();
    }
}
