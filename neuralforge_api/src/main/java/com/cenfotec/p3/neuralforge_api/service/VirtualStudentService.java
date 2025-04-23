package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.VirtualStudentEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.VirtualStudentMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.VirtualStudentResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import com.cenfotec.p3.neuralforge_api.repository.VirtualStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VirtualStudentService {
    @Autowired
    private VirtualStudentRepository repository;

    @Autowired
    private UserRepository userRepository;

    public List<VirtualStudentResource> getAll() {
        return repository.findAll().stream()
                .map(VirtualStudentMapper::toResource)
                .toList();
    }

    public VirtualStudentResource getById(String id) {
        VirtualStudentEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Virtual student not found"));
        return VirtualStudentMapper.toResource(entity);
    }

    public VirtualStudentResource create(VirtualStudentResource resource) {
        UserEntity user = userRepository.findById(resource.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        VirtualStudentEntity entity = VirtualStudentMapper.toEntity(resource, user);
        return VirtualStudentMapper.toResource(repository.save(entity));
    }

    public void delete(String id) {
        VirtualStudentEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Virtual student not found"));
        repository.delete(entity);
    }
}
