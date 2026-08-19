/* ============================
   URBAN EATS — CHATBOT WIDGET
   ============================ */

(function () {
  'use strict';

  const QUICK_OPTIONS = [
    { label: '🕐 Horarios',    texto: '¿Cuáles son los horarios de los restaurantes?' },
    { label: '📍 Ubicaciones', texto: '¿Dónde están ubicados los restaurantes?' },
    { label: '🍽️ Menús',      texto: '¿Qué platos hay disponibles?' },
    { label: '💰 Precios',     texto: '¿Cuáles son los precios del menú?' },
  ];

  const BIENVENIDA =
    '¡Hola! Soy **UrbanBot** 🤖, tu asistente de Urban Eats. Puedo ayudarte con horarios, ubicaciones, menús y precios. ¿En qué te puedo ayudar hoy?';

  /* ---- Obtener CSRF token de Laravel ---- */
  function getCsrfToken() {
    const meta = document.querySelector('meta[name="csrf-token"]');
    return meta ? meta.getAttribute('content') : '';
  }

  /* ---- Parsear markdown básico ---- */
  function parsearMarkdown(texto) {
    return texto
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/\n/g, '<br>');
  }

  /* ---- Crear estructura HTML del widget ---- */
  function crearWidget() {
    // Botón flotante
    const toggle = document.createElement('button');
    toggle.id = 'chatbot-toggle';
    toggle.setAttribute('aria-label', 'Abrir asistente virtual');
    toggle.innerHTML = '<span class="chatbot-toggle-icon">💬</span>';

    // Ventana
    const ventana = document.createElement('div');
    ventana.id = 'chatbot-window';
    ventana.setAttribute('role', 'dialog');
    ventana.setAttribute('aria-label', 'Asistente virtual Urban Eats');

    // Botones rápidos HTML
    const botonesHtml = QUICK_OPTIONS.map(
      (op) => `<button class="quick-btn" data-texto="${op.texto}">${op.label}</button>`
    ).join('');

    ventana.innerHTML = `
      <div class="chatbot-header">
        <div class="chatbot-avatar">🤖</div>
        <div class="chatbot-header-info">
          <strong>UrbanBot</strong>
          <span>Asistente virtual · Urban Eats</span>
        </div>
        <button class="chatbot-header-close" aria-label="Cerrar chat">
          <i class="fas fa-times"></i>
        </button>
      </div>
      <div class="chatbot-gdpr">
        <i class="fas fa-shield-alt"></i>
        Estás interactuando con una IA. Tus mensajes son procesados por Anthropic Claude.
      </div>
      <div class="chatbot-messages" id="chatbot-messages"></div>
      <div class="chatbot-quick-btns" id="chatbot-quick-btns">
        ${botonesHtml}
      </div>
      <div class="chatbot-input-area">
        <input
          type="text"
          id="chatbot-input"
          placeholder="Escribe tu pregunta..."
          maxlength="500"
          autocomplete="off"
          aria-label="Mensaje al asistente"
        />
        <button id="chatbot-send" aria-label="Enviar mensaje">
          <i class="fas fa-paper-plane"></i>
        </button>
      </div>
    `;

    document.body.appendChild(toggle);
    document.body.appendChild(ventana);

    return { toggle, ventana };
  }

  /* ---- Agregar burbuja al chat ---- */
  function agregarBurbuja(texto, tipo) {
    const contenedor = document.getElementById('chatbot-messages');
    const burbuja = document.createElement('div');
    burbuja.className = `chat-burbuja ${tipo}`;
    burbuja.innerHTML = parsearMarkdown(texto);
    contenedor.appendChild(burbuja);
    contenedor.scrollTop = contenedor.scrollHeight;
    return burbuja;
  }

  /* ---- Indicador de escritura ---- */
  function mostrarEscribiendo() {
    const contenedor = document.getElementById('chatbot-messages');
    const typing = document.createElement('div');
    typing.className = 'chat-typing';
    typing.id = 'chat-typing';
    typing.innerHTML = '<span></span><span></span><span></span>';
    contenedor.appendChild(typing);
    contenedor.scrollTop = contenedor.scrollHeight;
  }

  function ocultarEscribiendo() {
    const typing = document.getElementById('chat-typing');
    if (typing) { typing.remove(); }
  }

  /* ---- Enviar mensaje al backend ---- */
  async function enviarMensaje(texto) {
    if (!texto.trim()) { return; }

    agregarBurbuja(texto, 'usuario');

    const input = document.getElementById('chatbot-input');
    const sendBtn = document.getElementById('chatbot-send');
    if (input) { input.value = ''; }
    if (sendBtn) { sendBtn.disabled = true; }

    mostrarEscribiendo();

    try {
      const res = await fetch('/chatbot', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-CSRF-TOKEN': getCsrfToken(),
          'Accept': 'application/json',
        },
        body: JSON.stringify({ mensaje: texto }),
      });

      const datos = await res.json();
      ocultarEscribiendo();
      agregarBurbuja(datos.respuesta || 'Algo salió mal, intenta de nuevo.', 'bot');
    } catch {
      ocultarEscribiendo();
      agregarBurbuja('No pude conectarme. Por favor, verifica tu conexión.', 'bot');
    } finally {
      if (sendBtn) { sendBtn.disabled = false; }
      if (input) { input.focus(); }
    }
  }

  /* ---- Inicializar widget ---- */
  function init() {
    const { toggle, ventana } = crearWidget();
    let abierto = false;

    // Activar pulso de atención después de 2 s
    setTimeout(() => toggle.classList.add('pulse'), 2000);

    function abrir() {
      abierto = true;
      ventana.classList.add('visible');
      toggle.classList.add('open');
      toggle.classList.remove('pulse');
      toggle.innerHTML = '<span class="chatbot-toggle-icon"><i class="fas fa-times"></i></span>';

      // Mensaje de bienvenida solo la primera vez
      const mensajes = document.getElementById('chatbot-messages');
      if (mensajes && mensajes.children.length === 0) {
        agregarBurbuja(BIENVENIDA, 'bot');
      }

      const input = document.getElementById('chatbot-input');
      if (input) { setTimeout(() => input.focus(), 300); }
    }

    function cerrar() {
      abierto = false;
      ventana.classList.remove('visible');
      toggle.classList.remove('open');
      toggle.innerHTML = '<span class="chatbot-toggle-icon">💬</span>';
    }

    // Toggle al hacer clic en el botón flotante
    toggle.addEventListener('click', () => (abierto ? cerrar() : abrir()));

    // Botón cerrar del header
    ventana.querySelector('.chatbot-header-close').addEventListener('click', cerrar);

    // Botones rápidos
    ventana.querySelectorAll('.quick-btn').forEach((btn) => {
      btn.addEventListener('click', () => enviarMensaje(btn.dataset.texto));
    });

    // Enviar con botón
    const sendBtn = document.getElementById('chatbot-send');
    sendBtn.addEventListener('click', () => {
      const input = document.getElementById('chatbot-input');
      enviarMensaje(input.value);
    });

    // Enviar con Enter
    document.getElementById('chatbot-input').addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        const input = document.getElementById('chatbot-input');
        enviarMensaje(input.value);
      }
    });
  }

  // Esperar a que el DOM esté listo
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
