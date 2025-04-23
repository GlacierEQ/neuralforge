package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ProjectEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.QuizResource;
import com.cenfotec.p3.neuralforge_api.repository.ProjectRepository;
import com.cenfotec.p3.neuralforge_api.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting between {@link QuizEntity} and {@link QuizResource}.
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class QuizMapper {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private QuizQuestionRepository questionRepository;
    
    @Autowired
    private QuizQuestionMapper questionMapper;
    
    /**
     * Converts a quiz entity into its corresponding resource.
     *
     * @param entity The quiz entity to be mapped.
     * @return A resource containing the mapped quiz data.
     */
    public QuizResource mapToResource(QuizEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return QuizResource.builder()
                .id(entity.getId())
                .creatorUserId(entity.getCreatorUserId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .projectId(entity.getProject().getId())
                .projectType(entity.getProject().getProjectType().name())
                .questions(entity.getQuestions() != null ? entity.getQuestions().stream()
                        .map(questionMapper::mapToResource)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .isDeleted(entity.isDeleted())
                .build();
    }
    
    /**
     * Converts a quiz resource into its corresponding entity.
     *
     * @param resource The quiz resource to be mapped.
     * @return An entity containing the mapped quiz data.
     */
    public QuizEntity mapToEntity(QuizResource resource) {
        if (resource == null) {
            return null;
        }
        
        ProjectEntity project = projectRepository.findById(resource.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Project not found with id: " + resource.getProjectId()));
        
        QuizEntity entity = QuizEntity.builder()
                .id(resource.getId())
                .creatorUserId(resource.getCreatorUserId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .project(project)
                .isDeleted(resource.isDeleted())
                .build();
        
        if (resource.getCreatedAt() != null) {
            entity.setCreatedAt(resource.getCreatedAt());
        }
        
        if (resource.getLastModifiedAt() != null) {
            entity.setLastModifiedAt(resource.getLastModifiedAt());
        }
        
        return entity;
    }
}
