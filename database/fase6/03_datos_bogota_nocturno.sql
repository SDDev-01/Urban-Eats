-- =====================================================================
-- Urban Eats - Fase 6 - 03_datos_bogota_nocturno.sql
--
-- Datos de prueba alineados con la identidad de la fase 6:
-- "comida real para quienes mueven la noche".
--   * Todas las cocinas quedan en Bogota, con horario nocturno,
--     coordenadas (para el mapa), categoria e imagen.
--   * Se agregan platos caseros y balanceados con calorias, etiquetas
--     y alergenos. Tambien hay antojos: no es un nicho exclusivo.
--   * Usuarios de prueba para la demostracion (ver USUARIOS_DE_PRUEBA.md).
--
-- Ejecutar UNA vez, despues de 01_migracion.sql.
-- Los nombres de cocinas, personas, NIT y direcciones son ficticios.
-- =====================================================================
USE urbaneats;

-- Contrasena de todos los usuarios de prueba (documentada en USUARIOS_DE_PRUEBA.md).
SET @clave_demo = '$2a$10$Z9UTVgEtSaSnsEQNZQds2OU4Klfn3pLbumpqN65KJ/eIgcz/VDidm';

-- ---------------------------------------------------------------------
-- 1) Bogota es un Distrito Capital, no un municipio de Cundinamarca.
-- ---------------------------------------------------------------------
UPDATE departamento SET Nombre = 'Bogotá D.C.' WHERE CodigoDepartamento = 1;

-- ---------------------------------------------------------------------
-- 2) Las cinco cocinas originales se reubican en Bogota con horario nocturno.
-- HoraCierre menor que HoraApertura = cierra al dia siguiente.
-- ---------------------------------------------------------------------
UPDATE restaurante SET CodigoCiudad = 1, Direccion = 'Cl. 60 # 9-24, Chapinero',
    Horario = '6:00 p. m. – 3:00 a. m.', HoraApertura = '18:00:00', HoraCierre = '03:00:00',
    Latitud = 4.6461000, Longitud = -74.0625000, Categoria = 'Casera',
    Imagen = '/images/restaurantes/Rico sabor.png'
WHERE CodigoRestaurante = 1;

UPDATE restaurante SET CodigoCiudad = 1, Direccion = 'Cra. 24 # 45-10, Teusaquillo',
    Horario = '6:00 p. m. – 2:00 a. m.', HoraApertura = '18:00:00', HoraCierre = '02:00:00',
    Latitud = 4.6352000, Longitud = -74.0752000, Categoria = 'Pizzas y pastas',
    Imagen = '/images/restaurantes/pizzeria_guisepe.png'
WHERE CodigoRestaurante = 2;

UPDATE restaurante SET CodigoCiudad = 1, Direccion = 'Cl. 119 # 6-40, Usaquén',
    Horario = '7:00 p. m. – 1:00 a. m.', HoraApertura = '19:00:00', HoraCierre = '01:00:00',
    Latitud = 4.6950000, Longitud = -74.0310000, Categoria = 'Asiática',
    Imagen = '/images/restaurantes/sushi_del_pacifico.png'
WHERE CodigoRestaurante = 3;

UPDATE restaurante SET CodigoCiudad = 1, Direccion = 'Cl. 38 Sur # 78-20, Kennedy',
    Horario = '8:00 p. m. – 6:00 a. m.', HoraApertura = '20:00:00', HoraCierre = '06:00:00',
    Latitud = 4.6280000, Longitud = -74.1515000, Categoria = 'Casera colombiana',
    Imagen = '/images/restaurantes/arepas-la costeña.png'
WHERE CodigoRestaurante = 4;

UPDATE restaurante SET CodigoCiudad = 1, Direccion = 'Cl. 13 # 50-25, Puente Aranda',
    Horario = '7:00 p. m. – 4:00 a. m.', HoraApertura = '19:00:00', HoraCierre = '04:00:00',
    Latitud = 4.6178000, Longitud = -74.1065000, Categoria = 'Parrilla',
    Imagen = '/images/restaurantes/parrilla-Santandereana.png'
