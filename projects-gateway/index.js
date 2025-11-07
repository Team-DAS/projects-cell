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

// --- 2. HTML del GraphQL Playground ---
const playgroundHTML = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>GraphQL Playground - Projects</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/graphql-playground-react/build/static/css/index.css" />
  <link rel="shortcut icon" href="https://cdn.jsdelivr.net/npm/graphql-playground-react/build/favicon.png" />
  <script src="https://cdn.jsdelivr.net/npm/graphql-playground-react/build/static/js/middleware.js"></script>
</head>
<body>
  <div id="root"></div>
  <script>
    window.addEventListener('load', function (event) {
      GraphQLPlayground.init(document.getElementById('root'), {
        endpoint: '/projects-cell/api/v1/projects/graphql',
        settings: {
          'request.credentials': 'same-origin',
        },
        tabs: [
          {
            endpoint: '/projects-cell/api/v1/projects/graphql',
            query: '# Bienvenido al GraphQL Playground\\n# Escribe tus queries aquí\\n\\nquery {\\n  # Tu query aquí\\n}',
          },
        ],
      })
    })
  </script>
</body>
</html>
`;

// --- 3. Registro de Rutas ---


fastify.get('/projects-cell/api/v1/projects/playground', async (request, reply) => {
  reply.type('text/html').send(playgroundHTML);
});


fastify.register(httpProxy, {
  upstream: SEARCHING_SERVICE_URL,
  prefix: '/projects-cell/api/v1/projects/graphql',
  rewritePrefix: '/graphql',
  replyOptions: {
    rewriteRequestHeaders: (originalReq, headers) => ({
      ...headers,
      'x-forwarded-host': originalReq.headers.host,
      'x-forwarded-proto': originalReq.headers['x-forwarded-proto'] || 'http',
    })
  }
});

fastify.register(httpProxy, {
  upstream: PROJECTS_SERVICE_URL,
  prefix: '/projects-cell/api/v1/projects',
  rewritePrefix: '/api/v1/projects',
  replyOptions: {
    rewriteRequestHeaders: (originalReq, headers) => ({
      ...headers,
      'x-forwarded-host': originalReq.headers.host,
      'x-forwarded-proto': originalReq.headers['x-forwarded-proto'] || 'http',
    })
  }
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