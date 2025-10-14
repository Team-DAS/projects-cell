package com.udeajobs.projects_cell.projects_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.udeajobs.projects_cell.projects_service.enums.ProjectStatus;
import lombok.Data;

/**
 * DTO para las respuestas de la API de proyectos.
 * Contiene toda la información del proyecto que se mostrará al cliente.
 *
 * @param projectId Identificador único público del proyecto (UUID)
 * @param employerId Identificador del empleador que creó el proyecto
 */
@Data
public class ProjectResponseDTO {

    /**
     * Identificador único público del proyecto (UUID).
     */
    private UUID projectId;

    /**
     * Identificador del empleador que creó el proyecto.
     */
    private UUID employerId;

    /**
     * Título del proyecto.
     */
    private String title;

    /**
     * Descripción detallada del proyecto.
     */
    private String description;

    /**
     * Estado actual del proyecto.
     */
    private ProjectStatus status;

    /**
     * Salario mínimo ofrecido para el proyecto.
     */
    private BigDecimal minSalary;

    /**
     * Salario máximo ofrecido para el proyecto.
     */
    private BigDecimal maxSalary;

    /**
     * Moneda en la que se expresan los salarios.
     */
    private String currency;

    /**
     * Ubicación física donde se desarrollará el proyecto.
     */
    private String location;

    /**
     * Indica si el proyecto puede realizarse de forma remota.
     */
    private Boolean isRemote;

    /**
     * Lista de habilidades requeridas para el proyecto.
     */
    private List<String> requiredSkills;

    /**
     * Nivel de experiencia requerido para el proyecto.
     */
    private String jobLevel;

    /**
     * Fecha y hora de creación del proyecto.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de última actualización del proyecto.
     */
    private LocalDateTime updatedAt;
}
