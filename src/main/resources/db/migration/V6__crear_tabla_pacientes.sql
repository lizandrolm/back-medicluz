-- ─── V6: Tabla de pacientes ───────────────────────────────────────────────────

CREATE TABLE pacientes (
    id                      BIGSERIAL       NOT NULL,
    version                 BIGINT          NOT NULL DEFAULT 0,
    fecha_registro          TIMESTAMP       NOT NULL,
    fecha_actualiza         TIMESTAMP       NOT NULL,
    usuario_registra        VARCHAR(150),
    usuario_actualiza       VARCHAR(150),

    -- Identificación
    codigo                  VARCHAR(20)     NOT NULL,
    primer_nombre           VARCHAR(100)    NOT NULL,
    apellido                VARCHAR(100)    NOT NULL,
    tipo_documento          VARCHAR(20)     NOT NULL,
    numero_documento        VARCHAR(20)     NOT NULL,
    fecha_nacimiento        DATE            NOT NULL,
    genero                  VARCHAR(15)     NOT NULL,
    estado_civil            VARCHAR(20),
    grupo_sanguineo         VARCHAR(5),
    nacionalidad            VARCHAR(100),
    ocupacion               VARCHAR(100),

    -- Contacto
    telefono_principal      VARCHAR(20),
    telefono_secundario     VARCHAR(20),
    correo                  VARCHAR(150),
    direccion               VARCHAR(200),
    ciudad                  VARCHAR(100),
    provincia               VARCHAR(100),
    codigo_postal           VARCHAR(10),

    -- Historia médica
    alergias                TEXT,
    enfermedades_cronicas   TEXT,
    medicamentos_actuales   TEXT,
    historial_quirurgico    TEXT,
    antecedentes_familiares TEXT,
    observaciones           TEXT,

    -- Contacto de emergencia
    nombre_emergencia       VARCHAR(100),
    telefono_emergencia     VARCHAR(100),
    parentesco_emergencia   VARCHAR(50),

    -- Seguro
    tiene_seguro            BOOLEAN         NOT NULL DEFAULT FALSE,
    empresa_seguro          VARCHAR(100),
    numero_poliza           VARCHAR(50),

    -- Estado
    estado                  VARCHAR(20)     NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT pk_pacientes             PRIMARY KEY (id),
    CONSTRAINT uk_paciente_codigo       UNIQUE (codigo),
    CONSTRAINT uk_paciente_documento    UNIQUE (tipo_documento, numero_documento)
);

CREATE INDEX idx_paciente_codigo    ON pacientes(codigo);
CREATE INDEX idx_paciente_nombre    ON pacientes(apellido, primer_nombre);
CREATE INDEX idx_paciente_estado    ON pacientes(estado);
CREATE INDEX idx_paciente_documento ON pacientes(tipo_documento, numero_documento);

COMMENT ON TABLE  pacientes              IS 'Registro completo de pacientes de la clínica';
COMMENT ON COLUMN pacientes.codigo       IS 'Código único autogenerado con formato PAC-AAAA-NNNN';
COMMENT ON COLUMN pacientes.tipo_documento IS 'Enum: CEDULA, PASAPORTE, RUC, OTRO';
COMMENT ON COLUMN pacientes.genero       IS 'Enum: MASCULINO, FEMENINO, OTRO';
COMMENT ON COLUMN pacientes.estado       IS 'Enum: ACTIVO, INACTIVO, CRITICO';
