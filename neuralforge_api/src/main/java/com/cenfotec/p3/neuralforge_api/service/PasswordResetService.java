package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.PasswordResetRequestResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Service responsible for handling password reset requests and processing password updates.
 * This includes generating a reset token, validating it, and updating the user's password.
 *
 * @author Fabian Vargas
 * @version 1.0
 */
@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService; // Service for generating and validating JWT-based reset tokens

    /**
     * Initiates a password reset request by generating a reset token and sending it via email.
     *
     * @param request Contains the email of the user requesting the password reset.
     * @throws NeuralForgeEmailException If there is an issue sending the reset email.
     */
    public void requestPasswordReset(PasswordResetRequestResource request) throws NeuralForgeEmailException {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());

        if (!userOpt.isPresent()) {
            throw new RuntimeException("The email is not registered in the system.");
        }

        UserEntity user = userOpt.get();
        String token = jwtService.generatePasswordResetToken(user.getId()); // Generate reset token
        emailService.sendPasswordResetEmail(user, token); // Send reset email with the token
    }

    /**
     * Resets the user's password using the provided token and new password.
     *
     * @param token      The password reset token.
     * @param newPassword The new password chosen by the user.
     */
    public void resetPassword(String token, String newPassword) {
        String userId;
        try {
            userId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class)); // Extract user ID from token
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token.");
        }

        Optional<UserEntity> userOpt = userRepository.findById(userId);

        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found.");
        }

        UserEntity user = userOpt.get();
        String hashedPassword = passwordEncoder.encode(newPassword); // Encrypt new password
        user.setPassword(hashedPassword);

        userRepository.save(user); // Save updated password in database
    }
}