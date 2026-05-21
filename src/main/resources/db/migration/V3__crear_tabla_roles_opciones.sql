-- ─── V3: Tabla de permisos rol → opción ──────────────────────────────────────

CREATE TABLE roles_opciones (
    id              BIGSERIAL   NOT NULL,
    id_rol          BIGINT      NOT NULL,
    id_opcion       BIGINT      NOT NULL,
    puede_ver       BOOLEAN     NOT NULL DEFAULT TRUE,
    puede_crear     BOOLEAN     NOT NULL DEFAULT FALSE,
    puede_editar    BOOLEAN     NOT NULL DEFAULT FALSE,
    puede_eliminar  BOOLEAN     NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_roles_opciones    PRIMARY KEY (id),
    CONSTRAINT uk_rol_opcion        UNIQUE (id_rol, id_opcion),
    CONSTRAINT fk_roles_opciones_rol    FOREIGN KEY (id_rol)    REFERENCES roles(id),
    CONSTRAINT fk_roles_opciones_opcion FOREIGN KEY (id_opcion) REFERENCES opciones(id)
);

CREATE INDEX idx_roles_opciones_rol    ON roles_opciones(id_rol);
CREATE INDEX idx_roles_opciones_opcion ON roles_opciones(id_opcion);

COMMENT ON TABLE roles_opciones IS 'Permisos CRUD por rol y opción del menú';
