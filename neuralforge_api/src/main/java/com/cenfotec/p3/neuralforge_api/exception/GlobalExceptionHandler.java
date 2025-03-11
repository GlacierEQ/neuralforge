package com.cenfotec.p3.neuralforge_api.exception;

import com.cenfotec.p3.neuralforge_api.exception.response.ExceptionResponse;
import com.cenfotec.p3.neuralforge_api.exception.response.MultipleExceptionResponse;
import com.cenfotec.p3.neuralforge_api.exception.response.SingleExceptionResponse;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String requestId = MDC.get("requestId");
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(error.getDefaultMessage());
        });

        ExceptionResponse response;
        if (errors.size() == 1) {
            response = new SingleExceptionResponse(requestId, errors.get(0));
        } else {
            response = new MultipleExceptionResponse(requestId, errors);
        }

        logger.error("Validation error occurred: {}", response);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ExceptionResponse> handleEntityExistsException(EntityExistsException ex){
        String requestId = MDC.get("requestId");
        logger.error("An element already exists inside the database: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SingleExceptionResponse.builder()
                .id(requestId)
                .exception(ex.getMessage())
                .build()
        );
    }
}