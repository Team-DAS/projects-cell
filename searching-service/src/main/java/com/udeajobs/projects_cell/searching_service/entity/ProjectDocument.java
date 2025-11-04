package com.udeajobs.projects_cell.searching_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento de Elasticsearch que representa un proyecto indexado.
 * <p>
 * Este documento combina información del proyecto base con su categorización,
 * permitiendo búsquedas eficientes y enriquecidas.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "#{@environment.getProperty('app.elasticsearch.index-name')}")
public class ProjectDocument {

    /**
     * Identificador único del proyecto.
     */
    @Id
    private String id;

    /**
     * UUID del proyecto en el sistema fuente.
     */
    @Field(type = FieldType.Keyword)
    private String projectId;

    /**
     * UUID del empleador que publica el proyecto.
     */
    @Field(type = FieldType.Keyword)
    private String employerId;

    /**
     * Título del proyecto.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    /**
     * Descripción detallada del proyecto.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    /**
     * Estado del proyecto (DRAFT, PUBLISHED, CLOSED, etc.).
     */
    @Field(type = FieldType.Keyword)
    private String status;

    /**
     * Salario mínimo ofrecido.
     */
    @Field(type = FieldType.Double)
    private Double minSalary;

    /**
     * Salario máximo ofrecido.
     */
    @Field(type = FieldType.Double)
    private Double maxSalary;

    /**
     * Moneda del salario (USD, EUR, COP, etc.).
     */
    @Field(type = FieldType.Keyword)
    private String currency;

    /**
     * Ubicación del proyecto.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String location;

    /**
     * Indica si el proyecto es remoto.
     */
    @Field(type = FieldType.Boolean)
    private Boolean isRemote;

    /**
     * Habilidades requeridas para el proyecto.
     */
    @Field(type = FieldType.Keyword)
    private List<String> requiredSkills;

    /**
     * Nivel del trabajo (JUNIOR, SEMI_SENIOR, SENIOR, etc.).
     */
    @Field(type = FieldType.Keyword)
    private String jobLevel;

    /**
     * Categoría principal del proyecto (asignada por el servicio de categorización).
     */
    @Field(type = FieldType.Keyword)
    private String mainCategory;

    /**
     * Etiquetas adicionales del proyecto (asignadas por el servicio de categorización).
     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

    /**
     * Fecha de última indexación/actualización en Elasticsearch.
     * <p>
     * Este campo rastrea cuándo fue la última vez que este documento
     * fue sincronizado con Elasticsearch, útil para auditoría y debugging.
     * </p>
     */
    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime indexedAt;
}

