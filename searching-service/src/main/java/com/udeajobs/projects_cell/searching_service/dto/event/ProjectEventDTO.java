package com.udeajobs.projects_cell.searching_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para eventos de proyecto recibidos desde RabbitMQ.
 * <p>
 * Este DTO representa los eventos de creación, actualización o eliminación
 * de proyectos que son publicados por el servicio de proyectos.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEventDTO {

    /**
     * Tipo de evento: CREATED, UPDATED, DELETED.
     */
    private String eventType;

    /**
     * Identificador único del proyecto.
     */
    private UUID projectId;

    /**
     * Identificador del empleador.
     */
    private UUID employerId;

    /**
     * Título del proyecto.
     */
    private String title;

    /**
     * Descripción del proyecto.
     */
    private String description;

    /**
     * Estado del proyecto.
     */
    private String status;

    /**
     * Salario mínimo ofrecido.
     */
    private Double minSalary;

    /**
     * Salario máximo ofrecido.
     */
    private Double maxSalary;

    /**
     * Moneda del salario.
     */
    private String currency;

    /**
     * Ubicación del proyecto.
     */
    private String location;

    /**
     * Indica si el proyecto es remoto.
     */
    private Boolean isRemote;

    /**
     * Habilidades requeridas.
     */
    private List<String> requiredSkills;

    /**
     * Nivel del trabajo.
     */
    private String jobLevel;

    /**
     * Fecha de creación.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha de actualización.
     */
    private LocalDateTime updatedAt;
}

