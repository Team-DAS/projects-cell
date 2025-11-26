# Searching Service - UdeAJobs

## 📋 Descripción del Proyecto

**Searching Service** es un microservicio especializado en búsquedas rápidas y eficientes de proyectos para la plataforma UdeAJobs. Utiliza **OpenSearch v2.19** como motor de búsqueda y proporciona una API GraphQL para consultas flexibles.

### 🎯 Propósito

Este servicio es parte de la célula de proyectos (projects-cell) y se encarga de:
- Indexar proyectos en OpenSearch para búsquedas rápidas
- Proporcionar búsquedas complejas con múltiples filtros
- Consumir eventos de RabbitMQ para mantener el índice sincronizado
- Enriquecer proyectos con información de categorización

## 🏗️ Arquitectura

### Componentes Principales

1. **GraphQL API** - Expone queries para búsqueda de proyectos
2. **OpenSearch v2.19** - Motor de búsqueda e indexación (compatible con Elasticsearch 7.x API)
3. **RabbitMQ Consumers** - Escuchan eventos de proyectos y categorización
4. **Prometheus Metrics** - Monitoreo y métricas del servicio

### Flujo de Datos

```
[Otros Servicios] ---> [RabbitMQ] ---> [Consumers]
                                           |
                                           v
                                    [Indexing Service]
                                           |
                                           v
                                      [OpenSearch]
                                           ^
                                           |
[Clientes] ---> [GraphQL API] ---> [Search Service]
```

## 🔧 Funcionalidades

### 1. Búsqueda Avanzada de Proyectos
Permite buscar proyectos con múltiples filtros:
- **Término de búsqueda** (título y descripción)
- **Habilidades requeridas**
- **Ubicación** (física o remota)
- **Rango salarial** (min/max y moneda)
- **Nivel del trabajo** (JUNIOR, SEMI_SENIOR, SENIOR)
- **Estado del proyecto** (DRAFT, PUBLISHED, CLOSED)
- **Categorías y tags**
- **Paginación y ordenamiento** (por fecha, salario, etc.)

### 2. Consumo de Eventos

#### Eventos de Proyectos (`project.events.queue`)
Escucha eventos de:
- `PROJECT_CREATED` - Indexa nuevos proyectos
- `PROJECT_UPDATED` - Actualiza proyectos existentes
- `PROJECT_DELETED` - Elimina proyectos del índice
- `PROJECT_PUBLISHED` - Marca proyectos como publicados

#### Eventos de Categorización (`project.categorization.queue`)
Enriquece proyectos con:
- Categoría principal (generada por IA)
- Tags relevantes
- Metadatos de categorización

## 🚀 Tecnologías Utilizadas

- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.7** - Framework principal
- **Spring Data Elasticsearch** - Integración con Elasticsearch
- **Spring GraphQL** - API GraphQL
- **Spring AMQP** - Cliente RabbitMQ
- **Elasticsearch 8.x** - Motor de búsqueda
- **RabbitMQ** - Message broker
- **Micrometer/Prometheus** - Métricas y monitoreo
- **Lombok** - Reducción de boilerplate
- **Gradle** - Build tool

## 📦 Requisitos Previos

### Software Necesario

1. **Java Development Kit (JDK) 21**
   ```bash
   java -version
   # Debe mostrar: java version "21.x.x"
   ```

2. **Elasticsearch 8.x**
   - Puede ejecutarse en Docker o instalación local
   - Puerto por defecto: `9200`

3. **RabbitMQ**
   - Puede ejecutarse en Docker o instalación local
   - Puerto por defecto: `5672`
   - Management UI: `15672`

    
### 2. GraphiQL Interface

Abre en tu navegador: **http://localhost:8083/graphiql**

Prueba esta query:
```graphql
query {
  searchProjects(input: {
    searchTerm: "desarrollador"
    page: 0
    size: 10
  }) {
    projects {
      projectId
      title
      description
      requiredSkills
      minSalary
      maxSalary
      currency
      location
      isRemote
    }
    totalElements
    totalPages
    hasNext
  }
}
```

### 3. Métricas Prometheus
```bash
curl http://localhost:8083/actuator/prometheus
```

## 📊 Endpoints Principales

### GraphQL Queries

