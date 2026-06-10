/* ============================
   SLIDER DEL BANNER - Compartido entre index.html y catalogo.html
   ============================ */
(function () {
  const slides = document.getElementById('banner-slides');
  const slideItems = document.querySelectorAll('.banner-slide');
  const navContainer = document.getElementById('banner-nav');
  const prevBtn = document.getElementById('banner-prev');
  const nextBtn = document.getElementById('banner-next');

  if (!slides || !navContainer || !prevBtn || !nextBtn) return;

  let currentSlide = 0;
  const totalSlides = slideItems.length;
  let autoSlideInterval;

  // Crear los indicadores (dots) como botones accesibles
  for (let i = 0; i < totalSlides; i++) {
    const dot = document.createElement('button');
    dot.type = 'button';
    dot.className = 'banner-dot';
    dot.setAttribute('aria-label', 'Ir al slide ' + (i + 1));
    if (i === 0) dot.classList.add('active');
    dot.addEventListener('click', function () { goToSlide(this.dataset.index); });
    dot.addEventListener('keydown', function (e) {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        goToSlide(this.dataset.index);
      }
    });
    dot.dataset.index = i;
    navContainer.appendChild(dot);
  }

  const dots = navContainer.querySelectorAll('.banner-dot');

  // Función para ir a un slide específico
  function goToSlide(index) {
    currentSlide = parseInt(index, 10);
    // Mover el slider
    slides.style.transform = 'translateX(-' + currentSlide * 100 + '%)';

    // Actualizar los dots
    for (let j = 0; j < dots.length; j++) {
      dots[j].classList.toggle('active', j === currentSlide);
    }
  }

  // Función para ir al siguiente slide
  function nextSlide() {
    currentSlide = (currentSlide + 1) % totalSlides;
    goToSlide(currentSlide);
  }

  // Función para ir al slide anterior
  function prevSlide() {
    currentSlide = (currentSlide - 1 + totalSlides) % totalSlides;
    goToSlide(currentSlide);
  }

  // Event listeners para las flechas
  nextBtn.addEventListener('click', function () {
    nextSlide();
    resetAutoSlide();
  });

  prevBtn.addEventListener('click', function () {
    prevSlide();
    resetAutoSlide();
  });

  // Auto-slide cada 5 segundos
  function startAutoSlide() {
    autoSlideInterval = setInterval(nextSlide, 5000);
  }

  // Reiniciar el auto-slide cuando el usuario interactúa
  function resetAutoSlide() {
    clearInterval(autoSlideInterval);
    startAutoSlide();
  }

  // Iniciar el auto-slide
  startAutoSlide();

  // Pausar el auto-slide cuando el mouse está sobre el banner
  const banner = document.querySelector('.food-banner');
  if (banner) {
    banner.addEventListener('mouseenter', function () {
      clearInterval(autoSlideInterval);
    });
    banner.addEventListener('mouseleave', function () {
      startAutoSlide();
    });
  }
})();
