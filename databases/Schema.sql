-- ===========
--  UrbanEats
-- ===========
CREATE DATABASE IF NOT EXISTS UrbanEats;
USE UrbanEats;

-- ============================
--  TABLA DEPARTAMENTO
-- ============================
CREATE TABLE IF NOT EXISTS Departamento (
                                             CodigoDepartamento INT PRIMARY KEY,
                                             Nombre             VARCHAR(255)
);

-- ============================
--  TABLA CIUDAD
-- ============================
CREATE TABLE IF NOT EXISTS Ciudad (
                                        CodigoCiudad       INT PRIMARY KEY,
                                        CodigoDepartamento INT NOT NULL,
                                        Nombre             VARCHAR(255),
                                        Latitud            DECIMAL(10, 8),
                                        Longitud           DECIMAL(10, 8),
                                        FOREIGN KEY (CodigoDepartamento) REFERENCES Departamento(CodigoDepartamento)
);

-- ============================
--  TABLA USUARIO
-- ============================
CREATE TABLE IF NOT EXISTS Usuario (
                                       CodigoUsuario      INT AUTO_INCREMENT PRIMARY KEY,
                                       Nombres            VARCHAR(100),
                                       Apellidos          VARCHAR(100),
                                       Direccion          VARCHAR(200),
                                       Telefono           VARCHAR(50),
                                       CorreoElectronico  VARCHAR(150)
);

-- ============================
--  TABLA ROL
-- ============================
CREATE TABLE IF NOT EXISTS Rol (
                                   CodigoRol INT AUTO_INCREMENT PRIMARY KEY,
                                   NombreRol VARCHAR(15),
                                   DescripcionRol VARCHAR(50)
);

-- ============================
--  TABLA ROL_USUARIO (N:N)
-- ============================
CREATE TABLE IF NOT EXISTS Rol_Usuario (
                                           CodigoUsuario INT,
                                           CodigoRol     INT,
                                           PRIMARY KEY (CodigoUsuario, CodigoRol),
                                           FOREIGN KEY (CodigoUsuario) REFERENCES Usuario(CodigoUsuario),
                                           FOREIGN KEY (CodigoRol)     REFERENCES Rol(CodigoRol)
);



-- ============================
--  TABLA CLIENTE
--  Se llena automáticamente via trigger crear_cliente_automaticamente
-- ============================
CREATE TABLE IF NOT EXISTS Cliente (
                                       CodigoCliente      INT AUTO_INCREMENT PRIMARY KEY,
                                       CodigoUsuario      INT NOT NULL,
                                       FOREIGN KEY (CodigoUsuario)      REFERENCES Usuario(CodigoUsuario)
);

-- ============================
--  TABLA REPARTIDOR
-- ============================
CREATE TABLE IF NOT EXISTS Repartidor (
                                          CodigoRepartidor   INT AUTO_INCREMENT PRIMARY KEY,
                                          CodigoUsuario      INT NOT NULL,
                                          FOREIGN KEY (CodigoUsuario)      REFERENCES Usuario(CodigoUsuario)
);

-- ============================
--  TABLA VEHICULO
-- ============================
CREATE TABLE IF NOT EXISTS Vehiculo (
                                        Placa          VARCHAR(20) PRIMARY KEY,
                                        CodigoRepartidor INT,
                                        Licencia       VARCHAR(50),
                                        TipoVehiculo   ENUM('Moto', 'Carro', 'Bicicleta') NOT NULL,
                                        SeguroVehiculo VARCHAR(100),
                                        SOAT           VARCHAR(100),
                                        FOREIGN KEY (CodigoRepartidor)	REFERENCES Repartidor(CodigoRepartidor)
);

-- ============================
--  TABLA GERENTE
-- ============================
CREATE TABLE IF NOT EXISTS Gerente (
									CodigoGerente INT AUTO_INCREMENT PRIMARY KEY,
                                    CodigoUsuario INT NOT NULL,
                                    FOREIGN KEY (CodigoUsuario) REFERENCES Usuario(CodigoUsuario)
);


-- ============================
--  TABLA RESTAURANTE
-- ============================
CREATE TABLE IF NOT EXISTS Restaurante (
                                           CodigoRestaurante INT AUTO_INCREMENT PRIMARY KEY,
                                           CodigoCiudad      INT NOT NULL UNIQUE,
                                           Nombre            VARCHAR(150),
                                           Ubicacion         VARCHAR(200),
                                           Horario           VARCHAR(100),
                                           Latitud           DECIMAL(10, 8),
                                           Longitud          DECIMAL(10, 8),
                                           FOREIGN KEY (CodigoCiudad) REFERENCES Ciudad(CodigoCiudad)
);

