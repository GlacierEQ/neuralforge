package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.DynamicContentEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.WeekEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.WeekResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeekMapper {

    public static WeekResource toResource(WeekEntity entity) {
        if (entity == null) return null;

        WeekResource resource = new WeekResource();
        resource.setId(entity.getId());
        resource.setTopic(entity.getTopic());
        resource.setVisible(entity.getVisible());
        resource.setContentIds(entity.getContent().stream()
                .map(DynamicContentEntity::getId)
                .collect(Collectors.toList()));
        return resource;
    }

    public static WeekEntity toEntity(WeekResource resource) {
        if (resource == null) return null;

        // NOTE: This maps content IDs only — you will need to resolve actual DynamicContentEntity objects via service/repo.
        List<DynamicContentEntity> dummyContent = new ArrayList<>();
        if (resource.getContentIds() != null) {
            for (String id : resource.getContentIds()) {
                DynamicContentEntity dce = new DynamicContentEntity();
                dce.setId(id);
                dummyContent.add(dce);
            }
        }

        return WeekEntity.builder()
                .id(resource.getId())
                .topic(resource.getTopic())
                .visible(resource.getVisible())
                .content(dummyContent)
                .build();
    }
}
