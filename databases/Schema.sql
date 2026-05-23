-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema UrbanEats
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema UrbanEats
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `UrbanEats` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `UrbanEats` ;

-- -----------------------------------------------------
-- Table `UrbanEats`.`Plato`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Plato` (
  `CodigoPlato` INT(11) NOT NULL AUTO_INCREMENT,
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
-- Table `UrbanEats`.`Alergeno`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Alergeno` (
  `CodigoAlergeno` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoPlato` INT(11) NULL DEFAULT NULL,
  `Nombre` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoAlergeno`),
  INDEX `CodigoPlato` (`CodigoPlato` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoPlato`)
    REFERENCES `UrbanEats`.`Plato` (`CodigoPlato`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Departamento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Departamento` (
  `CodigoDepartamento` INT(11) NOT NULL,
  `Nombre` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoDepartamento`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Ciudad`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Ciudad` (
  `CodigoCiudad` INT(11) NOT NULL,
  `CodigoDepartamento` INT(11) NOT NULL,
  `Nombre` VARCHAR(255) NULL DEFAULT NULL,
  `Latitud` DECIMAL(10,8) NULL DEFAULT NULL,
  `Longitud` DECIMAL(10,8) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoCiudad`),
  INDEX `CodigoDepartamento` (`CodigoDepartamento` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoDepartamento`)
    REFERENCES `UrbanEats`.`Departamento` (`CodigoDepartamento`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Usuario` (
  `CodigoUsuario` INT(11) NOT NULL AUTO_INCREMENT,
  `Nombres` VARCHAR(100) NULL DEFAULT NULL,
  `Apellidos` VARCHAR(100) NULL DEFAULT NULL,
  `Direccion` VARCHAR(200) NULL DEFAULT NULL,
  `Telefono` VARCHAR(50) NULL DEFAULT NULL,
  `CorreoElectronico` VARCHAR(150) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Cliente` (
  `CodigoCliente` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoUsuario` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoCliente`),
  INDEX `CodigoUsuario` (`CodigoUsuario` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `UrbanEats`.`Usuario` (`CodigoUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Repartidor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Repartidor` (
  `CodigoRepartidor` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoUsuario` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoRepartidor`),
  INDEX `CodigoUsuario` (`CodigoUsuario` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `UrbanEats`.`Usuario` (`CodigoUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Gerente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Gerente` (
  `CodigoGerente` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoUsuario` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoGerente`),
  INDEX `CodigoUsuario` (`CodigoUsuario` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `UrbanEats`.`Usuario` (`CodigoUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Restaurante`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Restaurante` (
  `CodigoRestaurante` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoCiudad` INT(11) NOT NULL,
  `CodigoGerente` INT(11) NOT NULL,
  `Nombre` VARCHAR(150) NULL DEFAULT NULL,
  `Ubicacion` VARCHAR(200) NULL DEFAULT NULL,
  `Horario` VARCHAR(100) NULL DEFAULT NULL,
  `Latitud` DECIMAL(10,8) NULL DEFAULT NULL,
  `Longitud` DECIMAL(10,8) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoRestaurante`),
  INDEX `CodigoCiudad` (`CodigoCiudad` ASC) VISIBLE,
  INDEX `CodigoGerente` (`CodigoGerente` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoCiudad`)
    REFERENCES `UrbanEats`.`Ciudad` (`CodigoCiudad`),
  CONSTRAINT `2`
    FOREIGN KEY (`CodigoGerente`)
    REFERENCES `UrbanEats`.`Gerente` (`CodigoGerente`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Envio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Envio` (
  `CodigoEnvio` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoCliente` INT(11) NOT NULL,
  `CodigoRepartidor` INT(11) NOT NULL,
  `CodigoRestaurante` INT(11) NOT NULL,
  `Descripcion` VARCHAR(300) NULL DEFAULT NULL,
  `FechaEnvio` DATE NULL DEFAULT NULL,
  `HoraEntrega` TIME NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoEnvio`),
  INDEX `CodigoCliente` (`CodigoCliente` ASC) VISIBLE,
  INDEX `CodigoRepartidor` (`CodigoRepartidor` ASC) VISIBLE,
  INDEX `CodigoRestaurante` (`CodigoRestaurante` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoCliente`)
    REFERENCES `UrbanEats`.`Cliente` (`CodigoCliente`),
  CONSTRAINT `2`
    FOREIGN KEY (`CodigoRepartidor`)
    REFERENCES `UrbanEats`.`Repartidor` (`CodigoRepartidor`),
  CONSTRAINT `3`
    FOREIGN KEY (`CodigoRestaurante`)
    REFERENCES `UrbanEats`.`Restaurante` (`CodigoRestaurante`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Menu` (
  `CodigoMenu` INT(11) NOT NULL AUTO_INCREMENT,
  `Categoria` VARCHAR(100) NULL DEFAULT NULL,
  `CodigoRestaurante` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoMenu`),
  INDEX `CodigoRestaurante` (`CodigoRestaurante` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoRestaurante`)
    REFERENCES `UrbanEats`.`Restaurante` (`CodigoRestaurante`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Opinion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Opinion` (
  `CodigoComentario` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoPlato` INT(11) NULL DEFAULT NULL,
  `CodigoCliente` INT(11) NULL DEFAULT NULL,
  `CodigoRepartidor` INT(11) NULL DEFAULT NULL,
  `Opinion` VARCHAR(300) NULL DEFAULT NULL,
  `Fecha` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoComentario`),
  INDEX `CodigoPlato` (`CodigoPlato` ASC) VISIBLE,
  INDEX `CodigoCliente` (`CodigoCliente` ASC) VISIBLE,
  INDEX `CodigoRepartidor` (`CodigoRepartidor` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoPlato`)
    REFERENCES `UrbanEats`.`Plato` (`CodigoPlato`),
  CONSTRAINT `2`
    FOREIGN KEY (`CodigoCliente`)
    REFERENCES `UrbanEats`.`Cliente` (`CodigoCliente`),
  CONSTRAINT `3`
    FOREIGN KEY (`CodigoRepartidor`)
    REFERENCES `UrbanEats`.`Repartidor` (`CodigoRepartidor`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Pago` (
  `CodigoPago` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoCliente` INT(11) NOT NULL,
  `CodigoEnvio` INT(11) NOT NULL,
  `Monto` DECIMAL(10,2) NULL DEFAULT NULL,
  `FechaPago` DATE NULL DEFAULT NULL,
  `HoraPago` TIME NULL DEFAULT NULL,
  `EstadoPago` ENUM('Aceptado', 'Rechazado') NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoPago`),
  INDEX `CodigoCliente` (`CodigoCliente` ASC) VISIBLE,
  INDEX `CodigoEnvio` (`CodigoEnvio` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoCliente`)
    REFERENCES `UrbanEats`.`Cliente` (`CodigoCliente`),
  CONSTRAINT `2`
    FOREIGN KEY (`CodigoEnvio`)
    REFERENCES `UrbanEats`.`Envio` (`CodigoEnvio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Pedido`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Pedido` (
  `CodigoPedido` INT(11) NOT NULL AUTO_INCREMENT,
  `CodigoEnvio` INT(11) NOT NULL,
  `CodigoRestaurante` INT(11) NOT NULL,
  `FechaPedido` DATE NULL DEFAULT NULL,
  `Estado` ENUM('En Proceso', 'Entregado', 'Cancelado') NOT NULL,
  PRIMARY KEY (`CodigoPedido`),
  UNIQUE INDEX `CodigoEnvio` (`CodigoEnvio` ASC) VISIBLE,
  INDEX `CodigoRestaurante` (`CodigoRestaurante` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoEnvio`)
    REFERENCES `UrbanEats`.`Envio` (`CodigoEnvio`),
  CONSTRAINT `2`
    FOREIGN KEY (`CodigoRestaurante`)
    REFERENCES `UrbanEats`.`Restaurante` (`CodigoRestaurante`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Plato_menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Plato_menu` (
  `CodigoMenu` INT(11) NOT NULL,
  `CodigoPlato` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoMenu`, `CodigoPlato`),
  INDEX `CodigoPlato` (`CodigoPlato` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoMenu`)
    REFERENCES `UrbanEats`.`Menu` (`CodigoMenu`),
  CONSTRAINT `2`
    FOREIGN KEY (`CodigoPlato`)
    REFERENCES `UrbanEats`.`Plato` (`CodigoPlato`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Rol` (
  `CodigoRol` INT(11) NOT NULL AUTO_INCREMENT,
  `NombreRol` VARCHAR(15) NULL DEFAULT NULL,
  `DescripcionRol` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`CodigoRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Rol_Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Rol_Usuario` (
  `CodigoUsuario` INT(11) NOT NULL,
  `CodigoRol` INT(11) NOT NULL,
  PRIMARY KEY (`CodigoUsuario`, `CodigoRol`),
  INDEX `CodigoRol` (`CodigoRol` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoUsuario`)
    REFERENCES `UrbanEats`.`Usuario` (`CodigoUsuario`),
  CONSTRAINT `2`
    FOREIGN KEY (`CodigoRol`)
    REFERENCES `UrbanEats`.`Rol` (`CodigoRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Transaccion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Transaccion` (
  `TransaccionID` VARCHAR(50) NOT NULL,
  `CodigoPago` INT(11) NOT NULL,
  `MetodoPago` VARCHAR(50) NULL DEFAULT NULL,
  `BancoNombre` VARCHAR(50) NULL DEFAULT NULL,
  `CUS` VARCHAR(50) NULL DEFAULT NULL,
  `CodigoRespuesta` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`TransaccionID`),
  UNIQUE INDEX `CodigoPago` (`CodigoPago` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoPago`)
    REFERENCES `UrbanEats`.`Pago` (`CodigoPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `UrbanEats`.`Vehiculo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UrbanEats`.`Vehiculo` (
  `Placa` VARCHAR(20) NOT NULL,
  `CodigoRepartidor` INT(11) NULL DEFAULT NULL,
  `Licencia` VARCHAR(50) NULL DEFAULT NULL,
  `TipoVehiculo` ENUM('Moto', 'Carro', 'Bicicleta') NOT NULL,
  `SeguroVehiculo` VARCHAR(100) NULL DEFAULT NULL,
  `SOAT` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`Placa`),
  INDEX `CodigoRepartidor` (`CodigoRepartidor` ASC) VISIBLE,
  CONSTRAINT `1`
    FOREIGN KEY (`CodigoRepartidor`)
    REFERENCES `UrbanEats`.`Repartidor` (`CodigoRepartidor`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

USE `UrbanEats` ;

-- -----------------------------------------------------
-- procedure ActualizarPedido
-- -----------------------------------------------------

DELIMITER $$
USE `UrbanEats`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ActualizarPedido`(IN CodigoPedido INT, Llegada boolean)
BEGIN
	DECLARE Mensaje Varchar(50);
	IF(Llegada = TRUE) THEN
		SET Mensaje = 'Entregado';
    ELSE
		SET Mensaje = 'Cancelado';
    END IF;
    
    IF EXISTS(
		SELECT 1 FROM Pedido p
		WHERE p.CodigoPedido = CodigoPedido
        AND Estado = 'En Proceso'
	)THEN 
		UPDATE Pedido p SET Estado = Mensaje 
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
USE `UrbanEats`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `VerRepartidor`(idRepartidor INT) RETURNS varchar(100) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_nombre VARCHAR(100);

    SELECT u.Nombres
    INTO v_nombre
    FROM Usuario u
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
USE `UrbanEats`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `VerificarPedidos`(IN CodigoCliente INT)
BEGIN
	IF EXISTS(
		SELECT 1 
        FROM Pedido p
        JOIN Envio e ON e.CodigoEnvio = p.CodigoEnvio WHERE CodigoCliente = e.CodigoCliente
    )THEN
		SELECT FechaPedido FROM Pedido p
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
USE `UrbanEats`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `ver_estado_pedido`(idPedido INT) RETURNS varchar(50) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE estadoPedido VARCHAR(50);
    SELECT Estado INTO estadoPedido
    FROM Pedido
    WHERE CodigoPedido = idPedido;
    RETURN estadoPedido;
END$$

DELIMITER ;
USE `UrbanEats`;

DELIMITER $$
USE `UrbanEats`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `UrbanEats`.`crear_cliente_automaticamente`
AFTER INSERT ON `UrbanEats`.`Usuario`
FOR EACH ROW
BEGIN
    INSERT INTO Cliente (CodigoUsuario)
    VALUES (NEW.CodigoUsuario);
END$$

USE `UrbanEats`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `UrbanEats`.`crear_pedido_automaticamente`
AFTER INSERT ON `UrbanEats`.`Envio`
FOR EACH ROW
BEGIN
    INSERT INTO Pedido (CodigoEnvio, CodigoRestaurante, FechaPedido, Estado)
    VALUES (NEW.CodigoEnvio, NEW.CodigoRestaurante, CURDATE(), 'En Proceso');
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
