package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for managing user operations such as creation and retrieval.
 * Handles user role assignment, password encoding, and database interactions.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserMapper userMapper = new UserMapper();

    /**
     * Creates a new user in the system.
     * If the user already exists, throws an {@link EntityExistsException}.
     * The new user is assigned the default role of {@code ROLE_STUDENT}.
     * The password is encoded before saving.
     *
     * @param user The {@link UserResource} containing user details.
     * @return The created user as a {@link UserResource}.
     * @throws EntityExistsException If a user with the provided email already exists.
     */
    public UserResource createUser(UserResource user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException("A user is already registered with this email address.");
        }

        UserRoleResource basicRole = userRoleService.getRoleByEnum(UserRoleEnum.ROLE_STUDENT);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(basicRole);

        UserEntity userEntity = userMapper.mapToEntity(user);

        return userMapper.mapToResource(userRepository.save(userEntity));
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
}
