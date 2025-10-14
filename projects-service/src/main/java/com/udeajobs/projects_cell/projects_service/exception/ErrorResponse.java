package com.udeajobs.projects_cell.projects_service.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * Clase que representa una respuesta de error estándar para la API.
 * Proporciona información detallada sobre errores que ocurren en la aplicación.
 */
@Data
@Builder
public class ErrorResponse {

    /**
     * Marca de tiempo cuando ocurrió el error.
     */
    private LocalDateTime timestamp;

    /**
     * Código de estado HTTP del error.
     */
    private int status;

    /**
     * Tipo de error.
     */
    private String error;

    /**
     * Mensaje descriptivo del error.
     */
    private String message;

    /**
     * Ruta donde ocurrió el error.
     */
    private String path;
}
