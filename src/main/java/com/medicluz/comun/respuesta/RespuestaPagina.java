package com.medicluz.comun.respuesta;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class RespuestaPagina<T> {

    private final List<T> contenido;
    private final int pagina;
    private final int tamano;
    private final long totalElementos;
    private final int totalPaginas;
    private final boolean esUltima;

    public static <T> RespuestaPagina<T> de(Page<T> pagina) {
        return RespuestaPagina.<T>builder()
                .contenido(pagina.getContent())
                .pagina(pagina.getNumber())
                .tamano(pagina.getSize())
                .totalElementos(pagina.getTotalElements())
                .totalPaginas(pagina.getTotalPages())
                .esUltima(pagina.isLast())
                .build();
    }
}
