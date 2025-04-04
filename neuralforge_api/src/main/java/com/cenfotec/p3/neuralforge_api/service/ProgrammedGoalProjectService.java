package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ProgrammedGoalProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProgrammedGoalProjectMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ProgrammedGoalProjectResource;
import com.cenfotec.p3.neuralforge_api.repository.ProgrammedGoalProjectRepository;
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
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class ProgrammedGoalProjectService {

    @Autowired
    private SelectedDaysService selectedDaysService;

    @Autowired
    private ProgrammedGoalProjectRepository programmedGoalProjectRepository;

    @Autowired
    private ProgrammedGoalProjectMapper programmedGoalProjectMapper;

    public void validateProjectOwnership(String projectOwnerId){
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentUser.getId().equals(projectOwnerId) && currentUser.getRole().getName() != UserRoleEnum.ROLE_ADMINISTRATOR){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the owner of the project or an admin is able to perform actions over this project");
        }
    }

    /**
     * Creates a new learning project.
     *
     * @param projectResource The {@link ProgrammedGoalProjectResource} containing the project details.
     * @return The created {@link ProgrammedGoalProjectResource} with assigned ID.
     * @throws ResponseStatusException if a project with the same name already exists.
     */
    public ProgrammedGoalProjectResource createProgrammedGoalProject(ProgrammedGoalProjectResource projectResource) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectResource.setCreatorUserId(user.getId());

        // Save SelectedDays first
        var savedSelectedDays = selectedDaysService.save(projectResource.getSelectedDays());

        // Map the resource to entity
        ProgrammedGoalProjectEntity entity = programmedGoalProjectMapper.mapToEntity(projectResource);

        // Inject the persisted SelectedDaysEntity
        entity.setSelectedDays(savedSelectedDays);

        // Save the complete project
        ProgrammedGoalProjectEntity savedEntity = programmedGoalProjectRepository.save(entity);

        return programmedGoalProjectMapper.mapToResource(savedEntity);
    }


    /**
     * Retrieves all learning projects.
     *
     * @return A list of {@link ProgrammedGoalProjectResource} objects.
     */
    public List<ProgrammedGoalProjectResource> getAllProgrammedGoalProjects() {
        return programmedGoalProjectRepository.findAll().stream()
                .map(programmedGoalProjectMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all learning projects created by the currently authenticated user.
     *
     * @return A list of {@link ProgrammedGoalProjectResource} objects created by the current user.
     */
    public List<ProgrammedGoalProjectResource> getCurrentUserProgrammedGoalProjects() {
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return programmedGoalProjectRepository.findByCreatorUserId(currentUser.getId()).stream()
                .map(programmedGoalProjectMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a learning project by its ID.
     *
     * @param id The ID of the learning project to retrieve.
     * @return The {@link ProgrammedGoalProjectResource} for the requested project.
     * @throws ResponseStatusException if the project is not found.
     */
    public ProgrammedGoalProjectResource getProgrammedGoalProjectById(String id) {
        ProgrammedGoalProjectEntity entity = programmedGoalProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "ProgrammedGoal project not found with ID: " + id));
        
        return programmedGoalProjectMapper.mapToResource(entity);
    }

    /**
     * Updates an existing learning project.
     *
     * @param id The ID of the learning project to update.
     * @param projectResource The {@link ProgrammedGoalProjectResource} containing updated details.
     * @return The updated {@link ProgrammedGoalProjectResource}.
     * @throws ResponseStatusException if the project is not found or name conflicts with an existing project.
     */
    public ProgrammedGoalProjectResource updateProgrammedGoalProject(String id, ProgrammedGoalProjectResource projectResource) {
        // Verify the project exists
        ProgrammedGoalProjectEntity existingEntity = programmedGoalProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "ProgrammedGoal project not found with ID: " + id));

        validateProjectOwnership(existingEntity.getCreatorUserId());

        // Update the entity with new values, preserving the ID
        projectResource.setId(id);
        ProgrammedGoalProjectEntity updatedEntity = programmedGoalProjectMapper.mapToEntity(projectResource);
        ProgrammedGoalProjectEntity savedEntity = programmedGoalProjectRepository.save(updatedEntity);
        return programmedGoalProjectMapper.mapToResource(savedEntity);
    }

    public ProgrammedGoalProjectResource toggleNotifications(String id) {

        // Verify the project exists
        ProgrammedGoalProjectEntity existingEntity = programmedGoalProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "ProgrammedGoal project not found with ID: " + id));

        validateProjectOwnership(existingEntity.getCreatorUserId());


        // Update the entity with new values, preserving the ID
        existingEntity.setNotify(!existingEntity.getNotify());

        ProgrammedGoalProjectEntity savedEntity = programmedGoalProjectRepository.save(existingEntity);
        return programmedGoalProjectMapper.mapToResource(savedEntity);
    }

    /**
     * Deletes a learning project by its ID.
     *
     * @param id The ID of the learning project to delete.
     * @throws ResponseStatusException if the project is not found.
     */
    public void deleteProgrammedGoalProject(String id) {
        ProgrammedGoalProjectEntity existingEntity = programmedGoalProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "ProgrammedGoal project not found with ID: " + id));

        validateProjectOwnership(existingEntity.getCreatorUserId());
        
        programmedGoalProjectRepository.deleteById(id);
    }
}