DELIMITER //

CREATE TRIGGER crear_cliente_automaticamente
AFTER INSERT ON Persona
FOR EACH ROW
BEGIN
	INSERT INTO Cliente (CodigoPersona)
    VALUES (NEW.CodigoPersona);
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER crear_pedido_automaticamente
AFTER INSERT ON Envio
FOR EACH ROW
BEGIN
	INSERT INTO Pedido(CodigoEnvio,Descripcion,FechaPedido,Estado)
    VALUES (NEW.CodigoEnvio,curdate(),"En proceso");
END //

DELIMITER ;