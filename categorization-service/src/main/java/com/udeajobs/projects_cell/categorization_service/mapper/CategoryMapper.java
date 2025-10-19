package com.udeajobs.projects_cell.categorization_service.mapper;

import com.udeajobs.projects_cell.categorization_service.dto.CategoryRequestDTO;
import com.udeajobs.projects_cell.categorization_service.dto.CategoryResponseDTO;
import com.udeajobs.projects_cell.categorization_service.entity.Category;
import com.udeajobs.projects_cell.categorization_service.events.dto.CategoryEventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper de MapStruct para convertir entre entidades Category y DTOs.
 *
 * @author UdeaJobs Team
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Convierte un CategoryRequestDTO a una entidad Category.
     *
     * @param dto DTO con los datos de entrada
     * @return Entidad Category
     */
    Category toCategory(CategoryRequestDTO dto);

    /**
     * Convierte una entidad Category a un CategoryResponseDTO.
     *
     * @param category Entidad Category
     * @return DTO de respuesta
     */
    CategoryResponseDTO toCategoryResponseDTO(Category category);

    /**
     * Actualiza una entidad Category existente con los datos de un DTO.
     *
     * @param dto DTO con los nuevos datos
     * @param category Entidad a actualizar
     */
    void updateCategoryFromDto(CategoryRequestDTO dto, @MappingTarget Category category);

    /**
     * Convierte la entidad Category al DTO de evento para RabbitMQ.
     *
     * @param category Entidad Category
     * @return DTO de evento para RabbitMQ
     */
    CategoryEventDTO toCategoryEventDTO(Category category);
}

