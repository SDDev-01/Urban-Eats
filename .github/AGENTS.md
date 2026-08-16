# Urban Eats - Agents Instructions

## Project Overview

Urban Eats is a **student academic project** for SENA Colombia - a web-based food delivery and logistics platform built with **Laravel 13**. It connects restaurants, customers, and delivery drivers in **Bogotá** (localized with Colombian pesos). Urban Eats is a connection/logistics platform: it does **not** prepare food or directly employ couriers.

**Important**: This is a learning project for a 4-person student team. Code must be **simple and explainable** - the team presents and defends their code every 3 months in "sustentaciones". Prioritize readability and understanding over clever solutions.

### Current Phase
- **Migrated to Laravel 13** — the project no longer uses plain HTML/CSS/JS with localStorage
- **MySQL database is connected and in use** via Eloquent ORM
- Eloquent models and relationships already exist — reuse them instead of writing raw SQL
- Do **not** create migrations to recreate existing tables; the schema already exists

## Key Clarifications (Wiki)
- **Not a real business**: academic-only scope.
- **Not a social network**: only reviews after an order is **Entregado**.
- **Traditional restaurant focus** (no niche-only positioning).
- **24/7 operation** with emphasis on night monitoring (12:00 a.m. – 5:00 a.m.).
- **Target usage**: Bogotá, mobile-friendly (Android 9+ / iOS 13+, 4G/Wi‑Fi).
- **Roles**: Cliente, Gerente/Restaurante, Repartidor y Técnico (antes Administrador).
- **Multi-rol**: un mismo usuario puede tener más de un rol.

## ⚠️ CRITICAL: Documentation-Driven Development

**ALWAYS consult `/docs/` before making changes.** The documentation represents what has been planned and approved for academic evaluation.

### The 5 Project Modules

The project is organized around **5 core management modules** (see use case diagrams):

1. **Gestión de Cliente** - Customer management
   - Create user, update information, shopping cart
   - Técnico (antes Administrador): validate/eliminate clients

2. **Gestión de Restaurante** - Restaurant management
   - Browse restaurants, view menus, reviews
   - Técnico (antes Administrador): create restaurants, manage menus

3. **Gestión de Repartidor** - Delivery driver management
   - Driver profile, payment preferences, delivery assignment
   - Técnico (antes Administrador): assign drivers, Cliente: rate drivers

4. **Gestión de Pago** - Payment management
   - Generate/view receipts, payment methods, refunds

5. **Gestión de Pedidos** - Order management (CENTRAL MODULE)
   - Create/modify/cancel orders, track delivery
   - Connects Cliente → Restaurante → Repartidor → Pago

### Documentation Structure

When working on features, **reference these diagrams**:

- **Use Case Diagrams** (`/docs/Diagrama de casos de usos/`): Defines what each actor (Cliente, Repartidor, Técnico (antes Administrador)) can do in each module
- **Class Diagram** (`/docs/Diagrama de Clases/`): Shows the object model (Cliente, Restaurante, Pedido, Pago, etc.)
- **Component Diagram** (`/docs/Diagrama de Componentes/`): Shows how the 5 modules interconnect
- **Deployment Diagram** (`/docs/Diagrama de despliegue/`): Shows how components are deployed
- **Package Diagram** (`/docs/Diagrama de paquetes/`): Organizes the system into packages
- **Process Diagram** (`/docs/Diagrama de procesos/`): High-level process flows
- **Activity Diagrams** (`/docs/Diagrama de actividades/`): Workflow for each module
- **Sequence Diagrams** (`/docs/Diagrama de secuencia/`): Interaction flows
- **Wireframes** (`/docs/Wireframes/`): UI design reference
- **Project Documentation** (`/docs/Documentación/`): Arquitectura, visión y análisis del proyecto
- **Database Model** (`/docs/Bases de datos/`): Entity-relationship and relational models
- **User Stories** (`/docs/Historias de usuario/`): Feature requirements
- **Data Dictionary** (`/docs/diccionario de datos/`): Field definitions

**Before implementing a feature**: Check if it exists in use cases, wireframes, or user stories. Follow the documented design.

## Business Rules (Resumen)
- **Pago antes del pedido**: si la pasarela rechaza, el pedido no se envía al restaurante.
- **Estados del pedido**: Solicitado → En Preparación → Listo para Envío → En Camino → Entregado / Cancelado.
- **Autonomía del restaurante**: puede **Aceptar/Rechazar** pedidos; sin stock no se habilita agregar al carrito.
- **Monto mínimo**: validar antes de pasar a pago.
- **Asignación de repartidores**: pueden **Aceptar/Rechazar**; si rechazan, se reasigna automáticamente.
- **GPS**: rastreo en tiempo real con margen de error máximo de **20 metros** (simulado por ahora).
- **Prioridad**: repartidores propios del restaurante tienen preferencia sobre externos.

