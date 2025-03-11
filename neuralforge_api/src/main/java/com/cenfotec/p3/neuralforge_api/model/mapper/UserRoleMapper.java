package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;

public class UserRoleMapper {
    public UserRoleResource mapToResource(UserRoleEntity role){
        return  UserRoleResource.builder()
                .description(role.getDescription())
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public UserRoleEntity mapToEntity(UserRoleResource role){
        return UserRoleEntity.builder()
                .description(role.getDescription())
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
