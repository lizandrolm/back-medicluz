-- V3: Patients table
CREATE TABLE IF NOT EXISTS patients (
    id                              BIGSERIAL    PRIMARY KEY,
    code                            VARCHAR(20)  NOT NULL UNIQUE,

    -- Personal
    first_name                      VARCHAR(100) NOT NULL,
    last_name                       VARCHAR(100) NOT NULL,
    document_type                   VARCHAR(20)  NOT NULL CHECK (document_type IN ('CEDULA','PASAPORTE','RUC','OTRO')),
    document_number                 VARCHAR(20)  NOT NULL,
    date_of_birth                   DATE         NOT NULL,
    gender                          VARCHAR(10)  NOT NULL CHECK (gender IN ('MASCULINO','FEMENINO','OTRO')),
    marital_status                  VARCHAR(20)  CHECK (marital_status IN ('SOLTERO','CASADO','DIVORCIADO','VIUDO','UNION_LIBRE')),
    blood_type                      VARCHAR(5),
    nationality                     VARCHAR(100),
    occupation                      VARCHAR(100),

    -- Contact
    phone_primary                   VARCHAR(20),
    phone_secondary                 VARCHAR(20),
    email                           VARCHAR(150),
    address                         VARCHAR(200),
    city                            VARCHAR(100),
    province                        VARCHAR(100),
    postal_code                     VARCHAR(10),

    -- Medical history
    allergies                       TEXT,
    chronic_diseases                TEXT,
    current_medications             TEXT,
    surgical_history                TEXT,
    family_history                  TEXT,
    observations                    TEXT,

    -- Emergency contact
    emergency_contact_name          VARCHAR(100),
    emergency_contact_phone         VARCHAR(100),
    emergency_contact_relationship  VARCHAR(50),

    -- Insurance
    has_insurance                   BOOLEAN      NOT NULL DEFAULT FALSE,
    insurance_company               VARCHAR(100),
    insurance_policy_number         VARCHAR(50),

    -- Status
    status                          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                                        CHECK (status IN ('ACTIVE','INACTIVE','CRITICAL')),

    -- Audit
    created_at                      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMP    NOT NULL DEFAULT NOW(),

    UNIQUE (document_type, document_number)
);

CREATE INDEX idx_patients_code            ON patients (code);
CREATE INDEX idx_patients_document        ON patients (document_type, document_number);
CREATE INDEX idx_patients_status          ON patients (status);
CREATE INDEX idx_patients_name            ON patients (last_name, first_name);
