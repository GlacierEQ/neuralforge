package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.VirtualClassEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.VirtualStudentEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.WeekEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.VirtualClassResource;
import com.cenfotec.p3.neuralforge_api.model.resource.VirtualStudentResource;
import com.cenfotec.p3.neuralforge_api.model.resource.WeekResource;

import java.util.ArrayList;
import java.util.List;

public class VirtualClassMapper {

    public static VirtualClassResource toResource(VirtualClassEntity entity) {
        if (entity == null) return null;

        VirtualClassResource resource = new VirtualClassResource();
        resource.setId(entity.getId());
        resource.setOwnerId(entity.getOwner().getId());
        resource.setTitle(entity.getTitle());
        resource.setDescription(entity.getDescription());

        // Using forEach instead of stream().map()
        List<WeekEntity> weeks = entity.getWeeks();
        if (weeks != null) {
            List<WeekResource> weekResources = new ArrayList<>();
            weeks.forEach(weekEntity -> weekResources.add(WeekMapper.toResource(weekEntity)));
            resource.setWeeks(weekResources);
        }

        List<VirtualStudentEntity> students = entity.getStudents();
        if (students != null) {
            List<VirtualStudentResource> studentResources = new ArrayList<>();
            students.forEach(studentEntity -> studentResources.add(VirtualStudentMapper.toResource(studentEntity)));
            resource.setStudents(studentResources);
        }

        return resource;
    }

    public static VirtualClassEntity toEntity(VirtualClassResource resource, UserEntity owner) {
        if (resource == null) return null;

        VirtualClassEntity virtualClass = VirtualClassEntity.builder()
                .id(resource.getId())
                .owner(owner)
                .title(resource.getTitle())
                .description(resource.getDescription())
                .build();

        // Using forEach instead of stream().map()
        if (resource.getWeeks() != null) {
            List<WeekEntity> weekEntities = new ArrayList<>();
            resource.getWeeks().forEach(weekResource -> {
                WeekEntity weekEntity = WeekMapper.toEntity(weekResource);
                weekEntity.setVirtualClass(virtualClass);
                weekEntities.add(weekEntity);
            });
            virtualClass.setWeeks(weekEntities);
        }

        if (resource.getStudents() != null) {
            List<VirtualStudentEntity> studentEntities = new ArrayList<>();

            resource.getStudents().forEach(studentResource -> {
                UserEntity user = new UserEntity();
                user.setId(studentResource.getUserId());
                studentEntities.add(VirtualStudentMapper.toEntity(studentResource, user));
            });
            virtualClass.setStudents(studentEntities);
        }

        return virtualClass;
    }

}
