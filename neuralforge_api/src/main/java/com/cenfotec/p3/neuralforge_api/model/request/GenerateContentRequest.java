package com.cenfotec.p3.neuralforge_api.model.request;

import lombok.Data;

/**
 * Request class for generating dynamic content within the NeuralForge system.
 * Contains the necessary information to identify the project, material, title,
 * and content type for content generation.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Data
public class GenerateContentRequest {
    /**
     * The ID of the project for which to generate content.
     */
    private String projectId;
    
    /**
     * The ID of the material to use as a source for content generation.
     */
    private String materialId;
    
    /**
     * The title to assign to the generated content.
     */
    private String title;
    
    /**
     * The type of content to generate, corresponding to values in {@link com.cenfotec.p3.neuralforge_api.model.enums.DynamicContentTypeEnum}.
     */
    private String type;
}