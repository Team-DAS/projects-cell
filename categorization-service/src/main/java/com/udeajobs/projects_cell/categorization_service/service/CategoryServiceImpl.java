package com.udeajobs.projects_cell.categorization_service.service;

import com.udeajobs.projects_cell.categorization_service.dto.CategoryRequestDTO;
import com.udeajobs.projects_cell.categorization_service.dto.CategoryResponseDTO;
import com.udeajobs.projects_cell.categorization_service.entity.Category;
import com.udeajobs.projects_cell.categorization_service.enums.CategoryType;
import com.udeajobs.projects_cell.categorization_service.events.dto.CategoryEventDTO;
import com.udeajobs.projects_cell.categorization_service.exception.DuplicateResourceException;
import com.udeajobs.projects_cell.categorization_service.exception.ResourceNotFoundException;
import com.udeajobs.projects_cell.categorization_service.mapper.CategoryMapper;
import com.udeajobs.projects_cell.categorization_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de categorías.
 * Gestiona la lógica de negocio y publica eventos a RabbitMQ.
 *
 * @author UdeaJobs Team
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final RabbitTemplate rabbitTemplate;
    private final CategoryEventPublisher eventPublisher;

    /**
     * Crea una nueva categoría en el sistema y publica un evento.
     *
     * @param dto Datos de la categoría a crear
     * @return DTO con los datos de la categoría creada
     * @throws DuplicateResourceException si ya existe una categoría con ese nombre
     */
    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        log.info("Creando nueva categoría con nombre: {}", dto.getName());

        // Validar que no exista una categoría con el mismo nombre
        if (categoryRepository.existsByName(dto.getName())) {
            log.warn("Intento de crear categoría duplicada: {}", dto.getName());
            throw new DuplicateResourceException("Ya existe una categoría con el nombre: " + dto.getName());
        }

        Category category = categoryMapper.toCategory(dto);
        category.setCategoryId(UUID.randomUUID());

        Category savedCategory = categoryRepository.save(category);
        log.info("Categoría {} guardada en BBDD con ID: {}", savedCategory.getName(), savedCategory.getCategoryId());

        // Publicar evento de categoría creada
        eventPublisher.publishCategoryCreated(savedCategory);

        return categoryMapper.toCategoryResponseDTO(savedCategory);
    }

    /**
     * Obtiene una categoría por su UUID.
     *
     * @param categoryId UUID de la categoría
     * @return DTO con los datos de la categoría
     * @throws ResourceNotFoundException si no se encuentra la categoría
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(UUID categoryId) {
        log.info("Buscando categoría con ID: {}", categoryId);

        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada con ID: {}", categoryId);
                    return new ResourceNotFoundException("Categoría no encontrada con ID: " + categoryId);
                });

        log.info("Categoría encontrada: {}", category.getName());
        return categoryMapper.toCategoryResponseDTO(category);
    }

    /**
     * Obtiene todas las categorías de un tipo específico.
     *
     * @param type Tipo de categoría
     * @return Lista de categorías del tipo especificado
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getCategoriesByType(CategoryType type) {
        log.info("Buscando categorías de tipo: {}", type);

        List<Category> categories = categoryRepository.findByType(type);
        log.info("Se encontraron {} categorías de tipo {}", categories.size(), type);

        return categories.stream()
                .map(categoryMapper::toCategoryResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las categorías del sistema.
     *
     * @return Lista de todas las categorías
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        log.info("Obteniendo todas las categorías");

        List<Category> categories = categoryRepository.findAll();
        log.info("Se encontraron {} categorías en total", categories.size());

        return categories.stream()
                .map(categoryMapper::toCategoryResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una categoría existente y publica un evento.
     *
     * @param categoryId UUID de la categoría a actualizar
     * @param dto Nuevos datos de la categoría
     * @return DTO con los datos actualizados
     * @throws ResourceNotFoundException si no se encuentra la categoría
     * @throws DuplicateResourceException si el nuevo nombre ya existe
     */
    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(UUID categoryId, CategoryRequestDTO dto) {
        log.info("Actualizando categoría con ID: {}", categoryId);

        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada con ID: {}", categoryId);
                    return new ResourceNotFoundException("Categoría no encontrada con ID: " + categoryId);
                });

        if (!category.getName().equals(dto.getName()) && categoryRepository.existsByName(dto.getName())) {
            log.warn("Intento de actualizar a nombre duplicado: {}", dto.getName());
            throw new DuplicateResourceException("Ya existe una categoría con el nombre: " + dto.getName());
        }

        categoryMapper.updateCategoryFromDto(dto, category);

        Category updatedCategory = categoryRepository.save(category);
        log.info("Categoría {} actualizada correctamente", updatedCategory.getCategoryId());

        // Publicar evento de categoría update
        eventPublisher.publishCategoryUpdated(updatedCategory);

        return categoryMapper.toCategoryResponseDTO(updatedCategory);
    }

    /**
     * Elimina una categoría del sistema y publica un evento.
     *
     * @param categoryId UUID de la categoría a eliminar
     * @throws ResourceNotFoundException si no se encuentra la categoría
     */
    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        log.info("Eliminando categoría con ID: {}", categoryId);

        // 1. Buscar la categoría existente
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada con ID: {}", categoryId);
                    return new ResourceNotFoundException("Categoría no encontrada con ID: " + categoryId);
                });

        categoryRepository.delete(category);
        log.info("Categoría {} eliminada de BBDD", categoryId);

        // Publicar evento de categoría eliminada
        eventPublisher.publishCategoryDeleted(category);
    }
}

