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

## Una sesión por rol en el mismo navegador

El navegador guarda **una sesión por dirección**: todas las pestañas y ventanas que usan
`localhost:8080` son el mismo usuario. Si inicias sesión con otro usuario en esa misma
dirección, las otras pestañas pasan a ser ese usuario y sus paneles avisan
"Tu sesión cambió". Usa una dirección distinta para cada rol (todas abren la misma aplicación):

| Rol | Dirección | Usuario |
|---|---|---|
| Cliente | `http://localhost:8080` | `cliente.demo@urbaneats.test` |
| Cocina | `http://127.0.0.1:8080` | `cocina.demo@urbaneats.test` → «Como cocina» |
| Repartidor | `http://127.0.0.2:8080` | `repartidor.demo@urbaneats.test` → «Como repartidor» |
| Técnico | `http://127.0.0.3:8080` | `tecnico.demo@urbaneats.test` |

También sirve usar navegadores distintos (Chrome, Edge, Firefox). Una ventana de incógnito
cuenta como una sola dirección más: no sirve para dos roles a la vez.

Al entrar, la cocina y el repartidor eligen con qué rol trabajar. La barra muestra solo las
opciones de ese rol: la cocina y el repartidor no ven Restaurantes ni el carrito. Para pedir
comida con esas cuentas: **Cambiar rol → Como usuario**.

## Pagos de prueba con MercadoPago

1. En https://www.mercadopago.com.co/developers/panel/app → tu aplicación → **Credenciales de prueba**,
   copia la *Public Key* y el *Access Token* (empiezan con `TEST-`).
2. En el `.env` de `Urban-Eats`: `MERCADOPAGO_PUBLIC_KEY=TEST-...`, `MERCADOPAGO_ACCESS_TOKEN=TEST-...`
   y `PAGOS_MODO_DEMO=false`. Reinicia Spring Boot.
3. En el pago usa una tarjeta de prueba de Colombia (documentación oficial de MercadoPago):

| Tarjeta | Número | CVV | Vence |
|---|---|---|---|
| Mastercard crédito | `5254 1336 7440 3564` | 123 | 11/30 |
| Visa crédito | `4013 5406 8274 6260` | 123 | 11/30 |
| Visa débito | `4912 6840 6309 1904` | 123 | 11/30 |

El **nombre del titular** decide el resultado: `APRO` aprobado (el pedido llega a la cocina),
`OTHE` rechazado, `FUND` saldo insuficiente, `SECU` CVV inválido, `CONT` pendiente.
Documento: CC `123456789`. El correo del pagador debe ser distinto al de la cuenta vendedora.

Sin internet el día de la sustentación: `PAGOS_MODO_DEMO=true` deja el botón de pago simulado.

## Ruta del domicilio

La ruta por calles la calcula el servicio Python: con `ORS_API_KEY` usa OpenRouteService y, sin
clave, el servidor público de OSRM (OpenStreetMap). Sin internet dibuja una línea recta. El
repartidor ve las indicaciones paso a paso y los botones **Google Maps** y **Waze**, que en el
celular abren la navegación por voz hasta la entrega.
