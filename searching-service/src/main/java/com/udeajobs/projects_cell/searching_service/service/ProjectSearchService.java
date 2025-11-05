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
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

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
            Query query = buildSearchQuery(input);
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

        ProjectDocument project = projectRepository.findByProjectId(projectId.toString())
                .orElseThrow(() -> {
                    meterRegistry.counter("projects.not_found").increment();
                    return new ProjectNotFoundException(projectId);
                });

        return mapToResponse(project);
    }

    /**
     * Construye la query de búsqueda basada en el input.
     *
     * @param input criterios de búsqueda
     * @return Query para Elasticsearch
     */
    private Query buildSearchQuery(ProjectSearchInput input) {
        Pageable pageable = buildPageable(input);

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

        // Búsqueda por término general con multi_match
        if (input.getSearchTerm() != null && !input.getSearchTerm().isBlank()) {
            boolQueryBuilder.must(m -> m.multiMatch(mm -> mm
                    .query(input.getSearchTerm())
                    .fields("title^3", "description^2", "tags", "mainCategory")
                    .fuzziness("AUTO")
            ));
        }

        // Filtro por habilidades requeridas
        if (input.getRequiredSkills() != null && !input.getRequiredSkills().isEmpty()) {
            for (String skill : input.getRequiredSkills()) {
                boolQueryBuilder.filter(f -> f.term(t -> t
                        .field("requiredSkills")
                        .value(skill)
                ));
            }
        }

        // Filtro por ubicación
        if (input.getLocation() != null && !input.getLocation().isBlank()) {
            boolQueryBuilder.filter(f -> f.match(m -> m
                    .field("location")
                    .query(input.getLocation())
            ));
        }

        // Filtro por remoto
        if (input.getIsRemote() != null) {
            boolQueryBuilder.filter(f -> f.term(t -> t
                    .field("isRemote")
                    .value(input.getIsRemote())
            ));
        }

        // Filtro por rango salarial
        if (input.getMinSalary() != null) {
            boolQueryBuilder.filter(f -> f.range(r -> r
                    .number(n -> n
                            .field("maxSalary")
                            .gte(input.getMinSalary())
                    )
            ));
        }
        if (input.getMaxSalary() != null) {
            boolQueryBuilder.filter(f -> f.range(r -> r
                    .number(n -> n
                            .field("minSalary")
                            .lte(input.getMaxSalary())
                    )
            ));
        }

        // Filtro por moneda
        if (input.getCurrency() != null && !input.getCurrency().isBlank()) {
            boolQueryBuilder.filter(f -> f.term(t -> t
                    .field("currency")
                    .value(input.getCurrency())
            ));
        }

        // Filtro por nivel de trabajo
        if (input.getJobLevel() != null && !input.getJobLevel().isBlank()) {
            boolQueryBuilder.filter(f -> f.term(t -> t
                    .field("jobLevel")
                    .value(input.getJobLevel())
            ));
        }

        // Filtro por estado
        if (input.getStatus() != null && !input.getStatus().isBlank()) {
            boolQueryBuilder.filter(f -> f.term(t -> t
                    .field("status")
                    .value(input.getStatus())
            ));
        }

        // Filtro por categoría principal
        if (input.getMainCategory() != null && !input.getMainCategory().isBlank()) {
            boolQueryBuilder.filter(f -> f.term(t -> t
                    .field("mainCategory")
                    .value(input.getMainCategory())
            ));
        }

        // Filtro por tags
        if (input.getTags() != null && !input.getTags().isEmpty()) {
            for (String tag : input.getTags()) {
                boolQueryBuilder.filter(f -> f.term(t -> t
                        .field("tags")
                        .value(tag)
                ));
            }
        }

        return NativeQuery.builder()
                .withQuery(q -> q.bool(boolQueryBuilder.build()))
                .withPageable(pageable)
                .build();
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
            sort = Sort.by(Sort.Direction.DESC, "indexedAt");
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
                .projectId(UUID.fromString(document.getProjectId()))
                .employerId(UUID.fromString(document.getEmployerId()))
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
                .build();
    }
}

