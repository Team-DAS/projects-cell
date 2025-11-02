package com.udeajobs.projects_cell.projects_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.udeajobs.projects_cell.projects_service.dto.ProjectRequestDTO;
import com.udeajobs.projects_cell.projects_service.dto.ProjectResponseDTO;
import com.udeajobs.projects_cell.projects_service.entity.Project;

/**
 * Mapper de MapStruct para la conversión entre entidades Project y DTOs.
 * Facilita la transformación de datos entre las diferentes capas de la aplicación.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    /**
     * Convierte un ProjectRequestDTO a una entidad Project.
     *
     * @param requestDTO el DTO de solicitud a convertir
     * @return la entidad Project correspondiente
     */
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Project toProject(ProjectRequestDTO requestDTO);

    /**
     * Convierte una entidad Project a un ProjectResponseDTO.
     *
     * @param project la entidad Project a convertir
     * @return el DTO de respuesta correspondiente
     */
    ProjectResponseDTO toProjectResponseDTO(Project project);

    /**
     * Actualiza una entidad Project existente con los datos de un ProjectRequestDTO.
     * Utilizado para actualizaciones parciales de proyectos.
     *
     * @param dto el DTO con los nuevos datos
     * @param project la entidad Project a actualizar
     */
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProjectFromDto(ProjectRequestDTO dto, @MappingTarget Project project);
}
