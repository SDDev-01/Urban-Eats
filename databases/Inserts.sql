USE UrbanEats;

-- ============================
-- DEPARTAMENTO
-- ============================
INSERT INTO Departamento (CodigoDepartamento, Nombre) VALUES
(1, 'Cundinamarca'),
(2, 'Antioquia'),
(3, 'Valle del Cauca'),
(4, 'Atlántico'),
(5, 'Santander');

-- ============================
-- CIUDAD
-- ============================
INSERT INTO Ciudad (CodigoCiudad, CodigoDepartamento, Nombre, Latitud, Longitud) VALUES
(1, 1, 'Bogotá', 4.71099800, -74.07209900),
(2, 2, 'Medellín', 6.24422200, -75.58119900),
(3, 3, 'Cali', 3.45160000, -76.53200000),
(4, 4, 'Barranquilla', 10.96854400, -74.78132100),
(5, 5, 'Bucaramanga', 7.11930000, -73.12270000);

-- ============================
-- ROL
-- ============================
INSERT INTO Rol (CodigoRol, NombreRol, DescripcionRol) VALUES
(1, 'Cliente', 'Usuario que realiza pedidos'),
(2, 'Repartidor', 'Usuario que entrega pedidos'),
(3, 'Tecnico', 'Usuario con permisos de gestión'),
(4, 'Gerente', 'Usuario Administrador de Restaurantes');

-- ============================
-- USUARIO
-- (Trigger crea Cliente automáticamente por cada Usuario)
-- ============================
INSERT INTO Usuario (CodigoUsuario, Nombres, Apellidos, Direccion, Telefono, CorreoElectronico) VALUES
(1, 'Juan Carlos', 'Rodríguez Pérez', 'Av. Caracas 123, Bogotá', '3001234567', 'juan.rodriguez@email.com'),
(2, 'María Elena', 'González López', 'Calle 72 #10-34, Bogotá', '3012345678', 'maria.gonzalez@email.com'),
(3, 'Carlos Alberto', 'Martínez Sánchez', 'Cra 15 #93-45, Bogotá', '3023456789', 'carlos.martinez@email.com'),
(4, 'Ana Patricia', 'Fernández Torres', 'Av. Suba 67-21, Bogotá', '3034567890', 'ana.fernandez@email.com'),
(5, 'Luis Fernando', 'Ramírez Castro', 'Calle 80 #45-67, Bogotá', '3045678901', 'luis.ramirez@email.com'),
(6, 'Sofía Isabel', 'Herrera Mendoza', 'Cra 7 #45-12, Bogotá', '3056789012', 'sofia.herrera@email.com'),
(7, 'Diego Armando', 'Vargas Silva', 'Av. Boyacá 120-33, Bogotá', '3067890123', 'diego.vargas@email.com'),
(8, 'Valentina', 'Cruz Morales', 'Calle 100 #15-20, Bogotá', '3078901234', 'valentina.cruz@email.com'),
(9, 'Andrés Felipe', 'Reyes Aguilar', 'Cra 11 #82-19, Bogotá', '3089012345', 'andres.reyes@email.com'),
(10, 'Camila Andrea', 'Gómez Paredes', 'Av. El Dorado 68-40, Bogotá', '3090123456', 'camila.gomez@email.com'),
(11, 'Lucía', 'Paredes Soto', 'Calle 134 #19-50, Bogotá', '3101111111', 'lucia.paredes@email.com'),
(12, 'Renato', 'Campos Vera', 'Cra 9 #116-10, Bogotá', '3112222222', 'renato.campos@email.com'),
(13, 'Daniela', 'Fuentes Ríos', 'Calle 170 #7-55, Bogotá', '3123333333', 'daniela.fuentes@email.com'),
(14, 'Mateo', 'Quispe Bravo', 'Av. Ciudad de Cali 26-90, Bogotá', '3134444444', 'mateo.quispe@email.com'),
(15, 'Isabella', 'Torres Mena', 'Cra 68 #40-21, Bogotá', '3145555555', 'isabella.torres@email.com');

-- ============================
-- ROL_USUARIO
-- ============================
INSERT INTO Rol_Usuario (CodigoUsuario, CodigoRol) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1),       -- Clientes
(6, 2), (7, 2), (8, 2), (9, 2), (10, 2),      -- Repartidores
(11, 4), (12, 4), (13, 4),                    -- Gerentes
(14, 3), (15, 3);                             -- Técnicos 

