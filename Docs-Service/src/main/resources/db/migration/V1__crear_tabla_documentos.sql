CREATE TABLE IF NOT EXISTS documentos_autorizacion (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_menor          VARCHAR(100) NOT NULL,
    rut_menor             VARCHAR(12)  NOT NULL,
    fecha_nacimiento      DATE         NOT NULL,
    nombre_tutor          VARCHAR(100) NOT NULL,
    rut_tutor             VARCHAR(12)  NOT NULL,
    relacion_tutor        VARCHAR(20)  NOT NULL,
    pais_destino          VARCHAR(50)  NOT NULL,
    fecha_salida          DATE         NOT NULL,
    fecha_retorno         DATE         NOT NULL,
    estado                ENUM('PENDIENTE','APROBADO','RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',
    rut_funcionario_carga VARCHAR(12),
    fecha_carga           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones         TEXT
);

-- Datos de prueba
INSERT INTO documentos_autorizacion
    (nombre_menor, rut_menor, fecha_nacimiento, nombre_tutor, rut_tutor, relacion_tutor, pais_destino, fecha_salida, fecha_retorno, estado, rut_funcionario_carga)
VALUES
('Juan Pérez López',    '22222222-2', '2012-05-10', 'María López',   '12345678-9', 'MADRE',       'Argentina', '2026-07-01', '2026-07-15', 'APROBADO',  '12345678-9'),
('Ana Soto Rojas',      '33333333-3', '2015-08-20', 'Pedro Soto',    '11111111-1', 'PADRE',       'Brasil',    '2026-08-01', '2026-08-10', 'PENDIENTE', '12345678-9'),
('Luis Mora García',    '44444444-4', '2010-03-15', 'Carmen García', '98765432-1', 'TUTOR_LEGAL', 'Perú',      '2026-09-05', '2026-09-20', 'PENDIENTE', '98765432-1'),
('Sofia Díaz Torres',   '55555555-5', '2008-11-30', 'Jorge Díaz',    '11111111-1', 'PADRE',       'Uruguay',   '2026-06-15', '2026-06-25', 'RECHAZADO', '12345678-9');