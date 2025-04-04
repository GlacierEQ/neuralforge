package com.cenfotec.p3.neuralforge_api.model.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Resource object for capturing password update input from users.
 * Contains the current password for verification and the new password with validation constraints.
 *
 * Used in the password update endpoint for authenticated users.
 *
 * @author You
 * @version 1.0
 */
@Data
public class PasswordUpdateResource {

    /**
     * The user's current password used for verification before allowing a password change.
     */
    @NotBlank(message = "Current password cannot be empty")
    private String currentPassword;

    /**
     * The new password that the user wants to set.
     * Must be strong and meet specific validation rules for security.
     */
    @NotBlank(message = "New password cannot be empty")
    @Size(min = 8, max = 20, message = "New password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.,/?@#$%^&+=!]).+$",
            message = "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String newPassword;
}
