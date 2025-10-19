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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.category-events-exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key.created}")
    private String routingKeyCreated;

    @Value("${app.rabbitmq.routing-key.updated}")
    private String routingKeyUpdated;

    @Value("${app.rabbitmq.routing-key.deleted}")
    private String routingKeyDeleted;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param categoryRepository Repositorio de categorías
     * @param categoryMapper Mapper de entidades/DTOs
     * @param rabbitTemplate Template para publicar mensajes en RabbitMQ
     */
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper,
                               RabbitTemplate rabbitTemplate) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

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

        CategoryEventDTO eventDTO = categoryMapper.toCategoryEventDTO(savedCategory);
        eventDTO.setEventType("CREATED");

        // Publicar evento en RabbitMQ
        try {
            log.info("MessageConverter activo: {}", rabbitTemplate.getMessageConverter().getClass().getSimpleName());
            rabbitTemplate.convertAndSend(exchangeName, routingKeyCreated, eventDTO);
            log.info("Evento 'category.created' publicado para la categoría {}", savedCategory.getCategoryId());
        } catch (Exception e) {
            log.error("Error al publicar evento 'category.created' para {}: {}",
                    savedCategory.getCategoryId(), e.getMessage());
            // NOTA: La transacción de BBDD no se revierte. Podríamos implementar
            // un patrón Outbox para garantizar consistencia eventual.
        }

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

        // 1. Buscar la categoría existente
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada con ID: {}", categoryId);
                    return new ResourceNotFoundException("Categoría no encontrada con ID: " + categoryId);
                });

        // 2. Validar que el nuevo nombre no esté en uso (si cambió)
        if (!category.getName().equals(dto.getName()) && categoryRepository.existsByName(dto.getName())) {
            log.warn("Intento de actualizar a nombre duplicado: {}", dto.getName());
            throw new DuplicateResourceException("Ya existe una categoría con el nombre: " + dto.getName());
        }

        categoryMapper.updateCategoryFromDto(dto, category);

        Category updatedCategory = categoryRepository.save(category);
        log.info("Categoría {} actualizada correctamente", updatedCategory.getCategoryId());

        CategoryEventDTO eventDTO = categoryMapper.toCategoryEventDTO(updatedCategory);
        eventDTO.setEventType("UPDATED");

        // Publicar evento en RabbitMQ
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKeyUpdated, eventDTO);
            log.info("Evento 'category.updated' publicado para la categoría {}", updatedCategory.getCategoryId());
        } catch (Exception e) {
            log.error("Error al publicar evento 'category.updated' para {}: {}",
                    updatedCategory.getCategoryId(), e.getMessage());
        }

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

        CategoryEventDTO eventDTO = CategoryEventDTO.builder()
                .categoryId(categoryId)
                .eventType("DELETED")
                .build();

        // Publicar evento en RabbitMQ
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKeyDeleted, eventDTO);
            log.info("Evento 'category.deleted' publicado para la categoría {}", categoryId);
        } catch (Exception e) {
            log.error("Error al publicar evento 'category.deleted' para {}: {}",
                    categoryId, e.getMessage());
        }
    }
}

