package com.udeajobs.projects_cell.searching_service.controller;

import com.udeajobs.projects_cell.searching_service.dto.request.ProjectSearchInput;
import com.udeajobs.projects_cell.searching_service.dto.response.ProjectResponse;
import com.udeajobs.projects_cell.searching_service.dto.response.ProjectSearchResponse;
import com.udeajobs.projects_cell.searching_service.service.ProjectSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * Controlador GraphQL para operaciones de búsqueda de proyectos.
 * <p>
 * Expone queries de GraphQL para buscar proyectos en el índice de Elasticsearch.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ProjectSearchController {

    private final ProjectSearchService searchService;

    /**
     * Query de GraphQL para buscar proyectos con múltiples filtros.
     * <p>
     * Ejemplo de uso:
     * <pre>
     * query {
     *   searchProjects(input: {
     *     searchTerm: "Java Developer"
     *     requiredSkills: ["Java", "Spring Boot"]
     *     isRemote: true
     *     page: 0
     *     size: 10
     *   }) {
     *     projects {
     *       projectId
     *       title
     *       description
     *       requiredSkills
     *     }
     *     totalElements
     *     totalPages
     *   }
     * }
     * </pre>
     *
     * @param input criterios de búsqueda con validaciones
     * @return respuesta paginada con proyectos encontrados
     */
    @QueryMapping
    public ProjectSearchResponse searchProjects(@Argument @Valid ProjectSearchInput input) {
        log.info("Ejecutando query searchProjects con input: {}", input);
        return searchService.searchProjects(input);
    }

    /**
     * Query de GraphQL para buscar un proyecto específico por su ID.
     * <p>
     * Ejemplo de uso:
     * <pre>
     * query {
     *   findProjectById(projectId: "550e8400-e29b-41d4-a716-446655440000") {
     *     projectId
     *     title
     *     description
     *     status
     *     minSalary
     *     maxSalary
     *     currency
     *     location
     *     isRemote
     *     requiredSkills
     *     mainCategory
     *     tags
     *   }
     * }
     * </pre>
     *
     * @param projectId UUID del proyecto a buscar
     * @return proyecto encontrado
     */
    @QueryMapping
    public ProjectResponse findProjectById(@Argument UUID projectId) {
        log.info("Ejecutando query findProjectById para projectId: {}", projectId);
        return searchService.findProjectById(projectId);
    }
}

