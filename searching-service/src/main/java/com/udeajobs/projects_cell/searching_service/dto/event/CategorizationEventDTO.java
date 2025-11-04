package com.udeajobs.projects_cell.searching_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO para eventos de categorización de proyectos recibidos desde RabbitMQ.
 * <p>
 * Este DTO representa los eventos de categorización que son publicados
 * por el servicio de categorización, enriqueciendo el índice de búsqueda.
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
public class CategorizationEventDTO {

    /**
     * Identificador del proyecto categorizado.
     */
    private UUID projectId;

    /**
     * Categoría principal asignada al proyecto.
     */
    private String mainCategory;

    /**
     * Etiquetas adicionales asignadas al proyecto.
     */
    private List<String> tags;
}

