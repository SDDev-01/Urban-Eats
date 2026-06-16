USE urbaneats;

-- ============================
-- DEPARTAMENTO Y CIUDAD
-- ============================
INSERT INTO departamento (CodigoDepartamento, Nombre) VALUES
(1, 'Cundinamarca'), (2, 'Antioquia'), (3, 'Valle del Cauca'), (4, 'Atlántico'), (5, 'Santander');

INSERT INTO ciudad (CodigoCiudad, CodigoDepartamento, Nombre, Latitud, Longitud) VALUES
(1, 1, 'Bogotá', 4.71099800, -74.07209900),
(2, 2, 'Medellín', 6.24422200, -75.58119900),
(3, 3, 'Cali', 3.45160000, -76.53200000),
(4, 4, 'Barranquilla', 10.96854400, -74.78132100),
(5, 5, 'Bucaramanga', 7.11930000, -73.12270000);

-- ============================
-- ROL Y USUARIO (Con Pass Corta)
-- ============================
INSERT INTO rol (CodigoRol, NombreRol, DescripcionRol) VALUES
(1, 'Cliente', 'Hace pedidos'), (2, 'Repartidor', 'Entrega'), (3, 'Tecnico', 'Gestión'), (4, 'Gerente', 'Admin');

INSERT INTO usuario (CodigoUsuario, Correo, Password, Nombres, Apellidos) VALUES
(1, '1@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Juan Carlos', 'Rodríguez'),
(2, '2@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'María Elena', 'González'),
(3, '3@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Carlos Alberto', 'Martínez'),
(4, '4@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Ana Patricia', 'Fernández'),
(5, '5@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Luis Fernando', 'Ramírez'),
(6, '6@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Lucy', 'Emma'),
(7, '7@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Diego Armando', 'Vargas'),
(8, '8@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Valentina', 'Cruz'),
(9, '9@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Andrés Felipe', 'Reyes'),
(10, '10@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Camila Andrea', 'Gómez'),
(11, '11@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Lucía', 'Paredes'),
(12, '12@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Renato', 'Campos'),
(13, '13@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Daniela', 'Fuentes'),
(14, '14@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Mateo', 'Quispe'),
(15, '15@email.com', '\$2y\$12\$Y4Ds3p2Zlz9tKsuuqpc49O18b0wTCWFKeC/KL6EL3Y2ZeDpyIvAA2', 'Isabella', 'Torres');

-- ============================
-- ENTIDADES DERIVADAS DE USUARIO
-- ============================
INSERT INTO direccion (CodigoDireccion, Direccion, CodigoUsuario) VALUES
(1, 'Av. Caracas 123', 1), 
(2, 'Calle 72 #10-34', 2), 
(3, 'Cra 15 #93-45', 3), 
(4, 'Av. Suba 67-21', 4), 
(5, 'Calle 80 #45-67', 5),
(6, 'Cra 7 #45-12', 6), 
(7, 'Av. Boyacá 120-33', 7), 
(8, 'Calle 100 #15-20', 8), 
(9, 'Cra 11 #82-19', 9), 
(10, 'Av. El Dorado 68-40', 10),
(11, 'Calle 134 #19-50', 11), 
(12, 'Cra 9 #116-10', 12), 
(13, 'Calle 170 #7-55', 13), 
(14, 'Av. Ciudad de Cali 26-90', 14), 
(15, 'Cra 68 #40-21', 15);

INSERT INTO telefono (CodigoTelefono, Telefono, CodigoUsuario) VALUES
(1, '3001234567', 1), (2, '3012345678', 2), (3, '3023456789', 3), (4, '3034567890', 4), (5, '3045678901', 5),
(6, '3056789012', 6), (7, '3067890123', 7), (8, '3078901234', 8), (9, '3089012345', 9), (10, '3090123456', 10),
(11, '3101111111', 11), (12, '3112222222', 12), (13, '3123333333', 13), (14, '3134444444', 14), (15, '3145555555', 15);

INSERT INTO rol_usuario (CodigoUsuario, CodigoRol) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 2), (7, 2), (8, 2), (9, 2), (10, 2), (11, 4), (12, 4), (13, 4), (14, 3), (15, 3);

-- ============================
-- ROLES ESPECÍFICOS Y NEGOCIO
-- ============================
INSERT INTO repartidor (CodigoRepartidor, CodigoUsuario) VALUES (1, 6), (2, 7), (3, 8), (4, 9), (5, 10);

INSERT INTO vehiculo (Placa, CodigoRepartidor, TipoVehiculo, SeguroVehiculo, SOAT) VALUES
('ABC123', 1, 'Moto', 'Sura', 'SOAT1'), 
('DEF456', 2, 'Moto', 'Bolívar', 'SOAT2'),
('GHI789', 3, 'Carro', 'Sura', 'SOAT3'), 
('JKL012', 4, 'Moto', 'Liberty', 'SOAT4'),
('MNO345', 5, 'Bici', 'Bolívar', 'SOAT5');

INSERT INTO gerente (CodigoGerente, CodigoUsuario) VALUES 
(1, 11), 
(2, 12), 
(3, 13);

INSERT INTO restaurante (CodigoRestaurante, CodigoCiudad, CodigoGerente, Nombre, Direccion, Horario) VALUES
(1, 1, 1, 'Pollería El Rico Sabor', 'Av. Caracas 123', '10:00-22:00'),
(2, 2, 2, 'Pizzería Don Giuseppe', 'Calle 93 #11-20', '12:00-23:00'),
(3, 3, 3, 'Sushi Pacífico', 'Cra 4 #10-20', '11:00-22:30'),
(4, 4, 1, 'Arepas La Costeña', 'Calle 72 #45-10', '08:00-21:00'),
(5, 5, 2, 'Parrilla Santandereana', 'Av. 27 #36-50', '11:30-22:00');

