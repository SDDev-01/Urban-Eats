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