package com.udeajobs.projects_cell.projects_service.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udeajobs.projects_cell.projects_service.dto.ProjectRequestDTO;
import com.udeajobs.projects_cell.projects_service.dto.ProjectResponseDTO;
import com.udeajobs.projects_cell.projects_service.entity.Project;
import com.udeajobs.projects_cell.projects_service.enums.ProjectStatus;
import com.udeajobs.projects_cell.projects_service.exception.ResourceNotFoundException;
import com.udeajobs.projects_cell.projects_service.mapper.ProjectMapper;
import com.udeajobs.projects_cell.projects_service.repository.ProjectRepository;

/**
 * Implementación del servicio de proyectos.
 * Contiene la lógica de negocio para la gestión de proyectos en la plataforma.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO projectDTO) {
        logger.info("Creando nuevo proyecto para el empleador: {}", projectDTO.getEmployerId());

        try {
            Project project = projectMapper.toProject(projectDTO);
            Project savedProject = projectRepository.save(project);

            logger.info("Proyecto con UUID {} creado exitosamente", savedProject.getProjectId());
            return projectMapper.toProjectResponseDTO(savedProject);
        } catch (Exception e) {
            logger.error("Error al crear proyecto para empleador {}: {}",
                    projectDTO.getEmployerId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectByUuid(UUID projectId) {
        logger.info("Buscando proyecto con UUID: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    logger.error("Proyecto con UUID {} no encontrado", projectId);
                    return new ResourceNotFoundException("Proyecto con ID " + projectId + " no encontrado");
                });

        logger.info("Proyecto con UUID {} encontrado exitosamente", projectId);
        return projectMapper.toProjectResponseDTO(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByEmployer(UUID employerId) {
        logger.info("Buscando proyectos para el empleador: {}", employerId);

        try {
            List<Project> projects = projectRepository.findByEmployerId(employerId);

            logger.info("Encontrados {} proyectos para el empleador {}", projects.size(), employerId);
            return projects.stream()
                    .map(projectMapper::toProjectResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al buscar proyectos para empleador {}: {}",
                        employerId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProjectResponseDTO updateProject(UUID projectId, ProjectRequestDTO projectDTO) {
        logger.info("Actualizando proyecto con UUID: {}", projectId);

        try {
            Project existingProject = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        logger.error("Proyecto con UUID {} no encontrado para actualización", projectId);
                        return new ResourceNotFoundException("Proyecto con ID " + projectId + " no encontrado");
                    });

            projectMapper.updateProjectFromDto(projectDTO, existingProject);
            Project updatedProject = projectRepository.save(existingProject);

            logger.info("Proyecto con UUID {} actualizado exitosamente", projectId);
            return projectMapper.toProjectResponseDTO(updatedProject);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al actualizar proyecto {}: {}", projectId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteProject(UUID projectId) {
        logger.info("Eliminando proyecto con UUID: {}", projectId);

        try {
            if (!projectRepository.existsById(projectId)) {
                logger.error("Proyecto con UUID {} no encontrado para eliminación", projectId);
                throw new ResourceNotFoundException("Proyecto con ID " + projectId + " no encontrado");
            }

            projectRepository.deleteById(projectId);
            logger.info("Proyecto con UUID {} eliminado exitosamente", projectId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al eliminar proyecto {}: {}", projectId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjects() {
        logger.info("Obteniendo todos los proyectos");
        try {
            List<Project> projects = projectRepository.findAll();
            logger.info("Encontrados {} proyectos en total", projects.size());
            return projects.stream()
                    .map(projectMapper::toProjectResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener todos los proyectos: {}", e.getMessage(), e);
            throw e;
        }
    }
}
