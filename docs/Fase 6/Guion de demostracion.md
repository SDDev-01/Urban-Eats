# Guion de demostración – Sustentación Fase 6

**Urban Eats: comida real para quienes mueven la noche.** Duración sugerida de la demo: 10 minutos.

## Antes de entrar (15 minutos antes)

1. `powershell -ExecutionPolicy Bypass -File scripts\iniciar_demo.ps1` (MariaDB, Ollama, Python y Spring Boot).
2. `servicio-python\.venv\Scripts\python.exe -m scripts.demo.preparar_demo` (datos limpios: cocina de prueba en *Enviada* y 200 pedidos históricos).
3. Abrir tres sesiones en el navegador (las cookies son por dirección):
   - Cliente: `http://localhost:8080` → `cliente.demo@urbaneats.test`
   - Cocina: `http://127.0.0.1:8080` → `cocina.demo@urbaneats.test`
   - Técnico: ventana de incógnito → `tecnico.demo@urbaneats.test`
   - Contraseña de todos: ver `database/fase6/USUARIOS_DE_PRUEBA.md`
4. Abrir una consola en `servicio-python` y otra con Swagger: `http://127.0.0.1:8000/docs`.

## Orden de la demostración

| # | Qué se muestra | Quién | Ítem de la lista |
|---|---|---|---|
| 1 | Consola de `python main.py`: verificaciones en verde | Integrante B | 5 – ejecuta sin errores |
| 2 | Árbol de carpetas `main.py / models / schemas / services / routers` | B | 8 – estructura |
| 3 | `demo_cocina_paso_a_paso.py`: NIT con DV malo → 422; crear, listar con filtros, corregir, documentos, PDF falso rechazado, checklist, aprobar, borrar | B | 6 – CRUD · 7 – try/except |
| 4 | Panel del técnico: revisar la *Cocina Sin Papeles* (no se puede aprobar) y aprobar la *Cocina de Prueba* | B | 6 · realismo |
| 5 | Cliente pide en *Las Brasas 24h*, paga en modo demo y ve el mapa | Integrante A | flujo de 3 roles |
| 6 | Cocina acepta y marca listo; el cliente ve los pasos cambiar solos | A | flujo de 3 roles |
| 7 | Repartidor toma el pedido y pulsa **Simular recorrido**: la moto avanza en el mapa del cliente; marca entregado | A | flujo de 3 roles |
| 8 | Chat: "¿Dónde está mi pedido?" y "Recomiéndame algo balanceado por menos de $20.000" | B | IA local |
| 9 | `/reportes`: franja nocturna, ordenar por valor, descargar Excel y PDF | A | 10 – reportes |
| 10 | Diapositivas: presupuesto por sprints y negociación tecnológica | Ambos | 1 a 4 |

Alternativa al paso 5–7 sin navegador: `python -m scripts.demo.demo_pedido_3_roles` (pausa entre roles).

## Plan B

| Si falla… | Hacer |
|---|---|
| Ollama (lento o apagado) | El chat responde en **modo básico** con los mismos datos; explicar el `try/except` de `chat_service.py` |
| Internet (mapa u OpenRouteService) | La ruta se dibuja en línea recta (Haversine); los reportes y el CRUD no necesitan internet |
| MercadoPago | El modo demo de pagos está activo (`PAGOS_MODO_DEMO=true`) |
| Datos desordenados tras ensayar | Volver a correr `preparar_demo` (solo borra datos de prueba) |

## Preguntas probables del jurado

- **¿Por qué Python y Java juntos?** Microservicio: Java ya manejaba login, pedidos y pagos; Python aporta donde es fuerte (validación de datos, reportes, IA). Hablan por REST con una clave `X-API-KEY`; el navegador nunca habla con Python.
- **¿La IA puede ver pedidos de otros usuarios?** No: Python solo le pasa los datos de quien pregunta, y la IA nunca escribe SQL.
- **¿Qué pasa si dos repartidores toman el mismo pedido?** `UPDATE ... WHERE CodigoRepartidor IS NULL`: el segundo cambia 0 filas y recibe un aviso.
- **¿Cómo saben que la cocina es real?** Checklist automático: RUT, Cámara de Comercio de 30 días o menos, concepto sanitario favorable vigente y manipuladores con curso de 10 horas o más y certificado médico (Resolución 2674 de 2013). Los documentos de la demo son ficticios y dicen "sin validez legal".
