-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS UrbanEats;
USE UrbanEats;

-- ============================
--        TABLA PERSONA
-- ============================
CREATE TABLE IF NOT EXISTS Persona (
                         CodigoPersona INT AUTO_INCREMENT PRIMARY KEY,
                         Nombres VARCHAR(100),
                         Apellidos VARCHAR(100),
                         Direccion VARCHAR(200),
                         Telefono VARCHAR(50),
                         CorreoElectronico VARCHAR(150)
);

-- ============================
--  TABLA INFORMACIÓN BANCARIA
-- ============================
CREATE TABLE IF NOT EXISTS InformacionBancaria (
                                     CodigoInfoBancaria INT AUTO_INCREMENT PRIMARY KEY,
                                     NumeroCuenta VARCHAR(50),
                                     Banco VARCHAR(100),
                                     TipoCuenta VARCHAR(50),
                                     TitularCuenta VARCHAR(150),
                                     CodigoPersona INT,
                                     FOREIGN KEY (CodigoPersona) REFERENCES Persona(CodigoPersona)
);

-- ============================
--        TABLA CLIENTE
-- ============================
CREATE TABLE IF NOT EXISTS Cliente (
                         CodigoCliente INT AUTO_INCREMENT PRIMARY KEY,
                         CodigoPersona INT NOT NULL,
                         FOREIGN KEY (CodigoPersona) REFERENCES Persona(CodigoPersona)
);

-- ============================
--        TABLA VEHICULO
-- ============================
CREATE TABLE IF NOT EXISTS Vehiculo (
                          Placa VARCHAR(20) PRIMARY KEY,
                          Licencia VARCHAR(50),
                          TipoVehiculo VARCHAR(50),
                          SeguroVehiculo VARCHAR(100),
                          SOAT VARCHAR(100)
);

-- ============================
--        TABLA REPARTIDOR
-- ============================
CREATE TABLE IF NOT EXISTS Repartidor (
                            CodigoRepartidor INT AUTO_INCREMENT PRIMARY KEY,
                            CodigoPersona INT NOT NULL,
                            Placa VARCHAR(20),
                            FOREIGN KEY (CodigoPersona) REFERENCES Persona(CodigoPersona),
                            FOREIGN KEY (Placa) REFERENCES Vehiculo(Placa)
);

-- ============================
--           ENVÍO
-- ============================
CREATE TABLE IF NOT EXISTS Envio (
                       CodigoEnvio INT AUTO_INCREMENT PRIMARY KEY,
                       CodigoCliente INT NOT NULL,
                       CodigoRepartidor INT NOT NULL,
                       FechaEnvio DATE,
                       HoraEntrega TIME,
                       FOREIGN KEY (CodigoCliente) REFERENCES Cliente(CodigoCliente),
                       FOREIGN KEY (CodigoRepartidor) REFERENCES Repartidor(CodigoRepartidor)
);

-- ============================
--        RESTAURANTE
-- ============================
CREATE TABLE IF NOT EXISTS Restaurante (
                             CodigoRestaurante INT AUTO_INCREMENT PRIMARY KEY,
                            CodigoCiudad INT,
                             Nombre VARCHAR(150),
                             Ubicacion VARCHAR(200),
                             Horario VARCHAR(100),
                            Latitud DECIMAL(10, 8),
                            Longitud DECIMAL(10, 8)
                            FOREIGN KEY (CodigoCiudad) REFERENCES Ciudades(CodigoCiudad)
);

-- ============================
--            MENU
-- ============================
CREATE TABLE IF NOT EXISTS Menu (
                      CodigoMenu INT AUTO_INCREMENT PRIMARY KEY,
                      Categoria VARCHAR(100),
                      CodigoRestaurante INT NOT NULL,
                      FOREIGN KEY (CodigoRestaurante) REFERENCES Restaurante(CodigoRestaurante)
);

-- ============================
--            PLATO
-- ============================
CREATE TABLE IF NOT EXISTS Plato (
                       CodigoPlato INT AUTO_INCREMENT PRIMARY KEY,
                       Nombre VARCHAR(150),
                       Descripcion VARCHAR(300),
                       Precio DECIMAL(10,2),
                       TipoComida VARCHAR(100),
                       Alergenos VARCHAR(200),
                       Disponibilidad VARCHAR(50)
);

-- ============================
--      RELACIÓN MENU - PLATO (N:N)
-- ============================
CREATE TABLE IF NOT EXISTS Plato_menu (
                            CodigoMenu INT,
                            CodigoPlato INT,
                            PRIMARY KEY (CodigoMenu, CodigoPlato),
                            FOREIGN KEY (CodigoMenu) REFERENCES Menu(CodigoMenu),
                            FOREIGN KEY (CodigoPlato) REFERENCES Plato(CodigoPlato)
);

-- ============================
--          PEDIDO
-- ============================
CREATE TABLE IF NOT EXISTS Pedido (
                        CodigoPedido INT AUTO_INCREMENT PRIMARY KEY,
                        CodigoEnvio INT NOT NULL,
                        CodigoRestaurante INT NOT NULL,
                        Descripcion VARCHAR(300),
                        FechaPedido DATE,
                        Estado VARCHAR(50),
                        FOREIGN KEY (CodigoEnvio) REFERENCES Envio(CodigoEnvio),
                        FOREIGN KEY (CodigoRestaurante) REFERENCES Restaurante(CodigoRestaurante)
);

-- ============================
--            PAGO
-- ============================
CREATE TABLE IF NOT EXISTS Pago (
                      CodigoPago INT AUTO_INCREMENT PRIMARY KEY,
                      CodigoCliente INT NOT NULL,
                      CodigoInfoBancaria INT NOT NULL,
                      CodigoPedido INT NOT NULL,
                      Monto DECIMAL(10,2),
                      MetodoPago VARCHAR(50),
                      FechaPago DATE,
                      HoraPago TIME,
                      FOREIGN KEY (CodigoCliente) REFERENCES Cliente(CodigoCliente),
                      FOREIGN KEY (CodigoInfoBancaria) REFERENCES InformacionBancaria(CodigoInfoBancaria),
                      FOREIGN KEY (CodigoPedido) REFERENCES Pedido(CodigoPedido)
);

-- ============================
--  TABLA DEPARTAMENTOS
-- ============================
CREATE TABLE IF NOT EXISTS Departamentos(
                      CodigoDepartamento INT  PRIMARY KEY,
                      Nombre varchar(255)
  
);

-- ============================
--  TABLA CIUDADES
-- ============================
CREATE TABLE IF NOT EXISTS Ciudades (
                      CodigoCiudad INT PRIMARY KEY,
                      CodigoDepartamento INT,
                      Nombre VARCHAR(255),
                      Latitud DECIMAL(10, 8),
                      Longitud DECIMAL(10, 8),
                      FOREIGN KEY (CodigoDepartamento) REFERENCES Departamentos(CodigoDepartamento)
);
 
                      
