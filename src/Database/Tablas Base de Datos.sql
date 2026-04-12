-- =========================================================
-- BUGBUSTERS · PRODUCTO 4
-- IMPLEMENTACIÓN MEDIANTE ORM
-- SCRIPT DE CREACIÓN DE BASE DE DATOS Y TABLAS
-- =========================================================
-- Este script crea la base de datos del Producto 4 y su
-- estructura principal en MySQL.
--
-- Tablas incluidas:
--   - clientes
--   - articulos
--   - pedidos
--
-- Este producto parte del Producto 3, pero servirá como base
-- para la migración de la persistencia JDBC a JPA con Hibernate,
-- manteniendo la arquitectura MVC del proyecto.
--
-- Orden recomendado de ejecución:
--   1. Tablas Base de Datos.sql
--   2. Procedimientos almacenados.sql
--   3. Datos-Prueba.sql
-- =========================================================

CREATE DATABASE IF NOT EXISTS producto4
CHARACTER SET utf8mb4
COLLATE utf8mb4_spanish_ci;

USE producto4;

-- =========================================================
-- TABLA CLIENTES
-- =========================================================
CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    domicilio VARCHAR(150),
    nif VARCHAR(20),
    tipo_cliente ENUM('estandar', 'premium') NOT NULL
);

-- =========================================================
-- TABLA ARTICULOS
-- =========================================================
CREATE TABLE IF NOT EXISTS articulos (
    id_articulo INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    gastos_envio DECIMAL(10,2) NOT NULL,
    tiempo_preparacion INT NOT NULL,
    cantidad_disponible INT NOT NULL DEFAULT 0
);

-- =========================================================
-- TABLA PEDIDOS
-- =========================================================
CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,

    id_cliente INT NOT NULL,
    id_articulo INT NOT NULL,

    cantidad INT NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',

    CONSTRAINT fk_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON DELETE CASCADE,

    CONSTRAINT fk_articulo
        FOREIGN KEY (id_articulo)
        REFERENCES articulos(id_articulo)
        ON DELETE CASCADE
);

-- =========================================================
-- COMPROBACIONES OPCIONALES
-- =========================================================
-- SHOW TABLES;
-- DESCRIBE clientes;
-- DESCRIBE articulos;
-- DESCRIBE pedidos;