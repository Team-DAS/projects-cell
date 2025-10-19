package com.udeajobs.projects_cell.categorization_service.entity;

import com.udeajobs.projects_cell.categorization_service.enums.CategoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa una categoría en el sistema.
 * Las categorías pueden ser habilidades, etiquetas, niveles de trabajo o industrias.
 * 
 * @author UdeaJobs Team
 */
@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /**
     * Identificador único público de la categoría (UUID).
     * Este es el ID que se expone en la API.
     */
    @Id
    @Column(unique = true, nullable = false, updatable = false)
    private UUID categoryId;

    /**
     * Nombre de la categoría (debe ser único en el sistema).
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Descripción opcional de la categoría.
     */
    @Column(length = 255)
    private String description;

    /**
     * Tipo de categoría (SKILL, TAG, JOB_LEVEL, INDUSTRY).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;

    /**
     * Fecha y hora de creación del registro.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del registro.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

