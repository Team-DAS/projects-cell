package com.udeajobs.projects_cell.projects_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.udeajobs.projects_cell.projects_service.entity.Project;
import com.udeajobs.projects_cell.projects_service.events.dto.ProjectEventDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para publicar eventos de proyecto en RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${app.rabbitmq.project-events-exchange}")
    private String exchange;
    
    @Value("${app.rabbitmq.routing-key.created}")
    private String routingKeyCreated;
    
    @Value("${app.rabbitmq.routing-key.updated}")
    private String routingKeyUpdated;
    
    @Value("${app.rabbitmq.routing-key.deleted}")
    private String routingKeyDeleted;
    
    /**
     * Publica un evento de proyecto creado.
     */
    public void publishProjectCreated(Project project) {
        try {
            ProjectEventDTO event = buildEventDTO(project, "CREATED");
            rabbitTemplate.convertAndSend(exchange, routingKeyCreated, event);
            log.info("Evento CREATED publicado para proyecto {}", project.getProjectId());
        } catch (Exception e) {
            log.error("Error publicando evento CREATED para proyecto {}: {}", 
                     project.getProjectId(), e.getMessage(), e);
        }
    }
    
    /**
     * Publica un evento de proyecto actualizado.
     */
    public void publishProjectUpdated(Project project) {
        try {
            ProjectEventDTO event = buildEventDTO(project, "UPDATED");
            rabbitTemplate.convertAndSend(exchange, routingKeyUpdated, event);
            log.info("Evento UPDATED publicado para proyecto {}", project.getProjectId());
        } catch (Exception e) {
            log.error("Error publicando evento UPDATED para proyecto {}: {}", 
                     project.getProjectId(), e.getMessage(), e);
        }
    }
    
    /**
     * Publica un evento de proyecto eliminado.
     */
    public void publishProjectDeleted(Project project) {
        try {
            ProjectEventDTO event = buildEventDTO(project, "DELETED");
            rabbitTemplate.convertAndSend(exchange, routingKeyDeleted, event);
            log.info("Evento DELETED publicado para proyecto {}", project.getProjectId());
        } catch (Exception e) {
            log.error("Error publicando evento DELETED para proyecto {}: {}", 
                     project.getProjectId(), e.getMessage(), e);
        }
    }
    
    /**
     * Construye el DTO de evento a partir de una entidad Project.
     */
    private ProjectEventDTO buildEventDTO(Project project, String eventType) {
        return ProjectEventDTO.builder()
                .eventType(eventType)
                .projectId(project.getProjectId())
                .employerId(project.getEmployerId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus() != null ? project.getStatus().name() : null)
                .minSalary(project.getMinSalary() != null ? project.getMinSalary().doubleValue() : null)
                .maxSalary(project.getMaxSalary() != null ? project.getMaxSalary().doubleValue() : null)
                .currency(project.getCurrency())
                .location(project.getLocation())
                .isRemote(project.getIsRemote())
                .requiredSkills(project.getRequiredSkills())
                .jobLevel(project.getJobLevel())
                .build();
    }
}