## Architecture

### Technology Stack
- **Framework**: Laravel 13
- **Backend language**: PHP
- **ORM**: Eloquent
- **Template engine**: Blade
- **Database**: MySQL (connected and active)
- **Frontend**: HTML, CSS, JavaScript (no frontend frameworks)
- **Icons**: FontAwesome 6.5.0 (via CDN)

### Routing
- Use `Route::view(...)` for fully static pages with no dynamic data.
- Use `Route::get(...)` or `Route::post(...)` with a controller for any page that needs logic or database data.

```php
// Página estática
Route::view('/inicio', 'inicio');

// Página con lógica
Route::get('/perfil', [PerfilController::class, 'index']);
Route::post('/perfil/actualizar', [PerfilController::class, 'actualizar']);
```

### Controllers
The following controllers already exist and must be reused:
- **LoginController**: handles user login
- **RegistroController**: handles new user registration
- **LogoutController**: handles session termination

When adding new features, check whether an appropriate controller already exists before creating a new one.

### Authentication & Sessions
The project uses a custom authentication system built with Laravel sessions. Passwords are stored and verified using Laravel's `Hash` facade:

```php
// Al registrar
Hash::make($request->password)

// Al verificar
Hash::check($request->password, $usuario->password)
```

On login, the session stores at minimum:
- `CodigoUsuario`
- `Nombres`

Access session data in controllers:
```php
$codigo = session('CodigoUsuario');
$nombre = session('Nombres');
```

Access session data in Blade views:
```blade
{{ session('Nombres') }}
```

### Blade Views
All views use Blade syntax. Key conventions:

```blade
{{-- Imprimir una variable --}}
{{ $variable }}

{{-- Formularios POST siempre llevan @csrf --}}
<form method="POST" action="/ruta">
    @csrf
    ...
</form>

{{-- Reutilizar componentes compartidos --}}
    <!-- CHATBOT: estilos y meta CSRF -->
    <link rel="stylesheet" href="css/chatbot.css">

    <!-- NAVBAR -->
    <nav class="navbar">
    <a href="catalogo.html" class="navbar-logo">
        <img src="images/Logo.png" alt="Urban Eats">
        <span>Urban Eats</span>
    </a>
    <div class="navbar-links">
        <a href="restaurantes.html" class="navbar-link"><i class="fas fa-store"></i> Restaurantes</a>
    </div>
    <div class="navbar-right">
        <a href="perfil.html" class="navbar-usuario" style="text-decoration:none;">
        <i class="fas fa-user-circle"></i> <span id="navbar-nombre">Usuario</span>
        </a>
        <a href="carrito.html" class="btn-carrito" aria-label="Ver carrito">
        <i class="fas fa-shopping-cart"></i>
        <span class="carrito-badge" id="carrito-badge">0</span>
        </a>
    </div>
    </nav>

    <!-- CHATBOT SCRIPTS (cargados una sola vez) -->
        <script src="js/chatbot.js" defer></script>
```

### Navbar reutilizable
Legacy: el navbar vivía en un partial Blade (`resources/views/partials/navbar.blade.php`). En los templates Spring Boot (`src/main/resources/templates`) hoy está duplicado; idealmente extráelo a un fragmento Thymeleaf para evitar copy/paste.
```blade
    <!-- CHATBOT: estilos y meta CSRF -->
    <link rel="stylesheet" href="css/chatbot.css">

    <!-- NAVBAR -->
    <nav class="navbar">
    <a href="catalogo.html" class="navbar-logo">
        <img src="images/Logo.png" alt="Urban Eats">
        <span>Urban Eats</span>
    </a>
    <div class="navbar-links">
        <a href="restaurantes.html" class="navbar-link"><i class="fas fa-store"></i> Restaurantes</a>
    </div>
    <div class="navbar-right">
        <a href="perfil.html" class="navbar-usuario" style="text-decoration:none;">
        <i class="fas fa-user-circle"></i> <span id="navbar-nombre">Usuario</span>
        </a>
        <a href="carrito.html" class="btn-carrito" aria-label="Ver carrito">
        <i class="fas fa-shopping-cart"></i>
        <span class="carrito-badge" id="carrito-badge">0</span>
        </a>
    </div>
    </nav>

    <!-- CHATBOT SCRIPTS (cargados una sola vez) -->
        <script src="js/chatbot.js" defer></script>
```

### Eloquent Models & Relationships
Eloquent models and their relationships (`hasOne`, `hasMany`, `belongsTo`, `belongsToMany`) are already defined. Always use them instead of writing raw SQL queries.

