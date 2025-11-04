package com.udeajobs.projects_cell.searching_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el servicio de búsqueda.
 * <p>
 * Define las colas, exchanges, bindings y conversores de mensajes necesarios
 * para consumir eventos de proyectos y categorización.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    /**
     * Conversor de mensajes JSON.
     * <p>
     * Utiliza Jackson para serializar/deserializar mensajes automáticamente.
     * Configurado para usar el tipo del parámetro del método en lugar del __TypeId__ header.
     * Esto permite recibir mensajes de otros servicios con DTOs en diferentes paquetes.
     * </p>
     *
     * @return conversor de mensajes
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // Siempre usar el tipo INFERIDO del parámetro del método listener, ignorando __TypeId__ del header
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }

    /**
     * RabbitTemplate configurado con el conversor JSON.
     *
     * @param connectionFactory factory de conexiones de RabbitMQ
     * @return template configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Factory para listeners de RabbitMQ con configuración personalizada.
     *
     * @param connectionFactory factory de conexiones
     * @param configurer configurador automático
     * @return factory configurado
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    // ==================== Configuración de Eventos de Proyectos ====================

    /**
     * Cola para eventos de proyectos.
     *
     * @return cola durable
     */
    @Bean
    public Queue projectEventsQueue() {
        return QueueBuilder.durable(properties.getQueues().getProjectEvents())
                .build();
    }

    /**
     * Exchange para eventos de proyectos.
     *
     * @return exchange de tipo topic
     */
    @Bean
    public TopicExchange projectEventsExchange() {
        return ExchangeBuilder.topicExchange(properties.getExchanges().getProjectEvents())
                .durable(true)
                .build();
    }

    /**
     * Binding entre la cola y el exchange de eventos de proyectos.
     *
     * @return binding configurado
     */
    @Bean
    public Binding projectEventsBinding() {
        return BindingBuilder
                .bind(projectEventsQueue())
                .to(projectEventsExchange())
                .with(properties.getRoutingKeys().getProjectEvents());
    }

    // ==================== Configuración de Eventos de Categorización ====================

    /**
     * Cola para eventos de categorización.
     *
     * @return cola durable
     */
    @Bean
    public Queue categorizationEventsQueue() {
        return QueueBuilder.durable(properties.getQueues().getCategorizationEvents())
                .build();
    }

    /**
     * Exchange para eventos de categorización.
     *
     * @return exchange de tipo topic
     */
    @Bean
    public TopicExchange categorizationEventsExchange() {
        return ExchangeBuilder.topicExchange(properties.getExchanges().getCategorizationEvents())
                .durable(true)
                .build();
    }

    /**
     * Binding entre la cola y el exchange de eventos de categorización.
     *
     * @return binding configurado
     */
    @Bean
    public Binding categorizationEventsBinding() {
        return BindingBuilder
                .bind(categorizationEventsQueue())
                .to(categorizationEventsExchange())
                .with(properties.getRoutingKeys().getCategorizationEvents());
    }
}

