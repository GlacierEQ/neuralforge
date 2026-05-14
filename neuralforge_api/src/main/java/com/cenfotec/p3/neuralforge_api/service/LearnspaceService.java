package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.LearnspaceEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.LearnspaceMapper;
import com.cenfotec.p3.neuralforge_api.model.request.CreateLearnspaceRequest;
import com.cenfotec.p3.neuralforge_api.model.resource.LearnspaceResource;
import com.cenfotec.p3.neuralforge_api.repository.LearnspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearnspaceService {

    @Autowired
    private LearnspaceRepository learnspaceRepository;

    public LearnspaceResource crearLearnspace(CreateLearnspaceRequest request) {
        LearnspaceEntity entity = LearnspaceEntity.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .visibilidad(request.getVisibilidad())
                .build();

        return LearnspaceMapper.toResource(learnspaceRepository.save(entity));
    }

    public List<LearnspaceResource> listarLearnspaces() {
        return learnspaceRepository.findAll()
                .stream()
                .map(LearnspaceMapper::toResource)
                .collect(Collectors.toList());
    }
}
