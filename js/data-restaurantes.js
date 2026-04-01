/* ============================
   URBAN EATS - Datos de Restaurantes
   Base de datos temporal en localStorage
   ============================ */

// 4 Restaurantes de prueba
const restaurantes = [
  {
    id: 1,
    nombre: "Green Kitchen",
    ubicacion: "Calle 72 #10-34, Bogotá",
    horario: "8:00 AM - 10:00 PM",
    logo: "🥗",
    descripcion: "Especialistas en comida saludable y bowls nutritivos. Ingredientes 100% orgánicos.",
    color: "#d5f5e3"
  },
  {
    id: 2,
    nombre: "Healthy Bowls Co.",
    ubicacion: "Carrera 15 #85-23, Bogotá",
    horario: "7:00 AM - 9:00 PM",
    logo: "🍲",
    descripcion: "Los mejores bowls de la ciudad. Perfecto para deportistas y vida activa.",
    color: "#fef9e7"
  },
  {
    id: 3,
    nombre: "Fresh & Fit",
    ubicacion: "Calle 100 #18-45, Bogotá",
    horario: "9:00 AM - 11:00 PM",
    logo: "🥑",
    descripcion: "Desayunos energéticos y snacks saludables. Tu mejor opción para empezar el día.",
    color: "#e8f4fd"
  },
  {
    id: 4,
    nombre: "Vital Meals",
    ubicacion: "Avenida 19 #104-55, Bogotá",
    horario: "10:00 AM - 10:00 PM",
    logo: "🍗",
    descripcion: "Platos principales balanceados con proteína de calidad. Comida fitness deliciosa.",
    color: "#fdecea"
  }
];

