package com.udeajobs.projects_cell.projects_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.udeajobs.projects_cell.projects_service.enums.ProjectStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.Data;

/**
 * Entidad JPA que representa un proyecto en la plataforma de freelancers.
 * Contiene toda la información necesaria para gestionar el ciclo de vida de un proyecto.
 */
@Data
@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_project_uuid", columnList = "project_id"),
    @Index(name = "idx_employer_id", columnList = "employer_id")
})
public class Project {

    /**
     * Identificador único público del proyecto (UUID).
     */
    @Column(name = "project_id", updatable = false, nullable = false, unique = true)
    @Id
    private UUID projectId;

    /**
     * Identificador del empleador que creó el proyecto.
     */
    @Column(name = "employer_id", nullable = false)
    private UUID employerId;

    /**
     * Título del proyecto.
     */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /**
     * Descripción detallada del proyecto.
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Estado actual del proyecto.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status = ProjectStatus.OPEN;

    /**
     * Salario mínimo ofrecido para el proyecto.
     */
    @Column(name = "min_salary", precision = 10, scale = 2)
    private BigDecimal minSalary;

    /**
     * Salario máximo ofrecido para el proyecto.
     */
    @Column(name = "max_salary", precision = 10, scale = 2)
    private BigDecimal maxSalary;

    /**
     * Moneda en la que se expresan los salarios (código ISO de 3 caracteres).
     */
    @Column(name = "currency", length = 3)
    private String currency;

    /**
     * Ubicación física donde se desarrollará el proyecto.
     */
    @Column(name = "location")
    private String location;

    /**
     * Indica si el proyecto puede realizarse de forma remota.
     */
    @Column(name = "is_remote", nullable = false)
    private Boolean isRemote = false;

    /**
     * Lista de habilidades requeridas para el proyecto.
     */
    @ElementCollection
    @CollectionTable(name = "project_required_skills", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill")
    private List<String> requiredSkills;

    /**
     * Nivel de experiencia requerido para el proyecto.
     */
    @Column(name = "job_level")
    private String jobLevel;

    /**
     * Fecha y hora de creación del proyecto.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de última actualización del proyecto.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Genera automáticamente el UUID del proyecto antes de persistir.
     */
    @PrePersist
    private void generateProjectId() {
        if (this.projectId == null) {
            this.projectId = UUID.randomUUID();
        }
    }
}