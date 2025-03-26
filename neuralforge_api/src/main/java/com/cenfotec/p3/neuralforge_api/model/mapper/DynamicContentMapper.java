package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.DynamicContentResource;

/**
 * Mapper class for converting DynamicContentEntity to DynamicContentResource.
 */
public class DynamicContentMapper {

    public DynamicContentResource mapToResource(DynamicContentEntity entity) {
        if (entity == null) {
            return null;
        }

        DynamicContentResource resource = new DynamicContentResource();
        resource.setId(entity.getId());
        resource.setTitle(entity.getTitle());
        resource.setCreationDate(entity.getCreationDate());
        resource.setPath(entity.getPath());
        resource.setEmail(entity.getEmail());

        // Asignamos el campo type
        resource.setType(entity.getType());

        return resource;
    }

    public DynamicContentEntity mapToEntity(DynamicContentResource resource) {
        if (resource == null) {
            return null;
        }

        DynamicContentEntity entity = new DynamicContentEntity();
        entity.setId(resource.getId());
        entity.setTitle(resource.getTitle());
        entity.setCreationDate(resource.getCreationDate());
        entity.setPath(resource.getPath());
        entity.setEmail(resource.getEmail());

        // Asignamos el campo type
        entity.setType(resource.getType());

        return entity;
    }
}