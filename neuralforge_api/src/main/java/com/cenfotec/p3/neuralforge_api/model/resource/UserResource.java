package com.cenfotec.p3.neuralforge_api.model.resource;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResource {

    private String id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email should be a valid email format"
    )
    private String email;

    private LocalDateTime createdAt;

    private UserRoleResource role;

    private Boolean status;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.,/?@#$%^&+=!]).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (.,/?@#$%^&+=!)"
    )
    private String password;
}
