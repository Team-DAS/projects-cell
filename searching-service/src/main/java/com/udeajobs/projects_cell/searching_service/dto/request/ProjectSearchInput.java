package com.udeajobs.projects_cell.searching_service.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de entrada para búsqueda de proyectos a través de GraphQL.
 * <p>
 * Permite filtrar proyectos por múltiples criterios y soporta paginación.
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
public class ProjectSearchInput {

    /**
     * Término de búsqueda general (busca en título y descripción).
     */
    private String searchTerm;

    /**
     * Lista de habilidades requeridas para filtrar.
     */
    private List<String> requiredSkills;

    /**
     * Ubicación para filtrar proyectos.
     */
    private String location;

    /**
     * Filtro para proyectos remotos.
     */
    private Boolean isRemote;

    /**
     * Salario mínimo para filtrar.
     */
    @Min(value = 0, message = "El salario mínimo debe ser mayor o igual a 0")
    private Double minSalary;

    /**
     * Salario máximo para filtrar.
     */
    @Min(value = 0, message = "El salario máximo debe ser mayor o igual a 0")
    private Double maxSalary;

    /**
     * Moneda del salario.
     */
    private String currency;

    /**
     * Nivel del trabajo.
     */
    private String jobLevel;

    /**
     * Estado del proyecto.
     */
    private String status;

    /**
     * Categoría principal del proyecto.
     */
    private String mainCategory;

    /**
     * Etiquetas para filtrar.
     */
    private List<String> tags;

    /**
     * Número de página (comienza en 0).
     */
    @Min(value = 0, message = "La página debe ser mayor o igual a 0")
    @Builder.Default
    private Integer page = 0;

    /**
     * Tamaño de la página.
     */
    @Min(value = 1, message = "El tamaño de la página debe ser mayor o igual a 1")
    @Builder.Default
    private Integer size = 10;

    /**
     * Campo por el cual ordenar los resultados.
     */
    private String sortBy;

    /**
     * Dirección del ordenamiento (ASC, DESC).
     */
    private String sortDirection;
}

