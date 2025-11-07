package com.udeajobs.projects_cell.projects_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuración de OpenAPI 3.0 para la documentación Swagger UI.
 *
 * Esta clase configura la información general de la API, incluyendo
 * título, descripción, versión, información de contacto, licencia
 * y servidores disponibles para la documentación interactiva.
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configura la documentación OpenAPI para el servicio de proyectos.
     *
     * Define toda la metadata de la API, incluyendo información general,
     * contacto del equipo, licencia y servidores disponibles.
     *
     * @return instancia de OpenAPI configurada con toda la metadata de la API
     */
    @Bean
    public OpenAPI projectsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UdeAJobs - Projects Service API")
                        .description("""
                                **API REST para la gestión de proyectos freelance en UdeAJobs**
                                
                                Este microservicio maneja el ciclo de vida completo de los proyectos en la plataforma, incluyendo:
                                
                                - 📋 **Gestión de Proyectos**: Creación, actualización, búsqueda y eliminación de proyectos
                                - 💰 **Rangos Salariales**: Definición de salarios mínimos y máximos con soporte multi-moneda
                                - 📍 **Ubicación y Modalidad**: Proyectos presenciales, remotos o híbridos
                                - 🛠️ **Habilidades Requeridas**: Lista de competencias técnicas necesarias
                                - 📊 **Estados del Proyecto**: OPEN, IN_PROGRESS, COMPLETED, CANCELED
                                - 👔 **Nivel de Experiencia**: Requisitos de seniority para el proyecto
                                
                                ### Características
                                - Arquitectura por capas (Controller → Service → Repository)
                                - Operaciones CRUD completas para proyectos
                                - Validación exhaustiva de datos con Bean Validation (JSR-303)
                                - Manejo centralizado de excepciones con mensajes descriptivos
                                - Filtrado y búsqueda avanzada de proyectos
                                
                                ### Base de datos
                                - PostgreSQL para almacenamiento relacional
                                - Tabla principal: `projects`
                                - Tabla de habilidades: `project_required_skills`
                                - Índices optimizados para búsquedas por UUID y employerId
                                
                                ### Integración
                                - Event-driven: Publica eventos de cambios de estado de proyectos
                                - Conecta con profile-service para validar empleadores y freelancers
                                - RabbitMQ para mensajería asíncrona entre microservicios
                                - Proporciona datos al dashboard-service para visualización
                                
                                ### Estados del proyecto
                                - **OPEN**: Proyecto publicado, aceptando propuestas
                                - **IN_PROGRESS**: Proyecto asignado a un freelancer
                                - **COMPLETED**: Proyecto finalizado exitosamente
                                - **CANCELED**: Proyecto cancelado por el empleador
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("UdeAJobs Development Team")
                                .email("udeajobs674@gmail.com")
                                .url("https://github.com/Team-DAS"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

