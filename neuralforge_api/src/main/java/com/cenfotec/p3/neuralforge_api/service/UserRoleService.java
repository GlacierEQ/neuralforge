package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserRoleMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import com.cenfotec.p3.neuralforge_api.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsible for managing user roles.
 * Provides methods to retrieve user roles based on enumerated values.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class UserRoleService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserRoleRepository userRoleRepository;

    protected final UserRoleMapper userRoleMapper = new UserRoleMapper();

    /**
     * Retrieves a user role based on the specified {@link UserRoleEnum}.
     *
     * @param role The {@link UserRoleEnum} representing the role to retrieve.
     * @return A {@link UserRoleResource} containing role details.
     * @throws ResponseStatusException If the role is not found in the database.
     */
    public UserRoleResource getRoleByEnum(UserRoleEnum role) {
        return userRoleMapper.mapToResource(
                userRoleRepository
                        .findByName(role)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"))
        );
    }

    /**
     * Retrieves all roles.
     *
     * @return A list of {@link UserRoleResource} containing all available roles.
     */
    public List<UserRoleResource> getAllRoles() {
        List<UserRoleEntity> roles = userRoleRepository.findAll();
        return roles.stream()
                .map(userRoleMapper::mapToResource)
                .collect(Collectors.toList());
    }

    public Optional<UserRoleEntity> getRoleById(String id) {
        return userRoleRepository.findById(id);
    }
}
