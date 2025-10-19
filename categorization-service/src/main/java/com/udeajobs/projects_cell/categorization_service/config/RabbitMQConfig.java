package com.udeajobs.projects_cell.categorization_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- Valores obtenidos desde application.properties ---
    @Value("${app.rabbitmq.project-events-exchange}")
    private String projectEventsExchange;

    @Value("${app.rabbitmq.routing-key.created}")
    private String routingKeyCreated;

    @Value("${app.rabbitmq.routing-key.updated}")
    private String routingKeyUpdated;

    @Value("${app.rabbitmq.routing-key.deleted}")
    private String routingKeyDeleted;

    // --- Cola específica de este servicio ---
    private static final String CATEGORY_QUEUE = "udea.categorization_service.queue";

    @Bean
    public Queue categoryQueue() {
        return new Queue(CATEGORY_QUEUE, true);
    }

    @Bean
    public TopicExchange projectEventsExchange() {
        return new TopicExchange(projectEventsExchange);
    }

    @Bean
    public Binding bindingProjectCreated(Queue categoryQueue, TopicExchange projectEventsExchange) {
        return BindingBuilder.bind(categoryQueue)
                .to(projectEventsExchange)
                .with(routingKeyCreated);
    }

    @Bean
    public Binding bindingProjectUpdated(Queue categoryQueue, TopicExchange projectEventsExchange) {
        return BindingBuilder.bind(categoryQueue)
                .to(projectEventsExchange)
                .with(routingKeyUpdated);
    }

    @Bean
    public Binding bindingProjectDeleted(Queue categoryQueue, TopicExchange projectEventsExchange) {
        return BindingBuilder.bind(categoryQueue)
                .to(projectEventsExchange)
                .with(routingKeyDeleted);
    }

    // --- Administración y conversión ---
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationRunner runner(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}