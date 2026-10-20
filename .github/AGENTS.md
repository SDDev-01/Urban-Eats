# Urban Eats - Agents Instructions

## Project Overview

Urban Eats is a **student academic project** for SENA Colombia (Tecnólogo ADSO): a web platform for **night-time food delivery in Bogotá**. Identity (phase 6): *"comida real para quienes mueven la noche"* — verified kitchens, home-style/balanced food (with a "Balanceado" seal, not niche-only), 24/7 operation focused on 10 p.m.–6 a.m., and visible deliveries (map + local AI assistant).

**Important**: The SCRUM team now has **2 people** who present and defend the code every 3 months. Code must be **simple and explainable**; comments in Spanish.

## Architecture

```
Browser ──► Spring Boot :8080 (Thymeleaf, Spring Security, JPA)  ──► MySQL/MariaDB `urbaneats`
                 │  RestClient + header X-API-KEY                       ▲
                 ▼                                                      │ same database
            Python FastAPI :8000 (servicio-python/) ────────────────────┘
                 ├─► Ollama :11434 (local LLM, chatbot)
                 └─► OpenRouteService (delivery route; straight-line fallback)
```

- The browser never calls Python: Java proxies (`integracion/ServicioPythonCliente.java`).
- Python owns its tables (`solicitud_restaurante`, `documento_solicitud`, `historial_solicitud`, `ruta_envio`). On approval it inserts the kitchen into Java's `restaurante`.

## Java (Spring Boot 4.1, Java 21)

- Packages: `controller`, `service` (interfaces `I*Service` + implementations), `dao` (Spring Data repositories), `entity`, `security`, `config`, `integracion`.
- Config comes from `.env` (see `.env.example`), with defaults in `application.properties`.
- Roles from `rol_usuario`: `CLIENTE`, `GERENTE` (kitchen), `REPARTIDOR`, `TECNICO`. URL rules in `SecurityConfig`.
- **Order states** (`EstadoPedido` enum + converter; DB values without accents): Solicitado → En preparacion → Listo para envio → En camino → Entregado / Cancelado. **All** state changes go through `PedidoEstadoService` (role/ownership checks + `historial_pedido`).
- The DB trigger `crear_pedido_automaticamente` creates the `pedido` when an `envio` is inserted — never insert a second `pedido` from Java (`PedidoDesdeCarritoService` looks it up).
- Payment before order: the order is created only when the payment is `approved`. `PAGOS_MODO_DEMO=true` simulates approval for demos.
- Prices and totals are always recomputed on the server; never trust cart prices from the browser.
- Escape any DB/user text before `innerHTML` (`window.UE.escaparHTML` in `app.js`).
- Records exposed to templates must be read with accessor syntax (`${fila.codigo()}`); JSON goes through `@ResponseBody` endpoints.

## Python (`servicio-python/`)

- `main.py` (startup checks) → `app/api.py` → `routers/` (modules) → `services/` (logic) → `models/` (SQLAlchemy) and `schemas/` (Pydantic validation).
- Custom exceptions in `core/excepciones.py` become JSON errors in Spanish. Use `try/except` for DB, HTTP (Ollama/ORS) and file errors.
- Reports use explicit parameterized SQL (`text()` with `:params`); ordering uses a whitelist.
- Tests: `pytest -q`. Demo scripts in `scripts/demo/` (require `MODO_DEMO=true`).

## Database

Install order: `Schema.sql` → `Inserts.sql` → `fase6/01_migracion.sql` → `fase6/03_datos_bogota_nocturno.sql` (see `database/README.md`). New columns must be NULL or have a DEFAULT (Hibernate inserts only mapped columns).

## Conventions

- Branches: `feature/…`, `fix/…`, `style/…`, `docs/…` from `main`; PRs to `main` (see README.md).
- Spanish (Colombia) UI text and commit messages. Currency `$18.500 COP`.
- Brand: `#A42828`, `#FEC10B`, `#F7643B`, `#F3F1E2`; titles Cocogoose (trial font without digits: digits fall back to Creato Display), body Creato Display.
- Test users: `database/fase6/USUARIOS_DE_PRUEBA.md`.
