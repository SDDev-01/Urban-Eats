-- =====================================================================
-- Urban Eats - Fase 6 - reparar_01_migracion.sql
--
-- Que hace: termina 01_migracion.sql en una base que la tiene aplicada
-- a medias (scripts\migrar_bd.ps1 dice "quedo aplicada a medias").
-- Es la misma migracion, pero cada paso revisa si ya esta hecho, asi
-- que se puede ejecutar las veces que haga falta.
--
-- Como se usa (desde la carpeta Urban-Eats, con respaldo previo):
--   C:\xampp\mysql\bin\mysql -u root -p urbaneats -e "source database/fase6/reparar_01_migracion.sql"
-- Requiere MariaDB 10.4+ (XAMPP), por los IF NOT EXISTS.
-- =====================================================================
USE urbaneats;

-- 1) Estados del pedido. Si ya estan migrados, los tres pasos no cambian nada.
ALTER TABLE pedido MODIFY Estado ENUM('Iniciando','En Proceso','Solicitado','En preparacion',
    'Listo para envio','En camino','Entregado','Cancelado') NOT NULL;

UPDATE pedido SET Estado = 'Solicitado' WHERE Estado = 'Iniciando';
UPDATE pedido SET Estado = 'En camino'  WHERE Estado = 'En Proceso';

ALTER TABLE pedido MODIFY Estado ENUM('Solicitado','En preparacion','Listo para envio',
    'En camino','Entregado','Cancelado') NOT NULL DEFAULT 'Solicitado';

-- 2) Fecha y hora real del pedido. Solo se llena en los pedidos que no la tienen.
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS FechaHora DATETIME NULL DEFAULT CURRENT_TIMESTAMP AFTER FechaPedido;

UPDATE pedido p
JOIN pago pg ON pg.CodigoEnvio = p.CodigoEnvio
SET p.FechaPedido = pg.FechaPago,
    p.FechaHora   = TIMESTAMP(pg.FechaPago, COALESCE(pg.HoraPago, '12:00:00'));

-- 3) Historial de estados. Solo se crea la fila inicial de los pedidos sin historial.
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

INSERT INTO historial_pedido (CodigoPedido, EstadoAnterior, EstadoNuevo, Comentario, Fecha)
SELECT p.CodigoPedido, NULL, p.Estado, 'Estado inicial (migracion fase 6)', COALESCE(p.FechaHora, NOW())
FROM pedido p
WHERE NOT EXISTS (SELECT 1 FROM historial_pedido h WHERE h.CodigoPedido = p.CodigoPedido);

-- 4) Trigger y procedimientos con los estados nuevos (se recrean siempre).
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

-- 5) Envio: direccion de entrega y ultima ubicacion del repartidor.
ALTER TABLE envio
  ADD COLUMN IF NOT EXISTS CodigoDireccion    INT(11) NULL DEFAULT NULL AFTER CodigoRestaurante,
  ADD COLUMN IF NOT EXISTS LatitudRepartidor  DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS LongitudRepartidor DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS FechaUbicacion     DATETIME NULL DEFAULT NULL,
  ADD INDEX IF NOT EXISTS idx_envio_direccion (CodigoDireccion),
  ADD CONSTRAINT envio_ibfk_4 FOREIGN KEY IF NOT EXISTS (CodigoDireccion)
    REFERENCES direccion (CodigoDireccion) ON DELETE SET NULL;

UPDATE envio e
JOIN cliente c ON c.CodigoCliente = e.CodigoCliente
SET e.CodigoDireccion = (SELECT MIN(d.CodigoDireccion) FROM direccion d
                         WHERE d.CodigoUsuario = c.CodigoUsuario)
WHERE e.CodigoDireccion IS NULL;

-- 6) Direccion: texto mas largo y coordenadas.
ALTER TABLE direccion
  MODIFY Direccion VARCHAR(200) NOT NULL,
  ADD COLUMN IF NOT EXISTS Latitud  DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS Longitud DECIMAL(10,7) NULL DEFAULT NULL;

-- 7) Restaurante (cocina).
ALTER TABLE restaurante
  ADD COLUMN IF NOT EXISTS Latitud       DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS Longitud      DECIMAL(10,7) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS HoraApertura  TIME NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS HoraCierre    TIME NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS Categoria     VARCHAR(60) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS Imagen        VARCHAR(200) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS AceptaPedidos TINYINT(1) NOT NULL DEFAULT 1,
  ADD COLUMN IF NOT EXISTS Activo        TINYINT(1) NOT NULL DEFAULT 1;

-- 8) Plato: calorias y etiquetas.
ALTER TABLE plato
  ADD COLUMN IF NOT EXISTS Calorias  INT(11) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS Etiquetas VARCHAR(200) NULL DEFAULT NULL;

-- 9) Opinion: calificacion y pedido calificado.
ALTER TABLE opinion
  ADD COLUMN IF NOT EXISTS Calificacion TINYINT(4) NULL DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS CodigoPedido INT(11) NULL DEFAULT NULL,
  ADD INDEX IF NOT EXISTS idx_opinion_pedido (CodigoPedido),
  ADD CONSTRAINT opinion_ibfk_4 FOREIGN KEY IF NOT EXISTS (CodigoPedido)
    REFERENCES pedido (CodigoPedido) ON DELETE SET NULL;

-- 10) Pago: in_mediation.
ALTER TABLE pago MODIFY EstadoPago ENUM('pending','approved','authorized','in_process',
    'in_mediation','rejected','cancelled','refunded','charged_back') NOT NULL DEFAULT 'pending';

-- 11) Vehiculo: solo 'Bicicleta'.
UPDATE vehiculo SET TipoVehiculo = 'Bicicleta' WHERE TipoVehiculo = 'Bici';
ALTER TABLE vehiculo MODIFY TipoVehiculo ENUM('Moto','Carro','Bicicleta') NOT NULL;

-- 12) Roles que faltaban.
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
