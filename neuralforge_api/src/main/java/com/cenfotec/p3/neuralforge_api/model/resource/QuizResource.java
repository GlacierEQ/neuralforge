package com.cenfotec.p3.neuralforge_api.model.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

/**
 * Resource class representing the data transfer object for a quiz.
 * Contains all necessary attributes for client-server communication.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResource {
    
    /**
     * Unique identifier for the quiz.
     */
    private String id;

    /**
     * ID of the user who created this quiz.
     */
    private String creatorUserId;
    
    /**
     * Title of the quiz.
     */
    @NotBlank(message = "Title is required")
    private String title;
    
    /**
     * Description of the quiz.
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    /**
     * The project ID this quiz belongs to.
     */
    @NotBlank(message = "Project ID is required")
    private String projectId;
    
    /**
     * The project type (learning, teaching, or programmed_goal)
     * This eliminates the need for separate API calls to determine project type
     */
    private String projectType;
    
    /**
     * List of questions in this quiz.
     */
    private List<QuizQuestionResource> questions;
    
    /**
     * Timestamp indicating when the quiz was created.
     */
    private Date createdAt;
    
    /**
     * Timestamp indicating when the quiz was last modified.
     */
    private Date lastModifiedAt;
    
    /**
     * Flag indicating if the quiz has been deleted.
     */
    private boolean isDeleted;
}
