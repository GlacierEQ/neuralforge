package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;

/**
 * Mapper class responsible for converting between {@link UserRoleEntity} and {@link UserRoleResource}.
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public class UserRoleMapper {

    /**
     * Converts a {@link UserRoleEntity} into a {@link UserRoleResource}.
     *
     * @param role The {@link UserRoleEntity} to be mapped.
     * @return A {@link UserRoleResource} containing the mapped role data.
     */
    public UserRoleResource mapToResource(UserRoleEntity role) {
        if (role == null) return null;
        return UserRoleResource.builder()
                .description(role.getDescription())
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    /**
     * Converts a {@link UserRoleResource} into a {@link UserRoleEntity}.
     *
     * @param role The {@link UserRoleResource} to be mapped.
     * @return A {@link UserRoleEntity} containing the mapped role data.
     */
    public UserRoleEntity mapToEntity(UserRoleResource role) {
        if (role == null) return null;
        return UserRoleEntity.builder()
                .description(role.getDescription())
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
