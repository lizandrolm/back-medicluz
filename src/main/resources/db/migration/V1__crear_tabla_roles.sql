-- ─── V1: Tabla de roles ──────────────────────────────────────────────────────

CREATE TABLE roles (
    id                  BIGSERIAL       NOT NULL,
    version             BIGINT          NOT NULL DEFAULT 0,
    fecha_registro      TIMESTAMP       NOT NULL,
    fecha_actualiza     TIMESTAMP       NOT NULL,
    usuario_registra    VARCHAR(150),
    usuario_actualiza   VARCHAR(150),
    nombre              VARCHAR(60)     NOT NULL,
    nombre_visualizacion VARCHAR(100)   NOT NULL,
    descripcion         VARCHAR(250),
    activo              BOOLEAN         NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_nombre UNIQUE (nombre)
);

COMMENT ON TABLE  roles                       IS 'Roles de acceso del sistema';
COMMENT ON COLUMN roles.nombre                IS 'Clave técnica en mayúsculas: ADMINISTRADOR, MEDICO, RECEPCIONISTA';
COMMENT ON COLUMN roles.nombre_visualizacion  IS 'Etiqueta legible para la interfaz';
