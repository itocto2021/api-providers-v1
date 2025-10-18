# 🏪 Gapsi Proveedores - Frontend

Sistema de gestión de proveedores para e-Commerce Gapsi desarrollado con React, TypeScript y Material-UI.

## 📋 Descripción

Aplicación web moderna para la gestión de proveedores que incluye:

- **Pantalla de bienvenida** con foto del candidato y versión desde API
- **Header corporativo** con logo oficial de Gapsi
- **Lista paginada de proveedores** con virtual scroll para rendimiento
- **Formulario para agregar proveedores** con validación completa
- **Funcionalidad para eliminar proveedores** con confirmación
- **PWA (Progressive Web App)** con Service Worker
- **Diseño responsive** con Material-UI
- **Navegación con React Router**
- **Imágenes optimizadas** con procesamiento automático durante el build

## 🚀 Tecnologías Utilizadas

- **React 18** - Biblioteca de UI
- **TypeScript** - Tipado estático
- **Material-UI (MUI)** - Componentes de diseño
- **React Router** - Navegación
- **Axios** - Cliente HTTP
- **React Window** - Virtual scroll para rendimiento
- **Vite** - Build tool y servidor de desarrollo
- **PWA** - Service Worker para funcionalidad offline

## 📦 Instalación y Configuración

### Prerrequisitos

- Node.js >= 16.0.0
- npm >= 8.0.0

### Pasos para Instalación

1. **Clonar o navegar al directorio del proyecto:**
   ```bash
   cd providers-front-end
   ```

2. **Instalar dependencias:**
   ```bash
   npm install
   ```

3. **Configurar variables de entorno (opcional):**
   ```bash
   # Crear archivo .env en la raíz del proyecto
   VITE_API_BASE_URL=http://localhost:8080/api/v1
   ```

4. **Iniciar servidor de desarrollo:**
   ```bash
   npm run dev
   ```

5. **Abrir en el navegador:**
   - URL: `http://localhost:3000`
   - La aplicación debería cargarse automáticamente

## 🎯 Scripts Disponibles

```bash
# Desarrollo
npm run dev          # Inicia servidor de desarrollo

# Producción
npm run build        # Construye la aplicación para producción
npm run preview      # Previsualiza el build de producción
```

## 🏗️ Estructura del Proyecto

```
src/
├── assets/              # Recursos estáticos
│   ├── candidato.png   # Foto del candidato (circular)
│   └── logo-gapsi.png  # Logo oficial de Gapsi
├── components/          # Componentes React
│   ├── Header.tsx      # Header con logo y navegación
│   ├── Welcome.tsx     # Pantalla de bienvenida con avatar
│   ├── ProvidersList.tsx# Lista de proveedores con paginación
│   ├── ProviderForm.tsx # Formulario para agregar proveedores
│   └── ExampleComponent.tsx
├── services/           # Servicios y API
│   └── apiService.ts   # Cliente para API REST
├── types/             # Definiciones TypeScript
│   └── index.d.ts     # Interfaces y tipos (incluye módulos de imágenes)
├── styles/            # Estilos globales
│   └── global.css     # CSS global
├── hooks/             # Hooks personalizados
│   └── useExample.ts  # Hook de ejemplo
├── App.tsx            # Componente principal
└── index.tsx          # Punto de entrada
```

## 🔌 API Integration

La aplicación se conecta con la API de backend en `http://localhost:8080/api/v1` con los siguientes endpoints:

- `GET /gapsi/info` - Información de la aplicación
- `GET /gapsi/providers` - Lista paginada de proveedores
- `POST /gapsi/providers` - Crear nuevo proveedor
- `DELETE /gapsi/providers/{id}` - Eliminar proveedor

### Configuración de API

El endpoint base se puede modificar en `src/services/apiService.ts`:

```typescript
const API_BASE_URL = 'http://localhost:8080/api/v1';
```

## 🎨 Características Implementadas

### ✅ Requisitos Funcionales Cumplidos

- **[Peso 3]** Header con logo oficial de e-Commerce Gapsi ✅
  - Logo real implementado desde `src/assets/logo-gapsi.png`
  - Navegación funcional al hacer clic
  - Diseño corporativo con fondo azul
- **[Peso 5]** Pantalla de bienvenida con:
  - Foto real del candidato en avatar circular ✅
  - Mensaje de bienvenida desde API REST ✅
  - Versión de la aplicación desde API REST ✅
  - Animaciones suaves con Material-UI ✅
- **[Peso 6]** Frontend con:
  - Lista paginada de proveedores ✅
  - Agregar proveedores con validación ✅
  - Eliminar proveedores con confirmación ✅
  - Validación de duplicados ✅

