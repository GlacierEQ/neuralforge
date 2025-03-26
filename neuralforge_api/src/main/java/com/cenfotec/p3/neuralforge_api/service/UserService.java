package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.exception.customTypes.NeuralForgeEmailException;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationInputResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import com.cenfotec.p3.neuralforge_api.repository.UserValidationRepository;
import com.cenfotec.p3.neuralforge_api.util.ValidationUtil;
import jakarta.persistence.EntityExistsException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for managing user operations such as creation, retrieval, and updates.
 * Handles user role assignment, password encoding, validation, and email notifications.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class UserService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserRoleService userRoleService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserValidationService userValidationService;

    @Autowired
    protected EmailService emailService;

    @Autowired
    protected ValidationUtil validationUtil;

    /**
     * Mapper instance for handling user entity transformations.
     */
    protected final UserMapper userMapper = new UserMapper();

    /**
     * Creates a new user account, assigns a default role, and sends a verification email.
     *
     * @param inputUser The {@link UserResource} containing user details.
     * @return The created {@link UserResource}.
     * @throws NeuralForgeEmailException If there is an issue sending the verification email.
     */
    public UserResource createUser(UserResource inputUser) throws NeuralForgeEmailException {
        if (userRepository.existsByEmail(inputUser.getEmail())) {
            throw new EntityExistsException("A user is already registered with this email address.");
        }

        UserRoleResource basicRole = userRoleService.getRoleByEnum(UserRoleEnum.ROLE_STUDENT);
        inputUser.setPassword(passwordEncoder.encode(inputUser.getPassword()));
        inputUser.setRole(basicRole);

        UserEntity storedUser = userRepository.save(userMapper.mapToEntity(inputUser));
        UserValidationResource storedUserValidation = userValidationService.createUserValidation(storedUser);

        emailService.sendUserVerificationEmail(storedUser, storedUserValidation.getVerificationCode());

        return userMapper.mapToResource(storedUser);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of {@link UserResource} representing all users.
     */
    public List<UserResource> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user.
     * @return The corresponding {@link UserResource}.
     */
    public UserResource getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no user associated to this email."));
        return userMapper.mapToResource(user);
    }

    /**
     * Updates user information while ensuring proper validation and security.
     *
     * @param email The email of the user to update.
     * @param inputUser The {@link UserResource} containing updated information.
     * @return The updated {@link UserResource}.
     */
    public UserResource handledUserUpdate(String email, UserResource inputUser) {
        if (!userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no user associated to this email.");
        }

        if (!Strings.isBlank(inputUser.getPassword())) {
            validationUtil.triggerValidations(inputUser, "password");
            inputUser.setPassword(passwordEncoder.encode(inputUser.getPassword()));
        }

        UserEntity user = userMapper.mapToEntity(inputUser);

        userRepository.updateUserIgnoringNulls(
                email,
                user.getName(),
                user.getLastName(),
                user.getPassword(),
                user.getVerified(),
                user.getRole() != null ? user.getRole().getId() : null,
                user.getStatus()
        );

        return getUserByEmail(email);
    }

    /**
     * Updates a user entity without validation constraints.
     *
     * @param user The {@link UserResource} containing user details.
     * @return The updated {@link UserEntity}.
     */
    private UserEntity rawUserUpdate(UserResource user) {
        return userRepository.save(userMapper.mapToEntity(user));
    }

    /**
     * Validates a user's registration using an input verification code.
     *
     * @param validationInput The {@link UserValidationInputResource} containing validation details.
     */
    public void validateInitialRegister(UserValidationInputResource validationInput) {
        UserResource user = getUserByEmail(validationInput.getEmail());
        userValidationService.validateInputCode(validationInput);
        user.setVerified(true);
        rawUserUpdate(user);
    }

    /**
     * Retrieves the currently authenticated user's information.
     *
     * @return The {@link UserResource} containing the current user's details.
     */
    public UserResource getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return userMapper.mapToResource(user);
    }

    /**
     * Updates the current user's profile information.
     * Only allows updating non-sensitive fields like first name and last name.
     *
     * @param inputUser The {@link UserResource} containing updated profile information.
     * @return The updated {@link UserResource}.
     */
    public UserResource updateCurrentUserProfile(UserResource inputUser) {
        // Get the current authenticated user's email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Create a limited user resource with only the fields we want to update
        UserResource limitedUser = new UserResource();
        limitedUser.setName(inputUser.getName());
        limitedUser.setLastName(inputUser.getLastName());

        // Use the existing handledUserUpdate method to perform the update
        // This will handle validation, error checking, and the actual update
        return handledUserUpdate(email, limitedUser);
    }

    /**
     * Deletes the currently authenticated user's account.
     * Retrieves the current user from the security context and removes them from the database.
     *
     * @throws ResponseStatusException if the user is not found.
     */
    public void deleteCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRepository.delete(user);
        // After deletion, the user will still be authenticated for the current request
        // The client-side should handle logging out and redirecting after successful deletion
    }
}
