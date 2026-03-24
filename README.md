# 📘 Guía de uso de Git y GitHub – Urban Eats

## ⚠️ Antes de empezar
Esta guía asume que **ya tienen Git Bash instalado**.  
Si no lo tienen, por favor instálenlo antes de continuar.

---

## 📥 Clonar el proyecto (solo la primera vez)

Si es la primera vez que van a trabajar en el proyecto:

```bash
cd Documents/
git clone https://github.com/SDDev-01/Urban-Eats
```

👉 Esto descargará el proyecto en la carpeta Documents

# 🔄 Preparar el proyecto antes de trabajar

Antes de hacer cualquier cambio, SIEMPRE ejecuten:

```bash
cd Documents/
cd Urban\ Eats/
git checkout main
git pull origin main
```

⚠️ Esto es MUY importante para evitar conflictos.

---

# 🌿 Crear una nueva rama

Cuando vayan a empezar algo nuevo:

```bash
git checkout -b nombre-rama
```

## 🧠 Convenciones de ramas

Para mantener orden en el proyecto, usaremos estas:

### 🆕 Nuevas funciones

    feature/lo-que-hicieron

👉 Ej: feature/login

### 🛠️ Arreglos de errores
    
    fix/lo-que-hicieron


👉 Ej: fix/error-login

### 🎨 Cambios visuales (CSS / diseño)

    style/lo-que-hicieron


👉 Ej: style/navbar

### 📝 Documentación

    docs/lo-que-hicieron


👉 Ej: docs/diagrama-bd

---

# 💻 Desarrollo

Una vez creen su rama, ya pueden trabajar normalmente en el proyecto
(código, archivos, etc).

---

# 💾 Guardar cambios

Cuando terminen:

```bash
git add .
git commit -m "descripcion clara de lo que hicieron"
```

👉 Ejemplo:

```bash
git commit -m "Se agrega validacion al formulario de registro"
```
---

# 🚀 Subir cambios (IMPORTANTE)
    
```bash
git push origin nombre-rama
```

👉 Siempre suban su propia rama, no main

---

# 🔀 Crear Pull Request (PR)

Después de hacer push:

- Ir a GitHub
- Buscar el botón "Compare & pull request"
- Crear el PR hacia main
- (Opcional) Explicar lo que hicieron
- Esperar revisión

---

# ❌ Errores comunes (NO hacer)
- Hacer cambios directamente en main ❌
- Subir cosas con git push origin main ❌
- No hacer git pull antes de empezar ❌
- Crear ramas sin estar en main ❌

---

# ✅ Buenas prácticas
- Usar nombres claros en ramas
- Hacer commits con mensajes entendibles
- Actualizar main antes de empezar algo nuevo
- Mantener cada rama enfocada en una sola cosa

---

# 🧩 Resumen rápido
1. Ir a main y actualizar
2. Crear rama
3. Trabajar
4. add + commit
5. push a su rama
6. Crear Pull Request en GitHub

