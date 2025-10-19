package com.udeajobs.projects_cell.categorization_service.dto;

import com.udeajobs.projects_cell.categorization_service.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de respuesta para una categoría.
 * Contiene los datos que se exponen en la API.
 *
 * @author UdeaJobs Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de una categoría")
public class CategoryResponseDTO {

    /**
     * Identificador único de la categoría.
     */
    @Schema(description = "Identificador único de la categoría", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID categoryId;

    /**
     * Nombre de la categoría.
     */
    @Schema(description = "Nombre de la categoría", example = "Java")
    private String name;

    /**
     * Descripción de la categoría.
     */
    @Schema(description = "Descripción de la categoría", example = "Lenguaje de programación orientado a objetos")
    private String description;

    /**
     * Tipo de categoría.
     */
    @Schema(description = "Tipo de categoría", example = "SKILL")
    private CategoryType type;
}