WHERE CodigoRestaurante = 5;

-- Calorias y etiquetas de los platos originales.
UPDATE plato SET Calorias = 750, Etiquetas = 'Alto en proteína' WHERE CodigoPlato = 1;
UPDATE plato SET Calorias = 120, Etiquetas = 'Sin lácteos' WHERE CodigoPlato = 2;
UPDATE plato SET Calorias = 1100 WHERE CodigoPlato = 3;
UPDATE plato SET Calorias = 850 WHERE CodigoPlato = 4;
UPDATE plato SET Calorias = 450, Etiquetas = 'Balanceado' WHERE CodigoPlato = 5;
UPDATE plato SET Calorias = 380 WHERE CodigoPlato = 6;
UPDATE plato SET Calorias = 420, Etiquetas = 'Vegetariano' WHERE CodigoPlato = 7;
UPDATE plato SET Calorias = 150 WHERE CodigoPlato = 8;
UPDATE plato SET Calorias = 900, Etiquetas = 'Alto en proteína' WHERE CodigoPlato = 9;
UPDATE plato SET Calorias = 150 WHERE CodigoPlato = 10;

-- Platos caseros y balanceados nuevos en las cocinas originales.
INSERT INTO plato (CodigoMenu, Nombre, Descripcion, Precio, TipoComida, Disponibilidad, Calorias, Etiquetas) VALUES
(1, 'Pechuga a la plancha con arroz integral', 'Con ensalada fresca y aguacate', 19000.00, 'Pollo', 'Disponible', 520, 'Balanceado,Alto en proteína'),
(1, 'Sopa de pollo con verduras', 'Caldo casero con papa y cilantro', 12000.00, 'Sopa', 'Disponible', 280, 'Balanceado,Casero'),
(2, 'Agua de panela con limón', 'Caliente o fría', 4000.00, 'Bebida', 'Disponible', 90, 'Sin lácteos'),
(4, 'Pasta integral con vegetales', 'Salteada con aceite de oliva', 24000.00, 'Pasta', 'Disponible', 560, 'Balanceado,Vegetariano'),
(5, 'Bowl poke de salmón', 'Arroz, salmón, edamame y pepino', 32000.00, 'Bowl', 'Disponible', 540, 'Balanceado,Alto en proteína'),
(7, 'Arepa de choclo con pollo desmechado', 'Con queso campesino', 14000.00, 'Arepa', 'Disponible', 520, 'Casero'),
(7, 'Huevos pericos con arepa', 'El desayuno de madrugada', 11000.00, 'Desayuno', 'Disponible', 480, 'Vegetariano,Casero'),
(8, 'Caldo de costilla', 'Con arepa y cilantro', 13000.00, 'Sopa', 'Disponible', 450, 'Casero'),
(9, 'Lomo a la plancha con ensalada', 'Con papa criolla', 30000.00, 'Carne', 'Disponible', 610, 'Balanceado,Alto en proteína');

-- Alergenos de los platos nuevos (se buscan por nombre para no depender del codigo).
INSERT INTO alergeno (CodigoPlato, Nombre)
SELECT CodigoPlato, 'Gluten' FROM plato WHERE Nombre = 'Pasta integral con vegetales' UNION ALL
SELECT CodigoPlato, 'Pescado' FROM plato WHERE Nombre = 'Bowl poke de salmón' UNION ALL
SELECT CodigoPlato, 'Soya' FROM plato WHERE Nombre = 'Bowl poke de salmón' UNION ALL
SELECT CodigoPlato, 'Lácteos' FROM plato WHERE Nombre = 'Arepa de choclo con pollo desmechado' UNION ALL
SELECT CodigoPlato, 'Huevo' FROM plato WHERE Nombre = 'Huevos pericos con arepa';

