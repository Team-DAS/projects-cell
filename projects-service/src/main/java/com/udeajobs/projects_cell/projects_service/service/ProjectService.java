package com.udeajobs.projects_cell.projects_service.service;

import java.util.List;
import java.util.UUID;

import com.udeajobs.projects_cell.projects_service.dto.ProjectRequestDTO;
import com.udeajobs.projects_cell.projects_service.dto.ProjectResponseDTO;

/**
 * Interfaz del servicio de proyectos que define las operaciones de negocio disponibles.
 * Proporciona métodos para la gestión completa del ciclo de vida de los proyectos.
 */
public interface ProjectService {

    /**
     * Crea un nuevo proyecto en el sistema.
     *
     * @param projectDTO los datos del proyecto a crear
     * @return el proyecto creado como DTO de respuesta
     */
    ProjectResponseDTO createProject(ProjectRequestDTO projectDTO);

    /**
     * Obtiene un proyecto específico por su UUID.
     *
     * @param projectId el UUID del proyecto a buscar
     * @return el proyecto encontrado como DTO de respuesta
     * @throws com.udeajobs.projects_cell.projects_service.exception.ResourceNotFoundException si el proyecto no existe
     */
    ProjectResponseDTO getProjectByUuid(UUID projectId);

    /**
     * Obtiene todos los proyectos asociados a un empleador específico.
     *
     * @param employerId el UUID del empleador
     * @return lista de proyectos del empleador como DTOs de respuesta
     */
    List<ProjectResponseDTO> getProjectsByEmployer(UUID employerId);

    /**
     * Actualiza un proyecto existente con nuevos datos.
     *
     * @param projectId el UUID del proyecto a actualizar
     * @param projectDTO los nuevos datos del proyecto
     * @return el proyecto actualizado como DTO de respuesta
     * @throws com.udeajobs.projects_cell.projects_service.exception.ResourceNotFoundException si el proyecto no existe
     */
    ProjectResponseDTO updateProject(UUID projectId, ProjectRequestDTO projectDTO);

    /**
     * Elimina un proyecto del sistema.
     *
     * @param projectId el UUID del proyecto a eliminar
     * @throws com.udeajobs.projects_cell.projects_service.exception.ResourceNotFoundException si el proyecto no existe
     */
    void deleteProject(UUID projectId);

    /**
     * Obtiene todos los proyectos del sistema.
     *
     * @return lista de todos los proyectos como DTOs de respuesta
     */
    List<ProjectResponseDTO> getAllProjects();
}
