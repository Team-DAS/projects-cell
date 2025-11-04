package com.udeajobs.projects_cell.searching_service.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para GraphQL.
 * <p>
 * Este componente intercepta las excepciones lanzadas durante la ejecución
 * de queries y mutations de GraphQL, transformándolas en respuestas apropiadas.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    /**
     * Resuelve excepciones durante el fetch de datos en GraphQL.
     *
     * @param ex excepcional lanzada
     * @param environment contexto de ejecución de GraphQL
     * @return error GraphQL formateado
     */
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment environment) {
        log.error("Error en operación GraphQL: {}", ex.getMessage(), ex);

        if (ex instanceof ProjectNotFoundException) {
            return GraphQLError.newError()
                    .errorType(CustomErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        }

        if (ex instanceof InvalidSearchParametersException) {
            return GraphQLError.newError()
                    .errorType(CustomErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        }

        if (ex instanceof IndexingException) {
            return GraphQLError.newError()
                    .errorType(CustomErrorType.INTERNAL_ERROR)
                    .message("Error al procesar la operación de indexación")
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        }

        if (ex instanceof MethodArgumentNotValidException validationEx) {
            String errors = validationEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return GraphQLError.newError()
                    .errorType(CustomErrorType.VALIDATION_ERROR)
                    .message("Errores de validación: " + errors)
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        }

        // Error genérico
        return GraphQLError.newError()
                .errorType(CustomErrorType.INTERNAL_ERROR)
                .message("Error interno del servidor")
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }

    /**
     * Enumeración de tipos de errores personalizados para GraphQL.
     */
    private enum CustomErrorType implements ErrorClassification {
        NOT_FOUND,
        BAD_REQUEST,
        VALIDATION_ERROR,
        INTERNAL_ERROR
    }
}