-- Los cinco pedidos originales son historicos: ya fueron entregados
-- (por eso tienen opiniones). Se cierran y se deja el rastro en el historial.
INSERT INTO historial_pedido (CodigoPedido, EstadoAnterior, EstadoNuevo, Comentario, Fecha)
SELECT CodigoPedido, Estado, 'Entregado', 'Pedido historico (datos fase 6)',
       DATE_ADD(COALESCE(FechaHora, NOW()), INTERVAL 40 MINUTE)
FROM pedido WHERE CodigoPedido BETWEEN 1 AND 5 AND Estado <> 'Entregado';
UPDATE pedido SET Estado = 'Entregado' WHERE CodigoPedido BETWEEN 1 AND 5;

-- ---------------------------------------------------------------------
-- 3) Coordenadas de las direcciones de los clientes y repartidores.
-- ---------------------------------------------------------------------
UPDATE direccion SET Direccion = 'Av. Caracas # 53-20, Chapinero', Latitud = 4.6400000, Longitud = -74.0680000 WHERE CodigoDireccion = 1;
UPDATE direccion SET Direccion = 'Cl. 72 # 10-34, Chapinero', Latitud = 4.6566000, Longitud = -74.0575000 WHERE CodigoDireccion = 2;
UPDATE direccion SET Direccion = 'Cra. 15 # 93-45, Chicó', Latitud = 4.6788000, Longitud = -74.0505000 WHERE CodigoDireccion = 3;
UPDATE direccion SET Direccion = 'Av. Suba # 115-21, Suba', Latitud = 4.7020000, Longitud = -74.0750000 WHERE CodigoDireccion = 4;
UPDATE direccion SET Direccion = 'Cl. 80 # 45-67, Barrios Unidos', Latitud = 4.6710000, Longitud = -74.0760000 WHERE CodigoDireccion = 5;
UPDATE direccion SET Latitud = 4.6315000, Longitud = -74.0660000 WHERE CodigoDireccion = 6;
UPDATE direccion SET Latitud = 4.6960000, Longitud = -74.1100000 WHERE CodigoDireccion = 7;
UPDATE direccion SET Latitud = 4.6865000, Longitud = -74.0480000 WHERE CodigoDireccion = 8;
UPDATE direccion SET Latitud = 4.6670000, Longitud = -74.0560000 WHERE CodigoDireccion = 9;
UPDATE direccion SET Latitud = 4.6620000, Longitud = -74.1110000 WHERE CodigoDireccion = 10;

-- ---------------------------------------------------------------------
-- 4) Usuarios de prueba para la demostracion (dominio reservado .test).
-- El trigger crear_cliente_automaticamente crea su fila en cliente.
-- ---------------------------------------------------------------------
INSERT INTO usuario (Correo, Password, Nombres, Apellidos) VALUES
('cliente.demo@urbaneats.test',     @clave_demo, 'Laura',   'Méndez Rojas'),
('cocina.demo@urbaneats.test',      @clave_demo, 'Andrés',  'Pardo Gil'),
('repartidor.demo@urbaneats.test',  @clave_demo, 'Sergio',  'Castaño Ríos'),
('tecnico.demo@urbaneats.test',     @clave_demo, 'Paula',   'Moreno Díaz'),
('solicitante.demo@urbaneats.test', @clave_demo, 'Marcela', 'Quintero Gómez');

SELECT CodigoUsuario INTO @u_cliente     FROM usuario WHERE Correo = 'cliente.demo@urbaneats.test';
SELECT CodigoUsuario INTO @u_cocina      FROM usuario WHERE Correo = 'cocina.demo@urbaneats.test';
SELECT CodigoUsuario INTO @u_repartidor  FROM usuario WHERE Correo = 'repartidor.demo@urbaneats.test';
SELECT CodigoUsuario INTO @u_tecnico     FROM usuario WHERE Correo = 'tecnico.demo@urbaneats.test';
SELECT CodigoUsuario INTO @u_solicitante FROM usuario WHERE Correo = 'solicitante.demo@urbaneats.test';

INSERT INTO telefono (Telefono, CodigoUsuario) VALUES
('3205550101', @u_cliente), ('3205550102', @u_cocina), ('3205550103', @u_repartidor),
('3205550104', @u_tecnico), ('3205550105', @u_solicitante);

