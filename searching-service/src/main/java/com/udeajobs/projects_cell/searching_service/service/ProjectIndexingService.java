package com.udeajobs.projects_cell.searching_service.service;

import com.udeajobs.projects_cell.searching_service.dto.event.CategorizationEventDTO;
import com.udeajobs.projects_cell.searching_service.dto.event.ProjectEventDTO;
import com.udeajobs.projects_cell.searching_service.entity.ProjectDocument;
import com.udeajobs.projects_cell.searching_service.exception.IndexingException;
import com.udeajobs.projects_cell.searching_service.repository.ProjectRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio para indexar proyectos en Elasticsearch.
 * <p>
 * Maneja la creación, actualización y eliminación de documentos en el índice,
 * así como el enriquecimiento con datos de categorización.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectIndexingService {

    private final ProjectRepository projectRepository;
    private final MeterRegistry meterRegistry;

    /**
     * Procesa un evento de proyecto (creación o actualización).
     *
     * @param eventDTO evento de proyecto
     * @throws IndexingException si ocurre un error al indexar
     */
    @Transactional
    public void handleProjectEvent(ProjectEventDTO eventDTO) {
        log.info("Procesando evento de proyecto: {} para projectId: {}",
                eventDTO.getEventType(), eventDTO.getProjectId());

        try {
            switch (eventDTO.getEventType().toUpperCase()) {
                case "CREATED" -> createProject(eventDTO);
                case "UPDATED" -> updateProject(eventDTO);
                case "DELETED" -> deleteProject(eventDTO.getProjectId());
                default -> {
                    log.warn("Tipo de evento desconocido: {}", eventDTO.getEventType());
                    meterRegistry.counter("projects.events.unknown", "type", eventDTO.getEventType()).increment();
                }
            }
        } catch (Exception e) {
            meterRegistry.counter("projects.indexing.errors", "operation", eventDTO.getEventType()).increment();
            log.error("Error al procesar evento de proyecto: {}", eventDTO.getProjectId(), e);
            throw new IndexingException(
                    String.format("Error al indexar proyecto %s", eventDTO.getProjectId()), e);
        }
    }

    /**
     * Procesa un evento de categorización de proyecto.
     *
     * @param eventDTO evento de categorización
     * @throws IndexingException si ocurre un error al actualizar
     */
    @Transactional
    public void handleCategorizationEvent(CategorizationEventDTO eventDTO) {
        log.info("Procesando evento de categorización para projectId: {}", eventDTO.getProjectId());

        try {
            Optional<ProjectDocument> existingProject = projectRepository.findByProjectId(eventDTO.getProjectId().toString());

            if (existingProject.isPresent()) {
                ProjectDocument project = existingProject.get();
                project.setMainCategory(eventDTO.getMainCategory());
                project.setTags(eventDTO.getTags());
                project.setIndexedAt(LocalDateTime.now());

                projectRepository.save(project);

                meterRegistry.counter("projects.categorization.success").increment();
                log.info("Proyecto {} enriquecido con categorización exitosamente", eventDTO.getProjectId());
            } else {
                log.warn("No se encontró el proyecto {} para enriquecer con categorización", eventDTO.getProjectId());
                meterRegistry.counter("projects.categorization.not_found").increment();
            }
        } catch (Exception e) {
            meterRegistry.counter("projects.categorization.errors").increment();
            log.error("Error al procesar evento de categorización: {}", eventDTO.getProjectId(), e);
            throw new IndexingException(
                    String.format("Error al enriquecer proyecto %s con categorización", eventDTO.getProjectId()), e);
        }
    }

    /**
     * Crea un nuevo documento de proyecto en el índice.
     *
     * @param eventDTO datos del proyecto
     */
    private void createProject(ProjectEventDTO eventDTO) throws IOException {
        ProjectDocument document = mapEventToDocument(eventDTO);
        document.setIndexedAt(LocalDateTime.now());

        projectRepository.save(document);

        meterRegistry.counter("projects.indexing.created").increment();
        log.info("Proyecto {} creado en el índice exitosamente", eventDTO.getProjectId());
    }

    /**
     * Actualiza un documento de proyecto existente en el índice.
     *
     * @param eventDTO datos actualizados del proyecto
     */
    private void updateProject(ProjectEventDTO eventDTO) throws IOException {
        Optional<ProjectDocument> existingProject = projectRepository.findByProjectId(eventDTO.getProjectId().toString());

        ProjectDocument document;
        if (existingProject.isPresent()) {
            // Actualizar proyecto existente preservando categorización
            document = existingProject.get();
            updateDocumentFromEvent(document, eventDTO);
        } else {
            // Crear nuevo si no existe
            log.info("Proyecto {} no existe, creando nuevo documento", eventDTO.getProjectId());
            document = mapEventToDocument(eventDTO);
        }

        document.setIndexedAt(LocalDateTime.now());
        projectRepository.save(document);

        meterRegistry.counter("projects.indexing.updated").increment();
        log.info("Proyecto {} actualizado en el índice exitosamente", eventDTO.getProjectId());
    }

    /**
     * Elimina un documento de proyecto del índice.
     *
     * @param projectId UUID del proyecto
     */
    private void deleteProject(java.util.UUID projectId) throws IOException {
        projectRepository.deleteByProjectId(projectId.toString());

        meterRegistry.counter("projects.indexing.deleted").increment();
        log.info("Proyecto {} eliminado del índice exitosamente", projectId);
    }

    /**
     * Mapea un evento de proyecto a un documento de Elasticsearch.
     *
     * @param eventDTO evento de proyecto
     * @return documento de Elasticsearch
     */
    private ProjectDocument mapEventToDocument(ProjectEventDTO eventDTO) {
        return ProjectDocument.builder()
                .id(eventDTO.getProjectId().toString())
                .projectId(eventDTO.getProjectId().toString())
                .employerId(eventDTO.getEmployerId().toString())
                .title(eventDTO.getTitle())
                .description(eventDTO.getDescription())
                .status(eventDTO.getStatus())
                .minSalary(eventDTO.getMinSalary())
                .maxSalary(eventDTO.getMaxSalary())
                .currency(eventDTO.getCurrency())
                .location(eventDTO.getLocation())
                .isRemote(eventDTO.getIsRemote())
                .requiredSkills(eventDTO.getRequiredSkills())
                .jobLevel(eventDTO.getJobLevel())
                .build();
    }

    /**
     * Actualiza un documento existente con datos de un evento,
     * preservando la información de categorización.
     *
     * @param document documento a actualizar
     * @param eventDTO evento con nuevos datos
     */
    private void updateDocumentFromEvent(ProjectDocument document, ProjectEventDTO eventDTO) {
        document.setEmployerId(eventDTO.getEmployerId().toString());
        document.setTitle(eventDTO.getTitle());
        document.setDescription(eventDTO.getDescription());
        document.setStatus(eventDTO.getStatus());
        document.setMinSalary(eventDTO.getMinSalary());
        document.setMaxSalary(eventDTO.getMaxSalary());
        document.setCurrency(eventDTO.getCurrency());
        document.setLocation(eventDTO.getLocation());
        document.setIsRemote(eventDTO.getIsRemote());
        document.setRequiredSkills(eventDTO.getRequiredSkills());
        document.setJobLevel(eventDTO.getJobLevel());
        // mainCategory y tags se preservan si ya existen
    }
}

