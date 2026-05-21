-- V1: Users table
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(100)  NOT NULL,
    last_name   VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    role        VARCHAR(20)   NOT NULL CHECK (role IN ('ADMIN','DOCTOR','RECEPTIONIST')),
    active      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users (email);

-- Seed default admin user (password: Admin1234!)
INSERT INTO users (first_name, last_name, email, password, role)
VALUES (
    'Admin',
    'MedicLuz',
    'admin@medicluz.com',
    '$2a$12$Vn3g9wTDX3TRktNW/5Xf2OM8MhNFkBGG1.dZrr4oXxQ0mEsf.ZLcW',
    'ADMIN'
) ON CONFLICT DO NOTHING;