-- La clienta trabaja en un contact center de la Calle 26 en turno nocturno.
INSERT INTO direccion (Direccion, CodigoUsuario, Latitud, Longitud) VALUES
('Av. Calle 26 # 69-76, Salitre (sede contact center)', @u_cliente, 4.6570000, -74.1080000),
('Cl. 45 # 22-10, Teusaquillo (casa)', @u_cliente, 4.6345000, -74.0735000),
('Cra. 7 # 40-62, Chapinero', @u_cocina, 4.6280000, -74.0640000),
('Cl. 63 # 24-15, Chapinero', @u_repartidor, 4.6510000, -74.0700000),
('Cra. 13 # 26-45, Santa Fe', @u_tecnico, 4.6150000, -74.0700000),
('Cl. 53 # 27-20, Galerías', @u_solicitante, 4.6430000, -74.0760000);

-- Roles: 1 Cliente, 2 Repartidor, 3 Tecnico, 4 Gerente (ver Inserts.sql).
INSERT INTO rol_usuario (CodigoUsuario, CodigoRol)
SELECT @u_cliente, CodigoRol FROM rol WHERE NombreRol = 'Cliente' UNION ALL
SELECT @u_cocina, CodigoRol FROM rol WHERE NombreRol IN ('Cliente', 'Gerente') UNION ALL
SELECT @u_repartidor, CodigoRol FROM rol WHERE NombreRol IN ('Cliente', 'Repartidor') UNION ALL
SELECT @u_tecnico, CodigoRol FROM rol WHERE NombreRol = 'Tecnico' UNION ALL
SELECT @u_solicitante, CodigoRol FROM rol WHERE NombreRol = 'Cliente';

-- Repartidor de prueba con moto (placa de moto colombiana: 3 letras, 2 numeros, 1 letra).
INSERT INTO repartidor (CodigoUsuario) VALUES (@u_repartidor);
SET @r_demo = LAST_INSERT_ID();
INSERT INTO vehiculo (Placa, CodigoRepartidor, TipoVehiculo, SeguroVehiculo, SOAT)
VALUES ('UEX12F', @r_demo, 'Moto', 'Póliza de prueba', 'SOAT de prueba vigente');

-- ---------------------------------------------------------------------
-- 5) Dos cocinas nocturnas nuevas del gerente de prueba.
-- ---------------------------------------------------------------------
INSERT INTO gerente (CodigoUsuario) VALUES (@u_cocina);
SET @g_demo = LAST_INSERT_ID();

-- Cerca de hospitales de la Cra. 7: personal de salud en turno.
INSERT INTO restaurante (CodigoCiudad, CodigoGerente, Nombre, Direccion, Horario,
    Latitud, Longitud, HoraApertura, HoraCierre, Categoria, Imagen)
VALUES (1, @g_demo, 'Don Pollo Nocturno', 'Cra. 7 # 40-20, Chapinero', '9:00 p. m. – 6:00 a. m.',
    4.6283000, -74.0645000, '21:00:00', '06:00:00', 'Casera', '/images/restaurantes/don-pollo.png');
SET @r_donpollo = LAST_INSERT_ID();

-- Cerca de la Zona Franca de Fontibon: contact centers y bodegas 24 horas.
INSERT INTO restaurante (CodigoCiudad, CodigoGerente, Nombre, Direccion, Horario,
    Latitud, Longitud, HoraApertura, HoraCierre, Categoria, Imagen)
VALUES (1, @g_demo, 'Las Brasas 24h', 'Cra. 106 # 15A-25, Fontibón (Zona Franca)', 'Abierto 24 horas',
    4.6789000, -74.1478000, '00:00:00', '23:59:00', 'Parrilla balanceada', '/images/restaurantes/las-brasas.png');
SET @r_brasas = LAST_INSERT_ID();

