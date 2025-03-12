package com.cenfotec.p3.neuralforge_api.model.resource;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents the user resource used for API responses and requests.
 * Contains user details, validation constraints, and role information.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResource {

    /**
     * Unique identifier for the user.
     */
    private String id;

    /**
     * First name of the user.
     * Must be between 2 and 50 characters long.
     */
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Email address of the user.
     * Must be in a valid email format.
     */
    @NotBlank(message = "Email cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email should be a valid email format"
    )
    private String email;

    /**
     * Timestamp indicating when the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * Role assigned to the user.
     */
    private UserRoleResource role;

    /**
     * Status of the user account (active/inactive).
     */
    private Boolean status;

    /**
     * User password.
     * Must be between 8 and 20 characters long and contain at least:
     * - One uppercase letter
     * - One lowercase letter
     * - One number
     * - One special character (.,/?@#$%^&+=!)
     */
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.,/?@#$%^&+=!]).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (.,/?@#$%^&+=!)"
    )
    private String password;
}
