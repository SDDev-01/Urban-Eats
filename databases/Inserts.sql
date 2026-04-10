USE UrbanEats;

-- ============================
--  DEPARTAMENTOS
-- ============================
INSERT INTO Departamentos (CodigoDepartamento, Nombre) VALUES
                                                           (1, 'Cundinamarca'),
                                                           (2, 'Antioquia'),
                                                           (3, 'Valle del Cauca'),
                                                           (4, 'Atlántico'),
                                                           (5, 'Santander');

-- ============================
--  CIUDADES
-- ============================
INSERT INTO Ciudades (CodigoCiudad, CodigoDepartamento, Nombre, Latitud, Longitud) VALUES
                                                                                       (1, 1, 'Bogotá',        4.71099800, -74.07209900),
                                                                                       (2, 2, 'Medellín',      6.24422200, -75.58119900),
                                                                                       (3, 3, 'Cali',          3.45160000, -76.53200000),
                                                                                       (4, 4, 'Barranquilla', 10.96854400, -74.78132100),
                                                                                       (5, 5, 'Bucaramanga',   7.11930000, -73.12270000);

-- ============================
--  ROL
-- ============================
INSERT INTO Rol (CodigoRol, TipoRol) VALUES
                                         (1, 'Cliente'),
                                         (2, 'Repartidor'),
                                         (3, 'Administrador');

-- ============================
--  USUARIO
--  El trigger crear_cliente_automaticamente
--     crea un registro en Cliente por cada fila.
-- ============================
INSERT INTO Usuario (CodigoUsuario, Nombres, Apellidos, Direccion, Telefono, CorreoElectronico, CodigoInfoBancaria) VALUES
                                                                                                    (1,  'Juan Carlos',    'Rodríguez Pérez',    'Av. Caracas 123, Bogotá',         '3001234567', 'juan.rodriguez@email.com', NULL),
                                                                                                    (2,  'María Elena',    'González López',     'Calle 72 #10-34, Bogotá',         '3012345678', 'maria.gonzalez@email.com', NULL),
                                                                                                    (3,  'Carlos Alberto', 'Martínez Sánchez',   'Cra 15 #93-45, Bogotá',           '3023456789', 'carlos.martinez@email.com', NULL),
                                                                                                    (4,  'Ana Patricia',   'Fernández Torres',   'Av. Suba 67-21, Bogotá',          '3034567890', 'ana.fernandez@email.com', NULL),
                                                                                                    (5,  'Luis Fernando',  'Ramírez Castro',     'Calle 80 #45-67, Bogotá',         '3045678901', 'luis.ramirez@email.com', NULL),
                                                                                                    (6,  'Sofía Isabel',   'Herrera Mendoza',    'Cra 7 #45-12, Bogotá',            '3056789012', 'sofia.herrera@email.com', NULL),
                                                                                                    (7,  'Diego Armando',  'Vargas Silva',       'Av. Boyacá 120-33, Bogotá',       '3067890123', 'diego.vargas@email.com', NULL),
                                                                                                    (8,  'Valentina',      'Cruz Morales',       'Calle 100 #15-20, Bogotá',        '3078901234', 'valentina.cruz@email.com', NULL),
                                                                                                    (9,  'Andrés Felipe',  'Reyes Aguilar',      'Cra 11 #82-19, Bogotá',           '3089012345', 'andres.reyes@email.com', NULL),
                                                                                                    (10, 'Camila Andrea',  'Gómez Paredes',      'Av. El Dorado 68-40, Bogotá',     '3090123456', 'camila.gomez@email.com', NULL),
                                                                                                    (11, 'Lucía',          'Paredes Soto',       'Calle 134 #19-50, Bogotá',        '3101111111', 'lucia.paredes@email.com', NULL),
                                                                                                    (12, 'Renato',         'Campos Vera',        'Cra 9 #116-10, Bogotá',           '3112222222', 'renato.campos@email.com', NULL),
                                                                                                    (13, 'Daniela',        'Fuentes Ríos',       'Calle 170 #7-55, Bogotá',         '3123333333', 'daniela.fuentes@email.com', NULL),
                                                                                                    (14, 'Mateo',          'Quispe Bravo',       'Av. Ciudad de Cali 26-90, Bogotá','3134444444', 'mateo.quispe@email.com', NULL),
                                                                                                    (15, 'Isabella',       'Torres Mena',        'Cra 68 #40-21, Bogotá',           '3145555555', 'isabella.torres@email.com', NULL);

-- ============================
--  ROL_USUARIO
-- ============================
INSERT INTO Rol_Usuario (CodigoUsuario, CodigoRol) VALUES
                                                       (1,  1),(2,  1),(3,  1),(4,  1),(5,  1),   -- Clientes
                                                       (6,  2),(7,  2),(8,  2),(9,  2),(10, 2),   -- Repartidores
                                                       (11, 3),(12, 3),(13, 3),(14, 3),(15, 3);   -- Administradores

