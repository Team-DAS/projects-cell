const fastify = require('fastify')({ logger: true });

// Importar las librerías necesarias
const httpProxy = require('@fastify/http-proxy');
const metrics = require('fastify-metrics'); 

fastify.register(metrics, { 
  endpoint: '/metrics',
});

// --- 1. Configuración (Leída de variables de entorno) ---
const PROJECTS_SERVICE_URL = process.env.PROJECTS_SERVICE_URL;
const SEARCHING_SERVICE_URL = process.env.SEARCHING_SERVICE_URL;

if (!PROJECTS_SERVICE_URL || !SEARCHING_SERVICE_URL) {
  fastify.log.error('Error: Las variables de entorno de los servicios no están definidas.');
  process.exit(1);
}

// --- 3. Registro de Rutas ---



fastify.register(httpProxy, {
  upstream: SEARCHING_SERVICE_URL,
  prefix: '/projects-cell/projects/graphql',  // <-- URL pública simple
  rewritePrefix: '/graphql',  // <-- URL interna
});

fastify.register(httpProxy, {
  upstream: SEARCHING_SERVICE_URL,
  prefix: '/projects-cell/projects/playground',  // <-- URL pública simple
  rewritePrefix: '/playground',  // <-- URL interna
});

fastify.register(httpProxy, {
  upstream: PROJECTS_SERVICE_URL,
  prefix: '/projects-cell/projects',  // <-- URL pública simple
  rewritePrefix: '/api/v1/projects',  // <-- URL interna con versión
});

// --- 4. Iniciar el Servidor ---
const start = async () => {
  try {
    await fastify.listen({ port: 8080, host: '0.0.0.0' });
  } catch (err) {
    fastify.log.error(err);
    process.exit(1);
  }
};
start();