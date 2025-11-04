package com.udeajobs.projects_cell.searching_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta paginada para búsquedas de proyectos.
 * <p>
 * Contiene los resultados de búsqueda junto con metadatos de paginación.
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
public class ProjectSearchResponse {

    /**
     * Lista de proyectos encontrados.
     */
    private List<ProjectResponse> projects;

    /**
     * Número de página actual.
     */
    private Integer currentPage;

    /**
     * Tamaño de la página.
     */
    private Integer pageSize;

    /**
     * Total de elementos encontrados.
     */
    private Long totalElements;

    /**
     * Total de páginas disponibles.
     */
    private Integer totalPages;

    /**
     * Indica si hay una página siguiente.
     */
    private Boolean hasNext;

    /**
     * Indica si hay una página anterior.
     */
    private Boolean hasPrevious;
}