-- ============================
--  INFORMACIÓN BANCARIA
-- ============================
INSERT INTO InformacionBancaria (NumeroCuenta, Banco, TipoCuenta, TitularCuenta, CodigoUsuario) VALUES
                                                                                                    ('1001234567', 'Bancolombia',  'Ahorro',    'Juan Carlos Rodríguez Pérez',    1),
                                                                                                    ('2001234567', 'Davivienda',   'Corriente', 'María Elena González López',     2),
                                                                                                    ('3001234567', 'Banco Bogotá', 'Ahorro',    'Carlos Alberto Martínez Sánchez',3),
                                                                                                    ('4001234567', 'BBVA',         'Credito',   'Ana Patricia Fernández Torres',  4),
                                                                                                    ('5001234567', 'Nequi',        'Ahorro',    'Luis Fernando Ramírez Castro',   5),
                                                                                                    ('6001234567', 'Bancolombia',  'Ahorro',    'Sofía Isabel Herrera Mendoza',   6),
                                                                                                    ('7001234567', 'Davivienda',   'Corriente', 'Diego Armando Vargas Silva',     7),
                                                                                                    ('8001234567', 'Banco Bogotá', 'Ahorro',    'Valentina Cruz Morales',         8),
                                                                                                    ('9001234567', 'BBVA',         'Credito',   'Andrés Felipe Reyes Aguilar',    9),
                                                                                                    ('1101234567', 'Nequi',        'Ahorro',    'Camila Andrea Gómez Paredes',    10);

-- ============================
--  CLIENTE
--  El trigger ya creó los registros automáticamente.
-- ============================

-- ============================
--  VEHICULO
-- ============================
INSERT INTO Vehiculo (Placa, Licencia, TipoVehiculo, SeguroVehiculo, SOAT) VALUES
                                                                               ('ABC123', 'LIC001', 'Moto',      'Sura',    'SOAT-2025-001'),
                                                                               ('DEF456', 'LIC002', 'Moto',      'Bolívar', 'SOAT-2025-002'),
                                                                               ('GHI789', 'LIC003', 'Carro',     'Sura',    'SOAT-2025-003'),
                                                                               ('JKL012', 'LIC004', 'Moto',      'Liberty', 'SOAT-2025-004'),
                                                                               ('MNO345', 'LIC005', 'Bicicleta', 'Bolívar', 'SOAT-2025-005'),
                                                                               ('PQR678', 'LIC006', 'Moto',      'Sura',    'SOAT-2025-006'),
                                                                               ('STU901', 'LIC007', 'Carro',     'Liberty', 'SOAT-2025-007'),
                                                                               ('VWX234', 'LIC008', 'Moto',      'Bolívar', 'SOAT-2025-008'),
                                                                               ('YZA567', 'LIC009', 'Bicicleta', 'Sura',    'SOAT-2025-009'),
                                                                               ('BCD890', 'LIC010', 'Moto',      'Liberty', 'SOAT-2025-010');

-- ============================
--  REPARTIDOR
-- ============================
INSERT INTO Repartidor (CodigoRepartidor, CodigoUsuario, Placa) VALUES
                                                                       (1,  6,  'ABC123'),
                                                                       (2,  7,  'DEF456'),
                                                                       (3,  8,  'GHI789'),
                                                                       (4,  9,  'JKL012'),
                                                                       (5,  10, 'MNO345'),
                                                                       (6,  6,  'PQR678'),
                                                                       (7,  7,  'STU901'),
                                                                       (8,  8,  'VWX234'),
                                                                       (9,  9,  'YZA567'),
                                                                       (10, 10, 'BCD890');

-- ============================
--  RESTAURANTE
-- ============================
INSERT INTO Restaurante (CodigoRestaurante, CodigoCiudad, Nombre, Ubicacion, Horario, Latitud, Longitud) VALUES
                                                                                                             (1,  1, 'Pollería El Rico Sabor',  'Av. Caracas 123, Bogotá',  '10:00-22:00', 4.71150000, -74.07250000),
                                                                                                             (2,  1, 'Pizzería Don Giuseppe',   'Calle 93 #11-20, Bogotá',  '12:00-23:00', 4.67600000, -74.04890000),
                                                                                                             (3,  1, 'Cevichería La Mar',       'Zona G, Bogotá',           '11:00-18:00', 4.64860000, -74.05730000),
                                                                                                             (4,  1, 'Hamburguesería Big Bite', 'Av. Suba 45, Bogotá',      '11:00-00:00', 4.72810000, -74.09300000),
                                                                                                             (5,  1, 'Taller de Cocina',        'Chapinero, Bogotá',        '13:00-21:00', 4.64800000, -74.06280000),
                                                                                                             (6,  1, 'Sabor Criollo',           'Centro Histórico, Bogotá', '09:00-20:00', 4.59810000, -74.07580000),
                                                                                                             (7,  1, 'Sushi Bar Samurai',       'Usaquén, Bogotá',          '12:00-22:00', 4.69500000, -74.03050000),
                                                                                                             (8,  1, 'Taco Libre',              'Zona T, Bogotá',           '11:00-23:00', 4.66720000, -74.05350000),
                                                                                                             (9,  1, 'Parrilla Argentina',      'Parque de la 93, Bogotá',  '18:00-00:00', 4.67650000, -74.04800000),
                                                                                                             (10, 1, 'Café del Bosque',         'La Calera, Cundinamarca',  '07:00-19:00', 4.72000000, -73.97000000);

