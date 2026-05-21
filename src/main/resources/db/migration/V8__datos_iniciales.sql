-- ─── V8: Datos iniciales ──────────────────────────────────────────────────────
-- Roles base del sistema
-- Usuario administrador inicial (contraseña: Admin@2025! — BCrypt strength 12)
-- Opciones del menú con permisos por rol

-- ─── Roles ───────────────────────────────────────────────────────────────────
INSERT INTO roles (nombre, nombre_visualizacion, descripcion, activo, creado_en, modificado_en)
VALUES
    ('ADMINISTRADOR', 'Administrador',   'Acceso total al sistema',                       TRUE, NOW(), NOW()),
    ('MEDICO',        'Médico',          'Gestión de pacientes, citas e historial clínico', TRUE, NOW(), NOW()),
    ('RECEPCIONISTA', 'Recepcionista',   'Agendamiento de citas y registro de pacientes',  TRUE, NOW(), NOW());

-- ─── Opciones padre (secciones del menú) ─────────────────────────────────────
INSERT INTO opciones (codigo, nombre, icono, ruta, orden_visualizacion, activo, id_padre, creado_en, modificado_en)
VALUES
    ('PANEL',           'Panel',            'LayoutDashboard',  '/dashboard',   1, TRUE, NULL, NOW(), NOW()),
    ('PACIENTES',       'Pacientes',        'Users',            '/patients',    2, TRUE, NULL, NOW(), NOW()),
    ('CITAS',           'Citas',            'CalendarDays',     '/citas',       3, TRUE, NULL, NOW(), NOW()),
    ('CONFIGURACION',   'Configuración',    'Settings',         NULL,           9, TRUE, NULL, NOW(), NOW());

-- ─── Opciones hijo ────────────────────────────────────────────────────────────
INSERT INTO opciones (codigo, nombre, icono, ruta, orden_visualizacion, activo, id_padre, creado_en, modificado_en)
VALUES
    ('NUEVO_PACIENTE',  'Nuevo paciente',   'UserPlus',         '/patients/new',    1, TRUE,
        (SELECT id FROM opciones WHERE codigo = 'PACIENTES'), NOW(), NOW()),
    ('AGENDA',          'Agenda',           'CalendarCheck',    '/citas/agenda',    1, TRUE,
        (SELECT id FROM opciones WHERE codigo = 'CITAS'), NOW(), NOW()),
    ('ROLES',           'Roles',            'Shield',           '/config/roles',    1, TRUE,
        (SELECT id FROM opciones WHERE codigo = 'CONFIGURACION'), NOW(), NOW()),
    ('USUARIOS',        'Usuarios',         'UserCog',          '/config/usuarios', 2, TRUE,
        (SELECT id FROM opciones WHERE codigo = 'CONFIGURACION'), NOW(), NOW());

-- ─── Permisos ADMINISTRADOR: acceso total ────────────────────────────────────
INSERT INTO roles_opciones (id_rol, id_opcion, puede_ver, puede_crear, puede_editar, puede_eliminar)
SELECT r.id, o.id, TRUE, TRUE, TRUE, TRUE
FROM roles r, opciones o
WHERE r.nombre = 'ADMINISTRADOR';

-- ─── Permisos MEDICO ─────────────────────────────────────────────────────────
INSERT INTO roles_opciones (id_rol, id_opcion, puede_ver, puede_crear, puede_editar, puede_eliminar)
SELECT r.id, o.id,
    TRUE,
    CASE WHEN o.codigo IN ('NUEVO_PACIENTE','AGENDA') THEN TRUE ELSE FALSE END,
    CASE WHEN o.codigo IN ('PACIENTES','CITAS','PANEL','AGENDA') THEN TRUE ELSE FALSE END,
    FALSE
FROM roles r, opciones o
WHERE r.nombre = 'MEDICO'
  AND o.codigo IN ('PANEL','PACIENTES','NUEVO_PACIENTE','CITAS','AGENDA');

-- ─── Permisos RECEPCIONISTA ───────────────────────────────────────────────────
INSERT INTO roles_opciones (id_rol, id_opcion, puede_ver, puede_crear, puede_editar, puede_eliminar)
SELECT r.id, o.id,
    TRUE,
    CASE WHEN o.codigo IN ('NUEVO_PACIENTE','AGENDA') THEN TRUE ELSE FALSE END,
    CASE WHEN o.codigo IN ('PACIENTES','CITAS','AGENDA') THEN TRUE ELSE FALSE END,
    FALSE
FROM roles r, opciones o
WHERE r.nombre = 'RECEPCIONISTA'
  AND o.codigo IN ('PANEL','PACIENTES','NUEVO_PACIENTE','CITAS','AGENDA');

-- ─── Usuario administrador inicial ───────────────────────────────────────────
-- Contraseña: Admin@2025!  (BCrypt $2a$12$)
INSERT INTO usuarios (primer_nombre, apellido, correo, contrasena, id_rol, activo,
                      intentos_fallidos, creado_en, modificado_en)
VALUES (
    'Administrador',
    'Sistema',
    'admin@medicluz.com',
    '$2a$12$LqVNfBBG3FqOV4lbw.1D9.JGx4eQn7r.Lk9Mz7MXoOhB2cW1sFFe',
    (SELECT id FROM roles WHERE nombre = 'ADMINISTRADOR'),
    TRUE,
    0,
    NOW(),
    NOW()
);
