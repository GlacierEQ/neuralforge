package com.cenfotec.p3.neuralforge_api.exception.response;

/**
 * Interface for standardizing exception response structures.
 * Provides a method to retrieve a unique identifier for the exception.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public interface ExceptionResponse {

    /**
     * Retrieves the unique identifier of the exception.
     *
     * @return A {@link String} representing the exception ID.
     */
    String getId();
}
