# Base de datos `urbaneats`

## Instalación nueva (XAMPP / MariaDB 10.4 o MySQL 8)

Ejecutar en este orden:

```bash
mysql -u root < database/Schema.sql
mysql -u root urbaneats < database/Inserts.sql
mysql -u root urbaneats < database/fase6/01_migracion.sql
mysql -u root urbaneats < database/fase6/03_datos_bogota_nocturno.sql
```

`fase6/02_tablas_servicio_python.sql` no hace falta ejecutarlo: el servicio Python crea sus tablas al arrancar (`python main.py`). Se deja como referencia.

## Base que ya estaba en uso

1. Si `cliente`, `repartidor` o `gerente` tienen filas duplicadas, revise primero `limpieza_datos_prueba.sql`.
2. Ejecute `fase6/01_migracion.sql` una sola vez.
3. `fase6/03_datos_bogota_nocturno.sql` es opcional: reubica las cocinas de prueba en Bogotá y crea los usuarios de prueba.

## Qué cambió en la fase 6

| Cambio | Para qué |
|---|---|
| `pedido.Estado`: Solicitado → En preparacion → Listo para envio → En camino → Entregado / Cancelado | Flujo real de cliente, cocina y repartidor |
| `pedido.FechaHora` y tabla `historial_pedido` | Hora real del pedido, auditoría y tiempos de entrega |
| `envio.CodigoDireccion` y posición del repartidor | Mapa y recorrido del domicilio |
| Coordenadas en `direccion` y `restaurante`, horario estructurado, `AceptaPedidos`, `Activo` | Mapa, cocinas abiertas ahora, pausar pedidos |
| `plato.Calorias` y `plato.Etiquetas` | Sello "Balanceado" e información nutricional |
| `opinion.Calificacion` y `opinion.CodigoPedido` | Calificar solo pedidos entregados |
| Tablas del servicio Python: `solicitud_restaurante`, `documento_solicitud`, `historial_solicitud`, `ruta_envio` | Verificación de cocinas y caché de rutas |

El trigger `crear_pedido_automaticamente` sigue creando el pedido al insertar el envío (ahora en estado "Solicitado"). Java ya no inserta un segundo pedido, que era lo que impedía guardar cualquier pedido.

Usuarios de prueba: ver `fase6/USUARIOS_DE_PRUEBA.md`.
