-- ─── V4: Tabla de usuarios ───────────────────────────────────────────────────

CREATE TABLE usuarios (
    id                  BIGSERIAL       NOT NULL,
    version             BIGINT          NOT NULL DEFAULT 0,
    creado_en           TIMESTAMP       NOT NULL,
    modificado_en       TIMESTAMP       NOT NULL,
    creado_por          VARCHAR(150),
    modificado_por      VARCHAR(150),
    primer_nombre       VARCHAR(100)    NOT NULL,
    apellido            VARCHAR(100)    NOT NULL,
    correo              VARCHAR(150)    NOT NULL,
    contrasena          VARCHAR(255)    NOT NULL,
    id_rol              BIGINT          NOT NULL,
    activo              BOOLEAN         NOT NULL DEFAULT TRUE,
    intentos_fallidos   INT             NOT NULL DEFAULT 0,
    bloqueado_hasta     TIMESTAMP,
    CONSTRAINT pk_usuarios          PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_correo   UNIQUE (correo),
    CONSTRAINT fk_usuarios_rol      FOREIGN KEY (id_rol) REFERENCES roles(id)
);

CREATE INDEX idx_usuarios_correo ON usuarios(correo);

COMMENT ON TABLE  usuarios                  IS 'Usuarios del sistema (médicos, recepcionistas, administradores)';
COMMENT ON COLUMN usuarios.contrasena       IS 'Hash BCrypt strength-12';
COMMENT ON COLUMN usuarios.intentos_fallidos IS 'Contador de intentos de login fallidos consecutivos';
COMMENT ON COLUMN usuarios.bloqueado_hasta  IS 'Si no es null, la cuenta está bloqueada hasta esa fecha/hora';
