USE UrbanEats;

-- ============================
--        TABLA PERSONA
-- ============================
INSERT INTO Persona (Nombres, Apellidos, Direccion, Telefono, CorreoElectronico) VALUES
                                                                                    ('Juan Carlos', 'Rodríguez Pérez', 'Av. Caracas 123, Bogotá', '3001234567', 'juan.rodriguez@email.com'),
                                                                                    ('María Elena', 'González López', 'Calle 72 #10-34, Bogotá', '3012345678', 'maria.gonzalez@email.com'),
                                                                                    ('Carlos Alberto', 'Martínez Sánchez', 'Cra 15 #93-45, Bogotá', '3023456789', 'carlos.martinez@email.com'),
                                                                                    ('Ana Patricia', 'Fernández Torres', 'Av. Suba 67-21, Bogotá', '3034567890', 'ana.fernandez@email.com'),
                                                                                    ('Luis Fernando', 'Ramírez Castro', 'Calle 80 #45-67, Bogotá', '3045678901', 'luis.ramirez@email.com'),
                                                                                    ('Sofía Isabel', 'Herrera Mendoza', 'Cra 7 #45-12, Bogotá', '3056789012', 'sofia.herrera@email.com'),
                                                                                    ('Diego Armando', 'Vargas Silva', 'Av. Boyacá 120-33, Bogotá', '3067890123', 'diego.vargas@email.com'),
                                                                                    ('Valentina Rose', 'Cruz Morales', 'Calle 100 #15-20, Bogotá', '3078901234', 'valentina.cruz@email.com'),
                                                                                    ('Andrés Felipe', 'Reyes Aguilar', 'Cra 11 #82-19, Bogotá', '3089012345', 'andres.reyes@email.com'),
                                                                                    ('Camila Andrea', 'Gómez Paredes', 'Av. El Dorado 68-40, Bogotá', '3090123456', 'camila.gomez@email.com');

-- ============================
--  TABLA INFORMACIÓN BANCARIA
-- ============================
INSERT INTO InformacionBancaria (NumeroCuenta, Banco, TipoCuenta, TitularCuenta, CodigoPersona) VALUES
                                                                                                    ('1234567890', 'BCP', 'Ahorros', 'Juan Carlos Rodríguez Pérez', 1),
                                                                                                    ('2345678901', 'Interbank', 'Corriente', 'María Elena González López', 2),
                                                                                                    ('3456789012', 'BBVA', 'Ahorros', 'Carlos Alberto Martínez Sánchez', 3),
                                                                                                    ('4567890123', 'Scotiabank', 'Corriente', 'Ana Patricia Fernández Torres', 4),
                                                                                                    ('5678901234', 'BCP', 'Ahorros', 'Luis Fernando Ramírez Castro', 5),
                                                                                                    ('6789012345', 'Interbank', 'Corriente', 'Sofía Isabel Herrera Mendoza', 6),
                                                                                                    ('7890123456', 'BBVA', 'Ahorros', 'Diego Armando Vargas Silva', 7),
                                                                                                    ('8901234567', 'Scotiabank', 'Corriente', 'Valentina Rose Cruz Morales', 8),
                                                                                                    ('9012345678', 'BCP', 'Ahorros', 'Andrés Felipe Reyes Aguilar', 9),
                                                                                                    ('0123456789', 'Interbank', 'Corriente', 'Camila Andrea Gómez Paredes', 10);

-- ============================
--        TABLA CLIENTE
-- ============================
INSERT INTO Cliente (CodigoPersona) VALUES
                                        (1), (2), (3), (4), (5), (6), (7), (8), (9), (10);

