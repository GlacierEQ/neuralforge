package com.cenfotec.p3.neuralforge_api.exception.customTypes;

/**
 * Generic exception class for the NeuralForge application.
 * Serves as the base exception for all custom exceptions within the system.
 *
 * This class extends {@link Exception} and provides constructors for various
 * exception scenarios, allowing specific custom exceptions to extend from it.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public class NeuralForgeGenericException extends Exception {

    /**
     * Constructs a new {@code NeuralForgeGenericException} with the specified detail message.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public NeuralForgeGenericException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code NeuralForgeGenericException} with the specified detail message and cause.
     *
     * @param message The detail message explaining the cause of the exception.
     * @param cause The underlying cause of the exception.
     */
    public NeuralForgeGenericException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code NeuralForgeGenericException} with the specified cause.
     *
     * @param cause The underlying cause of the exception.
     */
    public NeuralForgeGenericException(Throwable cause) {
        super(cause);
    }
}
