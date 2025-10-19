package com.udeajobs.projects_cell.categorization_service.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe.
 * Generalmente resulta en una respuesta HTTP 409 Conflict.
 *
 * @author UdeaJobs Team
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje de error
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

