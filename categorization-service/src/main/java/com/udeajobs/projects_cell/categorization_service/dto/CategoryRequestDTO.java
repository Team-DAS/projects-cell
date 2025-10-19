package com.udeajobs.projects_cell.categorization_service.dto;

import com.udeajobs.projects_cell.categorization_service.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar una categoría.
 * Contiene las validaciones necesarias para los datos de entrada.
 *
 * @author UdeaJobs Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de entrada para crear o actualizar una categoría")
public class CategoryRequestDTO {

    /**
     * Nombre de la categoría (debe ser único).
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Schema(description = "Nombre único de la categoría", example = "Java")
    private String name;

    /**
     * Descripción opcional de la categoría.
     */
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Schema(description = "Descripción de la categoría", example = "Lenguaje de programación orientado a objetos")
    private String description;

    /**
     * Tipo de categoría.
     */
    @NotNull(message = "El tipo de categoría es obligatorio")
    @Schema(description = "Tipo de categoría", example = "SKILL", allowableValues = {"SKILL", "TAG", "JOB_LEVEL", "INDUSTRY"})
    private CategoryType type;
}

