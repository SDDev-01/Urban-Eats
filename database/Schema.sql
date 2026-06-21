-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema urbaneats
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `urbaneats` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `urbaneats` ;

-- -----------------------------------------------------
-- Table `urbaneats`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`usuario` (
  `CodigoUsuario` INT(11) NOT NULL AUTO_INCREMENT,
  `Correo` VARCHAR(50) NOT NULL UNIQUE,
  `Password` VARCHAR(255) NOT NULL,
  `Nombres` VARCHAR(100) NULL DEFAULT NULL,
  `Apellidos` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`Telefono`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`telefono` (
  `CodigoTelefono` INT NOT NULL AUTO_INCREMENT,
  `Telefono` VARCHAR(45) NOT NULL,
  `CodigoUsuario` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoTelefono`),
  INDEX `fk_Telefono_usuario_idx` (`CodigoUsuario` ASC),
  CONSTRAINT `fk_Telefono_usuario`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `urbaneats`.`usuario` (`CodigoUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urbaneats`.`Direccion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`direccion` (
  `CodigoDireccion` INT NOT NULL AUTO_INCREMENT,
  `Direccion` VARCHAR(45) NOT NULL,
  `CodigoUsuario` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoDireccion`),
  INDEX `fk_Direccion_usuario1_idx` (`CodigoUsuario` ASC),
  CONSTRAINT `fk_Direccion_usuario1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `urbaneats`.`usuario` (`CodigoUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `urbaneats`.`plato`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`plato` (
  `CodigoPlato` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoMenu` INT(11) NULL DEFAULT NULL,
  `Nombre` VARCHAR(150) NULL DEFAULT NULL,
  `Descripcion` VARCHAR(300) NULL DEFAULT NULL,
  `Precio` DECIMAL(10,2) NULL DEFAULT NULL,
  `TipoComida` VARCHAR(100) NULL DEFAULT NULL,
  `Disponibilidad` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoPlato`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`alergeno`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`alergeno` (
  `CodigoAlergeno` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoPlato` INT(11) NULL DEFAULT NULL,
  `Nombre` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoAlergeno`),
  INDEX `CodigoPlato` (`CodigoPlato` ASC),
  CONSTRAINT `alergeno_ibfk_1`
    FOREIGN KEY (`CodigoPlato`)
    REFERENCES `urbaneats`.`plato` (`CodigoPlato`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`departamento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`departamento` (
  `CodigoDepartamento` INT(11) NOT NULL,
  `Nombre` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoDepartamento`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`ciudad`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`ciudad` (
  `CodigoCiudad` INT(11) NOT NULL,
  `CodigoDepartamento` INT(11) NOT NULL,
  `Nombre` VARCHAR(255) NULL DEFAULT NULL,
  `Latitud` DECIMAL(10,8) NULL DEFAULT NULL,
  `Longitud` DECIMAL(10,8) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoCiudad`),
  INDEX `CodigoDepartamento` (`CodigoDepartamento` ASC),
  CONSTRAINT `ciudad_ibfk_1`
    FOREIGN KEY (`CodigoDepartamento`)
    REFERENCES `urbaneats`.`departamento` (`CodigoDepartamento`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`cliente` (
  `CodigoCliente` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoUsuario` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoCliente`),
  INDEX `CodigoUsuario` (`CodigoUsuario` ASC),
  CONSTRAINT `cliente_ibfk_1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `urbaneats`.`usuario` (`CodigoUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`repartidor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`repartidor` (
  `CodigoRepartidor` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoUsuario` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoRepartidor`),
  INDEX `CodigoUsuario` (`CodigoUsuario` ASC),
  CONSTRAINT `repartidor_ibfk_1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `urbaneats`.`usuario` (`CodigoUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`gerente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`gerente` (
  `CodigoGerente` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoUsuario` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoGerente`),
  INDEX `CodigoUsuario` (`CodigoUsuario` ASC),
  CONSTRAINT `gerente_ibfk_1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `urbaneats`.`usuario` (`CodigoUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`restaurante`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`restaurante` (
  `CodigoRestaurante` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoCiudad` INT(11) NOT NULL,
  `CodigoGerente` INT(11) NOT NULL,
  `Nombre` VARCHAR(150) NULL DEFAULT NULL,
  `Direccion` VARCHAR(200) NULL DEFAULT NULL,
  `Horario` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoRestaurante`),
  INDEX `CodigoCiudad` (`CodigoCiudad` ASC),
  INDEX `CodigoGerente` (`CodigoGerente` ASC),
  CONSTRAINT `restaurante_ibfk_1`
    FOREIGN KEY (`CodigoCiudad`)
    REFERENCES `urbaneats`.`ciudad` (`CodigoCiudad`),
  CONSTRAINT `restaurante_ibfk_2`
    FOREIGN KEY (`CodigoGerente`)
    REFERENCES `urbaneats`.`gerente` (`CodigoGerente`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`envio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`envio` (
  `CodigoEnvio` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoCliente` INT(11) NOT NULL,
  `CodigoRepartidor` INT(11) NOT NULL,
  `CodigoRestaurante` INT(11) NOT NULL,
  `Descripcion` VARCHAR(300) NULL DEFAULT NULL,
  `FechaEnvio` DATE NULL DEFAULT NULL,
  `HoraEntrega` TIME NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoEnvio`),
  INDEX `CodigoCliente` (`CodigoCliente` ASC),
  INDEX `CodigoRepartidor` (`CodigoRepartidor` ASC),
  INDEX `CodigoRestaurante` (`CodigoRestaurante` ASC),
  CONSTRAINT `envio_ibfk_1`
    FOREIGN KEY (`CodigoCliente`)
    REFERENCES `urbaneats`.`cliente` (`CodigoCliente`),
  CONSTRAINT `envio_ibfk_2`
    FOREIGN KEY (`CodigoRepartidor`)
    REFERENCES `urbaneats`.`repartidor` (`CodigoRepartidor`),
  CONSTRAINT `envio_ibfk_3`
    FOREIGN KEY (`CodigoRestaurante`)
    REFERENCES `urbaneats`.`restaurante` (`CodigoRestaurante`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`menu` (
  `CodigoMenu` INT(11) NOT NULL AUTO_INCREMENT,
  `Categoria` VARCHAR(100) NULL DEFAULT NULL,
  `CodigoRestaurante` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoMenu`),
  INDEX `CodigoRestaurante` (`CodigoRestaurante` ASC),
  CONSTRAINT `menu_ibfk_1`
    FOREIGN KEY (`CodigoRestaurante`)
    REFERENCES `urbaneats`.`restaurante` (`CodigoRestaurante`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`opinion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`opinion` (
  `CodigoOpinion` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoPlato` INT(11) NULL DEFAULT NULL,
  `CodigoCliente` INT(11) NULL DEFAULT NULL,
  `CodigoRepartidor` INT(11) NULL DEFAULT NULL,
  `Opinion` VARCHAR(300) NULL DEFAULT NULL,
  `Fecha` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoOpinion`),
  INDEX `CodigoPlato` (`CodigoPlato` ASC),
  INDEX `CodigoCliente` (`CodigoCliente` ASC),
  INDEX `CodigoRepartidor` (`CodigoRepartidor` ASC),
  CONSTRAINT `opinion_ibfk_1`
    FOREIGN KEY (`CodigoPlato`)
    REFERENCES `urbaneats`.`plato` (`CodigoPlato`),
  CONSTRAINT `opinion_ibfk_2`
    FOREIGN KEY (`CodigoCliente`)
    REFERENCES `urbaneats`.`cliente` (`CodigoCliente`),
  CONSTRAINT `opinion_ibfk_3`
    FOREIGN KEY (`CodigoRepartidor`)
    REFERENCES `urbaneats`.`repartidor` (`CodigoRepartidor`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`pago` (
  `CodigoPago` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoCliente` INT(11) NOT NULL,
  `CodigoEnvio` INT(11) NOT NULL,
  `Monto` DECIMAL(10,2) NULL DEFAULT NULL,
  `FechaPago` DATE NULL DEFAULT NULL,
  `HoraPago` TIME NULL DEFAULT NULL,
  `EstadoPago` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
  PRIMARY KEY (`CodigoPago`),
  INDEX `CodigoCliente` (`CodigoCliente` ASC),
  INDEX `CodigoEnvio` (`CodigoEnvio` ASC),
  CONSTRAINT `pago_ibfk_1`
    FOREIGN KEY (`CodigoCliente`)
    REFERENCES `urbaneats`.`cliente` (`CodigoCliente`),
  CONSTRAINT `pago_ibfk_2`
    FOREIGN KEY (`CodigoEnvio`)
    REFERENCES `urbaneats`.`envio` (`CodigoEnvio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`pedido`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`pedido` (
  `CodigoPedido` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoEnvio` INT(11) NOT NULL,
  `CodigoRestaurante` INT(11) NOT NULL,
  `FechaPedido` DATE NULL DEFAULT NULL,
  `Estado` ENUM('Iniciando', 'En Proceso', 'Entregado', 'Cancelado') NOT NULL,
  PRIMARY KEY (`CodigoPedido`),
  UNIQUE INDEX `CodigoEnvio` (`CodigoEnvio` ASC),
  INDEX `CodigoRestaurante` (`CodigoRestaurante` ASC),
  CONSTRAINT `pedido_ibfk_1`
    FOREIGN KEY (`CodigoEnvio`)
    REFERENCES `urbaneats`.`envio` (`CodigoEnvio`),
  CONSTRAINT `pedido_ibfk_2`
    FOREIGN KEY (`CodigoRestaurante`)
    REFERENCES `urbaneats`.`restaurante` (`CodigoRestaurante`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- FK de plato → menu (se agrega al final porque plato se crea antes que menu)
-- ALTER TABLE `urbaneats`.`plato`
--   ADD INDEX `CodigoMenu` (`CodigoMenu` ASC),
--   ADD CONSTRAINT `plato_ibfk_menu`
--     FOREIGN KEY (`CodigoMenu`) REFERENCES `urbaneats`.`menu` (`CodigoMenu`);


-- -----------------------------------------------------
-- Table `urbaneats`.`detalle_pedido`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`detalle_pedido` (
  `CodigoDetalle` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoPedido` INT(11) NOT NULL,
  `CodigoPlato` INT(11) NOT NULL,
  `Cantidad` INT NOT NULL,
  `PrecioUnitario` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`CodigoDetalle`),
  INDEX `CodigoPedido` (`CodigoPedido` ASC),
  INDEX `CodigoPlato` (`CodigoPlato` ASC),
  CONSTRAINT `detalle_pedido_ibfk_1`
    FOREIGN KEY (`CodigoPedido`)
    REFERENCES `urbaneats`.`pedido` (`CodigoPedido`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `detalle_pedido_ibfk_2`
    FOREIGN KEY (`CodigoPlato`)
    REFERENCES `urbaneats`.`plato` (`CodigoPlato`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`rol` (
  `CodigoRol` INT(11) NOT NULL AUTO_INCREMENT,
  `NombreRol` VARCHAR(15) NULL DEFAULT NULL,
  `DescripcionRol` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`rol_usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`rol_usuario` (
  `CodigoUsuario` INT(11) NOT NULL,
  `CodigoRol` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoUsuario`, `CodigoRol`),
  INDEX `CodigoRol` (`CodigoRol` ASC),
  CONSTRAINT `rol_usuario_ibfk_1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `urbaneats`.`usuario` (`CodigoUsuario`),
  CONSTRAINT `rol_usuario_ibfk_2`
    FOREIGN KEY (`CodigoRol`)
    REFERENCES `urbaneats`.`rol` (`CodigoRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`transaccion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`transaccion` (
  `TransaccionID` VARCHAR(50) NOT NULL,
  `CodigoPago` INT(11) NOT NULL,
  `MetodoPago` VARCHAR(50) NULL DEFAULT NULL,
  `BancoNombre` VARCHAR(50) NULL DEFAULT NULL,
  `CUS` VARCHAR(50) NULL DEFAULT NULL,
  `CodigoRespuesta` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`TransaccionID`),
  UNIQUE INDEX `CodigoPago` (`CodigoPago` ASC),
  CONSTRAINT `transaccion_ibfk_1`
    FOREIGN KEY (`CodigoPago`)
    REFERENCES `urbaneats`.`pago` (`CodigoPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `urbaneats`.`vehiculo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `urbaneats`.`vehiculo` (
  `Placa` VARCHAR(20) NOT NULL,
  `CodigoRepartidor` INT(11) NULL DEFAULT NULL,
  `TipoVehiculo` ENUM('Moto', 'Carro', 'Bicicleta', 'Bici') NOT NULL,
  `SeguroVehiculo` VARCHAR(100) NULL DEFAULT NULL,
  `SOAT` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`Placa`),
  INDEX `CodigoRepartidor` (`CodigoRepartidor` ASC),
  CONSTRAINT `vehiculo_ibfk_1`
    FOREIGN KEY (`CodigoRepartidor`)
    REFERENCES `urbaneats`.`repartidor` (`CodigoRepartidor`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

USE `urbaneats` ;

-- -----------------------------------------------------
-- procedure ActualizarPedido
-- -----------------------------------------------------

DELIMITER $$
USE `urbaneats`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ActualizarPedido`(IN CodigoPedido INT, Llegada boolean)
BEGIN
	DECLARE Mensaje Varchar(50);
	IF(Llegada = TRUE) THEN
		SET Mensaje = 'Entregado';
    ELSE
		SET Mensaje = 'Cancelado';
    END IF;
    
    IF EXISTS(
		SELECT 1 FROM pedido p
		WHERE p.CodigoPedido = CodigoPedido
        AND Estado = 'En Proceso'
	)THEN 
		UPDATE pedido p SET Estado = Mensaje 
		WHERE p.CodigoPedido = CodigoPedido;
        SELECT 'Pedido actualizado correctamente' AS Mensaje;
	ELSE
		SELECT 'Este pedido ya ha sido modificado o no existe' AS Mensaje;
	END IF;
            
END$$

DELIMITER ;

-- -----------------------------------------------------
-- function VerRepartidor
-- -----------------------------------------------------

DELIMITER $$
USE `urbaneats`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `VerRepartidor`(idRepartidor INT) RETURNS varchar(100) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_nombre VARCHAR(100);

    SELECT u.Nombres
    INTO v_nombre
    FROM usuario u
              JOIN Repartidor r ON r.CodigoUsuario = u.CodigoUsuario
    WHERE r.CodigoRepartidor = idRepartidor
    LIMIT 1;

    RETURN COALESCE(v_nombre, 'REPARTIDOR NO ENCONTRADO');
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure VerificarPedidos
-- -----------------------------------------------------

DELIMITER $$
USE `urbaneats`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `VerificarPedidos`(IN CodigoCliente INT)
BEGIN
	IF EXISTS(
		SELECT 1 
        FROM pedido p
        JOIN Envio e ON e.CodigoEnvio = p.CodigoEnvio WHERE CodigoCliente = e.CodigoCliente
    )THEN
		SELECT FechaPedido FROM pedido p
        JOIN Envio e ON p.CodigoEnvio = e.CodigoEnvio
        WHERE CodigoCliente = e.CodigoCliente;
	ELSE
		SELECT 'No se encontro pedido alguno' AS 'SIN RESULTADO';
	END IF;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- function ver_estado_pedido
-- -----------------------------------------------------

DELIMITER $$
USE `urbaneats`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `ver_estado_pedido`(idPedido INT) RETURNS varchar(50) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE estadoPedido VARCHAR(50);
    SELECT Estado INTO estadoPedido
    FROM pedido
    WHERE CodigoPedido = idPedido;
    RETURN estadoPedido;
END$$

DELIMITER ;
USE `urbaneats`;

DELIMITER $$
USE `urbaneats`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `urbaneats`.`crear_cliente_automaticamente`
AFTER INSERT ON `urbaneats`.`usuario`
FOR EACH ROW
BEGIN
    INSERT INTO cliente (CodigoUsuario)
    VALUES (NEW.CodigoUsuario);
END$$


DELIMITER ;
USE `urbaneats`;

DELIMITER $$
USE `urbaneats`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `urbaneats`.`crear_pedido_automaticamente`
AFTER INSERT ON `urbaneats`.`envio`
FOR EACH ROW
BEGIN
    INSERT INTO pedido (CodigoEnvio, CodigoRestaurante, FechaPedido, Estado)
    VALUES (NEW.CodigoEnvio, NEW.CodigoRestaurante, CURDATE(), 'Iniciando');
END$$


DELIMITER ;

-- FK plato → menu (definida al final porque plato se crea antes que menu)
ALTER TABLE `urbaneats`.`plato`
  ADD INDEX `CodigoMenu_idx` (`CodigoMenu` ASC),
  ADD CONSTRAINT `plato_ibfk_menu`
    FOREIGN KEY (`CodigoMenu`)
    REFERENCES `urbaneats`.`menu` (`CodigoMenu`)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
