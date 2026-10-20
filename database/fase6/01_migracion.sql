-- =====================================================================
-- Urban Eats - Fase 6 - 01_migracion.sql
--
-- Que hace: adapta una base creada con Schema.sql (+ Inserts.sql) al
-- flujo de la fase 6: seis estados del pedido, historial de cambios,
-- hora real del pedido, coordenadas para el mapa, horario estructurado
-- de las cocinas e informacion nutricional de los platos.
--
-- Como se usa: ejecutar UNA sola vez, despues de Schema.sql e Inserts.sql.
--   mysql -u root urbaneats < database/fase6/01_migracion.sql
-- Compatible con MariaDB 10.4+ y MySQL 8.
--
-- Regla de la fase 6: toda columna nueva admite NULL o tiene DEFAULT,
-- para que los INSERT que arma Hibernate sigan funcionando.
-- =====================================================================
USE urbaneats;

-- ---------------------------------------------------------------------
-- 1) Estados del pedido
-- Se guardan sin tildes para evitar problemas de codificacion; la
-- aplicacion muestra el texto con tildes (EstadoPedido.java).
-- Primero se amplia el ENUM para que convivan los valores viejos y los
-- nuevos, luego se migran los datos y al final se deja la lista nueva.
-- ---------------------------------------------------------------------
ALTER TABLE pedido MODIFY Estado ENUM('Iniciando','En Proceso','Solicitado','En preparacion',
    'Listo para envio','En camino','Entregado','Cancelado') NOT NULL;

UPDATE pedido SET Estado = 'Solicitado' WHERE Estado = 'Iniciando';
UPDATE pedido SET Estado = 'En camino'  WHERE Estado = 'En Proceso';

ALTER TABLE pedido MODIFY Estado ENUM('Solicitado','En preparacion','Listo para envio',
    'En camino','Entregado','Cancelado') NOT NULL DEFAULT 'Solicitado';

-- ---------------------------------------------------------------------
-- 2) Fecha y hora real del pedido (FechaPedido solo guarda el dia).
-- Sirve para los reportes por franja horaria (identidad nocturna).
-- La base la llena sola al crear el pedido (DEFAULT CURRENT_TIMESTAMP).
-- ---------------------------------------------------------------------
ALTER TABLE pedido ADD COLUMN FechaHora DATETIME NULL DEFAULT CURRENT_TIMESTAMP AFTER FechaPedido;

-- Para los pedidos que ya existian se toma la fecha y hora del pago.
UPDATE pedido p
JOIN pago pg ON pg.CodigoEnvio = p.CodigoEnvio
SET p.FechaPedido = pg.FechaPago,
    p.FechaHora   = TIMESTAMP(pg.FechaPago, COALESCE(pg.HoraPago, '12:00:00'));

-- ---------------------------------------------------------------------
-- 3) Historial de estados: quien cambio el pedido, de que estado a cual
-- y a que hora. Con esto se calculan los tiempos de entrega.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS historial_pedido (
  CodigoHistorial INT(11) NOT NULL AUTO_INCREMENT,
  CodigoPedido    INT(11) NOT NULL,
  EstadoAnterior  VARCHAR(30) NULL DEFAULT NULL,
  EstadoNuevo     VARCHAR(30) NOT NULL,
  CodigoUsuario   INT(11) NULL DEFAULT NULL,
  Comentario      VARCHAR(300) NULL DEFAULT NULL,
  Fecha           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (CodigoHistorial),
  INDEX idx_historial_pedido (CodigoPedido),
  INDEX idx_historial_usuario (CodigoUsuario),
  CONSTRAINT historial_pedido_ibfk_1 FOREIGN KEY (CodigoPedido)
    REFERENCES pedido (CodigoPedido) ON DELETE CASCADE,
  CONSTRAINT historial_pedido_ibfk_2 FOREIGN KEY (CodigoUsuario)
    REFERENCES usuario (CodigoUsuario) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Cada pedido existente arranca su historial con el estado que tiene hoy.
INSERT INTO historial_pedido (CodigoPedido, EstadoAnterior, EstadoNuevo, Comentario, Fecha)
SELECT CodigoPedido, NULL, Estado, 'Estado inicial (migracion fase 6)', COALESCE(FechaHora, NOW())
FROM pedido;

-- ---------------------------------------------------------------------
-- 4) Trigger y procedimientos con los estados nuevos.
-- El trigger sigue creando el pedido al insertar el envio; Java ya no
-- inserta un segundo pedido sino que busca el que creo el trigger.
-- ---------------------------------------------------------------------
DROP TRIGGER IF EXISTS crear_pedido_automaticamente;
DELIMITER $$
CREATE TRIGGER crear_pedido_automaticamente
AFTER INSERT ON envio
FOR EACH ROW
BEGIN
    INSERT INTO pedido (CodigoEnvio, CodigoRestaurante, FechaPedido, Estado)
    VALUES (NEW.CodigoEnvio, NEW.CodigoRestaurante, CURDATE(), 'Solicitado');
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS ActualizarPedido;
DELIMITER $$
CREATE PROCEDURE ActualizarPedido(IN pCodigoPedido INT, IN pLlegada BOOLEAN)
BEGIN
    DECLARE vEstadoNuevo VARCHAR(30);
    IF (pLlegada = TRUE) THEN
        SET vEstadoNuevo = 'Entregado';
    ELSE
        SET vEstadoNuevo = 'Cancelado';
    END IF;

    -- Solo se cierra un pedido que va en camino.
    IF EXISTS (SELECT 1 FROM pedido p WHERE p.CodigoPedido = pCodigoPedido AND p.Estado = 'En camino') THEN
        UPDATE pedido p SET p.Estado = vEstadoNuevo WHERE p.CodigoPedido = pCodigoPedido;
        INSERT INTO historial_pedido (CodigoPedido, EstadoAnterior, EstadoNuevo, Comentario)
        VALUES (pCodigoPedido, 'En camino', vEstadoNuevo, 'Cerrado con ActualizarPedido');
        SELECT 'Pedido actualizado correctamente' AS Mensaje;
    ELSE
        SELECT 'Este pedido ya ha sido modificado o no existe' AS Mensaje;
    END IF;
