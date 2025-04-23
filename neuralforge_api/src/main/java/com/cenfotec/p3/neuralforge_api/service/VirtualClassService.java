package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.VirtualClassEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.VirtualStudentEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.VirtualClassMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.NotificationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.VirtualClassResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import com.cenfotec.p3.neuralforge_api.repository.VirtualClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VirtualClassService {

    @Autowired
    private VirtualClassRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public List<VirtualClassResource> getAll() {
        return repository.findAll().stream()
                .map(VirtualClassMapper::toResource)
                .toList();
    }

    public VirtualClassResource getById(String id) {
        VirtualClassEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Virtual class not found"));

        return VirtualClassMapper.toResource(entity);
    }

    public VirtualClassResource create(VirtualClassResource resource) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VirtualClassEntity entity = VirtualClassMapper.toEntity(resource, user);
        return VirtualClassMapper.toResource(repository.save(entity));
    }

    public VirtualClassResource update(String id, VirtualClassResource resource) {
        VirtualClassEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Virtual class not found"));

        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isOwner = entity.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().getName() == UserRoleEnum.ROLE_ADMINISTRATOR;

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to modify this class");
        }

        VirtualClassEntity updated = VirtualClassMapper.toEntity(resource, entity.getOwner());
        updated.setId(entity.getId());
        return VirtualClassMapper.toResource(repository.save(updated));
    }

    public void delete(String id) {
        VirtualClassEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Virtual class not found"));

        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isOwner = entity.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().getName() == UserRoleEnum.ROLE_ADMINISTRATOR;

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to delete this class");
        }

        repository.delete(entity);
    }
    public void addStudentToClass(String classId, String studentId) {
        VirtualClassEntity virtualClass = repository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Virtual class not found"));

        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isOwner = virtualClass.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().getName() == UserRoleEnum.ROLE_ADMINISTRATOR;

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to modify this class");
        }

        boolean alreadyExists = virtualClass.getStudents().stream()
                .anyMatch(vs -> vs.getUser().getId().equals(student.getId()));

        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already enrolled in this class");
        }

        VirtualStudentEntity newStudent = VirtualStudentEntity.builder()
                .user(student)
                .grade(null)
                .virtualClass(virtualClass)
                .build();

        virtualClass.getStudents().add(newStudent);
        repository.save(virtualClass);

        NotificationResource notification = NotificationResource.builder()
                .userId(student.getId())
                .title("You’ve been added to a virtual class!")
                .description("You've been enrolled in: " + virtualClass.getTitle())
                .redirectTo("/virtual-class/" + virtualClass.getId())
                .build();

        notificationService.createNotification(notification);
    }

    public List<VirtualClassResource> getMyClasses() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return repository.findByOwnerId(currentUser.getId()).stream()
                .map(VirtualClassMapper::toResource)
                .toList();
    }


}

