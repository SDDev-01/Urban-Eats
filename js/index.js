/* ============================
   SLIDER DEL BANNER
   ============================ */
(function() {
  const slides = document.getElementById('banner-slides');
  const slideItems = document.querySelectorAll('.banner-slide');
  const navContainer = document.getElementById('banner-nav');
  const prevBtn = document.getElementById('banner-prev');
  const nextBtn = document.getElementById('banner-next');
  
  let currentSlide = 0;
  const totalSlides = slideItems.length;
  let autoSlideInterval;

  // Crear los indicadores (dots)
  for (let i = 0; i < totalSlides; i++) {
    const dot = document.createElement('div');
    dot.className = 'banner-dot';
    if (i === 0) dot.classList.add('active');
    dot.addEventListener('click', () => goToSlide(i));
    navContainer.appendChild(dot);
  }

  const dots = document.querySelectorAll('.banner-dot');

  // Función para ir a un slide específico
  function goToSlide(index) {
    currentSlide = index;
    // Mover el slider
    slides.style.transform = `translateX(-${currentSlide * 100}%)`;
    
    // Actualizar los dots
    dots.forEach((dot, i) => {
      dot.classList.toggle('active', i === currentSlide);
    });
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
  nextBtn.addEventListener('click', () => {
    nextSlide();
    resetAutoSlide();
  });

  prevBtn.addEventListener('click', () => {
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
  banner.addEventListener('mouseenter', () => {
    clearInterval(autoSlideInterval);
  });

  banner.addEventListener('mouseleave', () => {
    startAutoSlide();
  });
})();
