package com.udeajobs.projects_cell.categorization_service.events.dto;

import com.udeajobs.projects_cell.categorization_service.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para eventos de categorías que se publican en RabbitMQ.
 * Este es el contrato del mensaje que otros servicios recibirán.
 *
 * @author UdeaJobs Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Tipo de evento: CREATED, UPDATED, DELETED
     */
    private String eventType;

    /**
     * Identificador único de la categoría.
     */
    private UUID categoryId;

    /**
     * Nombre de la categoría.
     */
    private String name;

    /**
     * Descripción de la categoría.
     */
    private String description;

    /**
     * Tipo de categoría (SKILL, TAG, JOB_LEVEL, INDUSTRY).
     */
    private CategoryType type;

    /**
     * Fecha y hora de creación.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de última actualización.
     */
    private LocalDateTime updatedAt;
}
