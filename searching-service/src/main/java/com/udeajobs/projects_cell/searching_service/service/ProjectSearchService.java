package com.udeajobs.projects_cell.searching_service.service;

import com.udeajobs.projects_cell.searching_service.dto.request.ProjectSearchInput;
import com.udeajobs.projects_cell.searching_service.dto.response.ProjectResponse;
import com.udeajobs.projects_cell.searching_service.dto.response.ProjectSearchResponse;
import com.udeajobs.projects_cell.searching_service.entity.ProjectDocument;
import com.udeajobs.projects_cell.searching_service.exception.InvalidSearchParametersException;
import com.udeajobs.projects_cell.searching_service.exception.ProjectNotFoundException;
import com.udeajobs.projects_cell.searching_service.repository.ProjectRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para realizar búsquedas de proyectos en Elasticsearch.
 * <p>
 * Proporciona métodos para búsquedas complejas con múltiples filtros
 * y paginación. Incluye métricas de Prometheus.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectSearchService {

    private final ProjectRepository projectRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final MeterRegistry meterRegistry;

    /**
     * Busca proyectos según los criterios especificados en el input.
     *
     * @param input criterios de búsqueda
     * @return respuesta paginada con proyectos encontrados
     * @throws InvalidSearchParametersException si los parámetros son inválidos
     */
    public ProjectSearchResponse searchProjects(ProjectSearchInput input) {
        log.info("Iniciando búsqueda de proyectos con criterios: {}", input);

        validateSearchInput(input);

        Counter searchCounter = meterRegistry.counter("projects.search.requests", "type", "general");
        searchCounter.increment();

        try {
            Criteria criteria = buildSearchCriteria(input);
            Pageable pageable = buildPageable(input);

            CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(query, ProjectDocument.class);

            List<ProjectResponse> projects = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            long totalHits = searchHits.getTotalHits();
            int totalPages = (int) Math.ceil((double) totalHits / input.getSize());

            log.info("Búsqueda completada. Encontrados {} proyectos", totalHits);

            return ProjectSearchResponse.builder()
                    .projects(projects)
                    .currentPage(input.getPage())
                    .pageSize(input.getSize())
                    .totalElements(totalHits)
                    .totalPages(totalPages)
                    .hasNext(input.getPage() < totalPages - 1)
                    .hasPrevious(input.getPage() > 0)
                    .build();

        } catch (Exception e) {
            meterRegistry.counter("projects.search.errors", "type", "search_execution").increment();
            log.error("Error al ejecutar búsqueda de proyectos", e);
            throw new InvalidSearchParametersException("Error al ejecutar la búsqueda: " + e.getMessage());
        }
    }

    /**
     * Busca un proyecto por su ID.
     *
     * @param projectId UUID del proyecto
     * @return respuesta con el proyecto encontrado
     * @throws ProjectNotFoundException si el proyecto no existe
     */
    public ProjectResponse findProjectById(UUID projectId) {
        log.info("Buscando proyecto con ID: {}", projectId);

        Counter findByIdCounter = meterRegistry.counter("projects.search.by_id");
        findByIdCounter.increment();

        ProjectDocument project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> {
                    meterRegistry.counter("projects.not_found").increment();
                    return new ProjectNotFoundException(projectId);
                });

        return mapToResponse(project);
    }

    /**
     * Construye los criterios de búsqueda basados en el input.
     *
     * @param input criterios de búsqueda
     * @return objeto Criteria para Elasticsearch
     */
    private Criteria buildSearchCriteria(ProjectSearchInput input) {
        Criteria criteria = new Criteria();

        // Búsqueda por término general (título y descripción)
        if (input.getSearchTerm() != null && !input.getSearchTerm().isBlank()) {
            Criteria titleCriteria = Criteria.where("title").contains(input.getSearchTerm());
            Criteria descriptionCriteria = Criteria.where("description").contains(input.getSearchTerm());
            criteria = criteria.subCriteria(titleCriteria.or(descriptionCriteria));
        }

        // Filtro por habilidades requeridas
        if (input.getRequiredSkills() != null && !input.getRequiredSkills().isEmpty()) {
            criteria = criteria.and(Criteria.where("requiredSkills").in(input.getRequiredSkills()));
        }

        // Filtro por ubicación
        if (input.getLocation() != null && !input.getLocation().isBlank()) {
            criteria = criteria.and(Criteria.where("location").contains(input.getLocation()));
        }

        // Filtro por remoto
        if (input.getIsRemote() != null) {
            criteria = criteria.and(Criteria.where("isRemote").is(input.getIsRemote()));
        }

        // Filtro por rango salarial
        if (input.getMinSalary() != null) {
            criteria = criteria.and(Criteria.where("maxSalary").greaterThanEqual(input.getMinSalary()));
        }
        if (input.getMaxSalary() != null) {
            criteria = criteria.and(Criteria.where("minSalary").lessThanEqual(input.getMaxSalary()));
        }

        // Filtro por moneda
        if (input.getCurrency() != null && !input.getCurrency().isBlank()) {
            criteria = criteria.and(Criteria.where("currency").is(input.getCurrency()));
        }

        // Filtro por nivel de trabajo
        if (input.getJobLevel() != null && !input.getJobLevel().isBlank()) {
            criteria = criteria.and(Criteria.where("jobLevel").is(input.getJobLevel()));
        }

        // Filtro por estado
        if (input.getStatus() != null && !input.getStatus().isBlank()) {
            criteria = criteria.and(Criteria.where("status").is(input.getStatus()));
        }

        // Filtro por categoría principal
        if (input.getMainCategory() != null && !input.getMainCategory().isBlank()) {
            criteria = criteria.and(Criteria.where("mainCategory").is(input.getMainCategory()));
        }

        // Filtro por tags
        if (input.getTags() != null && !input.getTags().isEmpty()) {
            criteria = criteria.and(Criteria.where("tags").in(input.getTags()));
        }

        return criteria;
    }

    /**
     * Construye el objeto Pageable para la paginación y ordenamiento.
     *
     * @param input criterios de búsqueda con información de paginación
     * @return objeto Pageable
     */
    private Pageable buildPageable(ProjectSearchInput input) {
        Sort sort;

        if (input.getSortBy() != null && !input.getSortBy().isBlank()) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(input.getSortDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, input.getSortBy());
        } else {
            // Ordenamiento por defecto: más recientes primero
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return PageRequest.of(input.getPage(), input.getSize(), sort);
    }

    /**
     * Valida los parámetros de búsqueda.
     *
     * @param input criterios de búsqueda
     * @throws InvalidSearchParametersException si los parámetros no son válidos
     */
    private void validateSearchInput(ProjectSearchInput input) {
        if (input.getMinSalary() != null && input.getMaxSalary() != null
                && input.getMinSalary() > input.getMaxSalary()) {
            throw new InvalidSearchParametersException(
                    "El salario mínimo no puede ser mayor que el salario máximo");
        }

        if (input.getPage() < 0) {
            throw new InvalidSearchParametersException("El número de página debe ser mayor o igual a 0");
        }

        if (input.getSize() <= 0) {
            throw new InvalidSearchParametersException("El tamaño de página debe ser mayor a 0");
        }

        if (input.getSize() > 100) {
            throw new InvalidSearchParametersException("El tamaño de página no puede exceder 100 elementos");
        }
    }

    /**
     * Mapea un documento de Elasticsearch a un DTO de respuesta.
     *
     * @param document documento de Elasticsearch
     * @return DTO de respuesta
     */
    private ProjectResponse mapToResponse(ProjectDocument document) {
        return ProjectResponse.builder()
                .projectId(document.getProjectId())
                .employerId(document.getEmployerId())
                .title(document.getTitle())
                .description(document.getDescription())
                .status(document.getStatus())
                .minSalary(document.getMinSalary())
                .maxSalary(document.getMaxSalary())
                .currency(document.getCurrency())
                .location(document.getLocation())
                .isRemote(document.getIsRemote())
                .requiredSkills(document.getRequiredSkills())
                .jobLevel(document.getJobLevel())
                .mainCategory(document.getMainCategory())
                .tags(document.getTags())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

