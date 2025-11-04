package com.udeajobs.projects_cell.searching_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udeajobs.projects_cell.searching_service.dto.event.ProjectEventDTO;
import com.udeajobs.projects_cell.searching_service.service.ProjectIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer de RabbitMQ para eventos de proyectos.
 * <p>
 * Escucha eventos de creación, actualización y eliminación de proyectos
 * y los procesa para mantener sincronizado el índice de Elasticsearch.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventConsumer {

    private final ProjectIndexingService indexingService;
    private final ObjectMapper objectMapper;

    /**
     * Consume eventos de proyectos desde RabbitMQ.
     * <p>
     * Este método es invocado automáticamente cuando llega un mensaje
     * a la cola configurada.
     * </p>
     *
     * @param message mensaje JSON con el evento del proyecto
     */
    @RabbitListener(queues = "${app.rabbitmq.queues.project-events}")
    public void consumeProjectEvent(String message) {
        log.info("Mensaje recibido de la cola de eventos de proyectos");
        log.debug("Contenido del mensaje: {}", message);

        try {
            ProjectEventDTO eventDTO = objectMapper.readValue(message, ProjectEventDTO.class);
            log.info("Evento deserializado correctamente: tipo={}, projectId={}",
                    eventDTO.getEventType(), eventDTO.getProjectId());

            indexingService.handleProjectEvent(eventDTO);

            log.info("Evento de proyecto procesado exitosamente: {}", eventDTO.getProjectId());

        } catch (Exception e) {
            log.error("Error al procesar mensaje de evento de proyecto: {}", message, e);
            // En un entorno de producción, aquí podrías:
            // - Enviar a una cola de dead letter
            // - Reintentar con backoff exponencial
            // - Alertar al equipo de operaciones
            throw new RuntimeException("Error al procesar evento de proyecto", e);
        }
    }
}

