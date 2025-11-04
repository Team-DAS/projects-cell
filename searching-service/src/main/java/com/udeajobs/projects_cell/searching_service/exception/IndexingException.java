package com.udeajobs.projects_cell.searching_service.exception;

/**
 * Excepción lanzada cuando ocurre un error al indexar un documento en Elasticsearch.
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
public class IndexingException extends RuntimeException {

    /**
     * Constructor con mensaje y causa.
     *
     * @param message mensaje de error
     * @param cause causa del error
     */
    public IndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor con mensaje.
     *
     * @param message mensaje de error
     */
    public IndexingException(String message) {
        super(message);
    }
}