-- ============================
--        TABLA VEHICULO
-- ============================
INSERT INTO Vehiculo (Placa, Licencia, TipoVehiculo, SeguroVehiculo, SOAT) VALUES
                                                                               ('ABC-123', 'LIC-001', 'Moto', 'Seguro La Positiva', 'SOAT-2025-001'),
                                                                               ('DEF-456', 'LIC-002', 'Moto', 'Seguro Rímac', 'SOAT-2025-002'),
                                                                               ('GHI-789', 'LIC-003', 'Auto', 'Seguro La Positiva', 'SOAT-2025-003'),
                                                                               ('JKL-012', 'LIC-004', 'Moto', 'Seguro Pacífico', 'SOAT-2025-004'),
                                                                               ('MNO-345', 'LIC-005', 'Auto', 'Seguro Rímac', 'SOAT-2025-005'),
                                                                               ('PQR-678', 'LIC-006', 'Moto', 'Seguro La Positiva', 'SOAT-2025-006'),
                                                                               ('STU-901', 'LIC-007', 'Moto', 'Seguro Pacífico', 'SOAT-2025-007'),
                                                                               ('VWX-234', 'LIC-008', 'Auto', 'Seguro Rímac', 'SOAT-2025-008'),
                                                                               ('YZA-567', 'LIC-009', 'Moto', 'Seguro La Positiva', 'SOAT-2025-009'),
                                                                               ('BCD-890', 'LIC-010', 'Moto', 'Seguro Pacífico', 'SOAT-2025-010');

-- ============================
--        TABLA REPARTIDOR
-- ============================
INSERT INTO Repartidor (CodigoPersona, Placa) VALUES
                                                  (1, 'ABC-123'),
                                                  (2, 'DEF-456'),
                                                  (3, 'GHI-789'),
                                                  (4, 'JKL-012'),
                                                  (5, 'MNO-345'),
                                                  (6, 'PQR-678'),
                                                  (7, 'STU-901'),
                                                  (8, 'VWX-234'),
                                                  (9, 'YZA-567'),
                                                  (10, 'BCD-890');

-- ============================
--        TABLA RESTAURANTE
-- ============================
INSERT INTO Restaurante (Nombre, Ubicacion, Horario, Latitud, Longitud, CodigoCiudad) VALUES
                                                                                          ('Pollería El Rico Sabor', 'Av. Caracas 123, Bogotá', '10:00 AM - 10:00 PM', 4.7115, -74.0725, 1),
                                                                                          ('Pizzería Don Giuseppe', 'Calle 93 #11-20, Bogotá', '12:00 PM - 11:00 PM', 4.6760, -74.0489, 1),
                                                                                          ('Cevichería La Mar', 'Zona G, Bogotá', '11:00 AM - 6:00 PM', 4.6486, -74.0573, 1),
                                                                                          ('Hamburguesería Big Bite', 'Av. Suba 45, Bogotá', '11:00 AM - 12:00 AM', 4.7281, -74.0930, 1),
                                                                                          ('Taller de Cocina', 'Chapinero, Bogotá', '1:00 PM - 9:00 PM', 4.6480, -74.0628, 1),
                                                                                          ('Sabor Criollo', 'Centro Histórico, Bogotá', '9:00 AM - 8:00 PM', 4.5981, -74.0758, 1),
                                                                                          ('Sushi Bar Samurai', 'Usaquén, Bogotá', '12:00 PM - 10:00 PM', 4.6950, -74.0305, 1),
                                                                                          ('Taco Libre', 'Zona T, Bogotá', '11:00 AM - 11:00 PM', 4.6672, -74.0535, 1),
                                                                                          ('Parrilla Argentina', 'Parque de la 93, Bogotá', '6:00 PM - 12:00 AM', 4.6765, -74.0480, 1),
                                                                                          ('Café del Bosque', 'La Calera, Cundinamarca', '7:00 AM - 7:00 PM', 4.7200, -73.9700, 1);

-- ============================
--            MENU
-- ============================
INSERT INTO Menu (Categoria, CodigoRestaurante) VALUES
                                                    ('Pollo a la Brasa', 1),
                                                    ('Pizzas Tradicionales', 2),
                                                    ('Ceviches y Mariscos', 3),
                                                    ('Hamburguesas', 4),
                                                    ('Platos Gourmet', 5),
                                                    ('Comida Criolla', 6),
                                                    ('Sushi y Rolls', 7),
                                                    ('Tacos Mexicanos', 8),
                                                    ('Carnes a la Parrilla', 9),
                                                    ('Desayunos y Café', 10);

