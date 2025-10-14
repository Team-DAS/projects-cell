package com.udeajobs.projects_cell.projects_service.exception;

/**
 * Excepción personalizada que se lanza cuando un recurso solicitado no se encuentra.
 * Extiende RuntimeException para ser una excepción no verificada.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor que acepta un mensaje de error.
     *
     * @param message el mensaje descriptivo del error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor que acepta un mensaje de error y una causa.
     *
     * @param message el mensaje descriptivo del error
     * @param cause la causa raíz de la excepción
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
