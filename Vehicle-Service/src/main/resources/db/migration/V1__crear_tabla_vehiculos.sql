CREATE TABLE IF NOT EXISTS vehiculos (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    patente                     VARCHAR(10)  NOT NULL UNIQUE,
    marca                       VARCHAR(255) NOT NULL,
    modelo                      VARCHAR(255) NOT NULL,
    anio                        INT          NOT NULL,
    rut_propietario             VARCHAR(255) NOT NULL,
    nombre_propietario          VARCHAR(255) NOT NULL,
    pais_destino                VARCHAR(255) NOT NULL,
    estado_formulario           VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',
    rut_funcionario_registro    VARCHAR(255) NULL,
    fecha_registro              DATETIME     NULL,
    fecha_actualizacion         DATETIME     NULL,
    observaciones               VARCHAR(500) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;