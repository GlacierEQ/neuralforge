package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;

/**
 * Mapper class responsible for converting between {@link UserEntity} and {@link UserResource}.
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public class UserMapper {

    protected final UserRoleMapper userRoleMapper = new UserRoleMapper();

    /**
     * Converts a {@link UserEntity} into a {@link UserResource}.
     *
     * @param user The {@link UserEntity} to be mapped.
     * @return A {@link UserResource} containing the mapped user data.
     */
    public UserResource mapToResource(UserEntity user) {
        return UserResource.builder()
                .id(user.getId())
                .role(userRoleMapper.mapToResource(user.getRole()))
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .lastPasswordChangeAt(user.getLastPasswordChangeAt())
                .email(user.getEmail())
                .status(user.getStatus())
                .lastName(user.getLastName())
                .name(user.getName())
                .verified(user.getVerified())
                .build();
    }

    /**
     * Converts a {@link UserResource} into a {@link UserEntity}.
     *
     * @param user The {@link UserResource} to be mapped.
     * @return A {@link UserEntity} containing the mapped user data.
     */
    public UserEntity mapToEntity(UserResource user) {
        return UserEntity.builder()
                .id(user.getId())
                .role(userRoleMapper.mapToEntity(user.getRole()))
                .createdAt(user.getCreatedAt())
                .lastPasswordChangeAt(user.getLastPasswordChangeAt())
                .email(user.getEmail())
                .status(user.getStatus())
                .lastName(user.getLastName())
                .name(user.getName())
                .password(user.getPassword())
                .verified(user.getVerified())
                .build();
    }
}
