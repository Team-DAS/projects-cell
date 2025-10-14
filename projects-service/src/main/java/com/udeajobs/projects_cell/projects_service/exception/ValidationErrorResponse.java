package com.udeajobs.projects_cell.projects_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * Clase que representa una respuesta de error de validación para la API.
 * Extiende ErrorResponse para incluir información específica sobre errores de validación de campos.
 */
@Data
@Builder
public class ValidationErrorResponse {

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

    /**
     * Mapa de errores de validación por campo.
     * La clave es el nombre del campo y el valor es el mensaje de error.
     */
    private Map<String, String> fieldErrors;
}
