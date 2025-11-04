package com.udeajobs.projects_cell.searching_service.exception;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra un proyecto en el índice de Elasticsearch.
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
public class ProjectNotFoundException extends RuntimeException {

    /**
     * Constructor con ID de proyecto.
     *
     * @param projectId ID del proyecto no encontrado
     */
    public ProjectNotFoundException(UUID projectId) {
        super(String.format("Proyecto con ID '%s' no encontrado en el índice de búsqueda", projectId));
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message mensaje de error
     */
    public ProjectNotFoundException(String message) {
        super(message);
    }
}

