-- ============================================================
--  Limpieza de datos de prueba  ·  Urban Eats
--  Generado el 12/09/2026 tras revisar las dependencias reales.
--
--  COMO USARLO
--    1. Haz una copia de seguridad antes de correrlo:
--         mysqldump -u root urbaneats > respaldo_urbaneats.sql
--    2. Corre el BLOQUE 1. Es el seguro y resuelve el problema del reporte.
--    3. El BLOQUE 2 borra restaurantes creados por cuentas del equipo.
--       Leelo y decide con ellos antes de ejecutarlo.
--
--  Los datos originales de Inserts.sql (restaurantes 1 al 5 con sus menus,
--  platos, envios y pedidos) NO se tocan en ninguno de los dos bloques.
-- ============================================================

USE urbaneats;


-- ============================================================
--  BLOQUE 1 · Seguro
--  Quita el plato con precio absurdo que distorsiona el reporte
--  y las cuentas de prueba creadas durante la verificacion.
-- ============================================================

START TRANSACTION;

-- 1.1  El plato de 999.999 pesos ("almuerzo", restaurante 15).
--      Es el que dispara el precio promedio del reporte.
DELETE FROM plato WHERE CodigoPlato = 13;

-- 1.2  Restaurante "Mi Restaurante Prueba" (codigo 9), creado durante las
--      pruebas de esta sesion. No tiene menus, platos, envios ni pedidos.
DELETE FROM restaurante WHERE CodigoRestaurante = 9;

-- 1.3  Cuentas de prueba: cp10, cp11, cp12, ger01 y qa114729.
--      Ninguna tiene envios asociados, por eso se pueden borrar enteras.
--      El orden importa: primero lo que apunta al usuario, al final el usuario.
DELETE FROM gerente    WHERE CodigoUsuario IN (19, 20, 21, 24, 30);
DELETE FROM cliente    WHERE CodigoUsuario IN (19, 20, 21, 24, 30);
DELETE FROM telefono   WHERE CodigoUsuario IN (19, 20, 21, 24, 30);
DELETE FROM direccion  WHERE CodigoUsuario IN (19, 20, 21, 24, 30);
DELETE FROM rol_usuario WHERE CodigoUsuario IN (19, 20, 21, 24, 30);
DELETE FROM usuario    WHERE CodigoUsuario IN (19, 20, 21, 24, 30);

COMMIT;

-- Comprobacion del bloque 1
SELECT 'Platos con precio absurdo' AS control, COUNT(*) AS quedan
  FROM plato WHERE Precio >= 500000;
SELECT 'Cuentas de prueba' AS control, COUNT(*) AS quedan
  FROM usuario WHERE Correo LIKE '%@ue.test' OR Correo LIKE '%urbaneats.test';


-- ============================================================
--  BLOQUE 2 · Requiere acuerdo del equipo
--
--  Estos restaurantes los crearon cuentas del equipo mientras probaban.
--  Ninguno tiene envios ni pedidos, asi que borrarlos no rompe historial,
--  pero son datos de otras personas. Revisalo antes de ejecutar.
--
--    13  "fasdfgsdfg"        texto sin sentido, sin contenido
--    11  "Kitchen Hausen"    duplicado
--    12  "Kitchen Hausen"    duplicado (se conserva el 10)
--     8  "El pollo"          duplicado sin contenido (se conserva el 7)
--    15  "restauprueba"      nombre de prueba
--
--  Se conservan a proposito: 6 (LA BRASA), 7 (El pollo), 10 (Kitchen
--  Hausen), 14 (Apple Green) y 16 (Pollo rico), porque parecen registros
--  intencionales aunque sean de prueba.
-- ============================================================

-- START TRANSACTION;
--
-- -- Primero los platos, luego los menus, al final el restaurante.
-- DELETE p FROM plato p JOIN menu m ON p.CodigoMenu = m.CodigoMenu
--   WHERE m.CodigoRestaurante IN (8, 11, 12, 13, 15);
-- DELETE FROM menu WHERE CodigoRestaurante IN (8, 11, 12, 13, 15);
-- DELETE FROM restaurante WHERE CodigoRestaurante IN (8, 11, 12, 13, 15);
--
-- COMMIT;


-- ============================================================
--  BLOQUE 3 · Evitar que vuelvan los perfiles duplicados
--
--  Las tablas cliente, repartidor y gerente no tienen restriccion de
--  unicidad sobre CodigoUsuario, y por eso un doble envio del formulario
--  llegaba a crear dos perfiles para el mismo usuario.
--
--  Ejecutalo solo despues de confirmar que no quedan duplicados:
--    SELECT CodigoUsuario, COUNT(*) FROM repartidor GROUP BY CodigoUsuario HAVING COUNT(*) > 1;
--    SELECT CodigoUsuario, COUNT(*) FROM gerente    GROUP BY CodigoUsuario HAVING COUNT(*) > 1;
--    SELECT CodigoUsuario, COUNT(*) FROM cliente    GROUP BY CodigoUsuario HAVING COUNT(*) > 1;
-- ============================================================

-- ALTER TABLE cliente    ADD UNIQUE INDEX ux_cliente_usuario    (CodigoUsuario);
-- ALTER TABLE repartidor ADD UNIQUE INDEX ux_repartidor_usuario (CodigoUsuario);
-- ALTER TABLE gerente    ADD UNIQUE INDEX ux_gerente_usuario    (CodigoUsuario);