```php
// ✅ Correcto — usar Eloquent
$pedidos = Cliente::find($id)->pedidos;

// ❌ Evitar — SQL manual innecesario
$pedidos = DB::select('SELECT * FROM pedidos WHERE cliente_id = ?', [$id]);
```

Do **not** create new migrations to recreate tables that already exist. The database schema is already in place.

### Profile Page Pattern
The profile page retrieves the authenticated user via `session('CodigoUsuario')`, queries the database through Eloquent, and passes the model to Blade for rendering:

```php
// En el controlador
public function index()
{
    $usuario = Usuario::findOrFail(session('CodigoUsuario'));
    return view('perfil', compact('usuario'));
}
```

```blade
{{-- En la vista --}}
<p>{{ $usuario->Nombres }}</p>
```

## Database Schema

The MySQL schema is defined in `/databases/` and is **already connected to the application**:

- **Schema.sql**: Complete database structure (UrbanEats database)
- **Inserts.sql**: Sample data for testing
- **Functions.sql**: Stored procedures and functions
- **triggers.sql**: Database triggers
- **Joins.sql**: Common query examples

**Key Tables**:
- `Usuario` → `Rol_Usuario` → `Rol` (users can have multiple roles: Cliente, Repartidor, Técnico (antes Administrador))
- `Cliente` → `Pedido` → `DetallePedido` (order structure)
- `Restaurante` → `Producto` (restaurant menu items)
- `Departamentos` → `Ciudades` (Colombian geographic data)

## Conventions

### Branch Naming
Follow the Git workflow defined in README.md:
- `feature/descripcion` - New features
- `fix/descripcion` - Bug fixes
- `style/descripcion` - CSS/visual changes
- `docs/descripcion` - Documentation updates

Always work from `main` branch and create pull requests for merging.

### Form Validation
- Error messages use IDs like `err-nombres`, `err-email` (prefixed with `err-`)
- Success/verification messages use IDs like `msg-verificacion`
- Validation functions clear previous errors before showing new ones

### Icon Library
FontAwesome 6.5.0 is used throughout (CDN link in all Blade views):
```html
<i class="fas fa-icon-name"></i>
```

## File Organization

```
/
├── app/
│   ├── Http/
│   │   └── Controllers/     # LoginController, RegistroController, LogoutController, etc.
│   └── Models/              # Modelos Eloquent (Usuario, Cliente, Pedido, etc.)
├── resources/
│   └── views/
│       ├── partials/        # Componentes reutilizables (navbar, footer, etc.)
│       └── *.blade.php      # Vistas de cada módulo
├── routes/
│   └── web.php              # Definición de rutas
├── databases/               # SQL schema and reference data
├── public/
│   ├── css/                 # Stylesheets (global + page-specific)
│   └── js/                  # JavaScript for frontend interactions
└── docs/                    # Documentation and diagrams (UML, wireframes, etc.)
```

## Code Standards for This Project

### Simplicity First
- **Keep code simple**: The team must explain all code in "sustentaciones" (quarterly presentations)
- Write code that is easy to read and understand, not clever or overly optimized
- Avoid complex patterns, advanced techniques, or anything hard to explain
- Prefer Eloquent relationships over raw SQL; prefer existing controllers over new ones

### Reuse Before Creating
- Before writing a new controller, model, or Blade partial — check if one already exists
- Reuse Blade partials (`@include`) to avoid duplicating HTML
- Reuse Eloquent relationships instead of rewriting queries

### Language
- **All code comments in Spanish**: Explain what the code does, not just what it is
- UI text in Spanish (Colombian Spanish)
- Git commit messages in Spanish (see README.md conventions)

### Colombian Localization
- **Currency format**: `$50.000 COP` (Colombian pesos with dot as thousands separator, no comma)
  - Examples from wireframes: `$18.500 COP`, `$22.000 COP`, `$16.000 COP`
- Geographic data based on Colombian departments and cities (see `Departamentos` and `Ciudades` tables)
- All content assumes Colombian context
- Target audience: "trabajadores, estudiantes y personas activas" (workers, students, active people)

### Database
- The database is **connected and active** — do not use localStorage for data that belongs in the DB
- Use Eloquent ORM; avoid raw `DB::select(...)` queries unless strictly necessary
- Do **not** create migrations to recreate tables that already exist
- SQL files in `/databases/` remain as reference and backup

## Notes

- **No localStorage for core data**: now that the DB is connected, persistent data lives in MySQL, not the browser
- **Testing**: Manual testing in browser (no automated test suite)
- **Evolution**: Project requirements and technologies may change every 3 months as new topics are learned at SENA
