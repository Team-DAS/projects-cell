package com.udeajobs.projects_cell.categorization_service.repository;

import com.udeajobs.projects_cell.categorization_service.entity.Category;
import com.udeajobs.projects_cell.categorization_service.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para gestionar operaciones de base de datos de Category.
 *
 * @author UdeaJobs Team
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Busca una categoría por su UUID público.
     *
     * @param categoryId El UUID de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<Category> findByCategoryId(UUID categoryId);

    /**
     * Busca una categoría por su nombre exacto.
     *
     * @param name El nombre de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<Category> findByName(String name);

    /**
     * Busca todas las categorías de un tipo específico.
     *
     * @param type El tipo de categoría (SKILL, TAG, JOB_LEVEL, INDUSTRY)
     * @return Lista de categorías del tipo especificado
     */
    List<Category> findByType(CategoryType type);

    /**
     * Verifica si existe una categoría con el nombre dado.
     *
     * @param name El nombre a verificar
     * @return true si existe, false si no
     */
    boolean existsByName(String name);
}

