USE UrbanEats;

-- ============================
--  FUNCIÓN: ver_estado_pedido
-- ============================
DELIMITER //

CREATE FUNCTION ver_estado_pedido(idPedido INT)
    RETURNS VARCHAR(50)
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE estadoPedido VARCHAR(50);
    SELECT Estado INTO estadoPedido
    FROM Pedido
    WHERE CodigoPedido = idPedido;
    RETURN estadoPedido;
END //

DELIMITER ;

-- ============================
--  FUNCIÓN: VerRepartidor
-- ============================
DELIMITER //

CREATE FUNCTION VerRepartidor(idRepartidor INT)
    RETURNS VARCHAR(100)
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE v_nombre VARCHAR(100);

    SELECT u.Nombres
    INTO v_nombre
    FROM Usuario u
             JOIN Repartidor r ON r.CodigoUsuario = u.CodigoUsuario
    WHERE r.CodigoRepartidor = idRepartidor
    LIMIT 1;

    RETURN COALESCE(v_nombre, 'REPARTIDOR NO ENCONTRADO');
END //

DELIMITER ;
 