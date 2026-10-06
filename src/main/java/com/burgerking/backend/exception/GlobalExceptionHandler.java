package com.burgerking.backend.exception;

import com.burgerking.backend.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        logger.warn("Database constraint violation while handling request", ex);
        return error(HttpStatus.CONFLICT,
                "La operación entra en conflicto con un recurso existente o relacionado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected exception while handling request", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        if (!ex.getBindingResult().getGlobalErrors().isEmpty()) {
            String globalMessages = ex.getBindingResult().getGlobalErrors().stream()
                    .map(error -> error.getObjectName() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            message = message.isEmpty() ? globalMessages : message + ", " + globalMessages;
        }

        return handleExceptionInternal(ex,
                responseBody(status, message), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ErrorResponse convertedBody;
        if (status.is5xxServerError()) {
            logger.error("Unexpected framework exception while handling request", ex);
            convertedBody = responseBody(status,
                    "Ocurrió un error interno en el servidor");
        } else if (body instanceof ErrorResponse errorResponse) {
            convertedBody = errorResponse;
        } else {
            convertedBody = responseBody(status, statusMessage(status));
        }

        return super.handleExceptionInternal(ex, convertedBody, headers, status, request);
    }

    private ErrorResponse responseBody(HttpStatusCode status, String message) {
        return new ErrorResponse(status.value(), statusMessage(status), message,
                LocalDateTime.now());
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(responseBody(status, message));
    }

    private String statusMessage(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved == null ? "Error" : resolved.getReasonPhrase();
    }
}
