DELIMITER //

CREATE TRIGGER crear_cliente_automaticamente
AFTER INSERT ON Persona
FOR EACH ROW
BEGIN
	INSERT INTO Cliente (CodigoPersona)
    VALUES (NEW.CodigoPersona);
END //

DELIMITER ;