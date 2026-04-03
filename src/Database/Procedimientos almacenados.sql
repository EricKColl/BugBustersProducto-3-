CREATE DATABASE IF NOT EXISTS producto3;
USE producto3;

-- Borramos para recrear con la nueva lógica
DROP PROCEDURE IF EXISTS insertar_cliente;
DROP PROCEDURE IF EXISTS actualizar_cliente;
DROP PROCEDURE IF EXISTS eliminar_cliente;
DROP PROCEDURE IF EXISTS insertar_articulo;
DROP PROCEDURE IF EXISTS actualizar_articulo;
DROP PROCEDURE IF EXISTS eliminar_articulo;
DROP PROCEDURE IF EXISTS insertar_pedido;
DROP PROCEDURE IF EXISTS actualizar_pedido;
DROP PROCEDURE IF EXISTS eliminar_pedido;

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
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL; -- Reenvía el error a Java
    END;

    START TRANSACTION;
        INSERT INTO clientes (email, nombre, domicilio, nif, tipo_cliente)
        VALUES (p_email, p_nombre, p_domicilio, p_nif, p_tipo_cliente);
    COMMIT;
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
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
        UPDATE clientes
        SET email = p_email,
            nombre = p_nombre,
            domicilio = p_domicilio,
            nif = p_nif,
            tipo_cliente = p_tipo_cliente
        WHERE id_cliente = p_id_cliente;
    COMMIT;
END $$

CREATE PROCEDURE eliminar_cliente (
    IN p_id_cliente INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
        DELETE FROM clientes WHERE id_cliente = p_id_cliente;
    COMMIT;
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
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
        INSERT INTO articulos (codigo, descripcion, precio_venta, gastos_envio, tiempo_preparacion)
        VALUES (p_codigo, p_descripcion, p_precio_venta, p_gastos_envio, p_tiempo_preparacion);
    COMMIT;
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
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
        UPDATE articulos
        SET codigo = p_codigo,
            descripcion = p_descripcion,
            precio_venta = p_precio_venta,
            gastos_envio = p_gastos_envio,
            tiempo_preparacion = p_tiempo_preparacion
        WHERE id_articulo = p_id_articulo;
    COMMIT;
END $$

CREATE PROCEDURE eliminar_articulo (
    IN p_id_articulo INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
        DELETE FROM articulos WHERE id_articulo = p_id_articulo;
    COMMIT;
END $$

-- =========================================================
-- PROCEDIMIENTOS ALMACENADOS - TABLA PEDIDOS
-- =========================================================

CREATE PROCEDURE insertar_pedido (
    IN p_id_cliente INT,
    IN p_id_articulo INT,
    IN p_cantidad INT,
    IN p_estado VARCHAR(20)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
        INSERT INTO pedidos (id_cliente, id_articulo, cantidad, fecha_hora, estado)
        VALUES (p_id_cliente, p_id_articulo, p_cantidad, DATE_ADD(NOW(), INTERVAL 2 HOUR), p_estado);
    COMMIT;
END $$

CREATE PROCEDURE actualizar_pedido (
    IN p_id_pedido INT,
    IN p_id_cliente INT,
    IN p_id_articulo INT,
    IN p_cantidad INT,
    IN p_estado VARCHAR(20)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
        UPDATE pedidos
        SET id_cliente = p_id_cliente,
            id_articulo = p_id_articulo,
            cantidad = p_cantidad,
            estado = p_estado
        WHERE id_pedido = p_id_pedido;
    COMMIT;
END $$

CREATE PROCEDURE eliminar_pedido (
    IN p_id_pedido INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
        DELETE FROM pedidos WHERE id_pedido = p_id_pedido;
    COMMIT;
END $$

DELIMITER ;