package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.WeekEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.WeekMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.WeekResource;
import com.cenfotec.p3.neuralforge_api.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WeekService {

    @Autowired
    private WeekRepository repository;

    public List<WeekResource> getAll() {
        return repository.findAll().stream()
                .map(WeekMapper::toResource)
                .toList();
    }

    public WeekResource getById(String id) {
        WeekEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Week not found"));
        return WeekMapper.toResource(entity);
    }

    public WeekResource create(WeekResource resource) {
        WeekEntity entity = WeekMapper.toEntity(resource);
        return WeekMapper.toResource(repository.save(entity));
    }

    public void delete(String id) {
        WeekEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Week not found"));
        repository.delete(entity);
    }
}
