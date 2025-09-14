-- Script simplificado para configurar la base de datos
USE banking_db;

-- Eliminar tablas si existen (en orden correcto por dependencias)
DROP TABLE IF EXISTS movimientos;
DROP TABLE IF EXISTS cuentas;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS personas;

-- Crear tabla personas
CREATE TABLE personas (
    persona_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero CHAR(1) NOT NULL CHECK (genero IN ('M', 'F')),
    edad INT NOT NULL CHECK (edad > 0),
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(200),
    telefono VARCHAR(20)
);

-- Crear tabla clientes
CREATE TABLE clientes (
    cliente_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (persona_id) REFERENCES personas(persona_id) ON DELETE CASCADE
);

-- Crear tabla cuentas
CREATE TABLE cuentas (
    cuenta_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(20) NOT NULL CHECK (tipo_cuenta IN ('AHORROS', 'CORRIENTE')),
    saldo_inicial DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id) ON DELETE CASCADE
);

-- Crear tabla movimientos
CREATE TABLE movimientos (
    movimiento_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuenta_id BIGINT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('DEPOSITO', 'RETIRO')),
    valor DECIMAL(15,2) NOT NULL,
    saldo DECIMAL(15,2) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cuenta_id) REFERENCES cuentas(cuenta_id) ON DELETE CASCADE
);

-- Crear índices
CREATE INDEX idx_personas_identificacion ON personas(identificacion);
CREATE INDEX idx_clientes_persona_id ON clientes(persona_id);
CREATE INDEX idx_cuentas_numero ON cuentas(numero_cuenta);
CREATE INDEX idx_cuentas_cliente_id ON cuentas(cliente_id);
CREATE INDEX idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);

-- Insertar datos de ejemplo
INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono) VALUES
('Marianela Montalvo', 'F', 35, '12345678', 'Calle 123 #45-67', '3001234567'),
('Carlos Rodríguez', 'M', 28, '87654321', 'Carrera 45 #78-90', '3109876543');

INSERT INTO clientes (persona_id, contrasena, estado) VALUES
(1, 'password123', TRUE),
(2, 'password456', TRUE);

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) VALUES
('1234567890', 'AHORROS', 1000.00, TRUE, 1),
('0987654321', 'CORRIENTE', 2000.00, TRUE, 2);

INSERT INTO movimientos (cuenta_id, tipo_movimiento, valor, saldo) VALUES
(1, 'DEPOSITO', 1000.00, 1000.00),
(2, 'DEPOSITO', 2000.00, 2000.00);
