package com.cenfotec.p3.neuralforge_api.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a response for multiple exceptions.
 * Implements {@link ExceptionResponse} to provide a standardized structure for error handling.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultipleExceptionResponse implements ExceptionResponse {

    /**
     * Unique identifier for the exception response.
     */
    private String id;

    /**
     * List of error messages associated with the exception.
     */
    private List<String> exception;
}
