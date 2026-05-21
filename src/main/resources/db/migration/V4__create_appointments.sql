-- V4: Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id                      BIGSERIAL    PRIMARY KEY,
    patient_id              BIGINT       NOT NULL REFERENCES patients(id),
    doctor_id               BIGINT       NOT NULL REFERENCES users(id),
    appointment_date_time   TIMESTAMP    NOT NULL,
    type                    VARCHAR(100) NOT NULL,
    status                  VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                                CHECK (status IN ('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW')),
    reason                  VARCHAR(200),
    notes                   TEXT,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_appointments_patient  ON appointments (patient_id);
CREATE INDEX idx_appointments_doctor   ON appointments (doctor_id);
CREATE INDEX idx_appointments_date     ON appointments (appointment_date_time);
CREATE INDEX idx_appointments_status   ON appointments (status);
