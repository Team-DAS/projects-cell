package com.udeajobs.projects_cell.projects_service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para las solicitudes de creación y actualización de proyectos.
 * Contiene validaciones JSR 303/Bean Validation para garantizar la integridad de los datos.
 */
@Data
public class ProjectRequestDTO {

    /**
     * Identificador del empleador que crea el proyecto.
     */
    @NotNull(message = "El ID del empleador es obligatorio")
    private UUID employerId;

    /**
     * Título del proyecto.
     */
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
    private String title;

    /**
     * Descripción detallada del proyecto.
     */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, message = "La descripción debe tener al menos 20 caracteres")
    private String description;

    /**
     * Salario mínimo ofrecido para el proyecto.
     */
    @NotNull(message = "El salario mínimo es obligatorio")
    @PositiveOrZero(message = "El salario mínimo debe ser mayor o igual a cero")
    private BigDecimal minSalary;

    /**
     * Salario máximo ofrecido para el proyecto.
     */
    @NotNull(message = "El salario máximo es obligatorio")
    @Positive(message = "El salario máximo debe ser mayor a cero")
    private BigDecimal maxSalary;

    /**
     * Moneda en la que se expresan los salarios (código ISO de 3 caracteres).
     */
    @NotBlank(message = "La moneda es obligatoria")
    @Size(min = 3, max = 3, message = "La moneda debe tener exactamente 3 caracteres")
    private String currency;

    /**
     * Ubicación física donde se desarrollará el proyecto.
     */
    private String location;

    /**
     * Indica si el proyecto puede realizarse de forma remota.
     */
    private Boolean isRemote = false;

    /**
     * Lista de habilidades requeridas para el proyecto.
     */
    @NotEmpty(message = "Debe especificar al menos una habilidad requerida")
    private List<String> requiredSkills;

    /**
     * Nivel de experiencia requerido para el proyecto.
     */
    private String jobLevel;
}
