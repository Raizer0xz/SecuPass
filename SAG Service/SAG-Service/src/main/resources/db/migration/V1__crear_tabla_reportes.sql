CREATE TABLE IF NOT EXISTS declaraciones_sag (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut_pasajero  VARCHAR(50)  NOT NULL,
    nombre_completo VARCHAR(255) NOT NULL,
    trae_productos_riesgo BOOLEAN NOT NULL DEFAULT FALSE,
    qr_token      VARCHAR(50)  UNIQUE NULL,
    fecha_creacion DATETIME    NULL,
    estado        VARCHAR(50)  NOT NULL DEFAULT 'SOLICITADO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS declaracion_productos (
    declaracion_id BIGINT       NOT NULL,
    producto       VARCHAR(255) NOT NULL,
    CONSTRAINT fk_declaracion_productos
        FOREIGN KEY (declaracion_id) REFERENCES declaraciones_sag(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;