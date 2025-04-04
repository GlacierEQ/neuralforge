package com.cenfotec.p3.neuralforge_api.exception.customTypes;

/**
 * Exception class representing email-related errors within the NeuralForge application.
 * Extends {@link NeuralForgeGenericException} to provide specific handling for email-related issues.
 *
 * This exception is typically thrown when there are validation errors, conflicts, or other
 * issues related to user email operations.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public class NeuralForgeEmailException extends NeuralForgeGenericException {

    /**
     * Constructs a new {@code NeuralForgeEmailException} with the specified detail message.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public NeuralForgeEmailException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code NeuralForgeEmailException} with the specified detail message and cause.
     *
     * @param message The detail message explaining the cause of the exception.
     * @param cause The underlying cause of the exception.
     */
    public NeuralForgeEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code NeuralForgeEmailException} with the specified cause.
     *
     * @param cause The underlying cause of the exception.
     */
    public NeuralForgeEmailException(Throwable cause) {
        super(cause);
    }
}