1. **Búsqueda de Proyectos**
   ```graphql
   query SearchProjects($input: ProjectSearchInput!) {
     searchProjects(input: $input) {
       projects { ... }
       currentPage
       totalElements
       totalPages
       hasNext
       hasPrevious
     }
   }
   ```

2. **Buscar Proyecto por ID**
   ```graphql
   query FindProject($projectId: ID!) {
     findProjectById(projectId: $projectId) {
       projectId
       title
       description
       # ... otros campos
     }
   }
   ```

### Actuator Endpoints

- `/actuator/health` - Estado del servicio
- `/actuator/metrics` - Métricas generales
- `/actuator/prometheus` - Métricas formato Prometheus
- `/actuator/info` - Información del servicio

## 🔍 Ejemplos de Uso

### Búsqueda por Habilidades
```graphql
query {
  searchProjects(input: {
    requiredSkills: ["Java", "Spring Boot", "GraphQL"]
    page: 0
    size: 20
  }) {
    projects {
      title
      requiredSkills
      minSalary
      currency
    }
    totalElements
  }
}
```

### Búsqueda con Filtro Salarial
```graphql
query {
  searchProjects(input: {
    minSalary: 3000
    maxSalary: 8000
    currency: "USD"
    isRemote: true
    page: 0
    size: 10
  }) {
    projects {
      title
      minSalary
      maxSalary
      location
      isRemote
    }
  }
}
```

### Búsqueda con Ordenamiento
```graphql
query {
  searchProjects(input: {
    status: "PUBLISHED"
    sortBy: "createdAt"
    sortDirection: "DESC"
    page: 0
    size: 15
  }) {
    projects {
      title
      createdAt
      status
    }
  }
}
```

## 🐛 Troubleshooting

### Problema: OpenSearch no conecta
```bash
# Verifica que OpenSearch esté corriendo
curl http://localhost:9200
# Debe devolver información del cluster con "distribution": "opensearch"

# Verifica los logs del servicio
tail -f logs/searching-service.log

# Verifica el contenedor si usas Docker
docker logs opensearch-container

# Solución: Verifica la URL en application.yml (OPENSEARCH_HOST)
```

### Problema: RabbitMQ no recibe mensajes
```bash
# Verifica las colas en RabbitMQ Management
# http://localhost:15672

# Verifica que las colas existan:
# - project.events.queue
# - project.categorization.queue

# Solución: Crear las colas manualmente o configurar auto-creación
```

### Problema: Error al indexar proyectos
```bash
# Verifica el mapping del índice
curl http://localhost:9200/projects/_mapping

# Elimina y recrea el índice si es necesario
curl -X DELETE http://localhost:9200/projects
```

## 📈 Monitoreo

### Métricas Clave

- `projects.search.requests` - Total de búsquedas realizadas
- `projects.search.errors` - Errores en búsquedas
- `projects.indexing.created` - Proyectos indexados
- `projects.indexing.updated` - Proyectos actualizados
- `projects.indexing.deleted` - Proyectos eliminados
- `projects.indexing.errors` - Errores de indexación

### Integración con Prometheus

Añade este job a tu `prometheus.yml`:
```yaml
scrape_configs:
  - job_name: 'searching-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8083']
```

## 🧪 Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con reporte
./gradlew test --info

# Ver reporte de tests
# Abre: build/reports/tests/test/index.html
```

## 📝 Notas Adicionales

### Sincronización de Datos
- El servicio NO tiene una base de datos propia
- Se sincroniza automáticamente mediante eventos de RabbitMQ
- Para sincronización inicial, debe haber un mecanismo de reindexación en otro servicio

### Escalabilidad
- El servicio es stateless y puede escalarse horizontalmente
- OpenSearch puede configurarse en cluster para alta disponibilidad
- RabbitMQ soporta múltiples consumers para procesamiento paralelo

### Seguridad
- En producción, habilita el Security Plugin de OpenSearch
- Usa credenciales seguras para RabbitMQ
- Considera añadir autenticación/autorización a la API GraphQL
- OpenSearch soporta TLS/SSL para conexiones seguras

## 👥 Autor

**UdeAJobs Team**
- Versión: 1.0
- Fecha: Noviembre 4, 2025

## 📄 Licencia

Este proyecto es parte del sistema UdeAJobs y está protegido por las políticas de la Universidad de Antioquia.

