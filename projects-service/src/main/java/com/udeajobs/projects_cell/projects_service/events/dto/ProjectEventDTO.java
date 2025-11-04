package com.udeajobs.projects_cell.projects_service.events.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para eventos de proyecto que se publican en RabbitMQ.
 * Este DTO será consumido por otros microservicios (categorization-service, searching-service).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEventDTO {
    
    private String eventType;  // CREATED, UPDATED, DELETED
    private UUID projectId;
    private UUID employerId;
    private String title;
    private String description;
    private String status;
    private Double minSalary;
    private Double maxSalary;
    private String currency;
    private String location;
    private Boolean isRemote;
    private List<String> requiredSkills;
    private String jobLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
