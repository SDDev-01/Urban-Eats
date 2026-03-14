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
                             Nombre VARCHAR(150),
                             Ubicacion VARCHAR(200),
                             Horario VARCHAR(100)
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


