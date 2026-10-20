-- =====================================================================
-- Urban Eats - Fase 6 - 02_tablas_servicio_python.sql
-- Tablas propias del servicio Python (vinculacion de cocinas y rutas).
-- Generado desde los modelos de servicio-python/app/models.
-- No hace falta ejecutarlo a mano: 'python main.py' las crea si faltan.
-- =====================================================================
USE urbaneats;

CREATE TABLE IF NOT EXISTS ruta_envio (
	`CodigoEnvio` INTEGER NOT NULL, 
	`Proveedor` VARCHAR(30) NOT NULL, 
	`DistanciaMetros` INTEGER NOT NULL, 
	`DuracionSegundos` INTEGER NOT NULL, 
	`CoordenadasJson` TEXT NOT NULL, 
	`FechaCalculo` DATETIME NOT NULL DEFAULT (now()), 
	PRIMARY KEY (`CodigoEnvio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS solicitud_restaurante (
	`CodigoSolicitud` INTEGER NOT NULL AUTO_INCREMENT, 
	`CodigoUsuario` INTEGER NOT NULL, 
	`NombreComercial` VARCHAR(150) NOT NULL, 
	`RazonSocial` VARCHAR(150) NOT NULL, 
	`TipoPersona` VARCHAR(10) NOT NULL, 
	`Nit` VARCHAR(15) NOT NULL, 
	`DigitoVerificacion` VARCHAR(1) NOT NULL, 
	`MatriculaMercantil` VARCHAR(30), 
	`TipoEstablecimiento` VARCHAR(20) NOT NULL, 
	`Categoria` VARCHAR(60) NOT NULL, 
	`Telefono` VARCHAR(20) NOT NULL, 
	`Correo` VARCHAR(100) NOT NULL, 
	`Direccion` VARCHAR(200) NOT NULL, 
	`Localidad` VARCHAR(40) NOT NULL, 
	`Latitud` NUMERIC(10, 7), 
	`Longitud` NUMERIC(10, 7), 
	`HoraApertura` TIME NOT NULL, 
	`HoraCierre` TIME NOT NULL, 
	`AutorizaDatos` BOOL NOT NULL, 
	`Estado` VARCHAR(20) NOT NULL, 
	`Observaciones` VARCHAR(500), 
	`CodigoRevisor` INTEGER, 
	`CodigoRestaurante` INTEGER, 
	`EsDemo` BOOL NOT NULL, 
	`FechaCreacion` DATETIME NOT NULL DEFAULT (now()), 
	`FechaActualizacion` DATETIME, 
	PRIMARY KEY (`CodigoSolicitud`), 
	FOREIGN KEY(`CodigoUsuario`) REFERENCES usuario (`CodigoUsuario`), 
	UNIQUE (`Nit`), 
	FOREIGN KEY(`CodigoRevisor`) REFERENCES usuario (`CodigoUsuario`), 
	FOREIGN KEY(`CodigoRestaurante`) REFERENCES restaurante (`CodigoRestaurante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS documento_solicitud (
	`CodigoDocumento` INTEGER NOT NULL AUTO_INCREMENT, 
	`CodigoSolicitud` INTEGER NOT NULL, 
	`TipoDocumento` VARCHAR(40) NOT NULL, 
	`NumeroDocumento` VARCHAR(50), 
	`Titular` VARCHAR(150), 
	`Resultado` VARCHAR(40), 
	`HorasCapacitacion` INTEGER, 
	`FechaExpedicion` DATE NOT NULL, 
	`FechaVencimiento` DATE, 
	`RutaArchivo` VARCHAR(255), 
	`HashArchivo` VARCHAR(64), 
	`EstadoRevision` VARCHAR(20) NOT NULL, 
	`Observacion` VARCHAR(300), 
	PRIMARY KEY (`CodigoDocumento`), 
	FOREIGN KEY(`CodigoSolicitud`) REFERENCES solicitud_restaurante (`CodigoSolicitud`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS historial_solicitud (
	`CodigoHistorial` INTEGER NOT NULL AUTO_INCREMENT, 
	`CodigoSolicitud` INTEGER NOT NULL, 
	`EstadoAnterior` VARCHAR(20), 
	`EstadoNuevo` VARCHAR(20) NOT NULL, 
	`CodigoUsuario` INTEGER, 
	`Comentario` VARCHAR(500), 
	`Fecha` DATETIME NOT NULL DEFAULT (now()), 
	PRIMARY KEY (`CodigoHistorial`), 
	FOREIGN KEY(`CodigoSolicitud`) REFERENCES solicitud_restaurante (`CodigoSolicitud`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

