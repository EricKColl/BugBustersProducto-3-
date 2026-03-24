USE producto3;

-- =========================================================
-- DATOS DE PRUEBA REALES
-- Basados en el estado actual de la base de datos
-- =========================================================

-- Borrado previo en orden correcto por claves foráneas
DELETE FROM pedidos;
DELETE FROM articulos;
DELETE FROM clientes;

-- =========================================================
-- CLIENTES
-- =========================================================
INSERT INTO clientes (id_cliente, email, nombre, domicilio, nif, tipo_cliente) VALUES
(2, 'cliente_actualizado@mail.com', 'Cliente Actualizado', 'Nueva Calle 123', '99999999X', 'premium'),
(3, 'cliente3@mail.com', 'Carlos', 'Calle C', '11111111C', 'estandar'),
(4, 'cliente4@mail.com', 'Laura', 'Calle D', '22222222D', 'premium'),
(5, 'cliente_sp1@mail.com', 'Cliente Procedimiento', 'Calle SQL 123', '12345678Z', 'estandar');

-- =========================================================
-- ARTICULOS
-- =========================================================
INSERT INTO articulos (id_articulo, codigo, descripcion, precio_venta, gastos_envio, tiempo_preparacion) VALUES
(2, 'ART001-EDIT', 'Artículo actualizado', 149.99, 7.50, 48),
(3, 'A003', 'Monitor Samsung', 199.99, 8.00, 24),
(4, 'A004', 'Teclado Logitech', 49.99, 5.00, 24),
(5, 'SP001', 'Artículo desde procedimiento', 99.99, 6.50, 24);

-- =========================================================
-- PEDIDOS
-- =========================================================
INSERT INTO pedidos (id_pedido, id_cliente, id_articulo, cantidad, fecha_hora, estado) VALUES
(2, 2, 2, 2, '2026-03-20 12:34:25', 'PENDIENTE');

-- =========================================================
-- REAJUSTE DE AUTO_INCREMENT
-- =========================================================
ALTER TABLE clientes AUTO_INCREMENT = 6;
ALTER TABLE articulos AUTO_INCREMENT = 6;
ALTER TABLE pedidos AUTO_INCREMENT = 3;