-- ─── V2: Tabla de opciones del menú ──────────────────────────────────────────

CREATE TABLE opciones (
    id                  BIGSERIAL       NOT NULL,
    version             BIGINT          NOT NULL DEFAULT 0,
    fecha_registro      TIMESTAMP       NOT NULL,
    fecha_actualiza     TIMESTAMP       NOT NULL,
    usuario_registra    VARCHAR(150),
    usuario_actualiza   VARCHAR(150),
    codigo              VARCHAR(60)     NOT NULL,
    nombre              VARCHAR(100)    NOT NULL,
    icono               VARCHAR(60),
    ruta                VARCHAR(150),
    orden_visualizacion INT             NOT NULL DEFAULT 0,
    activo              BOOLEAN         NOT NULL DEFAULT TRUE,
    id_padre            BIGINT,
    CONSTRAINT pk_opciones       PRIMARY KEY (id),
    CONSTRAINT uk_opciones_codigo UNIQUE (codigo),
    CONSTRAINT fk_opcion_padre   FOREIGN KEY (id_padre) REFERENCES opciones(id)
);

CREATE INDEX idx_opcion_padre ON opciones(id_padre);

COMMENT ON TABLE  opciones        IS 'Opciones del menú de navegación con soporte de jerarquía padre-hijo';
COMMENT ON COLUMN opciones.codigo IS 'Clave técnica: PACIENTES, CITAS, CONFIGURACION';
COMMENT ON COLUMN opciones.ruta   IS 'Ruta frontend: /pacientes. Null para secciones padre';
