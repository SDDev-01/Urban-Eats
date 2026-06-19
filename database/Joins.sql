-- Clientes con sus pedidos
SELECT c.CodigoCliente, u.Nombres, p.FechaPedido
	FROM Cliente c
    JOIN Usuario u ON c.CodigoUsuario = u.CodigoUsuario
    JOIN Envio e ON c.CodigoCliente = e.CodigoCliente
    JOIN Pedido p ON e.CodigoEnvio = p.CodigoEnvio;

-- Platos disponibles por restaurante
SELECT pl.Nombre AS "Plato", r.Nombre AS "Restaurante"
	FROM Restaurante r
    JOIN Menu m ON m.CodigoRestaurante = r.CodigoRestaurante
    JOIN Plato pl ON pl.CodigoMenu = m.CodigoMenu;

-- Clientes y fechas de pedido (incluye clientes sin pedidos)
SELECT u.Nombres, p.FechaPedido
	FROM Cliente c
    LEFT JOIN Usuario u ON c.CodigoUsuario = u.CodigoUsuario
    LEFT JOIN Envio e ON c.CodigoCliente = e.CodigoCliente
    LEFT JOIN Pedido p ON e.CodigoEnvio = p.CodigoEnvio;

-- Detalle completo de pedidos: cliente, platos, cantidades y precios
SELECT u.Nombres, pe.FechaPedido, pe.Estado,
       pl.Nombre AS Plato, dp.Cantidad, dp.PrecioUnitario,
       (dp.Cantidad * dp.PrecioUnitario) AS Subtotal
	FROM pedido pe
    JOIN envio e ON pe.CodigoEnvio = e.CodigoEnvio
    JOIN cliente c ON e.CodigoCliente = c.CodigoCliente
    JOIN usuario u ON c.CodigoUsuario = u.CodigoUsuario
    JOIN detalle_pedido dp ON dp.CodigoPedido = pe.CodigoPedido
    JOIN plato pl ON dp.CodigoPlato = pl.CodigoPlato;