END$$
DELIMITER ;

-- Las dos rutinas siguientes usaban nombres con mayuscula (Repartidor, Envio),
-- que fallan en servidores Linux. Se recrean con los nombres reales en minuscula.
DROP FUNCTION IF EXISTS VerRepartidor;
DELIMITER $$
CREATE FUNCTION VerRepartidor(idRepartidor INT) RETURNS VARCHAR(100)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_nombre VARCHAR(100);
    SELECT u.Nombres INTO v_nombre
    FROM usuario u
    JOIN repartidor r ON r.CodigoUsuario = u.CodigoUsuario
    WHERE r.CodigoRepartidor = idRepartidor
    LIMIT 1;
    RETURN COALESCE(v_nombre, 'REPARTIDOR NO ENCONTRADO');
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS VerificarPedidos;
DELIMITER $$
CREATE PROCEDURE VerificarPedidos(IN pCodigoCliente INT)
BEGIN
    IF EXISTS (SELECT 1 FROM pedido p JOIN envio e ON e.CodigoEnvio = p.CodigoEnvio
               WHERE e.CodigoCliente = pCodigoCliente) THEN
        SELECT p.CodigoPedido, p.FechaPedido, p.Estado
        FROM pedido p JOIN envio e ON e.CodigoEnvio = p.CodigoEnvio
        WHERE e.CodigoCliente = pCodigoCliente;
    ELSE
        SELECT 'No se encontro pedido alguno' AS SinResultado;
    END IF;
END$$
DELIMITER ;

-- ---------------------------------------------------------------------
-- 5) Envio: direccion de entrega y ultima ubicacion del repartidor.
-- ---------------------------------------------------------------------
ALTER TABLE envio
  ADD COLUMN CodigoDireccion    INT(11) NULL DEFAULT NULL AFTER CodigoRestaurante,
  ADD COLUMN LatitudRepartidor  DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN LongitudRepartidor DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN FechaUbicacion     DATETIME NULL DEFAULT NULL,
  ADD INDEX idx_envio_direccion (CodigoDireccion),
  ADD CONSTRAINT envio_ibfk_4 FOREIGN KEY (CodigoDireccion)
    REFERENCES direccion (CodigoDireccion) ON DELETE SET NULL;

-- Los envios viejos quedan con la primera direccion del cliente.
UPDATE envio e
JOIN cliente c ON c.CodigoCliente = e.CodigoCliente
SET e.CodigoDireccion = (SELECT MIN(d.CodigoDireccion) FROM direccion d
                         WHERE d.CodigoUsuario = c.CodigoUsuario);

-- ---------------------------------------------------------------------
-- 6) Direccion: texto mas largo y coordenadas (pin en el mapa).
-- ---------------------------------------------------------------------
ALTER TABLE direccion
  MODIFY Direccion VARCHAR(200) NOT NULL,
  ADD COLUMN Latitud  DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN Longitud DECIMAL(10,7) NULL DEFAULT NULL;

