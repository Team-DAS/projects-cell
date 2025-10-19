package com.udeajobs.projects_cell.categorization_service.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 * Generalmente resulta en una respuesta HTTP 404 Not Found.
 *
 * @author UdeaJobs Team
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje de error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

