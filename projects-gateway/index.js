const fastify = require('fastify')({ logger: true });

// Importar las librerías necesarias
const httpProxy = require('@fastify/http-proxy');
const jwt = require('jsonwebtoken');

// --- 1. Configuración (Leída de variables de entorno) ---
const PROJECTS_SERVICE_URL = process.env.PROJECTS_SERVICE_URL;
const SEARCH_SERVICE_URL = process.env.SEARCH_SERVICE_URL;
const JWT_SECRET = process.env.JWT_SECRET; 

const PUBLIC_ROUTES = ['/projects/graphql']; 

// --- 2. El "Hook" de Autenticación (El Middleware) ---
fastify.addHook('onRequest', async (request, reply) => {
  
  // Si la ruta es pública, dejarla pasar
  if (PUBLIC_ROUTES.some(route => request.url.startsWith(route))) {
    return;
  }

  const authHeader = request.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return reply.status(401).send({ error: 'Falta token de autorización' });
  }
  
  const token = authHeader.split(' ')[1];

  try {
    // --- CLAVE: Verificamos con el secreto ---
    const decoded = jwt.verify(token, JWT_SECRET); 

    // 3. ¡ÉXITO! Inyectar cabeceras.
    request.headers['x-user-id'] = decoded.sub; // 'subject(username)'
    request.headers['x-user-role'] = decoded.role; // 'claims.put("role", ...)'

  } catch (err) {
    fastify.log.warn(`Token inválido: ${err.message}`);
    return reply.status(401).send({ error: 'Token inválido o expirado' });
  }
});


// --- 3. Registro de Rutas (El Proxy) ---

fastify.register(httpProxy, {
  upstream: PROJECTS_SERVICE_URL,
  prefix: '/api/v1/projects', 
});

fastify.register(httpProxy, {
  upstream: SEARCH_SERVICE_URL,
  prefix: '/projects/graphql',    
  rewritePrefix: '/graphql'       
});
// --- 4. Iniciar el Servidor ---
const start = async () => {
  try {
    // Escuchamos en 0.0.0.0 (para Docker) y en el puerto 8080
    await fastify.listen({ port: 8080, host: '0.0.0.0' });
  } catch (err) {
    fastify.log.error(err);
    process.exit(1);
  }
};
start();