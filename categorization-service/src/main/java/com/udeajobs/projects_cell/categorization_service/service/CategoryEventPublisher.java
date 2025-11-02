package com.udeajobs.projects_cell.categorization_service.service;

import com.udeajobs.projects_cell.categorization_service.entity.Category;
import com.udeajobs.projects_cell.categorization_service.events.dto.CategoryEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio para publicar eventos de categorias en RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.category-events-exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key.created}")
    private String routingKeyCreated;

    @Value("${app.rabbitmq.routing-key.updated}")
    private String routingKeyUpdated;

    @Value("${app.rabbitmq.routing-key.deleted}")
    private String routingKeyDeleted;

    /**
     * Publica un evento de categoria creada.
     */
    public void publishCategoryCreated(Category category) {
        try {
            CategoryEventDTO event = buildEventDTO(category, "CREATED");
            rabbitTemplate.convertAndSend(exchange, routingKeyUpdated, event);
            log.info("Evento CREATED publicado para categoria {}", category.getCategoryId());
        } catch (Exception e) {
            log.error("Error publicando evento CREATED para categoria {}: {}",
                    category.getCategoryId(), e.getMessage(), e);
        }
    }

    /**
     * Publica un evento de categoria actualizada.
     */
    public void publishCategoryUpdated(Category category) {
        try {
            CategoryEventDTO event = buildEventDTO(category, "UPDATED");
            rabbitTemplate.convertAndSend(exchange, routingKeyUpdated, event);
            log.info("Evento UPDATED publicado para categoria {}", category.getCategoryId());
        } catch (Exception e) {
            log.error("Error publicando evento UPDATED para categoria {}: {}",
                    category.getCategoryId(), e.getMessage(), e);
        }
    }

    /**
     * Publica un evento de categoria eliminada.
     */
    public void publishCategoryDeleted(Category category) {
        try {
            CategoryEventDTO event = buildEventDTO(category, "DELETED");
            rabbitTemplate.convertAndSend(exchange, routingKeyUpdated, event);
            log.info("Evento DELETED publicado para categoria {}", category.getCategoryId());
        } catch (Exception e) {
            log.error("Error publicando evento DELETED para categoria {}: {}",
                    category.getCategoryId(), e.getMessage(), e);
        }
    }

    /**
     * Construye el DTO de evento a partir de una entidad Category.
     */
    private CategoryEventDTO buildEventDTO(Category category, String eventType) {
        return CategoryEventDTO.builder()
                .eventType(eventType)
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .type(category.getType())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }



}
