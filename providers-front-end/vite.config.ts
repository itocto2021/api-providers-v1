import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000, // Cambiado de 3001 a 3000 para consistencia
    open: '/welcome', // Abre automáticamente en la ruta welcome
    host: true, // Permite acceso desde la red
  },
  build: {
    outDir: 'dist',
    sourcemap: false, // Deshabilitar sourcemaps en producción
    minify: true, // Asegurar minificación
  },
  define: {
    // Asegurar que estamos en modo producción
    'process.env.NODE_ENV': '"production"'
  },
});