package com.cenfotec.p3.neuralforge_api.model.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Resource class representing the data transfer object for a quiz question.
 * Contains all necessary attributes for client-server communication.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionResource {
    
    /**
     * Unique identifier for the question.
     */
    private String id;
    
    /**
     * The text content of the question.
     */
    @NotBlank(message = "Question text is required")
    @Size(max = 1000, message = "Question text must not exceed 1000 characters")
    private String questionText;
    
    /**
     * The quiz ID this question belongs to.
     */
    private String quizId;
    
    /**
     * List of possible answers for this question.
     */
    private List<QuizAnswerResource> answers;
    
    /**
     * Index determining the order of questions within a quiz.
     */
    private int questionOrder;
    
    /**
     * Explanation text that explains why the correct answer is correct.
     */
    @Size(max = 1000, message = "Explanation must not exceed 1000 characters")
    private String explanation;
}
