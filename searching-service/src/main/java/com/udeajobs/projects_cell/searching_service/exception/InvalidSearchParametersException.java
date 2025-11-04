package com.udeajobs.projects_cell.searching_service.exception;

/**
 * Excepción lanzada cuando los parámetros de búsqueda no son válidos.
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
public class InvalidSearchParametersException extends RuntimeException {

    /**
     * Constructor con mensaje.
     *
     * @param message mensaje de error
     */
    public InvalidSearchParametersException(String message) {
        super(message);
    }
}

