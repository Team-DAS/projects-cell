package com.udeajobs.projects_cell.searching_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración de RabbitMQ.
 * <p>
 * Esta clase mapea las propiedades definidas en application.yml bajo el prefijo 'app.rabbitmq'.
 * Proporciona acceso a las configuraciones de colas, exchanges y routing keys
 * utilizadas por el servicio de búsqueda.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.rabbitmq")
public class RabbitMQProperties {

    /**
     * Configuración de colas de RabbitMQ.
     */
    private Queues queues = new Queues();

    /**
     * Configuración de exchanges de RabbitMQ.
     */
    private Exchanges exchanges = new Exchanges();

    /**
     * Configuración de routing keys de RabbitMQ.
     */
    private RoutingKeys routingKeys = new RoutingKeys();

    /**
     * Nombres de las colas utilizadas por el servicio.
     */
    @Data
    public static class Queues {
        /**
         * Cola para eventos de proyectos (creación, actualización, eliminación).
         */
        private String projectEvents;

        /**
         * Cola para eventos de categorización de proyectos.
         */
        private String categorizationEvents;
    }

    /**
     * Nombres de los exchanges utilizados por el servicio.
     */
    @Data
    public static class Exchanges {
        /**
         * Exchange para eventos de proyectos.
         */
        private String projectEvents;

        /**
         * Exchange para eventos de categorización.
         */
        private String categorizationEvents;
    }

    /**
     * Routing keys para el enrutamiento de mensajes.
     */
    @Data
    public static class RoutingKeys {
        /**
         * Routing key para eventos de proyectos.
         */
        private String projectEvents;

        /**
         * Routing key para eventos de categorización.
         */
        private String categorizationEvents;
    }
}

