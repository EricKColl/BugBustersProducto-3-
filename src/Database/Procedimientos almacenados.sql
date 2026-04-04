CREATE DATABASE IF NOT EXISTS producto3;
USE producto3;

-- Borramos para recrear sin lógica de transacción interna
DROP PROCEDURE IF EXISTS insertar_cliente;
DROP PROCEDURE IF EXISTS actualizar_cliente;
DROP PROCEDURE IF EXISTS eliminar_cliente;
DROP PROCEDURE IF EXISTS insertar_articulo;
DROP PROCEDURE IF EXISTS actualizar_articulo;
DROP PROCEDURE IF EXISTS eliminar_articulo;
DROP PROCEDURE IF EXISTS insertar_pedido;

DELIMITER $$

-- =========================================================
-- PROCEDIMIENTOS ALMACENADOS - TABLA CLIENTES
-- =========================================================

CREATE PROCEDURE insertar_cliente (
    IN p_email VARCHAR(100),
    IN p_nombre VARCHAR(100),
    IN p_domicilio VARCHAR(150),
    IN p_nif VARCHAR(20),
    IN p_tipo_cliente ENUM('estandar', 'premium')
)
BEGIN
    INSERT INTO clientes (email, nombre, domicilio, nif, tipo_cliente)
    VALUES (p_email, p_nombre, p_domicilio, p_nif, p_tipo_cliente);
END $$

CREATE PROCEDURE actualizar_cliente (
    IN p_id_cliente INT,
    IN p_email VARCHAR(100),
    IN p_nombre VARCHAR(100),
    IN p_domicilio VARCHAR(150),
    IN p_nif VARCHAR(20),
    IN p_tipo_cliente ENUM('estandar', 'premium')
)
BEGIN
    UPDATE clientes
    SET email = p_email,
        nombre = p_nombre,
        domicilio = p_domicilio,
        nif = p_nif,
        tipo_cliente = p_tipo_cliente
    WHERE id_cliente = p_id_cliente;
END $$

CREATE PROCEDURE eliminar_cliente (
    IN p_id_cliente INT
)
BEGIN
    DELETE FROM clientes WHERE id_cliente = p_id_cliente;
END $$

-- =========================================================
-- PROCEDIMIENTOS ALMACENADOS - TABLA ARTICULOS
-- =========================================================

CREATE PROCEDURE insertar_articulo (
    IN p_codigo VARCHAR(50),
    IN p_descripcion VARCHAR(200),
    IN p_precio_venta DECIMAL(10,2),
    IN p_gastos_envio DECIMAL(10,2),
    IN p_tiempo_preparacion INT
)
BEGIN
    INSERT INTO articulos (codigo, descripcion, precio_venta, gastos_envio, tiempo_preparacion)
    VALUES (p_codigo, p_descripcion, p_precio_venta, p_gastos_envio, p_tiempo_preparacion);
END $$

CREATE PROCEDURE actualizar_articulo (
    IN p_id_articulo INT,
    IN p_codigo VARCHAR(50),
    IN p_descripcion VARCHAR(200),
    IN p_precio_venta DECIMAL(10,2),
    IN p_gastos_envio DECIMAL(10,2),
    IN p_tiempo_preparacion INT
)
BEGIN
    UPDATE articulos
    SET codigo = p_codigo,
        descripcion = p_descripcion,
        precio_venta = p_precio_venta,
        gastos_envio = p_gastos_envio,
        tiempo_preparacion = p_tiempo_preparacion
    WHERE id_articulo = p_id_articulo;
END $$

CREATE PROCEDURE eliminar_articulo (
    IN p_id_articulo INT
)
BEGIN
    DELETE FROM articulos WHERE id_articulo = p_id_articulo;
END $$

-- =========================================================
-- PROCEDIMIENTOS ALMACENADOS - TABLA PEDIDOS
-- =========================================================

CREATE PROCEDURE insertar_pedido(
    IN p_id_cliente INT,
    IN p_id_articulo INT,
    IN p_cantidad INT,
    IN p_fecha_manual DATETIME,
    IN p_estado ENUM('PENDIENTE', 'ENVIADO'),
    OUT p_nuevo_id INT
)
BEGIN
    INSERT INTO pedidos (id_cliente, id_articulo, cantidad, fecha_hora, estado)
    VALUES (p_id_cliente, p_id_articulo, p_cantidad, p_fecha_manual, p_estado);

    SET p_nuevo_id = LAST_INSERT_ID();
END $$

DELIMITER ;