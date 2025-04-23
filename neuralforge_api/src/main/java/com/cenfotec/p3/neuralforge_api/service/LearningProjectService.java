package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.LearningProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.LearningProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.LearningProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.LearningProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for managing learning project operations such as creation,
 * retrieval, updates, and deletion.
 * 
 * This service handles the business logic related to learning-focused projects,
 * delegating data access operations to the repository layer and using mappers
 * for entity-resource conversions.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Service
public class LearningProjectService {

    @Autowired
    private LearningProjectRepository learningProjectRepository;

    @Autowired
    private LearningProjectMapper learningProjectMapper;

    /**
     * Creates a new learning project.
     *
     * @param projectResource The {@link LearningProjectResource} containing the project details.
     * @return The created {@link LearningProjectResource} with assigned ID.
     * @throws ResponseStatusException if a project with the same name already exists.
     */
    public LearningProjectResource createLearningProject(LearningProjectResource projectResource) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectResource.setCreatorUserId(user.getId());

        LearningProjectEntity entity = learningProjectMapper.mapToEntity(projectResource);
        LearningProjectEntity savedEntity = learningProjectRepository.save(entity);

        return learningProjectMapper.mapToResource(savedEntity);
    }

    /**
     * Retrieves all learning projects.
     *
     * @return A list of {@link LearningProjectResource} objects.
     */
    public List<LearningProjectResource> getAllLearningProjects() {
        return learningProjectRepository.findAll().stream()
                .map(learningProjectMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all learning projects created by the currently authenticated user.
     *
     * @return A list of {@link LearningProjectResource} objects created by the current user.
     */
    public List<LearningProjectResource> getCurrentUserLearningProjects() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return learningProjectRepository.findByCreatorUserId(currentUser.getId()).stream()
                .map(learningProjectMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a learning project by its ID.
     *
     * @param id The ID of the learning project to retrieve.
     * @return The {@link LearningProjectResource} for the requested project.
     * @throws ResponseStatusException if the project is not found.
     */
    public LearningProjectResource getLearningProjectById(String id) {

        LearningProjectEntity entity = learningProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Learning project not found with ID: " + id));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getId().equals(entity.getCreatorUserId()) && user.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to visualize this project");
        }

        return learningProjectMapper.mapToResource(entity);
    }

    /**
     * Updates an existing learning project.
     *
     * @param id The ID of the learning project to update.
     * @param projectResource The {@link LearningProjectResource} containing updated details.
     * @return The updated {@link LearningProjectResource}.
     * @throws ResponseStatusException if the project is not found or name conflicts with an existing project.
     */
    public LearningProjectResource updateLearningProject(String id, LearningProjectResource projectResource) {
        // Verify the project exists
        LearningProjectEntity existingEntity = learningProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Learning project not found with ID: " + id));
     
        // Update the entity with new values, preserving the ID
        projectResource.setId(id);
        LearningProjectEntity updatedEntity = learningProjectMapper.mapToEntity(projectResource);
        LearningProjectEntity savedEntity = learningProjectRepository.save(updatedEntity);
        return learningProjectMapper.mapToResource(savedEntity);
    }

    /**
     * Deletes a learning project by its ID.
     *
     * @param id The ID of the learning project to delete.
     * @throws ResponseStatusException if the project is not found.
     */
    public void deleteLearningProject(String id) {
        if (!learningProjectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Learning project not found with ID: " + id);
        }
        
        learningProjectRepository.deleteById(id);
    }
}