package com.udeajobs.projects_cell.categorization_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el servicio de categorias.
 * Este servicio publica eventos cuando se crean, actualizan o eliminan categorias.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.category-events-exchange}")
    private String categoryEventsExchange;

    /**
     * Define el exchange de tipo Topic donde se publicarán los eventos.
     * Los consumidores se suscribirán a este exchange con diferentes routing keys.
     */
    @Bean
    public TopicExchange categoryEventsExchange() {
        return new TopicExchange(categoryEventsExchange, true, false);
    }

    /**
     * Administrador de RabbitMQ para crear exchanges, colas y bindings automáticamente.
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Inicializa RabbitMQ creando los recursos definidos al arrancar la aplicación.
     */
    @Bean
    public ApplicationRunner initializeRabbitMQ(RabbitAdmin rabbitAdmin) {
        return args -> {
            rabbitAdmin.initialize();
        };
    }

    /**
     * Convertidor de mensajes JSON para serialización/deserialización automática.
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Template de RabbitMQ configurado con el convertidor JSON.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
