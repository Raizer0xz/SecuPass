CREATE DATABASE IF NOT EXISTS secupass_auth;
USE secupass_auth;

CREATE TABLE IF NOT EXISTS funcionarios (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut               VARCHAR(12)  NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,
    nombre            VARCHAR(100) NOT NULL,
    institucion       ENUM('PDI','SAG','ADUANA') NOT NULL,
    activo            BOOLEAN      NOT NULL DEFAULT TRUE,
    intentos_fallidos INT          NOT NULL DEFAULT 0,
    bloqueado_hasta   DATETIME     NULL,
    creado_en         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Funcionarios de prueba (contraseña: Pass1234)
INSERT INTO funcionarios (rut, password_hash, nombre, institucion) VALUES
('12345678-9', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Carlos Rojas',   'PDI'),
('98765432-1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Ana Morales',    'SAG'),
('11111111-1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Pedro Soto',     'ADUANA');