-- ============================
-- MENÚ Y PLATOS
-- ============================
INSERT INTO menu (CodigoMenu, Categoria, CodigoRestaurante) VALUES
(1, 'Pollo', 1), (2, 'Bebidas', 1), (3, 'Pizzas', 2), (4, 'Pastas', 2), (5, 'Sushi', 3), (6, 'Entradas', 3), (7, 'Arepas', 4), (8, 'Bebidas', 4), (9, 'Carnes', 5), (10,'Bebidas', 5);

INSERT INTO plato (CodigoPlato, CodigoMenu, Nombre, Descripcion, Precio, TipoComida, Disponibilidad) VALUES
(1, 1, 'Pollo asado 1/4', 'Con papa', 18000.00, 'Pollo', 'Disponible'),
(2, 2, 'Limonada', 'Con hielo', 6000.00, 'Bebida', 'Disponible'),
(3, 3, 'Pizza margarita', 'Mozzarella', 28000.00, 'Pizza', 'Disponible'),
(4, 4, 'Lasagna', 'Boloñesa', 32000.00, 'Pasta', 'Disponible'),
(5, 5, 'Roll california', 'Cangrejo', 26000.00, 'Sushi', 'Disponible'),
(6, 6, 'Gyozas', 'Empanaditas', 20000.00, 'Entrada', 'Disponible'),
(7, 7, 'Arepa con queso', 'Rellena', 10000.00, 'Arepa', 'Disponible'),
(8, 8, 'Gaseosa', '350ml', 5000.00, 'Bebida', 'Disponible'),
(9, 9, 'Churrasco', 'A la parrilla', 35000.00, 'Carne', 'Disponible'),
(10, 10, 'Cerveza', 'Nacional', 7000.00, 'Bebida', 'Disponible');

INSERT INTO alergeno (CodigoAlergeno, CodigoPlato, Nombre) VALUES
(1, 3, 'Gluten'), (2, 3, 'Lácteos'), (3, 4, 'Gluten'), (4, 4, 'Huevo'), (5, 4, 'Lácteos'), (6, 5, 'Mariscos'), (7, 6, 'Soya'), (8, 7, 'Lácteos'), (9, 10, 'Gluten');

-- ============================
-- ENVÍOS, PAGOS Y OPINIONES
-- ============================
INSERT INTO envio (CodigoEnvio, CodigoCliente, CodigoRepartidor, CodigoRestaurante, Descripcion, FechaEnvio, HoraEntrega)
SELECT 1, CodigoCliente, 1, 1, 'Pedido 1', '2026-05-20', '13:20:00' FROM cliente WHERE CodigoUsuario = 1 UNION ALL
SELECT 2, CodigoCliente, 2, 2, 'Pedido 2', '2026-05-20', '20:10:00' FROM cliente WHERE CodigoUsuario = 2 UNION ALL
SELECT 3, CodigoCliente, 3, 3, 'Pedido 3', '2026-05-21', '19:45:00' FROM cliente WHERE CodigoUsuario = 3 UNION ALL
SELECT 4, CodigoCliente, 4, 4, 'Pedido 4', '2026-05-21', '09:15:00' FROM cliente WHERE CodigoUsuario = 4 UNION ALL
SELECT 5, CodigoCliente, 5, 5, 'Pedido 5', '2026-05-22', '14:05:00' FROM cliente WHERE CodigoUsuario = 5;

INSERT INTO pago (CodigoPago, CodigoCliente, CodigoEnvio, Monto, FechaPago, HoraPago, EstadoPago) VALUES
(1, 1, 1, 24000.00, '2026-05-20', '13:00:00', 'Aceptado'), (2, 2, 2, 28000.00, '2026-05-20', '19:40:00', 'Aceptado'),
(3, 3, 3, 26000.00, '2026-05-21', '19:10:00', 'Aceptado'), (4, 4, 4, 10000.00, '2026-05-21', '08:50:00', 'Rechazado'),
(5, 5, 5, 35000.00, '2026-05-22', '13:30:00', 'Aceptado');

INSERT INTO transaccion (TransaccionID, CodigoPago, MetodoPago, BancoNombre, CUS, CodigoRespuesta) VALUES
('TX-0001', 1, 'Tarjeta', 'Bancolombia', 'CUS1', '00'), ('TX-0002', 2, 'PSE', 'Davivienda', 'CUS2', '00'),
('TX-0003', 3, 'Tarjeta', 'BBVA', 'CUS3', '00'), ('TX-0004', 4, 'Efectivo', 'N/A', 'CUS4', '05'), ('TX-0005', 5, 'Tarjeta', 'Bancolombia', 'CUS5', '00');

INSERT INTO opinion (CodigoOpinion, CodigoPlato, CodigoCliente, CodigoRepartidor, Opinion, Fecha) VALUES
(1, 1, 1, 1, 'Caliente y rico.', '2026-05-20'), (2, 3, 2, 2, 'Excelente.', '2026-05-20'),
(3, 5, 3, 3, 'Muy fresco.', '2026-05-21'), (4, 7, 4, 4, 'Un poco fría.', '2026-05-21'), (5, 9, 5, 5, 'Recomendado.', '2026-05-22');
