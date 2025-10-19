package com.udeajobs.projects_cell.categorization_service.service;

import com.udeajobs.projects_cell.categorization_service.dto.CategoryRequestDTO;
import com.udeajobs.projects_cell.categorization_service.dto.CategoryResponseDTO;
import com.udeajobs.projects_cell.categorization_service.enums.CategoryType;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz del servicio para gestionar categorías.
 * Define las operaciones de negocio disponibles.
 *
 * @author UdeaJobs Team
 */
public interface CategoryService {

    /**
     * Crea una nueva categoría en el sistema.
     *
     * @param dto Datos de la categoría a crear
     * @return DTO con los datos de la categoría creada
     * @throws DuplicateResourceException si ya existe una categoría con ese nombre
     */
    CategoryResponseDTO createCategory(CategoryRequestDTO dto);

    /**
     * Obtiene una categoría por su UUID.
     *
     * @param categoryId UUID de la categoría
     * @return DTO con los datos de la categoría
     * @throws ResourceNotFoundException si no se encuentra la categoría
     */
    CategoryResponseDTO getCategoryById(UUID categoryId);

    /**
     * Obtiene todas las categorías de un tipo específico.
     *
     * @param type Tipo de categoría (SKILL, TAG, JOB_LEVEL, INDUSTRY)
     * @return Lista de categorías del tipo especificado
     */
    List<CategoryResponseDTO> getCategoriesByType(CategoryType type);

    /**
     * Obtiene todas las categorías del sistema.
     *
     * @return Lista de todas las categorías
     */
    List<CategoryResponseDTO> getAllCategories();

    /**
     * Actualiza una categoría existente.
     *
     * @param categoryId UUID de la categoría a actualizar
     * @param dto Nuevos datos de la categoría
     * @return DTO con los datos actualizados
     * @throws ResourceNotFoundException si no se encuentra la categoría
     * @throws DuplicateResourceException si el nuevo nombre ya existe
     */
    CategoryResponseDTO updateCategory(UUID categoryId, CategoryRequestDTO dto);

    /**
     * Elimina una categoría del sistema.
     *
     * @param categoryId UUID de la categoría a eliminar
     * @throws ResourceNotFoundException si no se encuentra la categoría
     */
    void deleteCategory(UUID categoryId);
}