-- ============================
--  TABLA ENVÍO
-- ============================
CREATE TABLE IF NOT EXISTS Envio (
                                     CodigoEnvio       INT AUTO_INCREMENT PRIMARY KEY,
                                     CodigoCliente     INT NOT NULL,
                                     CodigoRepartidor  INT NOT NULL,
                                     CodigoRestaurante INT NOT NULL,
                                     Descripcion       VARCHAR(300),
                                     FechaEnvio        DATE,
                                     HoraEntrega       TIME,
                                     FOREIGN KEY (CodigoCliente)     REFERENCES Cliente(CodigoCliente),
                                     FOREIGN KEY (CodigoRepartidor)  REFERENCES Repartidor(CodigoRepartidor),
                                     FOREIGN KEY (CodigoRestaurante) REFERENCES Restaurante(CodigoRestaurante)
);

-- ============================
--  TABLA PEDIDO
--  Se llena automáticamente via trigger crear_pedido_automaticamente
-- ============================
CREATE TABLE IF NOT EXISTS Pedido (
                                      CodigoPedido      INT AUTO_INCREMENT PRIMARY KEY,
                                      CodigoEnvio       INT NOT NULL UNIQUE,
                                      CodigoRestaurante INT NOT NULL,
                                      FechaPedido       DATE,
                                      Estado            ENUM('En Proceso', 'Entregado', 'Cancelado') NOT NULL,
                                      FOREIGN KEY (CodigoEnvio)       REFERENCES Envio(CodigoEnvio),
                                      FOREIGN KEY (CodigoRestaurante) REFERENCES Restaurante(CodigoRestaurante)
);

-- ============================
--  TABLA MENU
-- ============================
CREATE TABLE IF NOT EXISTS Menu (
                                    CodigoMenu        INT AUTO_INCREMENT PRIMARY KEY,
                                    Categoria         VARCHAR(100),
                                    CodigoRestaurante INT NOT NULL,
                                    FOREIGN KEY (CodigoRestaurante) REFERENCES Restaurante(CodigoRestaurante)
);

-- ============================
--  TABLA PLATO
-- ============================
CREATE TABLE IF NOT EXISTS Plato (
                                     CodigoPlato    INT AUTO_INCREMENT PRIMARY KEY,
                                     Nombre         VARCHAR(150),
                                     Descripcion    VARCHAR(300),
                                     Precio         DECIMAL(10, 2),
                                     TipoComida     VARCHAR(100),
                                     Alergenos      VARCHAR(200),
                                     Disponibilidad VARCHAR(50)
);

-- ============================
--  TABLA ALERGENO
-- ============================
CREATE TABLE IF NOT EXISTS Alergeno(
									CodigoAlergeno INT AUTO_INCREMENT PRIMARY KEY,
                                    CodigoPlato INT,
                                    Nombre VARCHAR(50),
                                    FOREIGN KEY (CodigoPlato) REFERENCES Plato(CodigoPlato)
);


-- ============================
--  RELACIÓN MENU - PLATO (N:N)
-- ============================
CREATE TABLE IF NOT EXISTS Plato_menu (
                                          CodigoMenu  INT,
                                          CodigoPlato INT,
                                          PRIMARY KEY (CodigoMenu, CodigoPlato),
                                          FOREIGN KEY (CodigoMenu)  REFERENCES Menu(CodigoMenu),
                                          FOREIGN KEY (CodigoPlato) REFERENCES Plato(CodigoPlato)
);



-- ============================
--  TABLA PAGO
-- ============================
CREATE TABLE IF NOT EXISTS Pago (
                                    CodigoPago         INT AUTO_INCREMENT PRIMARY KEY,
                                    CodigoCliente      INT NOT NULL,
                                    CodigoEnvio        INT NOT NULL,
                                    Monto              DECIMAL(10, 2),
                                    FechaPago          DATE,
                                    HoraPago           TIME,
                                    EstadoPago         ENUM("Aceptado", "Rechazado"),
                                    FOREIGN KEY (CodigoCliente)      REFERENCES Cliente(CodigoCliente),
                                    FOREIGN KEY (CodigoEnvio)        REFERENCES Envio(CodigoEnvio)  
);

-- ============================
--  TABLA Transaccion
-- ============================
CREATE TABLE IF NOT EXISTS Transaccion(
										TransaccionID VARCHAR(50) PRIMARY KEY,
                                        CodigoPago INT NOT NULL UNIQUE,
                                        MetodoPago VARCHAR(50),
                                        BancoNombre VARCHAR(50),
                                        CUS VARCHAR(50),
                                        CodigoRespuesta VARCHAR(50),
                                        FOREIGN KEY (CodigoPago) REFERENCES Pago(CodigoPago)
);
 
-- ============================
--  TABLA OPINION
-- ============================
CREATE TABLE IF NOT EXISTS Opinion (
                                         CodigoComentario INT AUTO_INCREMENT PRIMARY KEY,
                                         CodigoPlato      INT,
                                         CodigoCliente    INT,
                                         CodigoRepartidor INT,
                                         Opinion          VARCHAR(300),
                                         Fecha            DATE,
                                         FOREIGN KEY (CodigoPlato)      REFERENCES Plato(CodigoPlato),
                                         FOREIGN KEY (CodigoCliente)    REFERENCES Cliente(CodigoCliente),
                                         FOREIGN KEY (CodigoRepartidor) REFERENCES Repartidor(CodigoRepartidor)
);

