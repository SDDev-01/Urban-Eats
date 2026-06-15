SELECT c.CodigoCliente, u.Nombres, p.FechaPedido
	FROM Cliente c
    JOIN Usuario u ON c.CodigoUsuario = u.CodigoUsuario
    JOIN Envio e ON c.CodigoCliente = e.CodigoCliente
    JOIN Pedido p ON e.CodigoEnvio = p.CodigoEnvio;
    
SELECT p.Nombre as "Plato", r.Nombre as "Restaurante"
	From Restaurante r
    JOIN Menu m ON m.CodigoRestaurante = r.CodigoRestaurante
    JOIN Plato_menu pm ON pm.CodigoMenu = m.CodigoMenu
    JOIN Plato p ON p.CodigoPlato = pm.CodigoPlato;
    
SELECT u.Nombres, p.FechaPedido 
	From Cliente c
    LEFT JOIN Usuario u ON c.CodigoUsuario = u.CodigoUsuario
	LEFT JOIN Envio e ON c.CodigoCliente = e.CodigoCliente
	LEFT JOIN Pedido p ON e.CodigoEnvio = p.CodigoEnvio;
    
    
