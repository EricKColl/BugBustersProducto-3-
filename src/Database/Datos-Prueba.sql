-- =========================================================
-- BUGBUSTERS · PRODUCTO 4
-- IMPLEMENTACIÓN MEDIANTE ORM
-- DATOS DE PRUEBA
-- =========================================================
-- Este script inserta datos de prueba iniciales para el
-- Producto 4, manteniendo la misma base funcional del
-- Producto 3 como punto de partida.
--
-- Orden recomendado de ejecución:
--   1. Tablas Base de Datos.sql
--   2. Procedimientos almacenados.sql
--   3. Datos-Prueba.sql
--
-- Importante:
--   - Los artículos se insertan con stock inicial
--   - Los pedidos se crean mediante el procedimiento
--     insertar_pedido, por lo que el stock se descuenta
--     automáticamente y queda consistente
-- =========================================================

USE producto4;

-- =========================================================
-- CLIENTES
-- =========================================================
INSERT INTO clientes (email, nombre, domicilio, nif, tipo_cliente) VALUES
('maria.garcia@bugbusters.com', 'María García López', 'Calle Alcalá 45, Madrid', '12345678A', 'premium'),
('juan.perez@bugbusters.com', 'Juan Pérez Martínez', 'Avenida Diagonal 320, Barcelona', '23456789B', 'estandar'),
('laura.sanchez@bugbusters.com', 'Laura Sánchez Ruiz', 'Calle Gran Vía 12, Madrid', '34567890C', 'premium'),
('carlos.lopez@bugbusters.com', 'Carlos López Fernández', 'Calle Colón 8, Valencia', '45678901D', 'estandar'),
('ana.martin@bugbusters.com', 'Ana Martín Gómez', 'Paseo de la Castellana 150, Madrid', '56789012E', 'premium'),
('david.rodriguez@bugbusters.com', 'David Rodríguez Torres', 'Calle Larios 22, Málaga', '67890123F', 'estandar');

-- =========================================================
-- ARTICULOS
-- Stock inicial antes de descontar los pedidos históricos
-- =========================================================
INSERT INTO articulos (
    codigo,
    descripcion,
    precio_venta,
    gastos_envio,
    tiempo_preparacion,
    cantidad_disponible
) VALUES
('A001', 'Monitor Samsung 27" QHD', 249.99, 9.50, 10, 12),
('A002', 'Teclado Logitech K120 USB', 19.99, 4.99, 20, 30),
('A003', 'Ratón Logitech G502 Gaming', 59.99, 5.99, 10, 20),
('A004', 'Portátil Dell 15 5520', 799.99, 12.50, 30, 8),
('A005', 'Auriculares Sony WH-1000XM5', 349.99, 6.99, 10, 15),
('A006', 'Disco SSD Samsung 1TB', 109.99, 5.50, 20, 25);

-- =========================================================
-- PEDIDOS
-- Se insertan mediante procedimiento para que el stock
-- se descuente automáticamente y quede coherente
-- =========================================================
CALL insertar_pedido(1, 1, 2, '2026-03-20 12:34:25', 'ENVIADO', @pedido_1);
CALL insertar_pedido(2, 3, 1, '2026-03-21 09:15:10', 'ENVIADO', @pedido_2);
CALL insertar_pedido(3, 2, 1, '2026-03-21 18:42:03', 'ENVIADO', @pedido_3);
CALL insertar_pedido(1, 4, 1, '2026-03-22 11:05:47', 'ENVIADO', @pedido_4);
CALL insertar_pedido(4, 5, 2, '2026-03-22 16:20:30', 'ENVIADO', @pedido_5);
CALL insertar_pedido(2, 6, 1, '2026-03-23 10:10:10', 'ENVIADO', @pedido_6);

-- =========================================================
-- CONSULTAS DE COMPROBACION OPCIONALES
-- =========================================================
-- SELECT * FROM clientes;
-- SELECT * FROM articulos;
-- SELECT * FROM pedidos;

-- =========================================================
-- LIMPIEZA TOTAL OPCIONAL
-- Quitar '--' solo si quieres vaciar completamente los datos
-- =========================================================
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE pedidos;
-- TRUNCATE TABLE articulos;
-- TRUNCATE TABLE clientes;
-- SET FOREIGN_KEY_CHECKS = 1;