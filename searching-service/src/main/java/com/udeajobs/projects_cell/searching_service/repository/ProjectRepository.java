package com.udeajobs.projects_cell.searching_service.repository;

import com.udeajobs.projects_cell.searching_service.entity.ProjectDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de búsqueda en Elasticsearch de proyectos.
 * <p>
 * Proporciona métodos de búsqueda personalizados utilizando
 * Spring Data Elasticsearch.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Repository
public interface ProjectRepository extends ElasticsearchRepository<ProjectDocument, String> {

    /**
     * Busca un proyecto por su UUID.
     *
     * @param projectId UUID del proyecto
     * @return Optional con el proyecto si existe
     */
    Optional<ProjectDocument> findByProjectId(UUID projectId);

    /**
     * Busca proyectos por estado con paginación.
     *
     * @param status estado del proyecto
     * @param pageable información de paginación
     * @return página de proyectos
     */
    Page<ProjectDocument> findByStatus(String status, Pageable pageable);

    /**
     * Busca proyectos por categoría principal.
     *
     * @param mainCategory categoría principal
     * @param pageable información de paginación
     * @return página de proyectos
     */
    Page<ProjectDocument> findByMainCategory(String mainCategory, Pageable pageable);

    /**
     * Busca proyectos que contengan una habilidad específica.
     *
     * @param skill habilidad a buscar
     * @param pageable información de paginación
     * @return página de proyectos
     */
    Page<ProjectDocument> findByRequiredSkillsContaining(String skill, Pageable pageable);

    /**
     * Busca proyectos por ubicación (búsqueda de texto completo).
     *
     * @param location ubicación
     * @param pageable información de paginación
     * @return página de proyectos
     */
    Page<ProjectDocument> findByLocationContaining(String location, Pageable pageable);

    /**
     * Busca proyectos remotos.
     *
     * @param isRemote true si es remoto
     * @param pageable información de paginación
     * @return página de proyectos
     */
    Page<ProjectDocument> findByIsRemote(Boolean isRemote, Pageable pageable);

    /**
     * Busca proyectos por nivel de trabajo.
     *
     * @param jobLevel nivel del trabajo
     * @param pageable información de paginación
     * @return página de proyectos
     */
    Page<ProjectDocument> findByJobLevel(String jobLevel, Pageable pageable);

    /**
     * Busca proyectos dentro de un rango salarial.
     *
     * @param minSalary salario mínimo
     * @param maxSalary salario máximo
     * @param pageable información de paginación
     * @return página de proyectos
     */
    @Query("{\"bool\": {\"must\": [{\"range\": {\"maxSalary\": {\"gte\": \"?0\"}}}, {\"range\": {\"minSalary\": {\"lte\": \"?1\"}}}]}}")
    Page<ProjectDocument> findBySalaryRange(Double minSalary, Double maxSalary, Pageable pageable);

    /**
     * Busca proyectos que contengan alguno de los tags especificados.
     *
     * @param tags lista de tags
     * @param pageable información de paginación
     * @return página de proyectos
     */
    Page<ProjectDocument> findByTagsIn(List<String> tags, Pageable pageable);

    /**
     * Elimina un proyecto por su UUID.
     *
     * @param projectId UUID del proyecto
     */
    void deleteByProjectId(UUID projectId);
}