-- ============================
--  MENU
-- ============================
INSERT INTO Menu (CodigoMenu, Categoria, CodigoRestaurante) VALUES
                                                                (1,  'Pollo a la Brasa',     1),
                                                                (2,  'Pizzas Tradicionales', 2),
                                                                (3,  'Ceviches y Mariscos',  3),
                                                                (4,  'Hamburguesas',         4),
                                                                (5,  'Platos Gourmet',       5),
                                                                (6,  'Comida Criolla',       6),
                                                                (7,  'Sushi y Rolls',        7),
                                                                (8,  'Tacos Mexicanos',      8),
                                                                (9,  'Carnes a la Parrilla', 9),
                                                                (10, 'Desayunos y Café',     10);

-- ============================
--  PLATO
-- ============================
INSERT INTO Plato (CodigoPlato, Nombre, Descripcion, Precio, TipoComida, Alergenos, Disponibilidad) VALUES
                                                                                                        (1,  'Pollo a la Brasa 1/4', 'Pollo asado con papas fritas y ensalada',         28500.00, 'Principal', 'Ninguno',         'Disponible'),
                                                                                                        (2,  'Pizza Margherita',      'Pizza con mozzarella, tomate y albahaca',         45000.00, 'Principal', 'Gluten, Lácteos', 'Disponible'),
                                                                                                        (3,  'Ceviche de Pescado',    'Pescado marinado en limón con ají y cebolla',     32000.00, 'Entrada',   'Pescado',         'Disponible'),
                                                                                                        (4,  'Hamburguesa Clásica',   'Carne de res, lechuga, tomate y queso',           25900.00, 'Principal', 'Gluten, Lácteos', 'Disponible'),
                                                                                                        (5,  'Lomo Saltado',          'Lomo fino salteado con papas y arroz',            38000.00, 'Principal', 'Ninguno',         'Disponible'),
                                                                                                        (6,  'Ajiaco Bogotano',       'Sopa tradicional de pollo con papas y guasca',    22000.00, 'Principal', 'Ninguno',         'Disponible'),
                                                                                                        (7,  'California Roll',       'Roll de aguacate, pepino y surimi',               28000.00, 'Entrada',   'Pescado, Gluten', 'Disponible'),
                                                                                                        (8,  'Tacos de Carnitas',     'Tres tacos de cerdo con salsa verde y cebolla',   22000.00, 'Principal', 'Gluten',          'Disponible'),
                                                                                                        (9,  'Churrasco Argentino',   'Carne de res a la parrilla con chimichurri',      55000.00, 'Principal', 'Ninguno',         'Disponible'),
                                                                                                        (10, 'Capuccino Artesanal',   'Café espresso doble con leche espumada y cacao', 12500.00, 'Bebida',    'Lácteos',         'Disponible');

-- ============================
--  PLATO_MENU
-- ============================
INSERT INTO Plato_menu (CodigoMenu, CodigoPlato) VALUES
                                                     (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10);

-- ============================
--  ENVIO
--   El trigger crear_pedido_automaticamente
--     insertará automáticamente en Pedido tras cada fila.
-- ============================
INSERT INTO Envio (CodigoEnvio, CodigoCliente, CodigoRepartidor, CodigoRestaurante, Descripcion, FechaEnvio, HoraEntrega) VALUES
                                                                                                                              (1,  1, 1,  1,  '1 Pollo a la Brasa 1/4 con papas', '2025-11-20', '13:30:00'),
                                                                                                                              (2,  2, 2,  2,  '1 Pizza Margherita mediana',        '2025-11-20', '14:15:00'),
                                                                                                                              (3,  3, 3,  3,  '2 Ceviches de Pescado',            '2025-11-21', '12:45:00'),
                                                                                                                              (4,  4, 4,  4,  '1 Hamburguesa Clásica con papas',  '2025-11-21', '19:20:00'),
                                                                                                                              (5,  5, 5,  5,  '1 Lomo Saltado grande',            '2025-11-22', '15:10:00'),
                                                                                                                              (6,  1, 6,  6,  '2 Ajiacos Bogotanos',              '2025-11-22', '20:30:00'),
                                                                                                                              (7,  2, 7,  7,  '3 California Rolls',               '2025-11-23', '16:45:00'),
                                                                                                                              (8,  3, 8,  8,  '2 Tacos de Carnitas',              '2025-11-23', '18:00:00'),
                                                                                                                              (9,  4, 9,  9,  '1 Churrasco Argentino',            '2025-11-24', '14:30:00'),
                                                                                                                              (10, 5, 10, 10, '2 Capuccinos artesanales',         '2025-11-24', '21:15:00');

