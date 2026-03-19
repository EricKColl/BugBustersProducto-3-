--Este archivo lo requiere la actividad y lo tenemos por si pasara algo--

-- =========================================
-- BASE DE DATOS
-- =========================================
CREATE DATABASE IF NOT EXISTS producto3
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE producto3;

-- =========================================
-- TABLA CLIENTES
-- =========================================
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100),
    domicilio VARCHAR(150),
    nif VARCHAR(20),
    tipo_cliente VARCHAR(20) -- ESTANDAR / PREMIUM
);

-- =========================================
-- TABLA ARTICULOS
-- =========================================
CREATE TABLE articulos (
    id_articulo INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    precio_venta DOUBLE,
    gastos_envio DOUBLE,
    tiempo_preparacion INT
);

-- =========================================
-- TABLA PEDIDOS
-- =========================================
CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT,
    id_articulo INT,
    cantidad INT,
    fecha_hora DATETIME,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',

    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_articulo) REFERENCES articulos(id_articulo)
);