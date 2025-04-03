package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProjectMaterialMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.TeachingProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.TeachingProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.TeachingProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing teaching projects.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Service
public class TeachingProjectService {

    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;

    @Autowired
    private TeachingProjectRepository teachingProjectRepository;

    @Autowired
    private TeachingProjectMapper teachingProjectMapper;

    @Autowired
    private ProjectMaterialMapper projectMaterialMapper;
    
    @Autowired
    private SelectedDaysService selectedDaysService;

    /**
     * Creates a new teaching project.
     *
     * @param teachingProject The teaching project resource to create.
     * @return The created teaching project resource.
     */
    @Transactional
    public TeachingProjectResource createTeachingProject(TeachingProjectResource teachingProject) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        teachingProject.setCreatorUserId(user.getId());
        
        // Save SelectedDays first
        var savedSelectedDays = selectedDaysService.save(teachingProject.getSelectedDays());
        
        TeachingProjectEntity entity = teachingProjectMapper.mapToEntity(teachingProject);
        
        // Inject the persisted SelectedDaysEntity
        entity.setSelectedDays(savedSelectedDays);
        
        entity = teachingProjectRepository.save(entity);
        return teachingProjectMapper.mapToResource(entity);
    }

    /**
     * Updates an existing teaching project.
     *
     * @param id The ID of the teaching project to update.
     * @param teachingProject The updated teaching project resource.
     * @return The updated teaching project resource.
     */
    @Transactional
    public TeachingProjectResource updateTeachingProject(String id, TeachingProjectResource teachingProject) {
        TeachingProjectEntity existingEntity = teachingProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + id));

        validateProjectOwnership(existingEntity.getCreatorUserId());

        teachingProject.setId(id);
        
        // Update SelectedDays if provided
        if (teachingProject.getSelectedDays() != null) {
            var savedSelectedDays = selectedDaysService.save(teachingProject.getSelectedDays());
            teachingProject.setSelectedDays(savedSelectedDays);
        }
        
        TeachingProjectEntity updatedEntity = teachingProjectMapper.mapToEntity(teachingProject);
        TeachingProjectEntity savedEntity = teachingProjectRepository.save(updatedEntity);
        return teachingProjectMapper.mapToResource(savedEntity);
    }

    /**
     * Retrieves a teaching project by its ID.
     *
     * @param id The ID of the teaching project to retrieve.
     * @return The teaching project resource.
     */
    public TeachingProjectResource getTeachingProject(String id) {
        TeachingProjectEntity entity = teachingProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + id));
        return teachingProjectMapper.mapToResource(entity);
    }

    /**
     * Retrieves all teaching projects.
     *
     * @return A list of teaching project resources.
     */
    public List<TeachingProjectResource> getAllTeachingProjects() {
        List<TeachingProjectEntity> entities = teachingProjectRepository.findAll();
        return entities.stream()
                .map(teachingProjectMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all teaching projects created by the currently authenticated user.
     *
     * @return A list of teaching project resources created by the current user.
     */
    public List<TeachingProjectResource> getCurrentUserTeachingProjects() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return teachingProjectRepository.findByCreatorUserId(currentUser.getId()).stream()
                .map(teachingProjectMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a teaching project by its ID.
     *
     * @param id The ID of the teaching project to delete.
     */
    @Transactional
    public void deleteTeachingProject(String id) {
        TeachingProjectEntity existingEntity = teachingProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + id));

        validateProjectOwnership(existingEntity.getCreatorUserId());
        
        teachingProjectRepository.deleteById(id);
    }

    /**
     * Deletes a material from a teaching project.
     *
     * @param projectId The ID of the teaching project.
     * @param materialId The ID of the material to delete.
     */
    @Transactional
    public void deleteMaterial(String projectId, String materialId) {
        TeachingProjectEntity project = teachingProjectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Teaching project not found with id: " + projectId));

        validateProjectOwnership(project.getCreatorUserId());

        project.getMaterials().removeIf(material -> material.getId().equals(materialId));
        teachingProjectRepository.save(project);
    }

    /**
     * Validates that the current user owns the project or is an administrator.
     *
     * @param projectOwnerId The ID of the project owner.
     * @throws ResponseStatusException if the user is not authorized.
     */
    private void validateProjectOwnership(String projectOwnerId) {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentUser.getId().equals(projectOwnerId) && currentUser.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "Only the owner of the project or an admin is able to perform actions over this project");
        }
    }

    /**
     * Uploads a file to the server.
     *
     * @param file The file to upload.
     * @return The name of the uploaded file.
     * @throws IOException If there is an error uploading the file.
     */
    private String uploadFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return fileName;
    }
} 