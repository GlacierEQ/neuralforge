package com.cenfotec.p3.neuralforge_api.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response for a single exception.
 * Implements {@link ExceptionResponse} to provide a standardized structure for error handling.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleExceptionResponse implements ExceptionResponse {

    /**
     * Unique identifier for the exception response.
     */
    private String id;

    /**
     * Message describing the exception.
     */
    private String exception;
}
