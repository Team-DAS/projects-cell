package com.udeajobs.projects_cell.categorization_service.controller;

import com.udeajobs.projects_cell.categorization_service.dto.CategoryRequestDTO;
import com.udeajobs.projects_cell.categorization_service.dto.CategoryResponseDTO;
import com.udeajobs.projects_cell.categorization_service.enums.CategoryType;
import com.udeajobs.projects_cell.categorization_service.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar categorías.
 * Expone endpoints para operaciones CRUD de categorías.
 *
 * @author UdeaJobs Team
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "API para gestión de categorías, habilidades y etiquetas")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Crea una nueva categoría.
     *
     * @param dto Datos de la categoría a crear
     * @return ResponseEntity con la categoría creada y código 201
     */
    @PostMapping
    @Operation(summary = "Crear una nueva categoría",
               description = "Crea una nueva categoría en el sistema. El nombre debe ser único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                     description = "Categoría creada exitosamente",
                     content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
        @ApiResponse(responseCode = "400",
                     description = "Datos de entrada inválidos",
                     content = @Content),
        @ApiResponse(responseCode = "409",
                     description = "Ya existe una categoría con ese nombre",
                     content = @Content)
    })
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.createCategory(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene una categoría por su UUID.
     *
     * @param categoryId UUID de la categoría
     * @return ResponseEntity con la categoría encontrada
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "Obtener categoría por ID",
               description = "Obtiene los detalles de una categoría específica por su UUID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "Categoría encontrada",
                     content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
        @ApiResponse(responseCode = "404",
                     description = "Categoría no encontrada",
                     content = @Content)
    })
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @PathVariable UUID categoryId) {
        CategoryResponseDTO response = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todas las categorías de un tipo específico.
     *
     * @param type Tipo de categoría (SKILL, TAG, JOB_LEVEL, INDUSTRY)
     * @return ResponseEntity con la lista de categorías
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Obtener categorías por tipo",
               description = "Obtiene todas las categorías de un tipo específico (SKILL, TAG, JOB_LEVEL, INDUSTRY)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "Lista de categorías obtenida exitosamente",
                     content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class)))
    })
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByType(
            @PathVariable CategoryType type) {
        List<CategoryResponseDTO> response = categoryService.getCategoriesByType(type);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todas las categorías del sistema.
     *
     * @return ResponseEntity con la lista de todas las categorías
     */
    @GetMapping
    @Operation(summary = "Obtener todas las categorías",
               description = "Obtiene la lista completa de categorías del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "Lista de categorías obtenida exitosamente",
                     content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class)))
    })
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param categoryId UUID de la categoría a actualizar
     * @param dto Nuevos datos de la categoría
     * @return ResponseEntity con la categoría actualizada
     */
    @PutMapping("/{categoryId}")
    @Operation(summary = "Actualizar una categoría",
               description = "Actualiza los datos de una categoría existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "Categoría actualizada exitosamente",
                     content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
        @ApiResponse(responseCode = "400",
                     description = "Datos de entrada inválidos",
                     content = @Content),
        @ApiResponse(responseCode = "404",
                     description = "Categoría no encontrada",
                     content = @Content),
        @ApiResponse(responseCode = "409",
                     description = "Ya existe otra categoría con ese nombre",
                     content = @Content)
    })
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.updateCategory(categoryId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina una categoría del sistema.
     *
     * @param categoryId UUID de la categoría a eliminar
     * @return ResponseEntity vacío con código 204
     */
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Eliminar una categoría",
               description = "Elimina una categoría del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                     description = "Categoría eliminada exitosamente",
                     content = @Content),
        @ApiResponse(responseCode = "404",
                     description = "Categoría no encontrada",
                     content = @Content)
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}

