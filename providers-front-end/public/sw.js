// Service Worker para funcionalidad PWA básica - Versión mejorada
const CACHE_NAME = 'gapsi-providers-v1';

// Solo cachear recursos que realmente existen
const urlsToCache = [
  '/',
  '/manifest.json'
];

// Instalar Service Worker
self.addEventListener('install', (event) => {
  console.log('Service Worker instalando...');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => {
        console.log('Cache abierto');
        // Cachear solo la página principal por ahora
        return cache.addAll(['/']);
      })
      .catch((error) => {
        console.error('Error al cachear recursos:', error);
      })
  );
  // Forzar activación inmediata
  self.skipWaiting();
});

// Interceptar solicitudes de red
self.addEventListener('fetch', (event) => {
  // Solo interceptar solicitudes GET
  if (event.request.method !== 'GET') {
    return;
  }

  // Ignorar solicitudes a APIs externas
  if (event.request.url.includes('localhost:8080') || 
      event.request.url.includes('api') ||
      event.request.url.includes('hot-update')) {
    return;
  }

  event.respondWith(
    caches.match(event.request)
      .then((response) => {
        // Devolver desde cache si está disponible
        if (response) {
          return response;
        }
        
        // Si no está en cache, hacer fetch
        return fetch(event.request)
          .then((response) => {
            // Verificar que la respuesta sea válida
            if (!response || response.status !== 200 || response.type !== 'basic') {
              return response;
            }

            // Clonar la respuesta para guardar en cache
            const responseToCache = response.clone();

            caches.open(CACHE_NAME)
              .then((cache) => {
                cache.put(event.request, responseToCache);
              });

            return response;
          })
          .catch(() => {
            // En caso de error, devolver página offline básica
            if (event.request.destination === 'document') {
              return caches.match('/');
            }
          });
      })
  );
});

// Activar Service Worker
self.addEventListener('activate', (event) => {
  console.log('Service Worker activado');
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (cacheName !== CACHE_NAME) {
            console.log('Eliminando cache antigua:', cacheName);
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
  // Tomar control inmediatamente
  self.clients.claim();
});