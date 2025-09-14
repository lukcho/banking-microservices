-- Script de creación de base de datos para el sistema bancario
-- Base de datos: banking_db

CREATE DATABASE IF NOT EXISTS banking_db;
USE banking_db;

-- Tabla de personas (tabla padre)
CREATE TABLE IF NOT EXISTS personas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero CHAR(1) NOT NULL CHECK (genero IN ('M', 'F', 'O')),
    edad INT NOT NULL CHECK (edad >= 0 AND edad <= 150),
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NULL
);

-- Tabla de clientes (hereda de personas)
CREATE TABLE IF NOT EXISTS clientes (
    cliente_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE
);

-- Tabla de cuentas
CREATE TABLE IF NOT EXISTS cuentas (
    cuenta_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(20) NOT NULL CHECK (tipo_cuenta IN ('Ahorros', 'Corriente')),
    saldo_inicial DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (saldo_inicial >= 0),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NULL
);

-- Tabla de movimientos
CREATE TABLE IF NOT EXISTS movimientos (
    movimiento_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('Deposito', 'Retiro')),
    valor DECIMAL(15,2) NOT NULL CHECK (valor > 0),
    saldo DECIMAL(15,2) NOT NULL,
    cuenta_id BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cuenta_id) REFERENCES cuentas(cuenta_id) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento
-- Nota: Los índices se crean solo si no existen
CREATE INDEX idx_personas_identificacion ON personas(identificacion);
CREATE INDEX idx_clientes_persona_id ON clientes(persona_id);
CREATE INDEX idx_cuentas_numero ON cuentas(numero_cuenta);
CREATE INDEX idx_cuentas_cliente_id ON cuentas(cliente_id);
CREATE INDEX idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);

-- Datos de ejemplo
INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono) VALUES
('Marianela Montalvo', 'F', 35, '12345678', 'Calle 123 #45-67', '3001234567'),
('Carlos Rodríguez', 'M', 28, '87654321', 'Carrera 45 #78-90', '3109876543'),
('Ana García', 'F', 42, '11223344', 'Avenida 5 #12-34', '3155555555');

INSERT INTO clientes (persona_id, contrasena, estado) VALUES
(1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE),
(2, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE),
(3, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE);

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) VALUES
('225487', 'Corriente', 100.00, TRUE, 1),
('225488', 'Ahorros', 500.00, TRUE, 1),
('225489', 'Corriente', 200.00, TRUE, 2),
('225490', 'Ahorros', 1000.00, TRUE, 3);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) VALUES
('2024-01-01 10:00:00', 'Deposito', 100.00, 200.00, 1),
('2024-01-02 14:30:00', 'Retiro', 50.00, 150.00, 1),
('2024-01-03 09:15:00', 'Deposito', 200.00, 350.00, 1),
('2024-01-01 11:00:00', 'Deposito', 300.00, 800.00, 2),
('2024-01-02 16:45:00', 'Retiro', 100.00, 700.00, 2);

-- Consultas de verificación
SELECT 'Verificación de datos insertados:' as mensaje;

SELECT 'Personas:' as tabla, COUNT(*) as total FROM personas
UNION ALL
SELECT 'Clientes:', COUNT(*) FROM clientes
UNION ALL
SELECT 'Cuentas:', COUNT(*) FROM cuentas
UNION ALL
SELECT 'Movimientos:', COUNT(*) FROM movimientos;

-- Ejemplo de consulta de estado de cuenta
SELECT 
    p.nombre as Cliente,
    c.numero_cuenta as Numero_Cuenta,
    c.tipo_cuenta as Tipo,
    c.saldo_inicial as Saldo_Inicial,
    c.estado as Estado,
    SUM(CASE WHEN m.tipo_movimiento = 'Deposito' THEN m.valor ELSE -m.valor END) as Movimiento_Total,
    COALESCE(SUM(CASE WHEN m.tipo_movimiento = 'Deposito' THEN m.valor ELSE -m.valor END), 0) + c.saldo_inicial as Saldo_Disponible
FROM personas p
JOIN clientes cl ON p.id = cl.persona_id
JOIN cuentas c ON cl.cliente_id = c.cliente_id
LEFT JOIN movimientos m ON c.cuenta_id = m.cuenta_id
WHERE p.identificacion = '12345678'
GROUP BY p.nombre, c.numero_cuenta, c.tipo_cuenta, c.saldo_inicial, c.estado;