-- ============================
-- REPARTIDOR
-- ============================
INSERT INTO Repartidor (CodigoRepartidor, CodigoUsuario) VALUES
(1, 6), (2, 7), (3, 8), (4, 9), (5, 10);

-- ============================
-- VEHICULO
-- ============================
INSERT INTO Vehiculo (Placa, CodigoRepartidor, Licencia, TipoVehiculo, SeguroVehiculo, SOAT) VALUES
('ABC123', 1, 'LIC001', 'Moto', 'Sura', 'SOAT-2025-001'),
('DEF456', 2, 'LIC002', 'Moto', 'Bolívar', 'SOAT-2025-002'),
('GHI789', 3, 'LIC003', 'Carro', 'Sura', 'SOAT-2025-003'),
('JKL012', 4, 'LIC004', 'Moto', 'Liberty', 'SOAT-2025-004'),
('MNO345', 5, 'LIC005', 'Bicicleta', 'Bolívar', 'SOAT-2025-005');

-- ============================
-- GERENTE
-- ============================
INSERT INTO Gerente (CodigoGerente, CodigoUsuario) VALUES
(1, 11),
(2, 12),
(3, 13);

-- ============================
-- RESTAURANTE
-- ============================
INSERT INTO Restaurante (CodigoRestaurante, CodigoCiudad, CodigoGerente, Nombre, Ubicacion, Horario, Latitud, Longitud) VALUES
(1, 1, 1, 'Pollería El Rico Sabor', 'Av. Caracas 123, Bogotá', '10:00-22:00', 4.71150000, -74.07250000),
(2, 2, 2, 'Pizzería Don Giuseppe', 'Calle 93 #11-20, Medellín', '12:00-23:00', 6.24400000, -75.58100000),
(3, 3, 3, 'Sushi Pacífico', 'Cra 4 #10-20, Cali', '11:00-22:30', 3.45190000, -76.53180000),
(4, 4, 1, 'Arepas La Costeña', 'Calle 72 #45-10, Barranquilla', '08:00-21:00', 10.96870000, -74.78110000),
(5, 5, 2, 'Parrilla Santandereana', 'Av. 27 #36-50, Bucaramanga', '11:30-22:00', 7.11950000, -73.12250000);
-- ============================
-- MENU
-- ============================
INSERT INTO Menu (CodigoMenu, Categoria, CodigoRestaurante) VALUES
(1, 'Pollo',    1),
(2, 'Bebidas',  1),
(3, 'Pizzas',   2),
(4, 'Pastas',   2),
(5, 'Sushi',    3),
(6, 'Entradas', 3),
(7, 'Arepas',   4),
(8, 'Bebidas',  4),
(9, 'Carnes',   5),
(10,'Bebidas',  5);

-- ============================
-- PLATO
-- ============================
INSERT INTO Plato (CodigoPlato, Nombre, Descripcion, Precio, TipoComida, Disponibilidad) VALUES
(1,  'Pollo asado 1/4',  'Porción de pollo asado con papa', 18000.00, 'Pollo',  'Disponible'),
(2,  'Limonada natural', 'Limonada con hielo',               6000.00, 'Bebida', 'Disponible'),
(3,  'Pizza margarita',  'Queso mozzarella y albahaca',     28000.00, 'Pizza',  'Disponible'),
(4,  'Lasagna boloñesa', 'Lasagna tradicional',             32000.00, 'Pasta',  'Disponible'),
(5,  'Roll california',  'Cangrejo, aguacate y pepino',     26000.00, 'Sushi',  'Disponible'),
(6,  'Gyozas',           'Empanaditas japonesas',           20000.00, 'Entrada','Disponible'),
(7,  'Arepa con queso',  'Arepa rellena con queso',         10000.00, 'Arepa',  'Disponible'),
(8,  'Gaseosa',          'Bebida gaseosa 350ml',             5000.00, 'Bebida', 'Disponible'),
(9,  'Churrasco',        'Carne a la parrilla con ensalada',35000.00, 'Carne',  'Disponible'),
(10, 'Cerveza',          'Cerveza nacional',                 7000.00, 'Bebida', 'Disponible');

