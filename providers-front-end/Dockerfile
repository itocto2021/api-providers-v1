# Multi-stage build para optimizar el tamaño de la imagen final

# Etapa 1: Build
FROM node:18-alpine AS builder

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de dependencias
COPY package.json package-lock.json* ./

# Instala todas las dependencias (incluyendo devDependencies para el build)
RUN npm ci --silent

# Copia el código fuente
COPY . .

# Construye la aplicación para producción
RUN npm run build

# Etapa 2: Producción
FROM nginx:alpine AS production

# Copia los archivos construidos desde la etapa anterior
COPY --from=builder /app/dist /usr/share/nginx/html

# Copia configuración personalizada de nginx (opcional)
# COPY nginx.conf /etc/nginx/nginx.conf

# Configura nginx para manejar rutas SPA (Single Page Application)
RUN echo 'server { \
    listen 80; \
    server_name localhost; \
    root /usr/share/nginx/html; \
    index index.html; \
    \
    # Manejo de rutas para SPA \
    location / { \
        try_files $uri $uri/ /index.html; \
    } \
    \
    # Configuración de cache para archivos estáticos \
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ { \
        expires 1y; \
        add_header Cache-Control "public, immutable"; \
    } \
    \
    # Compresión gzip \
    gzip on; \
    gzip_vary on; \
    gzip_min_length 1024; \
    gzip_proxied expired no-cache no-store private auth; \
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json; \
}' > /etc/nginx/conf.d/default.conf

# Expone el puerto 80
EXPOSE 80

# Comando para iniciar nginx
CMD ["nginx", "-g", "daemon off;"]