### ✅ Requisitos No Funcionales Cumplidos

- **[Peso 3]** Virtual scroll en lista de proveedores ✅
- **[Peso 2]** Material-UI implementado ✅
- **[Peso 2]** PWA con Service Worker ✅
- **[Peso 1]** Font Awesome desde CDN ✅

## 🔧 Patrones de Diseño Implementados

1. **Service Layer Pattern** (`src/services/apiService.ts`)
   - Centraliza todas las llamadas a la API
   - Manejo uniforme de errores
   - Configuración reutilizable de axios

2. **Component Composition Pattern** (componentes React)
   - Componentes reutilizables y modulares
   - Separación de responsabilidades
   - Props tipadas con TypeScript

3. **Asset Management Pattern** (`src/assets/`)
   - Importación de imágenes como módulos
   - Optimización automática durante el build
   - Declaraciones de tipos para archivos estáticos

## 🖼️ Gestión de Imágenes

### Imágenes Incluidas

- **`candidato.png`** - Foto del candidato mostrada como avatar circular
- **`logo-gapsi.png`** - Logo oficial de Gapsi en el header

### Características de Optimización

- **Procesamiento automático** durante `npm run build`
- **Nombres con hash** para cacheo (`candidato.114157d1.png`)
- **Compresión automática** para reducir tamaño
- **Tipos TypeScript** declarados para importación segura

## 📱 Funcionalidad PWA

- **Service Worker** registrado automáticamente
- **Manifest.json** configurado para instalación
- **Cacheo offline** de recursos estáticos
- **Iconos** optimizados para diferentes dispositivos

## 🎭 Características de UX/UI

- **Animaciones suaves** con Material-UI
- **Feedback visual** en todas las acciones
- **Estados de carga** en operaciones async
- **Confirmaciones** para acciones destructivas
- **Validación en tiempo real** en formularios
- **Diseño responsive** para móviles y desktop

## 🚦 Estados de la Aplicación

La aplicación maneja los siguientes estados:

- **Loading** - Durante operaciones async
- **Error** - Con mensajes descriptivos
- **Empty** - Cuando no hay datos
- **Success** - Confirmaciones de acciones

## 🔍 Navegación

- `/` - Redirección a `/welcome`
- `/welcome` - Pantalla de bienvenida
- `/providers` - Gestión de proveedores

## 🛠️ Troubleshooting

### Problemas Comunes

1. **Error de conexión con API:**
   - Verificar que el backend esté ejecutándose en el puerto 8080
   - Comprobar CORS en el servidor backend

2. **Dependencias no instaladas:**
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

3. **Puerto ocupado:**
   ```bash
   # Vite usa puerto 3000 por defecto
   # Se puede cambiar en vite.config.ts
   ```

4. **Imágenes no se cargan:**
   - Verificar que los archivos estén en `src/assets/`
   - Comprobar que las declaraciones de tipos estén en `src/types/index.d.ts`
   - Ejecutar `npm run build` para regenerar los assets optimizados

## 📊 Información del Build

El último build exitoso generó:

```
dist/assets/logo-gapsi.2f6a6a54.png     6.66 KiB
dist/assets/candidato.114157d1.png    5203.63 KiB
dist/index.html                         2.24 KiB
dist/assets/index.bc54241d.css          1.40 KiB
dist/assets/index.368032b5.js         518.40 KiB
```

### Notas de Rendimiento

- Bundle principal: ~518 KiB (gzipped: ~167 KiB)
- Imágenes optimizadas automáticamente
- CSS minimizado: 1.40 KiB
- Consideraciones para code-splitting disponibles

## 🎯 Características Destacadas

### Material-UI Integration
- **Tema personalizado** con colores corporativos
- **Componentes styled** para diseño consistente
- **Animaciones** con Fade transitions
- **Sistema de grid** responsive

### TypeScript Benefits
- **Tipado estricto** en toda la aplicación
- **Interfaces** para API responses
- **Props tipadas** en todos los componentes
- **Autocompletado** mejorado en el IDE

### Optimizaciones
- **Virtual scrolling** para listas grandes
- **Lazy loading** de componentes
- **Memoización** donde es apropiado
- **Bundle splitting** preparado para implementar

---

**Desarrollado por:** Ilder Tocto  
**Empresa:** Gapsi  
**Versión:** 1.0.0  
**Fecha:** Octubre 2025  

### Contacto
- **Repositorio:** api-providers-v1
- **Branch:** development
- **Tecnologías:** React + TypeScript + Material-UI + Vite