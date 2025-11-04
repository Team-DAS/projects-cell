package com.udeajobs.projects_cell.searching_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta que representa un proyecto en los resultados de búsqueda.
 * <p>
 * Contiene toda la información relevante del proyecto para mostrar al usuario.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    /**
     * Identificador único del proyecto.
     */
    private UUID projectId;

    /**
     * Identificador del empleador.
     */
    private UUID employerId;

    /**
     * Título del proyecto.
     */
    private String title;

    /**
     * Descripción del proyecto.
     */
    private String description;

    /**
     * Estado del proyecto.
     */
    private String status;

    /**
     * Salario mínimo ofrecido.
     */
    private Double minSalary;

    /**
     * Salario máximo ofrecido.
     */
    private Double maxSalary;

    /**
     * Moneda del salario.
     */
    private String currency;

    /**
     * Ubicación del proyecto.
     */
    private String location;

    /**
     * Indica si el proyecto es remoto.
     */
    private Boolean isRemote;

    /**
     * Habilidades requeridas.
     */
    private List<String> requiredSkills;

    /**
     * Nivel del trabajo.
     */
    private String jobLevel;

    /**
     * Categoría principal del proyecto.
     */
    private String mainCategory;

    /**
     * Etiquetas del proyecto.
     */
    private List<String> tags;

    /**
     * Fecha de creación.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha de actualización.
     */
    private LocalDateTime updatedAt;
}

