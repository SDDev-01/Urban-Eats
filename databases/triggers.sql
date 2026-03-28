DELIMITER //

-- Al insertar en Usuario, crea automáticamente el Cliente
CREATE TRIGGER crear_cliente_automaticamente
    AFTER INSERT ON Usuario
    FOR EACH ROW
BEGIN
    INSERT INTO Cliente (CodigoUsuario)
    VALUES (NEW.CodigoUsuario);
END //

DELIMITER ;

DELIMITER //

-- Al insertar en Envio, crea automáticamente el Pedido
CREATE TRIGGER crear_pedido_automaticamente
    AFTER INSERT ON Envio
    FOR EACH ROW
BEGIN
    INSERT INTO Pedido (CodigoEnvio, CodigoRestaurante, FechaPedido, Estado)
    VALUES (NEW.CodigoEnvio, NEW.CodigoRestaurante, CURDATE(), 'En Proceso');
END //

DELIMITER ;
 