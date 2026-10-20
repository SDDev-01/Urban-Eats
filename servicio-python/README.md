# Servicio Python de Urban Eats (FastAPI)

Microservicio que acompaña a la aplicación Spring Boot. Comparte la misma base de datos MySQL/MariaDB y se encarga de:

| Módulo (`app/routers`) | Qué hace |
|---|---|
| `solicitudes.py` | **CRUD** de las solicitudes de vinculación de cocinas, y aprobación por el técnico |
| `documentos.py` | Documentos legales y sanitarios (PDF de máximo 5 MB): crear, listar, descargar, revisar y eliminar |
| `reportes.py` | Reportes con **filtros, ordenamiento y paginación**, exportables a CSV, Excel y PDF |
| `chat.py` | Asistente virtual con **IA local (Ollama)** conectado a los pedidos reales |
| `rutas.py` | Ruta del domicilio con OpenRouteService (o línea recta si no hay API key) |
| `salud.py` | Estado del servicio, la base de datos y la IA |

## Estructura

```
servicio-python/
├── main.py              # arranque: verifica .env, base de datos e IA y levanta el servidor
├── app/
│   ├── api.py           # crea FastAPI y registra los routers
│   ├── core/            # configuracion (.env), base de datos, seguridad (X-API-KEY), excepciones
│   ├── models/          # tablas (SQLAlchemy)
│   ├── schemas/         # validaciones de entrada y salida (Pydantic)
│   ├── services/        # logica de negocio (CRUD, reportes, chat, rutas, exportacion)
│   ├── routers/         # los "modulos" de la API
│   └── utils/           # validadores colombianos: NIT con DV (DIAN), telefono, localidades
├── scripts/demo/        # modo demostracion para la sustentacion
└── tests/               # pruebas automaticas (pytest)
```

## Cómo ejecutarlo

Requisitos: Python 3.13 o superior (probado con 3.15) y la base `urbaneats` en XAMPP.

```powershell
cd servicio-python
python -m venv .venv
.venv\Scripts\python.exe -m pip install -r requirements.txt
copy .env.example .env        # y escribir API_KEY = la misma PYTHON_API_KEY del .env de Spring Boot
.venv\Scripts\python.exe main.py
```

Al arrancar muestra el estado de cada parte, por ejemplo:

```
[OK] API_KEY configurada
[OK] Base de datos conectada (MariaDB/MySQL 10.4.32-MariaDB)
[OK] Tablas del servicio verificadas (solicitudes, documentos, rutas)
[!] IA local no disponible (falta: ollama pull qwen3:4b): el chat respondera en modo basico.
[OK] Documentacion interactiva: http://127.0.0.1:8000/docs
```

En **http://127.0.0.1:8000/docs** está Swagger: botón **Authorize**, escribir la clave `X-API-KEY` y probar cada operación.

### IA local (opcional, gratis)

1. Instalar Ollama desde https://ollama.com/download
2. `ollama pull qwen3:4b` (unos 2,5 GB; si el computador es lento, `llama3.2:3b` y cambiar `OLLAMA_MODEL` en el `.env`)
3. Reiniciar `main.py`. Sin Ollama, el chat responde en **modo básico** con los mismos datos reales.

### Ruta por calles (opcional, gratis)

Crear una cuenta en https://openrouteservice.org, copiar la API key en `ORS_API_KEY` del `.env` y reiniciar. Sin key, la ruta es una línea recta.

## Pruebas

```powershell
.venv\Scripts\python.exe -m pytest -q
```

## Modo demostración (sustentación)

Con `MODO_DEMO=true` en el `.env`:

```powershell
.venv\Scripts\python.exe -m scripts.demo.preparar_demo            # reinicia los datos de prueba (repetible)
.venv\Scripts\python.exe -m scripts.demo.demo_cocina_paso_a_paso  # CRUD + validaciones + reporte, paso a paso
.venv\Scripts\python.exe -m scripts.demo.demo_pedido_3_roles      # pedido cliente -> cocina -> repartidor
```

Agregar `--sin-pausas` a los dos últimos los convierte en pruebas automáticas.
Los documentos de prueba llevan la marca **"DOCUMENTO DE PRUEBA – SIN VALIDEZ LEGAL"** y datos ficticios.