-- ============================
-- ALERGENO (tabla)
-- ============================
INSERT INTO Alergeno (CodigoAlergeno, CodigoPlato, Nombre) VALUES
(1, 3, 'Gluten'),
(2, 3, 'Lácteos'),
(3, 4, 'Gluten'),
(4, 4, 'Huevo'),
(5, 4, 'Lácteos'),
(6, 5, 'Mariscos'),
(7, 6, 'Soya'),
(8, 7, 'Lácteos'),
(9, 10,'Gluten');

-- ============================
-- PLATO_MENU (N:N)
-- ============================
INSERT INTO Plato_menu (CodigoMenu, CodigoPlato) VALUES
(1, 1), (2, 2),
(3, 3), (4, 4),
(5, 5), (6, 6),
(7, 7), (8, 8),
(9, 9), (10,10);

-- ============================
-- ENVIO
-- (Trigger crea Pedido automáticamente)
-- Se consulta CodigoCliente por CodigoUsuario para no depender del AUTO_INCREMENT.
-- ============================
INSERT INTO Envio (CodigoEnvio, CodigoCliente, CodigoRepartidor, CodigoRestaurante, Descripcion, FechaEnvio, HoraEntrega)
SELECT 1, CodigoCliente, 1, 1, 'Pedido de pollo y limonada',   '2026-05-20', '13:20:00' FROM Cliente WHERE CodigoUsuario = 1 UNION ALL
SELECT 2, CodigoCliente, 2, 2, 'Pedido de pizza margarita',    '2026-05-20', '20:10:00' FROM Cliente WHERE CodigoUsuario = 2 UNION ALL
SELECT 3, CodigoCliente, 3, 3, 'Pedido de sushi california',   '2026-05-21', '19:45:00' FROM Cliente WHERE CodigoUsuario = 3 UNION ALL
SELECT 4, CodigoCliente, 4, 4, 'Pedido de arepa con queso',    '2026-05-21', '09:15:00' FROM Cliente WHERE CodigoUsuario = 4 UNION ALL
SELECT 5, CodigoCliente, 5, 5, 'Pedido de churrasco',          '2026-05-22', '14:05:00' FROM Cliente WHERE CodigoUsuario = 5;

-- ============================
-- PAGO
-- ============================
INSERT INTO Pago (CodigoPago, CodigoCliente, CodigoEnvio, Monto, FechaPago, HoraPago, EstadoPago) VALUES
(1, 1, 1, 24000.00, '2026-05-20', '13:00:00', 'Aceptado'),
(2, 2, 2, 28000.00, '2026-05-20', '19:40:00', 'Aceptado'),
(3, 3, 3, 26000.00, '2026-05-21', '19:10:00', 'Aceptado'),
(4, 4, 4, 10000.00, '2026-05-21', '08:50:00', 'Rechazado'),
(5, 5, 5, 35000.00, '2026-05-22', '13:30:00', 'Aceptado');

-- ============================
-- TRANSACCION (1:1 con Pago por UNIQUE(CodigoPago))
-- ============================
INSERT INTO Transaccion (TransaccionID, CodigoPago, MetodoPago, BancoNombre, CUS, CodigoRespuesta) VALUES
('TX-0001', 1, 'Tarjeta',   'Bancolombia', 'CUS-1001', '00'),
('TX-0002', 2, 'PSE',       'Davivienda',  'CUS-1002', '00'),
('TX-0003', 3, 'Tarjeta',   'BBVA',        'CUS-1003', '00'),
('TX-0004', 4, 'Efectivo',  'N/A',         'CUS-1004', '05'),
('TX-0005', 5, 'Tarjeta',   'Bancolombia', 'CUS-1005', '00');

-- ============================
-- OPINION
-- ============================
INSERT INTO Opinion (CodigoComentario, CodigoPlato, CodigoCliente, CodigoRepartidor, Opinion, Fecha) VALUES
(1, 1, 1, 1, 'Muy buen sabor y llegó caliente.', '2026-05-20'),
(2, 3, 2, 2, 'La pizza estaba excelente.',       '2026-05-20'),
(3, 5, 3, 3, 'El sushi muy fresco.',             '2026-05-21'),
(4, 7, 4, 4, 'Rica pero un poco fría.',          '2026-05-21'),
(5, 9, 5, 5, 'Carne en su punto. Recomendado.',  '2026-05-22');

-- ============================
-- NO INSERTAR EN Pedido:
-- se crea automáticamente por trigger al insertar Envio.
-- ============================