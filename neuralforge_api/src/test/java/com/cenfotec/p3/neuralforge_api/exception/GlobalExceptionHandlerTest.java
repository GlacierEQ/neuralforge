package com.cenfotec.p3.neuralforge_api.exception;

import com.cenfotec.p3.neuralforge_api.exception.response.ExceptionResponse;
import com.cenfotec.p3.neuralforge_api.exception.response.MultipleExceptionResponse;
import com.cenfotec.p3.neuralforge_api.exception.response.SingleExceptionResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private final String requestId = "test-request-id";

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void givenAuthorizationDeniedException_whenHandle_thenReturnUnauthorizedResponse() {
        // Given
        AuthorizationDeniedException exception = new AuthorizationDeniedException("Unauthorized");

        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            mockedMDC.when(() -> MDC.get("requestId")).thenReturn(requestId);

            // When
            ResponseEntity<ExceptionResponse> response = exceptionHandler.handleAuthorizationDeniedException(exception);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertTrue(response.getBody() instanceof SingleExceptionResponse);
            assertEquals("You don't have enough permissions to access this content.", ((SingleExceptionResponse) response.getBody()).getException());
            assertEquals(requestId, response.getBody().getId());
        }
    }

    @Test
    void givenResponseStatusException_whenHandle_thenReturnCorrectResponse() {
        // Given
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found");

        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            mockedMDC.when(() -> MDC.get("requestId")).thenReturn(requestId);

            // When
            ResponseEntity<ExceptionResponse> response = exceptionHandler.handleResponseStatusException(exception);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody() instanceof SingleExceptionResponse);
            assertEquals("Not Found", ((SingleExceptionResponse) response.getBody()).getException());
            assertEquals(requestId, response.getBody().getId());
        }
    }

    @Test
    void givenValidationException_whenHandle_thenReturnBadRequestResponse() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("field", "Invalid value")));

        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            mockedMDC.when(() -> MDC.get("requestId")).thenReturn(requestId);

            // When
            ResponseEntity<ExceptionResponse> response = exceptionHandler.handleValidationExceptions(exception);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof SingleExceptionResponse);
            assertEquals("Invalid value", ((SingleExceptionResponse) response.getBody()).getException());
            assertEquals(requestId, response.getBody().getId());
        }
    }

    @Test
    void givenMultipleValidationErrors_whenHandle_thenReturnMultipleErrorsResponse() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        when(bindingResult.getAllErrors()).thenReturn(List.of(
                new ObjectError("field1", "Error 1"),
                new ObjectError("field2", "Error 2")
        ));

        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            mockedMDC.when(() -> MDC.get("requestId")).thenReturn(requestId);

            // When
            ResponseEntity<ExceptionResponse> response = exceptionHandler.handleValidationExceptions(exception);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof MultipleExceptionResponse);
            assertEquals(List.of("Error 1", "Error 2"), ((MultipleExceptionResponse) response.getBody()).getException());
            assertEquals(requestId, response.getBody().getId());
        }
    }

    @Test
    void givenEntityExistsException_whenHandle_thenReturnBadRequestResponse() {
        // Given
        EntityExistsException exception = new EntityExistsException("Entity already exists");

        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            mockedMDC.when(() -> MDC.get("requestId")).thenReturn(requestId);

            // When
            ResponseEntity<ExceptionResponse> response = exceptionHandler.handleEntityExistsException(exception);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof SingleExceptionResponse);
            assertEquals("Entity already exists", ((SingleExceptionResponse) response.getBody()).getException());
            assertEquals(requestId, response.getBody().getId());
        }
    }

    @Test
    void givenExpiredJwtException_whenHandle_thenReturnUnauthorizedResponse() {
        // Given
        ExpiredJwtException exception = mock(ExpiredJwtException.class);

        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            mockedMDC.when(() -> MDC.get("requestId")).thenReturn(requestId);

            // When
            ResponseEntity<ExceptionResponse> response = exceptionHandler.handleExpiredJwtException(exception);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertTrue(response.getBody() instanceof SingleExceptionResponse);
            assertEquals("The JWT token sent has already expired.", ((SingleExceptionResponse) response.getBody()).getException());
            assertEquals(requestId, response.getBody().getId());
        }
    }

    @Test
    void givenGenericException_whenHandle_thenReturnInternalServerErrorResponse() {
        // Given
        Exception exception = new Exception("Something went wrong");

        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            mockedMDC.when(() -> MDC.get("requestId")).thenReturn(requestId);

            // When
            ResponseEntity<ExceptionResponse> response = exceptionHandler.handleGenericException(exception);

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.getBody() instanceof SingleExceptionResponse);
            assertEquals("Something went wrong", ((SingleExceptionResponse) response.getBody()).getException());
            assertEquals(requestId, response.getBody().getId());
        }
    }
}
