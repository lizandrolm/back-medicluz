package com.medicluz.service.impl;

import com.medicluz.comun.excepcion.PeticionInvalidaExcepcion;
import com.medicluz.comun.excepcion.RecursoNoEncontradoExcepcion;
import com.medicluz.dto.RespuestaOpcion;
import com.medicluz.dto.SolicitudOpcion;
import com.medicluz.entidad.Opcion;
import com.medicluz.repository.OpcionRepository;
import com.medicluz.service.OpcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpcionServiceImpl implements OpcionService {

    private final OpcionRepository opcionRepository;

    @Override @Transactional(readOnly = true)
    public List<RespuestaOpcion> obtenerArbol() {
        return opcionRepository.buscarArbol().stream().map(o -> aRespuesta(o, true)).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<RespuestaOpcion> listar() {
        return opcionRepository.findAll().stream().map(o -> aRespuesta(o, false)).toList();
    }

    @Override @Transactional(readOnly = true)
    public RespuestaOpcion buscarPorId(Long id) {
        return aRespuesta(obtenerOpcion(id), true);
    }

    @Override @Transactional
    public RespuestaOpcion crear(SolicitudOpcion solicitud) {
        if (opcionRepository.existsByCodigo(solicitud.codigo())) {
            throw new PeticionInvalidaExcepcion("Ya existe una opción con código: " + solicitud.codigo());
        }
        Opcion opcion = Opcion.builder()
                .codigo(solicitud.codigo()).nombre(solicitud.nombre())
                .icono(solicitud.icono()).ruta(solicitud.ruta())
                .padre(resolverPadre(solicitud.idPadre()))
                .ordenVisualizacion(solicitud.ordenVisualizacion())
                .build();
        return aRespuesta(opcionRepository.save(opcion), false);
    }

    @Override @Transactional
    public RespuestaOpcion actualizar(Long id, SolicitudOpcion solicitud) {
        Opcion opcion = obtenerOpcion(id);
        if (!opcion.getCodigo().equals(solicitud.codigo())
                && opcionRepository.existsByCodigo(solicitud.codigo())) {
            throw new PeticionInvalidaExcepcion("Ya existe una opción con código: " + solicitud.codigo());
        }
        opcion.setCodigo(solicitud.codigo()); opcion.setNombre(solicitud.nombre());
        opcion.setIcono(solicitud.icono()); opcion.setRuta(solicitud.ruta());
        opcion.setPadre(resolverPadre(solicitud.idPadre()));
        opcion.setOrdenVisualizacion(solicitud.ordenVisualizacion());
        return aRespuesta(opcionRepository.save(opcion), true);
    }

    @Override @Transactional
    public RespuestaOpcion cambiarEstado(Long id) {
        Opcion opcion = obtenerOpcion(id);
        opcion.setActivo(!opcion.isActivo());
        opcion.getHijos().forEach(h -> h.setActivo(opcion.isActivo()));
        return aRespuesta(opcionRepository.save(opcion), true);
    }

    @Override @Transactional
    public void eliminar(Long id) {
        Opcion opcion = obtenerOpcion(id);
        if (!opcion.getHijos().isEmpty()) {
            throw new PeticionInvalidaExcepcion("No se puede eliminar una opción con hijos. Elimine los hijos primero.");
        }
        if (!opcion.getRolesOpciones().isEmpty()) {
            throw new PeticionInvalidaExcepcion("No se puede eliminar una opción asignada a roles.");
        }
        opcionRepository.delete(opcion);
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    private Opcion resolverPadre(Long idPadre) {
        if (idPadre == null) return null;
        Opcion padre = obtenerOpcion(idPadre);
        if (padre.getPadre() != null) {
            throw new PeticionInvalidaExcepcion("Solo se permite un nivel de jerarquía.");
        }
        return padre;
    }

    private RespuestaOpcion aRespuesta(Opcion o, boolean conHijos) {
        return RespuestaOpcion.builder()
                .id(o.getId()).codigo(o.getCodigo()).nombre(o.getNombre())
                .icono(o.getIcono()).ruta(o.getRuta())
                .ordenVisualizacion(o.getOrdenVisualizacion()).activo(o.isActivo())
                .idPadre(o.getPadre() != null ? o.getPadre().getId() : null)
                .nombrePadre(o.getPadre() != null ? o.getPadre().getNombre() : null)
                .hijos(conHijos
                        ? o.getHijos().stream().map(h -> aRespuesta(h, false)).toList()
                        : List.of())
                .build();
    }

    private Opcion obtenerOpcion(Long id) {
        return opcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Opción", id));
    }
}
