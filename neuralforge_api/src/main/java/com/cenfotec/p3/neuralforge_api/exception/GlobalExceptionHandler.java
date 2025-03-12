package com.cenfotec.p3.neuralforge_api.exception;

import com.cenfotec.p3.neuralforge_api.exception.response.ExceptionResponse;
import com.cenfotec.p3.neuralforge_api.exception.response.MultipleExceptionResponse;
import com.cenfotec.p3.neuralforge_api.exception.response.SingleExceptionResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for managing application-wide exceptions.
 * Provides standardized responses for different types of exceptions.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ResponseStatusException} and returns an appropriate HTTP response.
     *
     * @param ex The exception thrown.
     * @return A {@link ResponseEntity} containing a {@link SingleExceptionResponse}.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex) {
        String requestId = MDC.get("requestId");
        logger.error("A status exception has occurred: {}", ex);
        return ResponseEntity.status(ex.getStatusCode()).body(SingleExceptionResponse.builder()
                .id(requestId)
                .exception(ex.getMessage())
                .build()
        );
    }

    /**
     * Handles validation exceptions that occur when method arguments fail validation.
     *
     * @param ex The exception containing validation errors.
     * @return A {@link ResponseEntity} containing either a {@link SingleExceptionResponse} or {@link MultipleExceptionResponse}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String requestId = MDC.get("requestId");
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));

        ExceptionResponse response = (errors.size() == 1)
                ? new SingleExceptionResponse(requestId, errors.get(0))
                : new MultipleExceptionResponse(requestId, errors);

        logger.error("Validation error occurred: {}", response);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles {@link EntityExistsException} when attempting to create a database entity that already exists.
     *
     * @param ex The exception thrown.
     * @return A {@link ResponseEntity} containing a {@link SingleExceptionResponse}.
     */
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ExceptionResponse> handleEntityExistsException(EntityExistsException ex) {
        String requestId = MDC.get("requestId");
        logger.error("An element already exists inside the database: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SingleExceptionResponse.builder()
                .id(requestId)
                .exception(ex.getMessage())
                .build()
        );
    }

    /**
     * Handles {@link ExpiredJwtException} when an expired JWT token is provided.
     *
     * @param ex The exception thrown.
     * @return A {@link ResponseEntity} containing a {@link SingleExceptionResponse} with an unauthorized status.
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        String requestId = MDC.get("requestId");
        logger.error("The JWT token sent has already expired: {}", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(SingleExceptionResponse.builder()
                .id(requestId)
                .exception("The JWT token sent has already expired.")
                .build()
        );
    }

    /**
     * Handles generic exceptions that do not fall under other specific categories.
     *
     * @param ex The exception thrown.
     * @return A {@link ResponseEntity} containing a {@link SingleExceptionResponse} with an internal server error status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        String requestId = MDC.get("requestId");
        logger.error("An unknown exception has occurred: {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SingleExceptionResponse.builder()
                .id(requestId)
                .exception(ex.getMessage())
                .build()
        );
    }
}
