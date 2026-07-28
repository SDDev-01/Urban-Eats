    <!-- CHATBOT: estilos y meta CSRF -->
    <link rel="stylesheet" href="{{ asset('css/chatbot.css') }}">
    <meta name="csrf-token" content="{{ csrf_token() }}">

    <!-- NAVBAR -->
    <nav class="navbar">
    <a href="{{ url('/catalogo') }}" class="navbar-logo">
        <img src="{{ asset('images/Logo.png') }}" alt="Urban Eats">
        <span>Urban Eats</span>
    </a>
    <div class="navbar-links">
        <a href="{{ url('/restaurantes') }}" class="navbar-link"><i class="fas fa-store"></i> Restaurantes</a>
    </div>
    <div class="navbar-right">
        <a href="{{ url('/perfil') }}" class="navbar-usuario" style="text-decoration:none;">
        <i class="fas fa-user-circle"></i> <span id="navbar-nombre">{{ session('Nombres', 'Usuario') }}</span>
        </a>
        <a href="{{ url('/carrito') }}" class="btn-carrito" aria-label="Ver carrito">
        <i class="fas fa-shopping-cart"></i>
        <span class="carrito-badge" id="carrito-badge">0</span>
        </a>
    </div>
    </nav>

    <!-- CHATBOT SCRIPTS (cargados una sola vez) -->
    @once
        <script src="{{ asset('js/chatbot.js') }}" defer></script>
    @endonce