INSERT INTO menu (Categoria, CodigoRestaurante) VALUES ('Sopas y caldos', @r_donpollo);
SET @m_sopas = LAST_INSERT_ID();
INSERT INTO menu (Categoria, CodigoRestaurante) VALUES ('Platos fuertes', @r_donpollo);
SET @m_fuertes = LAST_INSERT_ID();
INSERT INTO menu (Categoria, CodigoRestaurante) VALUES ('Bebidas calientes', @r_donpollo);
SET @m_calientes = LAST_INSERT_ID();
INSERT INTO menu (Categoria, CodigoRestaurante) VALUES ('Bowls', @r_brasas);
SET @m_bowls = LAST_INSERT_ID();
INSERT INTO menu (Categoria, CodigoRestaurante) VALUES ('Parrilla', @r_brasas);
SET @m_parrilla = LAST_INSERT_ID();
INSERT INTO menu (Categoria, CodigoRestaurante) VALUES ('Jugos', @r_brasas);
SET @m_jugos = LAST_INSERT_ID();

INSERT INTO plato (CodigoMenu, Nombre, Descripcion, Precio, TipoComida, Disponibilidad, Calorias, Etiquetas) VALUES
(@m_sopas, 'Caldo de costilla', 'Con papa, cilantro y arepa', 12000.00, 'Sopa', 'Disponible', 380, 'Casero'),
(@m_sopas, 'Ajiaco santafereño', 'Con pollo, mazorca, alcaparras y crema', 18000.00, 'Sopa', 'Disponible', 620, 'Casero'),
(@m_fuertes, 'Pechuga en salsa de champiñones', 'Con arroz y ensalada', 20000.00, 'Pollo', 'Disponible', 590, 'Balanceado,Alto en proteína'),
(@m_fuertes, 'Sudado de pollo con arroz', 'Receta de la casa', 16000.00, 'Pollo', 'Disponible', 640, 'Casero'),
(@m_calientes, 'Tinto', 'Café colombiano', 2500.00, 'Bebida', 'Disponible', 5, 'Sin azúcar'),
(@m_calientes, 'Aromática de frutas', 'Sin azúcar añadida', 2500.00, 'Bebida', 'Disponible', 15, 'Sin azúcar'),
(@m_calientes, 'Chocolate con queso', 'Con pan', 6000.00, 'Bebida', 'Disponible', 350, ''),
(@m_bowls, 'Bowl de pollo, quinua y aguacate', 'Con vegetales asados', 21000.00, 'Bowl', 'Disponible', 540, 'Balanceado,Alto en proteína,Sin gluten'),
(@m_bowls, 'Wrap integral de pavo', 'Con lechuga, tomate y hummus', 16000.00, 'Wrap', 'Disponible', 430, 'Balanceado'),
(@m_parrilla, 'Hamburguesa artesanal', 'Para el antojo de madrugada', 24000.00, 'Hamburguesa', 'Disponible', 980, ''),
(@m_parrilla, 'Pechuga asada con ensalada', 'Con papa criolla', 19000.00, 'Pollo', 'Disponible', 510, 'Balanceado,Alto en proteína'),
(@m_jugos, 'Jugo natural de mora', 'En agua, sin azúcar', 6000.00, 'Bebida', 'Disponible', 80, 'Sin azúcar'),
(@m_jugos, 'Avena caliente', 'Con canela', 5000.00, 'Bebida', 'Disponible', 220, 'Casero');

INSERT INTO alergeno (CodigoPlato, Nombre)
SELECT CodigoPlato, 'Lácteos' FROM plato WHERE Nombre IN ('Ajiaco santafereño', 'Pechuga en salsa de champiñones', 'Chocolate con queso', 'Avena caliente') UNION ALL
SELECT CodigoPlato, 'Gluten' FROM plato WHERE Nombre IN ('Wrap integral de pavo', 'Hamburguesa artesanal') UNION ALL
SELECT CodigoPlato, 'Sésamo' FROM plato WHERE Nombre = 'Wrap integral de pavo' UNION ALL
SELECT CodigoPlato, 'Huevo' FROM plato WHERE Nombre = 'Hamburguesa artesanal';
