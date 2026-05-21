-- ─── V7: Tabla de citas médicas ───────────────────────────────────────────────

CREATE TABLE citas (
    id                  BIGSERIAL       NOT NULL,
    version             BIGINT          NOT NULL DEFAULT 0,
    fecha_registro      TIMESTAMP       NOT NULL,
    fecha_actualiza     TIMESTAMP       NOT NULL,
    usuario_registra    VARCHAR(150),
    usuario_actualiza   VARCHAR(150),
    id_paciente         BIGINT          NOT NULL,
    id_medico           BIGINT          NOT NULL,
    fecha_hora          TIMESTAMP       NOT NULL,
    tipo                VARCHAR(100)    NOT NULL,
    estado              VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    motivo              VARCHAR(200),
    notas               TEXT,
    CONSTRAINT pk_citas             PRIMARY KEY (id),
    CONSTRAINT fk_cita_paciente     FOREIGN KEY (id_paciente) REFERENCES pacientes(id),
    CONSTRAINT fk_cita_medico       FOREIGN KEY (id_medico)   REFERENCES usuarios(id)
);

CREATE INDEX idx_cita_paciente ON citas(id_paciente);
CREATE INDEX idx_cita_medico   ON citas(id_medico);
CREATE INDEX idx_cita_fecha    ON citas(fecha_hora);
CREATE INDEX idx_cita_estado   ON citas(estado);

COMMENT ON TABLE  citas         IS 'Citas médicas agendadas en la clínica';
COMMENT ON COLUMN citas.tipo    IS 'Tipo de consulta: CONSULTA_GENERAL, CONTROL, EMERGENCIA, etc.';
COMMENT ON COLUMN citas.estado  IS 'Enum: PENDIENTE, CONFIRMADA, EN_CURSO, COMPLETADA, CANCELADA, NO_ASISTIO';
