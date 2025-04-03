package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.ProjectMaterialEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.ProjectMaterialMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ProjectMaterialResource;
import com.cenfotec.p3.neuralforge_api.repository.ProjectMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing project materials.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Service
public class ProjectMaterialService {

    @Autowired
    private ProjectMaterialRepository projectMaterialRepository;

    @Autowired
    private ProjectMaterialMapper projectMaterialMapper;

    /**
     * Creates a new project material.
     *
     * @param material The project material resource to create.
     * @return The created project material resource.
     */
    @Transactional
    public ProjectMaterialResource createProjectMaterial(ProjectMaterialResource material) {
        ProjectMaterialEntity entity = projectMaterialMapper.mapToEntity(material);
        entity = projectMaterialRepository.save(entity);
        return projectMaterialMapper.mapToResource(entity);
    }

    /**
     * Updates an existing project material.
     *
     * @param id The ID of the project material to update.
     * @param material The updated project material resource.
     * @return The updated project material resource.
     */
    @Transactional
    public ProjectMaterialResource updateProjectMaterial(Long id, ProjectMaterialResource material) {
        ProjectMaterialEntity existingEntity = projectMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project material not found with id: " + id));

        existingEntity.setType(material.getType());
        existingEntity.setFileName(material.getFileName());
        existingEntity.setFileUrl(material.getFileUrl());
        existingEntity.setDescription(material.getDescription());
        existingEntity.setHyperlink(material.getHyperlink());

        existingEntity = projectMaterialRepository.save(existingEntity);
        return projectMaterialMapper.mapToResource(existingEntity);
    }

    /**
     * Retrieves a project material by its ID.
     *
     * @param id The ID of the project material to retrieve.
     * @return The project material resource.
     */
    public ProjectMaterialResource getProjectMaterial(Long id) {
        ProjectMaterialEntity entity = projectMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project material not found with id: " + id));
        return projectMaterialMapper.mapToResource(entity);
    }

    /**
     * Retrieves all project materials.
     *
     * @return A list of project material resources.
     */
    public List<ProjectMaterialResource> getAllProjectMaterials() {
        List<ProjectMaterialEntity> entities = projectMaterialRepository.findAll();
        return entities.stream()
                .map(projectMaterialMapper::mapToResource)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a project material by its ID.
     *
     * @param id The ID of the project material to delete.
     */
    @Transactional
    public void deleteProjectMaterial(Long id) {
        if (!projectMaterialRepository.existsById(id)) {
            throw new RuntimeException("Project material not found with id: " + id);
        }
        projectMaterialRepository.deleteById(id);
    }
} 