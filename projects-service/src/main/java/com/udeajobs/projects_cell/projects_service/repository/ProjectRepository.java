package com.udeajobs.projects_cell.projects_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udeajobs.projects_cell.projects_service.entity.Project;

/**
 * Repositorio para la gestión de operaciones de base de datos de la entidad Project.
 * Proporciona métodos básicos de CRUD y consultas personalizadas.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * Busca todos los proyectos asociados a un empleador específico.
     *
     * @param employerId el UUID del empleador
     * @return una lista de proyectos del empleador especificado
     */
    List<Project> findByEmployerId(UUID employerId);
}