-- ============================
--            PLATO
-- ============================
INSERT INTO Plato (Nombre, Descripcion, Precio, TipoComida, Alergenos, Disponibilidad) VALUES
                                                                                           ('Pollo a la Brasa 1/4', 'Pollo asado con papas fritas y ensalada', 28.50, 'Principal', 'Ninguno', 'Disponible'),
                                                                                           ('Pizza Margherita', 'Pizza con mozzarella, tomate y albahaca', 45.00, 'Principal', 'Gluten, Lácteos', 'Disponible'),
                                                                                           ('Ceviche de Pescado', 'Pescado marinado en limón con ají y cebolla', 32.00, 'Entrada', 'Pescado', 'Disponible'),
                                                                                           ('Hamburguesa Clásica', 'Carne de res, lechuga, tomate y queso', 25.90, 'Principal', 'Gluten, Lácteos', 'Disponible'),
                                                                                           ('Lomo Saltado', 'Lomo fino salteado con papas y arroz', 38.00, 'Principal', 'Ninguno', 'Disponible'),
                                                                                           ('Aji de Gallina', 'Pollo desmechado en salsa de ají amarillo', 24.50, 'Principal', 'Lácteos, Nueces', 'Disponible'),
                                                                                           ('Sushi California Roll', 'Roll de palta, pepino y surimi', 28.00, 'Entrada', 'Pescado, Gluten', 'Disponible'),
                                                                                           ('Tacos de Carnitas', 'Tres tacos de cerdo con salsa verde', 22.00, 'Principal', 'Gluten', 'Disponible'),
                                                                                           ('Churrasco Argentino', 'Carne de res a la parrilla con chimichurri', 55.00, 'Principal', 'Ninguno', 'Disponible'),
                                                                                           ('Café Capuccino', 'Café espresso con leche espumada y cacao', 12.50, 'Bebida', 'Lácteos', 'Disponible');

-- ============================
--      RELACIÓN MENU - PLATO
-- ============================
INSERT INTO Plato_menu (CodigoMenu, CodigoPlato) VALUES
                                                     (1, 1),  -- Pollo a la Brasa -> Pollo a la Brasa 1/4
                                                     (2, 2),  -- Pizzas Tradicionales -> Pizza Margherita
                                                     (3, 3),  -- Ceviches -> Ceviche de Pescado
                                                     (4, 4),  -- Hamburguesas -> Hamburguesa Clásica
                                                     (5, 5),  -- Platos Gourmet -> Lomo Saltado
                                                     (6, 6),  -- Comida Criolla -> Aji de Gallina
                                                     (7, 7),  -- Sushi -> California Roll
                                                     (8, 8),  -- Tacos -> Tacos de Carnitas
                                                     (9, 9),  -- Carnes -> Churrasco Argentino
                                                     (10, 10); -- Café -> Capuccino

-- ============================
--          ENVÍO
-- ============================
INSERT INTO Envio (CodigoCliente, CodigoRepartidor, FechaEnvio, HoraEntrega) VALUES
                                                                                 (1, 1, '2025-11-20', '13:30:00'),
                                                                                 (2, 2, '2025-11-20', '14:15:00'),
                                                                                 (3, 3, '2025-11-21', '12:45:00'),
                                                                                 (4, 4, '2025-11-21', '19:20:00'),
                                                                                 (5, 5, '2025-11-22', '15:10:00'),
                                                                                 (6, 6, '2025-11-22', '20:30:00'),
                                                                                 (7, 7, '2025-11-23', '16:45:00'),
                                                                                 (8, 8, '2025-11-23', '18:00:00'),
                                                                                 (9, 9, '2025-11-24', '14:30:00'),
                                                                                 (10, 10, '2025-11-24', '21:15:00');

