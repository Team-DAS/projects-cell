package com.udeajobs.projects_cell.searching_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udeajobs.projects_cell.searching_service.dto.event.CategorizationEventDTO;
import com.udeajobs.projects_cell.searching_service.service.ProjectIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer de RabbitMQ para eventos de categorización de proyectos.
 * <p>
 * Escucha eventos de categorización y enriquece el índice de Elasticsearch
 * con información de categorías y tags.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategorizationEventConsumer {

    private final ProjectIndexingService indexingService;
    private final ObjectMapper objectMapper;

    /**
     * Consume eventos de categorización desde RabbitMQ.
     * <p>
     * Este método es invocado automáticamente cuando llega un mensaje
     * a la cola configurada.
     * </p>
     *
     * @param message mensaje JSON con el evento de categorización
     */
    @RabbitListener(queues = "${app.rabbitmq.queues.categorization-events}")
    public void consumeCategorizationEvent(String message) {
        log.info("Mensaje recibido de la cola de eventos de categorización");
        log.debug("Contenido del mensaje: {}", message);

        try {
            CategorizationEventDTO eventDTO = objectMapper.readValue(message, CategorizationEventDTO.class);
            log.info("Evento de categorización deserializado correctamente: projectId={}, mainCategory={}",
                    eventDTO.getProjectId(), eventDTO.getMainCategory());

            indexingService.handleCategorizationEvent(eventDTO);

            log.info("Evento de categorización procesado exitosamente: {}", eventDTO.getProjectId());

        } catch (Exception e) {
            log.error("Error al procesar mensaje de evento de categorización: {}", message, e);
            // En un entorno de producción, aquí podrías:
            // - Enviar a una cola de dead letter
            // - Reintentar con backoff exponencial
            // - Alertar al equipo de operaciones
            throw new RuntimeException("Error al procesar evento de categorización", e);
        }
    }
}

