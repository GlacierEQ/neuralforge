
package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.LearnspaceEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.LearnspaceResource;

public class LearnspaceMapper {

    public static LearnspaceResource toResource(LearnspaceEntity entity) {
        return LearnspaceResource.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .visibilidad(entity.getVisibilidad())
                .build();
    }
}

