USE UrbanEats;

-- ============================
--        TABLA PERSONA
-- ============================
INSERT INTO Persona (Nombres, Apellidos, Direccion, Telefono, CorreoElectronico) VALUES
                                                                                     ('Juan Carlos', 'Rodríguez Pérez', 'Av. Central 123, Lima', '987654321', 'juan.rodriguez@email.com'),
                                                                                     ('María Elena', 'González López', 'Calle San Martín 456, Lima', '923456789', 'maria.gonzalez@email.com'),
                                                                                     ('Carlos Alberto', 'Martínez Sánchez', 'Jr. Las Flores 789, Lima', '912345678', 'carlos.martinez@email.com'),
                                                                                     ('Ana Patricia', 'Fernández Torres', 'Av. Los Álamos 321, Lima', '934567890', 'ana.fernandez@email.com'),
                                                                                     ('Luis Fernando', 'Ramírez Castro', 'Calle Real 654, Lima', '945678901', 'luis.ramirez@email.com'),
                                                                                     ('Sofía Isabel', 'Herrera Mendoza', 'Jr. Santa Rosa 987, Lima', '956789012', 'sofia.herrera@email.com'),
                                                                                     ('Diego Armando', 'Vargas Silva', 'Av. Bolognesi 147, Lima', '967890123', 'diego.vargas@email.com'),
                                                                                     ('Valentina Rose', 'Cruz Morales', 'Calle Los Robles 258, Lima', '978901234', 'valentina.cruz@email.com'),
                                                                                     ('Andrés Felipe', 'Reyes Aguilar', 'Jr. Las Palmeras 369, Lima', '989012345', 'andres.reyes@email.com'),
                                                                                     ('Camila Andrea', 'Gómez Paredes', 'Av. Tacna 741, Lima', '990123456', 'camila.gomez@email.com');

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
INSERT INTO Restaurante (Nombre, Ubicacion, Horario) VALUES
                                                         ('Pollería El Rico Sabor', 'Av. Aviación 567, Lima', '10:00 AM - 10:00 PM'),
                                                         ('Pizzería Don Giuseppe', 'Calle Manuel Gonzales 234, Lima', '12:00 PM - 11:00 PM'),
                                                         ('Cevichería La Mar', 'Jr. Monterrey 789, Lima', '11:00 AM - 6:00 PM'),
                                                         ('Hamburguesería Big Bite', 'Av. Javier Prado 456, Lima', '11:00 AM - 12:00 AM'),
                                                         ('Taller de Cocina', 'Calle Las Artes 321, Lima', '1:00 PM - 9:00 PM'),
                                                         ('Sabor Criollo', 'Jr. Huancavelica 654, Lima', '9:00 AM - 8:00 PM'),
                                                         ('Sushi Bar Samurai', 'Av. Caminos del Inca 987, Lima', '12:00 PM - 10:00 PM'),
                                                         ('Taco Libre', 'Calle México 147, Lima', '11:00 AM - 11:00 PM'),
                                                         ('Parrilla Argentina', 'Jr. Mendoza 258, Lima', '6:00 PM - 12:00 AM'),
                                                         ('Café del Bosque', 'Av. La Molina 369, Lima', '7:00 AM - 7:00 PM');

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
                                                                                                    (11, 'Lucía', 'Paredes Soto', 'Av. Primavera 100, Lima', '911111111', 'lucia.paredes@example.com'),
                                                                                                    (12, 'Renato', 'Campos Vera', 'Calle Las Lomas 233, Lima', '922222222', 'renato.campos@example.com'),
                                                                                                    (13, 'Daniela', 'Fuentes Ríos', 'Jr. Palmeras 560, Lima', '933333333', 'daniela.fuentes@example.com'),
                                                                                                    (14, 'Mateo', 'Quispe Bravo', 'Av. Arequipa 450, Lima', '944444444', 'mateo.quispe@example.com'),
                                                                                                    (15, 'Isabella', 'Torres Mena', 'Calle Los Cedros 345, Lima', '955555555', 'isabella.torres@example.com');

