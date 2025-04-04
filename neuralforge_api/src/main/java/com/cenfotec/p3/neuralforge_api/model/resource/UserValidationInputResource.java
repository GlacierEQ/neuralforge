package com.cenfotec.p3.neuralforge_api.model.resource;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationInputResource {
    @NotBlank(message = "Email cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email should be a valid email format"
    )
    private String email;

    @Min(value = 100000, message = "Verification code must be at least 6 digits")
    @Max(value = 999999, message = "Verification code must be at most 6 digits")
    private int verificationCode;
}
