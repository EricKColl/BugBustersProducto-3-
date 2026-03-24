
-- =========================
-- TABLA CLIENTES
-- =========================
CREATE DATABASE IF NOT EXISTS producto3;
USE producto3;

CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    domicilio VARCHAR(150),
    nif VARCHAR(20),
    tipo_cliente ENUM('estandar', 'premium') NOT NULL
);

-- =========================
-- TABLA ARTÍCULOS
-- =========================
CREATE TABLE articulos (
    id_articulo INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    gastos_envio DECIMAL(10,2),
    tiempo_preparacion INT
);

-- =========================
-- TABLA PEDIDOS
-- =========================
CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,

    id_cliente INT NOT NULL,
    id_articulo INT NOT NULL,

    cantidad INT NOT NULL,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50),

    -- RELACIONES
    CONSTRAINT fk_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON DELETE CASCADE,

    CONSTRAINT fk_articulo
        FOREIGN KEY (id_articulo)
        REFERENCES articulos(id_articulo)
        ON DELETE CASCADE
);