// Productos con restaurante asignado
const productos = [
  // GREEN KITCHEN (id: 1) - Especialista en Bowls y Ensaladas
  {
    id: 1,
    nombre: "Bowl Mediterráneo",
    categoria: "Bowls",
    descripcion: "Ensalada fresca con quinoa, garbanzos, pepino, tomate cherry, aceitunas y aderezo de tahini.",
    precio: 18500,
    emoji: "🥗",
    color: "#d5f5e3",
    calorias: 450,
    proteina: "18g",
    imagen: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop",
    restauranteId: 1,
    restauranteNombre: "Green Kitchen",
    restauranteLogo: "🥗"
  },
  {
    id: 5,
    nombre: "Bowl de Quinoa y Salmón",
    categoria: "Bowls",
    descripcion: "Quinoa tricolor, salmón ahumado, edamame, aguacate y vinagreta de jengibre.",
    precio: 26000,
    emoji: "🐟",
    color: "#e8f4fd",
    calorias: 490,
    proteina: "32g",
    imagen: "https://images.unsplash.com/photo-1559847844-5315695dadae?w=400&h=300&fit=crop",
    restauranteId: 1,
    restauranteNombre: "Green Kitchen",
    restauranteLogo: "🥗"
  },
  {
    id: 9,
    nombre: "Ensalada César Fit",
    categoria: "Platos Principales",
    descripcion: "Lechuga romana, pollo a la plancha, crutones integrales y aderezo césar light.",
    precio: 19000,
    emoji: "🥙",
    color: "#e9f7ef",
    calorias: 380,
    proteina: "26g",
    imagen: "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=400&h=300&fit=crop",
    restauranteId: 1,
    restauranteNombre: "Green Kitchen",
    restauranteLogo: "🥗"
  },
  {
    id: 13,
    nombre: "Bowl VeganoPower",
    categoria: "Bowls",
    descripcion: "Base de arroz integral, tofu marinado, kale, remolacha, garbanzos y hummus.",
    precio: 21000,
    emoji: "🌱",
    color: "#d5f5e3",
    calorias: 520,
    proteina: "24g",
    imagen: "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop",
    restauranteId: 1,
    restauranteNombre: "Green Kitchen",
    restauranteLogo: "🥗"
  },
  {
    id: 14,
    nombre: "Ensalada Asiática",
    categoria: "Bowls",
    descripcion: "Mix de lechugas, edamame, mandarina, almendras, pollo teriyaki y aderezo de sésamo.",
    precio: 20500,
    emoji: "🥢",
    color: "#fef9e7",
    calorias: 430,
    proteina: "28g",
    imagen: "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&h=300&fit=crop",
    restauranteId: 1,
    restauranteNombre: "Green Kitchen",
    restauranteLogo: "🥗"
  },

  // HEALTHY BOWLS CO. (id: 2) - Especialista en Bowls y Bebidas
  {
    id: 11,
    nombre: "Bowl Buddha",
    categoria: "Bowls",
    descripcion: "Arroz integral, tempeh, brócoli al vapor, zanahoria, aguacate y salsa de maní.",
    precio: 20000,
    emoji: "🍲",
    color: "#fef9e7",
    calorias: 510,
    proteina: "22g",
    imagen: "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop",
    restauranteId: 2,
    restauranteNombre: "Healthy Bowls Co.",
    restauranteLogo: "🍲"
  },
  {
    id: 6,
    nombre: "Jugo Verde Detox",
    categoria: "Bebidas",
    descripcion: "Espinaca, pepino, apio, manzana verde, limón y jengibre. 100% natural.",
    precio: 12000,
    emoji: "🥤",
    color: "#eafaf1",
    calorias: 95,
    proteina: "2g",
    imagen: "https://images.unsplash.com/photo-1610970881699-44a5587cabec?w=400&h=300&fit=crop",
    restauranteId: 2,
    restauranteNombre: "Healthy Bowls Co.",
    restauranteLogo: "🍲"
  },
  {
    id: 10,
    nombre: "Kombucha de Frutas",
    categoria: "Bebidas",
    descripcion: "Kombucha artesanal con frutas tropicales, fermentada naturalmente. Rico en probióticos.",
    precio: 9500,
    emoji: "🍹",
    color: "#f4ecf7",
    calorias: 60,
    proteina: "0g",
    imagen: "https://images.unsplash.com/photo-1544145945-35e9d6e0a76f?w=400&h=300&fit=crop",
    restauranteId: 2,
    restauranteNombre: "Healthy Bowls Co.",
    restauranteLogo: "🍲"
  },
  {
    id: 15,
    nombre: "Bowl Proteico Post-Gym",
    categoria: "Bowls",
    descripcion: "Arroz yamani, pechuga de pavo, huevo duro, espinaca, batata y aguacate.",
    precio: 23500,
    emoji: "💪",
    color: "#fdecea",
    calorias: 580,
    proteina: "42g",
    imagen: "https://images.unsplash.com/photo-1546069901-eacef0df6022?w=400&h=300&fit=crop",
    restauranteId: 2,
    restauranteNombre: "Healthy Bowls Co.",
    restauranteLogo: "🍲"
  },
  {
    id: 16,
    nombre: "Batido Proteico Triple",
    categoria: "Bebidas",
    descripcion: "Leche de almendras, banana, mantequilla de maní, proteína vegetal y cacao.",
    precio: 14500,
    emoji: "🥤",
    color: "#fef3e2",
    calorias: 380,
    proteina: "28g",
    imagen: "https://images.unsplash.com/photo-1622597467836-f3285f2131b8?w=400&h=300&fit=crop",
    restauranteId: 2,
    restauranteNombre: "Healthy Bowls Co.",
    restauranteLogo: "🍲"
  },

  // FRESH & FIT (id: 3) - Especialista en Desayunos y Wraps
  {
    id: 3,
    nombre: "Smoothie Bowl Energético",
    categoria: "Desayunos",
    descripcion: "Base de açaí y plátano con granola, frutas frescas, coco rallado y miel.",
    precio: 16000,
    emoji: "🍓",
    color: "#fdecea",
    calorias: 380,
    proteina: "12g",
    imagen: "https://images.unsplash.com/photo-1590301157890-4810ed352733?w=400&h=300&fit=crop",
    restauranteId: 3,
    restauranteNombre: "Fresh & Fit",
    restauranteLogo: "🥑"
  },
  {
    id: 7,
    nombre: "Avena Overnight",
    categoria: "Desayunos",
    descripcion: "Avena remojada con leche de almendra, chía, frutos rojos y miel de abeja.",
    precio: 13500,
    emoji: "🥣",
    color: "#fdf2e9",
    calorias: 320,
    proteina: "11g",
    imagen: "https://images.unsplash.com/photo-1590301157890-4810ed352733?w=400&h=300&fit=crop",
    restauranteId: 3,
    restauranteNombre: "Fresh & Fit",
    restauranteLogo: "🥑"
  },
  {
    id: 12,
    nombre: "Tostadas de Aguacate",
    categoria: "Desayunos",
    descripcion: "Pan artesanal integral con aguacate triturado, huevo pochado, semillas y limón.",
    precio: 14000,
    emoji: "🥑",
    color: "#d5f5e3",
    calorias: 350,
    proteina: "16g",
    imagen: "https://images.unsplash.com/photo-1588137378633-dea1336ce1e2?w=400&h=300&fit=crop",
    restauranteId: 3,
    restauranteNombre: "Fresh & Fit",
    restauranteLogo: "🥑"
  },
  {
    id: 4,
    nombre: "Wrap de Vegetales",
    categoria: "Wraps",
    descripcion: "Tortilla integral rellena de hummus, aguacate, espinaca, zanahoria y pimientos asados.",
    precio: 15000,
    emoji: "🌯",
    color: "#fef3e2",
    calorias: 340,
    proteina: "14g",
    imagen: "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400&h=300&fit=crop",
    restauranteId: 3,
    restauranteNombre: "Fresh & Fit",
    restauranteLogo: "🥑"
  },
  {
    id: 17,
    nombre: "Pancakes Proteicos",
    categoria: "Desayunos",
    descripcion: "Pancakes de avena y banana con yogurt griego, arándanos y jarabe de maple.",
    precio: 15500,
    emoji: "🥞",
    color: "#fef9e7",
    calorias: 420,
    proteina: "22g",
    imagen: "https://images.unsplash.com/photo-1528207776546-365bb710ee93?w=400&h=300&fit=crop",
    restauranteId: 3,
    restauranteNombre: "Fresh & Fit",
    restauranteLogo: "🥑"
  },
  {
    id: 18,
    nombre: "Wrap de Salmón Ahumado",
    categoria: "Wraps",
    descripcion: "Tortilla de espinaca, salmón ahumado, queso crema light, alcaparras y eneldo.",
    precio: 19500,
    emoji: "🌯",
    color: "#e8f4fd",
    calorias: 390,
    proteina: "26g",
    imagen: "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=400&h=300&fit=crop",
    restauranteId: 3,
    restauranteNombre: "Fresh & Fit",
    restauranteLogo: "🥑"
  },

  // VITAL MEALS (id: 4) - Especialista en Platos Principales y Wraps
  {
    id: 2,
    nombre: "Pollo a la Parrilla",
    categoria: "Platos Principales",
    descripcion: "Pechuga de pollo marinada con hierbas, acompañada de vegetales asados y batata.",
    precio: 22000,
    emoji: "🍗",
    color: "#fef9e7",
    calorias: 520,
    proteina: "38g",
    imagen: "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=400&h=300&fit=crop",
    restauranteId: 4,
    restauranteNombre: "Vital Meals",
    restauranteLogo: "🍗"
  },
  {
    id: 8,
    nombre: "Wrap de Pollo y Palta",
    categoria: "Wraps",
    descripcion: "Tortilla de espinaca con pollo grillado, aguacate, lechuga, tomate y salsa griega.",
    precio: 17500,
    emoji: "🌯",
    color: "#f9ebea",
    calorias: 410,
    proteina: "29g",
    imagen: "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=400&h=300&fit=crop",
    restauranteId: 4,
    restauranteNombre: "Vital Meals",
    restauranteLogo: "🍗"
  },
  {
    id: 19,
    nombre: "Salmón a la Plancha",
    categoria: "Platos Principales",
    descripcion: "Filete de salmón con costra de sésamo, arroz integral y vegetales al wok.",
    precio: 28000,
    emoji: "🐟",
    color: "#e8f4fd",
    calorias: 550,
    proteina: "36g",
    imagen: "https://images.unsplash.com/photo-1580959375944-c6f52d6d3f02?w=400&h=300&fit=crop",
    restauranteId: 4,
    restauranteNombre: "Vital Meals",
    restauranteLogo: "🍗"
  },
  {
    id: 20,
    nombre: "Pechuga Rellena Caprese",
    categoria: "Platos Principales",
    descripcion: "Pechuga de pollo rellena de tomate, mozzarella y albahaca. Salsa de tomate natural.",
    precio: 24500,
    emoji: "🍗",
    color: "#fdecea",
    calorias: 480,
    proteina: "40g",
    imagen: "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400&h=300&fit=crop",
    restauranteId: 4,
    restauranteNombre: "Vital Meals",
    restauranteLogo: "🍗"
  },
  {
    id: 21,
    nombre: "Wrap BBQ Pulled Chicken",
    categoria: "Wraps",
    descripcion: "Tortilla con pollo desmechado en salsa BBQ, col morada, zanahoria y cilantro.",
    precio: 18000,
    emoji: "🌯",
    color: "#fef3e2",
    calorias: 460,
    proteina: "32g",
    imagen: "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400&h=300&fit=crop",
    restauranteId: 4,
    restauranteNombre: "Vital Meals",
    restauranteLogo: "🍗"
  },
  {
    id: 22,
    nombre: "Carne Asada Colombiana",
    categoria: "Platos Principales",
    descripcion: "Carne de res a la parrilla con chimichurri, papa criolla y ensalada fresca.",
    precio: 26500,
    emoji: "🥩",
    color: "#fef9e7",
    calorias: 590,
    proteina: "44g",
    imagen: "https://images.unsplash.com/photo-1558030006-450675393462?w=400&h=300&fit=crop",
    restauranteId: 4,
    restauranteNombre: "Vital Meals",
    restauranteLogo: "🍗"
  }
];

// Función para obtener restaurante por ID
function obtenerRestaurantePorId(id) {
  return restaurantes.find(r => r.id === parseInt(id));
}

// Función para obtener productos de un restaurante
function obtenerProductosDeRestaurante(restauranteId) {
  return productos.filter(p => p.restauranteId === parseInt(restauranteId));
}

// Función para obtener todos los restaurantes
function obtenerRestaurantes() {
  return restaurantes;
}

// Función para obtener todos los productos
function obtenerProductos() {
  return productos;
}

// Cargar datos en localStorage (simulando BD)
function inicializarDatosRestaurantes() {
  if (!localStorage.getItem('ue_restaurantes')) {
    localStorage.setItem('ue_restaurantes', JSON.stringify(restaurantes));
  }
  if (!localStorage.getItem('ue_productos')) {
    localStorage.setItem('ue_productos', JSON.stringify(productos));
  }
}

// Inicializar datos al cargar el script
inicializarDatosRestaurantes();
