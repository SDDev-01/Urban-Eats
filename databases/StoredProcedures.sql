DELIMITER //
CREATE PROCEDURE VerificarPedidos(IN CodigoCliente INT)
BEGIN
	IF EXISTS(
		SELECT 1 
        FROM Pedido p
        JOIN Envio e ON e.CodigoEnvio = p.CodigoEnvio WHERE CodigoCliente = e.CodigoCliente
    )THEN
		SELECT FechaPedido FROM Pedido p
        JOIN Envio e ON p.CodigoEnvio = e.CodigoEnvio
        WHERE CodigoCliente = e.CodigoCliente;
	ELSE
		SELECT "No se encontro pedido alguno" AS "SIN RESULTADO";
	END IF;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE ActualizarPedido(IN CodigoPedido INT, Llegada boolean)
BEGIN
	DECLARE Mensaje Varchar(50);
	IF(Llegada = TRUE) THEN
		SET Mensaje = "Entregado";
    ELSE
		SET Mensaje = "Cancelado";
    END IF;
    
    IF EXISTS(
		SELECT 1 FROM Pedido 
		WHERE p.CodigoPedido = Codigo
        AND Estado = "En Proceso"
	)THEN 
		UPDATE Pedido p SET Estado = Mensaje 
		WHERE p.CodigoPedido = CodigoPedido;
        SELECT "Pedido actualizado correctamente" AS Mensaje;
	ELSE
		SELECT "Este pedido ya ha sido modificado o no existe" AS Mensaje;
	END IF;
            
END//
DELIMITER ;