-- ============================
--  PEDIDO
--  No se inserta manualmente — lo genera el trigger.
--  Solo actualizamos el Estado final.
-- ============================
UPDATE Pedido SET Estado = 'Entregado' WHERE CodigoEnvio IN (1,2,3,4,5,6,7);
UPDATE Pedido SET Estado = 'Cancelado' WHERE CodigoEnvio = 10;
-- Envíos 8 y 9 quedan en 'En Proceso' (valor por defecto del trigger)

-- ============================
--  PAGO

-- ============================
INSERT INTO Pago (CodigoPago, CodigoCliente, CodigoEnvio, Monto, MetodoPago, FechaPago, HoraPago) VALUES
                                                                                                  (1,  1, 1,  28500.00, 'Tarjeta',  '2025-11-20', '12:15:00'),
                                                                                                  (2,  2, 2,  45000.00, 'Efectivo', '2025-11-20', '13:00:00'),
                                                                                                  (3,  3, 3,  64000.00, 'Tarjeta',  '2025-11-21', '11:30:00'),
                                                                                                  (4,  4, 4,  25900.00, 'Efectivo', '2025-11-21', '18:45:00'),
                                                                                                  (5,  5, 5,  38000.00, 'Tarjeta',  '2025-11-22', '14:20:00'),
                                                                                                  (6,  1, 6,  44000.00, 'Efectivo', '2025-11-22', '19:10:00'),
                                                                                                  (7,  2, 7,  84000.00, 'Tarjeta',  '2025-11-23', '15:35:00'),
                                                                                                  (8,  3, 8,  44000.00, 'Efectivo', '2025-11-23', '17:00:00'),
                                                                                                  (9,  4, 9,  55000.00, 'Tarjeta',  '2025-11-24', '13:15:00'),
                                                                                                  (10, 5, 10, 25000.00, 'Efectivo', '2025-11-24', '20:45:00');

-- ============================
--  OPINIONES
-- ============================
INSERT INTO Opiniones (CodigoComentario, CodigoPlato, CodigoCliente, CodigoRepartidor, Opinion, Fecha) VALUES
                                                                                                           (1,  1,  1, 1,  'El pollo estaba perfecto, llegó caliente y a tiempo.',          '2025-11-20'),
                                                                                                           (2,  2,  2, 2,  'Pizza deliciosa, masa crocante. El repartidor muy amable.',     '2025-11-20'),
                                                                                                           (3,  3,  3, 3,  'Ceviche fresco y bien preparado. Excelente servicio.',          '2025-11-21'),
                                                                                                           (4,  4,  4, 4,  'Hamburguesa jugosa, llegó en perfecto estado.',                 '2025-11-21'),
                                                                                                           (5,  5,  5, 5,  'El lomo saltado tenía muy buen sabor. Recomendado.',            '2025-11-22'),
                                                                                                           (6,  6,  1, 6,  'El ajiaco estaba delicioso, muy auténtico.',                    '2025-11-22'),
                                                                                                           (7,  7,  2, 7,  'Rolls frescos y bien empacados. Entrega rápida.',               '2025-11-23'),
                                                                                                           (8,  8,  3, 8,  'Los tacos estaban buenos pero llegaron un poco fríos.',         '2025-11-23'),
                                                                                                           (9,  9,  4, 9,  'El churrasco espectacular, el mejor que he probado en Bogotá.', '2025-11-24'),
                                                                                                           (10, 10, 5, 10, 'El capuccino llegó frío, esperaba mejor temperatura.',          '2025-11-24');
-- ============================
--  USUARIO - Asignar CodigoInfoBancaria después de crear InformacionBancaria
--  Se usa JOIN con CodigoUsuario para que no dependa del AUTO_INCREMENT.
-- ============================
UPDATE Usuario u
    JOIN InformacionBancaria ib ON ib.CodigoUsuario = u.CodigoUsuario
SET u.CodigoInfoBancaria = ib.CodigoInfoBancaria;
