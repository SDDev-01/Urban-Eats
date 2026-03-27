-- ==============
--  UrbanEats
-- ==============
CREATE DATABASE IF NOT EXISTS UrbanEats;
USE UrbanEats;

-- ============================
--  TABLA DEPARTAMENTOS
-- ============================
CREATE TABLE IF NOT EXISTS Departamentos (
                                             CodigoDepartamento INT PRIMARY KEY,
                                             Nombre             VARCHAR(255)
);

-- ============================
--  TABLA CIUDADES
-- ============================
CREATE TABLE IF NOT EXISTS Ciudades (
                                        CodigoCiudad       INT PRIMARY KEY,
                                        CodigoDepartamento INT,
                                        Nombre             VARCHAR(255),
                                        Latitud            DECIMAL(10, 8),
                                        Longitud           DECIMAL(10, 8),
                                        FOREIGN KEY (CodigoDepartamento) REFERENCES Departamentos(CodigoDepartamento)
);

-- ============================
--  TABLA USUARIO
-- ============================
CREATE TABLE IF NOT EXISTS Usuario (
                                       CodigoPersona     INT AUTO_INCREMENT PRIMARY KEY,
                                       Nombres           VARCHAR(100),
                                       Apellidos         VARCHAR(100),
                                       Direccion         VARCHAR(200),
                                       Telefono          VARCHAR(50),
                                       CorreoElectronico VARCHAR(150)
);

-- ============================
--  TABLA ROL
-- ============================
CREATE TABLE IF NOT EXISTS Rol (
                                   CodigoRol INT AUTO_INCREMENT PRIMARY KEY,
                                   TipoRol   ENUM('Cliente', 'Repartidor', 'Administrador') NOT NULL
);

-- ============================
--  TABLA ROL_USUARIO (N:N)
-- ============================
CREATE TABLE IF NOT EXISTS Rol_Usuario (
                                           CodigoPersona INT,
                                           CodigoRol     INT,
                                           PRIMARY KEY (CodigoPersona, CodigoRol),
                                           FOREIGN KEY (CodigoPersona) REFERENCES Usuario(CodigoPersona),
                                           FOREIGN KEY (CodigoRol)     REFERENCES Rol(CodigoRol)
);

-- ============================
--  TABLA INFORMACIÓN BANCARIA
-- ============================
CREATE TABLE IF NOT EXISTS InformacionBancaria (
                                                   CodigoInfoBancaria INT AUTO_INCREMENT PRIMARY KEY,
                                                   NumeroCuenta       VARCHAR(50),
                                                   Banco              VARCHAR(100),
                                                   TipoCuenta         ENUM('Ahorro', 'Corriente', 'Credito') NOT NULL,
                                                   TitularCuenta      VARCHAR(150),
                                                   CodigoPersona      INT,
                                                   FOREIGN KEY (CodigoPersona) REFERENCES Usuario(CodigoPersona)
);

-- ============================
--  TABLA CLIENTE
--  Se llena automáticamente via trigger crear_cliente_automaticamente
-- ============================
CREATE TABLE IF NOT EXISTS Cliente (
                                       CodigoCliente      INT AUTO_INCREMENT PRIMARY KEY,
                                       CodigoPersona      INT NOT NULL,
                                       CodigoInfoBancaria INT,
                                       FOREIGN KEY (CodigoPersona)      REFERENCES Usuario(CodigoPersona),
                                       FOREIGN KEY (CodigoInfoBancaria) REFERENCES InformacionBancaria(CodigoInfoBancaria)
);

-- ============================
--  TABLA VEHICULO
-- ============================
CREATE TABLE IF NOT EXISTS Vehiculo (
                                        Placa          VARCHAR(20) PRIMARY KEY,
                                        Licencia       VARCHAR(50),
                                        TipoVehiculo   ENUM('Moto', 'Carro', 'Bicicleta') NOT NULL,
                                        SeguroVehiculo VARCHAR(100),
                                        SOAT           VARCHAR(100)
);

-- ============================
--  TABLA REPARTIDOR
-- ============================
CREATE TABLE IF NOT EXISTS Repartidor (
                                          CodigoRepartidor   INT AUTO_INCREMENT PRIMARY KEY,
                                          CodigoPersona      INT NOT NULL,
                                          Placa              VARCHAR(20),
                                          CodigoInfoBancaria INT,
                                          FOREIGN KEY (CodigoPersona)      REFERENCES Usuario(CodigoPersona),
                                          FOREIGN KEY (Placa)              REFERENCES Vehiculo(Placa),
                                          FOREIGN KEY (CodigoInfoBancaria) REFERENCES InformacionBancaria(CodigoInfoBancaria)
);

-- ============================
--  TABLA RESTAURANTE
-- ============================
CREATE TABLE IF NOT EXISTS Restaurante (
                                           CodigoRestaurante INT AUTO_INCREMENT PRIMARY KEY,
                                           CodigoCiudad      INT,
                                           Nombre            VARCHAR(150),
                                           Ubicacion         VARCHAR(200),
                                           Horario           VARCHAR(100),
                                           Latitud           DECIMAL(10, 8),
                                           Longitud          DECIMAL(10, 8),
                                           FOREIGN KEY (CodigoCiudad) REFERENCES Ciudades(CodigoCiudad)
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
--  TABLA PEDIDO
--  ⚡ Se llena automáticamente via trigger crear_pedido_automaticamente
-- ============================
CREATE TABLE IF NOT EXISTS Pedido (
                                      CodigoPedido      INT AUTO_INCREMENT PRIMARY KEY,
                                      CodigoEnvio       INT NOT NULL,
                                      CodigoRestaurante INT NOT NULL,
                                      FechaPedido       DATE,
                                      Estado            ENUM('En Proceso', 'Entregado', 'Cancelado') NOT NULL,
                                      FOREIGN KEY (CodigoEnvio)       REFERENCES Envio(CodigoEnvio),
                                      FOREIGN KEY (CodigoRestaurante) REFERENCES Restaurante(CodigoRestaurante)
);

-- ============================
--  TABLA PAGO
--  CodigoPedido → reemplazado por CodigoEnvio
--  Flujo: Cliente paga al confirmar el Envio
-- ============================
CREATE TABLE IF NOT EXISTS Pago (
                                    CodigoPago         INT AUTO_INCREMENT PRIMARY KEY,
                                    CodigoCliente      INT NOT NULL,
                                    CodigoInfoBancaria INT NOT NULL,
                                    CodigoEnvio        INT NOT NULL,
                                    Monto              DECIMAL(10, 2),
                                    MetodoPago         ENUM('Tarjeta', 'Efectivo') NOT NULL,
                                    FechaPago          DATE,
                                    HoraPago           TIME,
                                    FOREIGN KEY (CodigoCliente)      REFERENCES Cliente(CodigoCliente),
                                    FOREIGN KEY (CodigoInfoBancaria) REFERENCES InformacionBancaria(CodigoInfoBancaria),
                                    FOREIGN KEY (CodigoEnvio)        REFERENCES Envio(CodigoEnvio)
);

-- ============================
--  TABLA OPINIONES
-- ============================
CREATE TABLE IF NOT EXISTS Opiniones (
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