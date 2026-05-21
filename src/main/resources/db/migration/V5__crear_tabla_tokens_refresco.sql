-- ─── V5: Tabla de tokens de refresco ─────────────────────────────────────────

CREATE TABLE tokens_refresco (
    id                  BIGSERIAL       NOT NULL,
    token_hash          VARCHAR(64)     NOT NULL,
    id_usuario          BIGINT          NOT NULL,
    fecha_expiracion    TIMESTAMPTZ     NOT NULL,
    revocado            BOOLEAN         NOT NULL DEFAULT FALSE,
    ip_origen           VARCHAR(45),
    fecha_registro      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_tokens_refresco       PRIMARY KEY (id),
    CONSTRAINT uk_tokens_refresco_hash  UNIQUE (token_hash),
    CONSTRAINT fk_tokens_refresco_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX idx_tokens_refresco_hash    ON tokens_refresco(token_hash);
CREATE INDEX idx_tokens_refresco_usuario ON tokens_refresco(id_usuario);

COMMENT ON TABLE  tokens_refresco                IS 'Tokens de refresco JWT. Solo se almacena el hash SHA-256';
COMMENT ON COLUMN tokens_refresco.token_hash     IS 'SHA-256 del token en hex — nunca se guarda el token en texto plano';
COMMENT ON COLUMN tokens_refresco.ip_origen      IS 'IP del cliente al momento de generación (IPv4 o IPv6)';
COMMENT ON COLUMN tokens_refresco.fecha_registro IS 'Fecha y hora de creación del token';
