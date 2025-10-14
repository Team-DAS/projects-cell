package com.udeajobs.projects_cell.projects_service.enums;

/**
 * Enum que define los posibles estados de un proyecto.
 */
public enum ProjectStatus {
    /**
     * Proyecto abierto, esperando propuestas de freelancers.
     */
    OPEN,

    /**
     * Proyecto en progreso, asignado a un freelancer.
     */
    IN_PROGRESS,

    /**
     * Proyecto completado exitosamente.
     */
    COMPLETED,

    /**
     * Proyecto cancelado.
     */
    CANCELED
}