-- ============================
--          PEDIDO
-- ============================
INSERT INTO Pedido (CodigoEnvio, CodigoRestaurante, Descripcion, FechaPedido, Estado) VALUES
                                                                                          (1, 1, '1 Pollo a la Brasa 1/4 con papas', '2025-11-20', 'Entregado'),
                                                                                          (2, 2, '1 Pizza Margherita mediana', '2025-11-20', 'Entregado'),
                                                                                          (3, 3, '2 Ceviches de Pescado', '2025-11-21', 'Entregado'),
                                                                                          (4, 4, '1 Hamburguesa Clásica con papas', '2025-11-21', 'Entregado'),
                                                                                          (5, 5, '1 Lomo Saltado grande', '2025-11-22', 'En proceso'),
                                                                                          (6, 6, '2 Aji de Gallina', '2025-11-22', 'Entregado'),
                                                                                          (7, 7, '3 California Rolls', '2025-11-23', 'Entregado'),
                                                                                          (8, 8, '2 Tacos de Carnitas', '2025-11-23', 'Cancelado'),
                                                                                          (9, 9, '1 Churrasco Argentino', '2025-11-24', 'Entregado'),
                                                                                          (10, 10, '2 Capuccinos y 1 tarta', '2025-11-24', 'En proceso');

-- ============================
--            PAGO
-- ============================
INSERT INTO Pago (CodigoCliente, CodigoInfoBancaria, CodigoPedido, Monto, MetodoPago, FechaPago, HoraPago) VALUES
                                                                                                               (1, 1, 1, 28.50, 'Tarjeta de Crédito', '2025-11-20', '12:15:00'),
                                                                                                               (2, 2, 2, 45.00, 'Yape', '2025-11-20', '13:00:00'),
                                                                                                               (3, 3, 3, 64.00, 'Tarjeta de Débito', '2025-11-21', '11:30:00'),
                                                                                                               (4, 4, 4, 25.90, 'Efectivo', '2025-11-21', '18:45:00'),
                                                                                                               (5, 5, 5, 38.00, 'Tarjeta de Crédito', '2025-11-22', '14:20:00'),
                                                                                                               (6, 6, 6, 49.00, 'Yape', '2025-11-22', '19:10:00'),
                                                                                                               (7, 7, 7, 84.00, 'Tarjeta de Débito', '2025-11-23', '15:35:00'),
                                                                                                               (8, 8, 8, 44.00, 'Efectivo', '2025-11-23', '17:00:00'),
                                                                                                               (9, 9, 9, 55.00, 'Tarjeta de Crédito', '2025-11-24', '13:15:00'),
                                                                                                               (10, 10, 10, 37.50, 'Yape', '2025-11-24', '20:45:00');

-- ============================
--   INSERTAR 5 PERSONAS
-- ============================
INSERT INTO Persona (CodigoPersona, Nombres, Apellidos, Direccion, Telefono, CorreoElectronico) VALUES
                                                                                                    (11, 'Lucía', 'Paredes Soto', 'Calle 134 #19-50, Bogotá', '3101111111', 'lucia.paredes@example.com'),
                                                                                                    (12, 'Renato', 'Campos Vera', 'Cra 9 #116-10, Bogotá', '3112222222', 'renato.campos@example.com'),
                                                                                                    (13, 'Daniela', 'Fuentes Ríos', 'Calle 170 #7-55, Bogotá', '3123333333', 'daniela.fuentes@example.com'),
                                                                                                    (14, 'Mateo', 'Quispe Bravo', 'Av. Ciudad de Cali 26-90, Bogotá', '3134444444', 'mateo.quispe@example.com'),
                                                                                                    (15, 'Isabella', 'Torres Mena', 'Cra 68 #40-21, Bogotá', '3145555555', 'isabella.torres@example.com');

-- ============================
--         DEPARTAMENTOS
-- ============================
INSERT INTO Departamentos (CodigoDepartamento, Nombre) VALUES
                                                            (1, 'Cundinamarca'),
                                                            (2, 'Antioquia'),
                                                            (3, 'Valle del Cauca'),
                                                            (4, 'Atlántico'),
                                                            (5, 'Santander');

-- ============================
--           Ciudades
-- ============================
INSERT INTO Ciudades (CodigoCiudad, CodigoDepartamento, Nombre, Latitud, Longitud) VALUES
                                                                                      (1, 1, 'Bogotá', 4.7110, -74.0721),
                                                                                      (2, 2, 'Medellín', 6.2442, -75.5812),
                                                                                      (3, 3, 'Cali', 3.4516, -76.5320),
                                                                                      (4, 4, 'Barranquilla', 10.9685, -74.7813),
                                                                                      (5, 5, 'Bucaramanga', 7.1193, -73.1227);