-- ---------------------------------------------------------------------
-- 7) Restaurante (cocina): ubicacion, horario estructurado, categoria,
-- imagen, si recibe pedidos ahora y si esta activa (documentos vigentes).
-- HoraCierre menor que HoraApertura significa que cierra al dia siguiente
-- (por ejemplo 22:00 a 06:00).
-- ---------------------------------------------------------------------
ALTER TABLE restaurante
  ADD COLUMN Latitud       DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN Longitud      DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN HoraApertura  TIME NULL DEFAULT NULL,
  ADD COLUMN HoraCierre    TIME NULL DEFAULT NULL,
  ADD COLUMN Categoria     VARCHAR(60) NULL DEFAULT NULL,
  ADD COLUMN Imagen        VARCHAR(200) NULL DEFAULT NULL,
  ADD COLUMN AceptaPedidos TINYINT(1) NOT NULL DEFAULT 1,
  ADD COLUMN Activo        TINYINT(1) NOT NULL DEFAULT 1;

-- ---------------------------------------------------------------------
-- 8) Plato: calorias y etiquetas (sello "Balanceado", vegetariano, etc.).
-- Etiquetas va separada por comas: "Balanceado,Alto en proteina".
-- ---------------------------------------------------------------------
ALTER TABLE plato
  ADD COLUMN Calorias  INT(11) NULL DEFAULT NULL,
  ADD COLUMN Etiquetas VARCHAR(200) NULL DEFAULT NULL;

-- ---------------------------------------------------------------------
-- 9) Opinion: calificacion de 1 a 5 y el pedido calificado
-- (solo se califica despues de "Entregado").
-- ---------------------------------------------------------------------
ALTER TABLE opinion
  ADD COLUMN Calificacion TINYINT(4) NULL DEFAULT NULL,
  ADD COLUMN CodigoPedido INT(11) NULL DEFAULT NULL,
  ADD INDEX idx_opinion_pedido (CodigoPedido),
  ADD CONSTRAINT opinion_ibfk_4 FOREIGN KEY (CodigoPedido)
    REFERENCES pedido (CodigoPedido) ON DELETE SET NULL;

-- ---------------------------------------------------------------------
-- 10) Pago: se agrega in_mediation, que MercadoPago tambien puede devolver.
-- ---------------------------------------------------------------------
ALTER TABLE pago MODIFY EstadoPago ENUM('pending','approved','authorized','in_process',
    'in_mediation','rejected','cancelled','refunded','charged_back') NOT NULL DEFAULT 'pending';

-- ---------------------------------------------------------------------
-- 11) Vehiculo: 'Bici' y 'Bicicleta' eran lo mismo; queda solo 'Bicicleta'.
-- ---------------------------------------------------------------------
UPDATE vehiculo SET TipoVehiculo = 'Bicicleta' WHERE TipoVehiculo = 'Bici';
ALTER TABLE vehiculo MODIFY TipoVehiculo ENUM('Moto','Carro','Bicicleta') NOT NULL;

-- ---------------------------------------------------------------------
-- 12) Roles que faltaban: todo repartidor tiene el rol Repartidor y todo
-- gerente el rol Gerente (antes solo se miraba si existia la fila).
-- ---------------------------------------------------------------------
INSERT INTO rol_usuario (CodigoUsuario, CodigoRol)
SELECT r.CodigoUsuario, ro.CodigoRol
FROM repartidor r
JOIN rol ro ON ro.NombreRol = 'Repartidor'
WHERE NOT EXISTS (SELECT 1 FROM rol_usuario ru
                  WHERE ru.CodigoUsuario = r.CodigoUsuario AND ru.CodigoRol = ro.CodigoRol);

INSERT INTO rol_usuario (CodigoUsuario, CodigoRol)
SELECT g.CodigoUsuario, ro.CodigoRol
FROM gerente g
JOIN rol ro ON ro.NombreRol = 'Gerente'
WHERE NOT EXISTS (SELECT 1 FROM rol_usuario ru
                  WHERE ru.CodigoUsuario = g.CodigoUsuario AND ru.CodigoRol = ro.CodigoRol);

-- Nota para bases que ya estaban en uso (no la instalacion nueva):
-- si cliente, repartidor o gerente no tienen UNIQUE(CodigoUsuario),
-- ejecutar primero el bloque 3 de database/limpieza_datos_prueba.sql.
