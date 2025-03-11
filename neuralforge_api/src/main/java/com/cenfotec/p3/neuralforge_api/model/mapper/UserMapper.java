package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;

public class UserMapper {

    private final UserRoleMapper userRoleMapper = new UserRoleMapper();

    public UserResource mapToResource(UserEntity user){
        return UserResource.builder()
                .id(user.getId())
                .role(userRoleMapper.mapToResource(user.getRole()))
                .createdAt(user.getCreatedAt())
                .email(user.getEmail())
                .status(user.getStatus())
                .lastName(user.getLastName())
                .name(user.getName())
                .build();
    }

    public UserEntity mapToEntity(UserResource user){
        return UserEntity.builder()
                .id(user.getId())
                .role(userRoleMapper.mapToEntity(user.getRole()))
                .createdAt(user.getCreatedAt())
                .email(user.getEmail())
                .status(user.getStatus())
                .lastName(user.getLastName())
                .name(user.getName())
                .password(user.getPassword())
                .build();
    }
}
