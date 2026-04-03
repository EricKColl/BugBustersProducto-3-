USE producto3;

-- =========================================================
-- DATOS DE PRUEBA REALES
-- Basados en el estado actual de la base de datos
-- =========================================================
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
-- =========================================================
INSERT INTO articulos (codigo, descripcion, precio_venta, gastos_envio, tiempo_preparacion) VALUES
('A001', 'Monitor Samsung 27" QHD', 249.99, 9.50, 24),
('A002', 'Teclado Logitech K120 USB', 19.99, 4.99, 24),
('A003', 'Ratón Logitech G502 Gaming', 59.99, 5.99, 24),
('A004', 'Portátil Dell 15 5520', 799.99, 12.50, 48),
('A005', 'Auriculares Sony WH-1000XM5', 349.99, 6.99, 24),
('A006', 'Disco SSD Samsung 1TB', 109.99, 5.50, 24);

-- =========================================================
-- PEDIDOS
-- =========================================================
INSERT INTO pedidos (id_cliente, id_articulo, cantidad, fecha_hora, estado) VALUES
(1, 1, 2, '2026-03-20 12:34:25', 'PENDIENTE'),
(2, 3, 1, '2026-03-21 09:15:10', 'ENVIADO'),
(3, 2, 1, '2026-03-21 18:42:03', 'PENDIENTE'),
(1, 4, 1, '2026-03-22 11:05:47', 'ENVIADO'),
(4, 5, 2, '2026-03-22 16:20:30', 'PENDIENTE'),
(2, 6, 1, '2026-03-23 10:10:10', 'ENVIADO');

-- =========================================================
-- PARA LIMPIEZA TOTAL (Quitar '--' para ejecutar)
-- =========================================================
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE pedidos;
-- TRUNCATE TABLE articulos;
-- TRUNCATE TABLE clientes;
-- SET FOREIGN_KEY_CHECKS = 1;