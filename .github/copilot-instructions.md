# Urban Eats - Copilot Instructions

## Project Overview

Urban Eats is a **student academic project** for SENA Colombia - a web-based food delivery platform built with vanilla HTML, CSS, and JavaScript. The application allows customers to browse restaurants, place orders, and track deliveries in Colombia (localized with Colombian pesos).

**Important**: This is a learning project for a 4-person student team. Code must be **simple and explainable** - the team presents and defends their code every 3 months in "sustentaciones". Prioritize readability and understanding over clever solutions.

### Current Phase
- Using localStorage for data persistence (no backend yet)
- Database exists as SQL files in `/databases/` but is **not connected** to the application
- Database integration planned for a future learning phase

## ⚠️ CRITICAL: Documentation-Driven Development

**ALWAYS consult `/docs/` before making changes.** The documentation represents what has been planned and approved for academic evaluation.

### The 5 Project Modules

The project is organized around **5 core management modules** (see use case diagrams):

1. **Gestión de Cliente** - Customer management
   - Create user, update information, shopping cart
   - Admin: validate/eliminate clients
   - Files: `cliente.html`, `cliente.js`, `cliente.css`

2. **Gestión de Restaurante** - Restaurant management  
   - Browse restaurants, view menus, reviews
   - Admin: create restaurants, manage menus
   - Files: `restaurante.html`, `restaurante.js`, `restaurante.css`, `catalogo.html`, `catalogo.js`, `catalogo.css`

3. **Gestión de Repartidor** - Delivery driver management
   - Driver profile, payment preferences, delivery assignment
   - Admin: assign drivers, Client: rate drivers
   - Files: `repartidor.html`, `repartidor.js`, `repartidor.css`

4. **Gestión de Pago** - Payment management
   - Generate/view receipts, payment methods, refunds
   - Files: `pago.html`, `pago.js`, `pago.css`

5. **Gestión de Pedidos** - Order management (CENTRAL MODULE)
   - Create/modify/cancel orders, track delivery
   - Connects Cliente → Restaurante → Repartidor → Pago
   - Files: `carrito.html`, `carrito.js`, `carrito.css`, `rastreo.html`, `rastreo.js`, `rastreo.css`

### Documentation Structure

When working on features, **reference these diagrams**:

- **Use Case Diagrams** (`/docs/Diagrama de casos de usos/`): Defines what each actor (Cliente, Repartidor, Administrador) can do in each module
- **Class Diagram** (`/docs/Diagrama de Clases/`): Shows the object model (Cliente, Restaurante, Pedido, Pago, etc.)
- **Component Diagram** (`/docs/Diagrama de Componentes/`): Shows how the 5 modules interconnect
- **Activity Diagrams** (`/docs/Diagrama de actividades/`): Workflow for each module
- **Sequence Diagrams** (`/docs/Diagrama de secuencia/`): Interaction flows
- **Wireframes** (`/docs/Wireframes/`): UI design reference
- **Database Model** (`/docs/bases de datos/`): Entity-relationship and relational models
- **User Stories** (`/docs/Historias de usuario/`): Feature requirements
- **Data Dictionary** (`/docs/diccionario de datos/`): Field definitions

**Before implementing a feature**: Check if it exists in use cases, wireframes, or user stories. Follow the documented design.

## Architecture

### Page Structure
The application follows a **multi-page architecture** where each HTML file represents a distinct user role or workflow:

- **index.html**: Landing page for unauthenticated users
- **login.html / registro.html**: Authentication pages
- **cliente.html**: Customer profile management
- **restaurante.html**: Restaurant profile management
- **catalogo.html**: Browse restaurants and menu items
- **carrito.html**: Shopping cart and order management
- **pago.html**: Payment processing page
- **repartidor.html**: Delivery driver interface
- **rastreo.html**: Order tracking page
- **mapa.html**: Map view for delivery tracking

### JavaScript Modules
Each page has a corresponding JS file in `/js/` that follows a **functional module pattern**:

- **app.js**: Global utilities (navbar state, user display, cart badge)
- Page-specific modules (cliente.js, restaurante.js, carrito.js, etc.) handle their own state

### State Management
All application state is stored in **localStorage** with prefixed keys:

- `ue_cliente`: Customer profile data
- `ue_restaurante`: Restaurant profile data
- `ue_usuario`: User authentication data
- `ue_carrito`: Shopping cart items
- `ue_pedido_actual`: Current order being processed
- `ue_sesion`: Session flag

**Pattern**: Each module defines a default object, loads from localStorage, and provides a `persistir()` function to save changes.

### Styling Architecture
CSS follows a **CSS Custom Properties (variables) + component pattern**:

- **styles.css**: Global variables (colors, spacing, typography) and shared components (navbar, buttons, forms)
- Page-specific CSS files extend global styles for page-specific layouts
- **Reference**: `/docs/Wireframes/` for UI design

**Key CSS Variables** (defined in `:root`):
- `--verde`: Primary brand color (#2ecc71) - healthy food theme
- `--navbar-height`: Fixed navbar height (64px)
- `--fuente`: System font stack
- `--sombra`, `--radio`: Design tokens for shadows and border radius

## Database Schema

The MySQL schema is defined in `/databases/`:

- **Schema.sql**: Complete database structure (UrbanEats database)
- **Inserts.sql**: Sample data for testing
- **Functions.sql**: Stored procedures and functions
- **triggers.sql**: Database triggers
- **Joins.sql**: Common query examples

**Key Tables**:
- `Usuario` → `Rol_Usuario` → `Rol` (users can have multiple roles: Cliente, Repartidor, Administrador)
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

### LocalStorage Keys
All localStorage keys use the `ue_` prefix (UrbanEats) to avoid conflicts:
```javascript
localStorage.getItem('ue_cliente')
localStorage.setItem('ue_carrito', JSON.stringify(carrito))
```

### Data Persistence Pattern
Each module follows this pattern:
```javascript
const DEFAULT = { /* default values */ };
let data = JSON.parse(localStorage.getItem('ue_key') || 'null') || { ...DEFAULT };

function persistir() {
  localStorage.setItem('ue_key', JSON.stringify(data));
}
```

### Form Validation
- Error messages use IDs like `err-nombres`, `err-email` (prefixed with `err-`)
- Success/verification messages use IDs like `msg-verificacion`
- Validation functions clear previous errors before showing new ones

### Navbar Active State
The global `app.js` automatically adds the `.activo` class to navbar links matching the current page:
```javascript
// Runs on page load to highlight current page in navbar
marcarActivo()
```

### Icon Library
FontAwesome 6.5.0 is used throughout (CDN link in all HTML files):
```html
<i class="fas fa-icon-name"></i>
```

## File Organization

```
/
├── *.html                 # Main application pages
├── css/                   # Stylesheets (global + page-specific)
├── js/                    # JavaScript modules (global + page-specific)
├── databases/             # SQL schema and data
└── docs/                  # Documentation and diagrams (UML, wireframes, etc.)
```

## Code Standards for This Project

### Simplicity First
- **Keep code simple**: The team must explain all code in "sustentaciones" (quarterly presentations)
- Write code that is easy to read and understand, not clever or overly optimized
- Modern JavaScript features (ES6+) are fine, but keep logic straightforward
- Avoid complex patterns, advanced techniques, or anything hard to explain

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

### Technology Stack
- **HTML/CSS/JS only**: No frameworks, no build tools, no npm
- This matches what the team is currently learning at SENA
- Future phases may introduce new technologies as the curriculum progresses
- Direct file editing - changes visible on browser refresh

### Database
- SQL files in `/databases/` are for reference and future integration only
- **Not currently connected** to the application
- All data currently managed through localStorage
- Database connection is planned for a future learning phase

## Notes

- **No build process**: Direct HTML/CSS/JS - changes are immediately visible on refresh
- **No package.json**: No npm dependencies or build tools
- **Testing**: Manual testing in browser (no automated test suite)
- **Evolution**: Project requirements and technologies may change every 3 months as new topics are learned
