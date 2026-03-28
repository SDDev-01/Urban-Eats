SELECT c.CodigoCliente, u.Nombres, p.FechaPedido
	FROM Cliente c
    JOIN Usuario u ON c.CodigoUsuario = u.CodigoUsuario
    JOIN Envio e ON c.CodigoCliente = e.CodigoCliente
    JOIN Pedido p ON e.CodigoEnvio = p.CodigoEnvio;
    
