# Usuarios de prueba (fase 6)

Solo para la base local y la demostración. Datos ficticios; el dominio `.test` está reservado y no recibe correos.

Contraseña de todos: `UrbanEats2026*`

| Correo | Rol | Para qué sirve en la demo |
|---|---|---|
| `cliente.demo@urbaneats.test` | Cliente | Laura, asesora de un contact center (turno nocturno). Hace el pedido y lo sigue en el mapa. |
| `cocina.demo@urbaneats.test` | Gerente (cocina) | Andrés, dueño de *Don Pollo Nocturno* y *Las Brasas 24h*. Acepta, prepara y marca listo. |
| `repartidor.demo@urbaneats.test` | Repartidor | Sergio, moto `UEX12F`. Toma el pedido, simula el recorrido y entrega. |
| `tecnico.demo@urbaneats.test` | Técnico | Paula, verificadora de Urban Eats. Revisa y aprueba la *Cocina de Prueba Urban Eats*. |
| `solicitante.demo@urbaneats.test` | Cliente | Marcela, quien solicita vincular la *Cocina de Prueba Urban Eats*. Al aprobarse, queda como gerente. |

Los usuarios originales de `Inserts.sql` (`1@email.com` … `15@email.com`) siguen existiendo con su contraseña original.

## Tres sesiones en el mismo navegador

Las cookies de sesión se guardan por *host*. Para entrar con tres roles a la vez, usa una dirección distinta para cada uno:

- Cliente: `http://localhost:8080`
- Cocina: `http://127.0.0.1:8080`
- Repartidor: `http://<nombre-del-equipo>:8080` (por ejemplo `http://DESKTOP-XXXX:8080`) o una ventana de incógnito
