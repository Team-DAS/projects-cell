package com.udeajobs.projects_cell.projects_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udeajobs.projects_cell.projects_service.dto.ProjectRequestDTO;
import com.udeajobs.projects_cell.projects_service.dto.ProjectResponseDTO;
import com.udeajobs.projects_cell.projects_service.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de proyectos.
 * Proporciona endpoints para operaciones CRUD sobre proyectos en la plataforma de freelancers.
 */
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Proyectos", description = "API para la gestión de proyectos")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Crea un nuevo proyecto en el sistema.
     *
     * @param projectRequestDTO los datos del proyecto a crear
     * @return ResponseEntity con el proyecto creado y estado 201 Created
     */
    @PostMapping
    @Operation(summary = "Crear nuevo proyecto", description = "Crea un nuevo proyecto en la plataforma")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Proyecto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody ProjectRequestDTO projectRequestDTO) {

        ProjectResponseDTO createdProject = projectService.createProject(projectRequestDTO);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    /**
     * Obtiene un proyecto específico por su UUID.
     *
     * @param projectId el UUID del proyecto a buscar
     * @return ResponseEntity con el proyecto encontrado y estado 200 OK
     */
    @GetMapping("/{projectId}")
    @Operation(summary = "Obtener proyecto por ID", description = "Obtiene un proyecto específico por su UUID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proyecto encontrado"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ProjectResponseDTO> getProjectById(
            @Parameter(description = "UUID del proyecto", required = true)
            @PathVariable UUID projectId) {

        ProjectResponseDTO project = projectService.getProjectByUuid(projectId);
        return ResponseEntity.ok(project);
    }

    /**
     * Obtiene todos los proyectos de un empleador específico.
     *
     * @param employerId el UUID del empleador
     * @return ResponseEntity con la lista de proyectos y estado 200 OK
     */
    @GetMapping("/user/{employerId}")
    @Operation(summary = "Obtener proyectos por empleador",
               description = "Obtiene todos los proyectos asociados a un empleador específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de proyectos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByEmployer(
            @Parameter(description = "UUID del empleador", required = true)
            @PathVariable UUID employerId) {

        List<ProjectResponseDTO> projects = projectService.getProjectsByEmployer(employerId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Actualiza un proyecto existente.
     *
     * @param projectId el UUID del proyecto a actualizar
     * @param projectRequestDTO los nuevos datos del proyecto
     * @return ResponseEntity con el proyecto actualizado y estado 200 OK
     */
    @PutMapping("/{projectId}")
    @Operation(summary = "Actualizar proyecto", description = "Actualiza un proyecto existente con nuevos datos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proyecto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @Parameter(description = "UUID del proyecto", required = true)
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectRequestDTO projectRequestDTO) {

        ProjectResponseDTO updatedProject = projectService.updateProject(projectId, projectRequestDTO);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Elimina un proyecto del sistema.
     *
     * @param projectId el UUID del proyecto a eliminar
     * @return ResponseEntity con estado 204 No Content
     */
    @DeleteMapping("/{projectId}")
    @Operation(summary = "Eliminar proyecto", description = "Elimina un proyecto del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Proyecto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "UUID del proyecto", required = true)
            @PathVariable UUID projectId) {

        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los proyectos del sistema.
     *
     * @return ResponseEntity con la lista de proyectos y estado 200 OK
     */
    @GetMapping
    @Operation(summary = "Obtener todos los proyectos",
               description = "Obtiene todos los proyectos del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de proyectos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        List<ProjectResponseDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
}
