package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.VirtualStudentEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.VirtualStudentResource;

public class VirtualStudentMapper {

    public static VirtualStudentResource toResource(VirtualStudentEntity entity) {
        if (entity == null) return null;

        VirtualStudentResource resource = new VirtualStudentResource();
        resource.setId(entity.getId());
        resource.setUserId(entity.getUser().getId());
        resource.setGrade(entity.getGrade());
        return resource;
    }

    public static VirtualStudentEntity toEntity(VirtualStudentResource resource, UserEntity user) {
        if (resource == null) return null;

        return VirtualStudentEntity.builder()
                .id(resource.getId())
                .user(user)
                .grade(resource.getGrade())
                .build();
    }
}
