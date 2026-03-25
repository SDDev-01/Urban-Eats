DELIMITER //

CREATE Function ver_estado_pedido(idPedido INT)
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
	DECLARE estadoPedido VARCHAR(50);
	SELECT Estado INTO estadoPedido from Pedido WHERE CodigoPedido = idPedido;
    RETURN estadoPedido;
END //

DELIMITER ;
 DELIMITER //

CREATE FUNCTION VerRepartidor(CodigoRepartidor INT)
RETURNS VARCHAR(50)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_nombre VARCHAR(50);

    SELECT Nombres 
      INTO v_nombre 
      FROM Persona 
     WHERE CodigoRepartidor = CodigoPersona
     LIMIT 1;

    -- Siempre debes devolver algo
    RETURN COALESCE(v_nombre, 'REPARTIDOR NO ENCONTRADO');

END //

DELIMITER ;
