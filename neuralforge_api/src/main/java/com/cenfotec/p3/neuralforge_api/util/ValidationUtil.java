package com.cenfotec.p3.neuralforge_api.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Utility class for performing field-level validation using Jakarta Validation API.
 * Allows triggering validation on specific fields of an object.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Component
public class ValidationUtil {

    /**
     * Validator factory for creating validator instances.
     */
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     * Validator instance for executing validations.
     */
    private static final Validator validator = factory.getValidator();

    /**
     * Triggers validation for a specific field in a given object.
     *
     * @param object The object to be validated.
     * @param fieldName The specific field to validate.
     * @param <T> The type of the object being validated.
     * @throws ConstraintViolationException If validation constraints are violated.
     */
    public <T> void triggerValidations(T object, String fieldName) {
        Set<ConstraintViolation<T>> violations = validator.validateProperty(object, fieldName);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
