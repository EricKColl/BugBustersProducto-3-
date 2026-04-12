-- =========================================================
-- BUGBUSTERS · PRODUCTO 4
-- IMPLEMENTACIÓN MEDIANTE ORM
-- PROCEDIMIENTOS ALMACENADOS DE APOYO
-- =========================================================
-- Este script crea los procedimientos almacenados utilizados
-- en la base de datos del proyecto.
--
-- Aunque el Producto 4 migrará la persistencia a JPA/Hibernate,
-- se deja esta capa preparada para:
--   - mantener compatibilidad durante la transición
--   - facilitar pruebas manuales en MySQL
--   - conservar coherencia con la base heredada del Producto 3
--
-- Requisito previo:
--   Haber ejecutado antes el script "Tablas Base de Datos.sql"
--
-- Base de datos objetivo:
--   producto4
-- =========================================================

USE producto4;

DROP PROCEDURE IF EXISTS insertar_cliente;
DROP PROCEDURE IF EXISTS eliminar_cliente;
DROP PROCEDURE IF EXISTS insertar_articulo;
DROP PROCEDURE IF EXISTS sumar_stock_articulo;
DROP PROCEDURE IF EXISTS eliminar_articulo;
DROP PROCEDURE IF EXISTS insertar_pedido;
DROP PROCEDURE IF EXISTS eliminar_pedido;

DELIMITER $$

-- =========================================================
-- INSERTAR CLIENTE
-- =========================================================
CREATE PROCEDURE insertar_cliente(
    IN p_email VARCHAR(100),
    IN p_nombre VARCHAR(100),
    IN p_domicilio VARCHAR(150),
    IN p_nif VARCHAR(20),
    IN p_tipo_cliente VARCHAR(20)
)
BEGIN
    INSERT INTO clientes (email, nombre, domicilio, nif, tipo_cliente)
    VALUES (p_email, p_nombre, p_domicilio, p_nif, p_tipo_cliente);
END $$

-- =========================================================
-- ELIMINAR CLIENTE
-- =========================================================
CREATE PROCEDURE eliminar_cliente(
    IN p_id_cliente INT
)
BEGIN
    DELETE FROM clientes
    WHERE id_cliente = p_id_cliente;
END $$

-- =========================================================
-- INSERTAR ARTICULO
-- =========================================================
CREATE PROCEDURE insertar_articulo(
    IN p_codigo VARCHAR(50),
    IN p_descripcion VARCHAR(200),
    IN p_precio_venta DECIMAL(10,2),
    IN p_gastos_envio DECIMAL(10,2),
    IN p_tiempo_preparacion INT,
    IN p_cantidad_disponible INT
)
BEGIN
    INSERT INTO articulos (
        codigo,
        descripcion,
        precio_venta,
        gastos_envio,
        tiempo_preparacion,
        cantidad_disponible
    )
    VALUES (
        p_codigo,
        p_descripcion,
        p_precio_venta,
        p_gastos_envio,
        p_tiempo_preparacion,
        p_cantidad_disponible
    );
END $$

-- =========================================================
-- SUMAR STOCK DE ARTICULO
-- =========================================================
CREATE PROCEDURE sumar_stock_articulo(
    IN p_codigo VARCHAR(50),
    IN p_cantidad INT
)
BEGIN
    IF p_cantidad <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cantidad a sumar debe ser mayor que cero.';
    END IF;

    UPDATE articulos
    SET cantidad_disponible = cantidad_disponible + p_cantidad
    WHERE codigo = p_codigo;
END $$

-- =========================================================
-- ELIMINAR ARTICULO
-- =========================================================
CREATE PROCEDURE eliminar_articulo(
    IN p_id_articulo INT
)
BEGIN
    DELETE FROM articulos
    WHERE id_articulo = p_id_articulo;
END $$

-- =========================================================
-- INSERTAR PEDIDO
-- Inserta el pedido y descuenta automáticamente el stock
-- del artículo si hay unidades suficientes.
-- =========================================================
CREATE PROCEDURE insertar_pedido(
    IN p_id_cliente INT,
    IN p_id_articulo INT,
    IN p_cantidad INT,
    IN p_fecha_hora DATETIME,
    IN p_estado VARCHAR(50),
    OUT p_id_pedido INT
)
BEGIN
    DECLARE v_existe_articulo INT DEFAULT 0;
    DECLARE v_stock_actual INT DEFAULT 0;

    START TRANSACTION;

    IF p_cantidad <= 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cantidad del pedido debe ser mayor que cero.';
    END IF;

    SELECT COUNT(*)
    INTO v_existe_articulo
    FROM articulos
    WHERE id_articulo = p_id_articulo;

    IF v_existe_articulo = 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El artículo indicado no existe.';
    END IF;

    SELECT cantidad_disponible
    INTO v_stock_actual
    FROM articulos
    WHERE id_articulo = p_id_articulo
    FOR UPDATE;

    IF v_stock_actual < p_cantidad THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente para crear el pedido.';
    END IF;

    INSERT INTO pedidos (
        id_cliente,
        id_articulo,
        cantidad,
        fecha_hora,
        estado
    )
    VALUES (
        p_id_cliente,
        p_id_articulo,
        p_cantidad,
        p_fecha_hora,
        p_estado
    );

    SET p_id_pedido = LAST_INSERT_ID();

    UPDATE articulos
    SET cantidad_disponible = cantidad_disponible - p_cantidad
    WHERE id_articulo = p_id_articulo;

    COMMIT;
END $$

-- =========================================================
-- ELIMINAR PEDIDO
-- Restaura el stock del artículo antes de eliminar el pedido
-- =========================================================
CREATE PROCEDURE eliminar_pedido(
    IN p_id_pedido INT
)
BEGIN
    DECLARE v_existe_pedido INT DEFAULT 0;
    DECLARE v_id_articulo INT;
    DECLARE v_cantidad INT;

    START TRANSACTION;

    SELECT COUNT(*)
    INTO v_existe_pedido
    FROM pedidos
    WHERE id_pedido = p_id_pedido;

    IF v_existe_pedido = 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El pedido indicado no existe.';
    END IF;

    SELECT id_articulo, cantidad
    INTO v_id_articulo, v_cantidad
    FROM pedidos
    WHERE id_pedido = p_id_pedido
    FOR UPDATE;

    UPDATE articulos
    SET cantidad_disponible = cantidad_disponible + v_cantidad
    WHERE id_articulo = v_id_articulo;

    DELETE FROM pedidos
    WHERE id_pedido = p_id_pedido;

    COMMIT;
END $$

DELIMITER ;