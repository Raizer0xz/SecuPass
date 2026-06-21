CREATE TABLE IF NOT EXISTS registros_auditoria (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut               VARCHAR(12)  NOT NULL,
    institucion       VARCHAR(20)  NOT NULL,
    accion            VARCHAR(50)  NOT NULL,
    entidad_afectada  VARCHAR(50),
    id_entidad        VARCHAR(50),
    detalle           TEXT,
    ip_origen         VARCHAR(45),
    resultado         ENUM('EXITOSO','FALLIDO') NOT NULL,
    fecha_hora        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Datos de prueba
INSERT INTO registros_auditoria (rut, institucion, accion, entidad_afectada, id_entidad, detalle, ip_origen, resultado) VALUES
('12345678-9', 'PDI',    'LOGIN',               NULL,       NULL,  'Login exitoso',                          '192.168.1.10', 'EXITOSO'),
('98765432-1', 'SAG',    'LOGIN',               NULL,       NULL,  'Login exitoso',                          '192.168.1.11', 'EXITOSO'),
('12345678-9', 'PDI',    'CONSULTA_VEHICULO',   'VEHICULO', 'ABC123', 'Consulta patente ABC123',             '192.168.1.10', 'EXITOSO'),
('98765432-1', 'SAG',    'CARGA_DOCUMENTO',     'DOCUMENTO','1',   'Carga autorización menor',              '192.168.1.11', 'EXITOSO'),
('11111111-1', 'ADUANA', 'LOGIN',               NULL,       NULL,  'Intento fallido - contraseña incorrecta','192.168.1.12', 'FALLIDO'),
('11111111-1', 'ADUANA', 'LOGIN',               NULL,       NULL,  'Intento fallido - contraseña incorrecta','192.168.1.12', 'FALLIDO'),
('11111111-1', 'ADUANA', 'LOGIN',               NULL,       NULL,  'Cuenta bloqueada por 3 intentos',       '192.168.1.12', 'FALLIDO'),
('12345678-9', 'PDI',    'GENERACION_REPORTE',  'REPORTE',  '1',   'Reporte mensual enero 2026',            '192.168.1.10', 'EXITOSO');