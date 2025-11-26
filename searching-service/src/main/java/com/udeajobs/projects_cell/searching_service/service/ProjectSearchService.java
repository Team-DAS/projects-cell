package com.udeajobs.projects_cell.searching_service.service;

import com.udeajobs.projects_cell.searching_service.dto.request.ProjectSearchInput;
import com.udeajobs.projects_cell.searching_service.dto.response.ProjectResponse;
import com.udeajobs.projects_cell.searching_service.dto.response.ProjectSearchResponse;
import com.udeajobs.projects_cell.searching_service.entity.ProjectDocument;
import com.udeajobs.projects_cell.searching_service.repository.ProjectRepository;
import com.udeajobs.projects_cell.searching_service.repository.ProjectSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para realizar búsquedas de proyectos en OpenSearch.
 * <p>
 * Proporciona métodos para búsquedas complejas con múltiples filtros
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectSearchService {

    private final ProjectSearchRepository searchRepository;
    private final ProjectRepository projectRepository;

    public ProjectSearchResponse searchProjects(ProjectSearchInput input) {
        try {
            var response = searchRepository.search(input);

            List<ProjectResponse> projects = response.hits().hits().stream()
                    .map(hit -> mapToResponse(hit.source()))
                    .collect(Collectors.toList());

            long total = response.hits().total().value();
            int totalPages = (int) Math.ceil((double) total / input.getSize());

            return ProjectSearchResponse.builder()
                    .projects(projects)
                    .currentPage(input.getPage())
                    .pageSize(input.getSize())
                    .totalElements(total)
                    .totalPages(totalPages)
                    .hasNext(input.getPage() < totalPages - 1)
                    .hasPrevious(input.getPage() > 0)
                    .build();

        } catch (IOException e) {
            log.error("Error ejecutando búsqueda", e);
            throw new RuntimeException("Error ejecutando búsqueda", e);
        }
    }

    public ProjectResponse findProjectById(UUID projectId) {
        try {
            var result = projectRepository.findByProjectId(projectId.toString());

            return result
                    .map(this::mapToResponse)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + projectId));

        } catch (IOException e) {
            log.error("Error consultando proyecto {}", projectId, e);
            throw new RuntimeException("Error consultando proyecto " + projectId, e);
        }
    }

    private ProjectResponse mapToResponse(ProjectDocument d) {
        return ProjectResponse.builder()
                .projectId(UUID.fromString(d.getProjectId()))
                .employerId(UUID.fromString(d.getEmployerId()))
                .title(d.getTitle())
                .description(d.getDescription())
                .status(d.getStatus())
                .minSalary(d.getMinSalary())
                .maxSalary(d.getMaxSalary())
                .currency(d.getCurrency())
                .location(d.getLocation())
                .isRemote(d.getIsRemote())
                .requiredSkills(d.getRequiredSkills())
                .jobLevel(d.getJobLevel())
                .mainCategory(d.getMainCategory())
                .tags(d.getTags())
                .build();
    }
}


