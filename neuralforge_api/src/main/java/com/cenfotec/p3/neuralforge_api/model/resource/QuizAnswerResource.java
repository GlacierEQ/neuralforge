package com.cenfotec.p3.neuralforge_api.model.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Resource class representing the data transfer object for a quiz answer option.
 * Contains all necessary attributes for client-server communication.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerResource {
    
    /**
     * Unique identifier for the answer.
     */
    private String id;
    
    /**
     * The text content of the answer option.
     */
    @NotBlank(message = "Answer text is required")
    @Size(max = 500, message = "Answer text must not exceed 500 characters")
    private String answerText;
    
    /**
     * The question ID this answer belongs to.
     */
    private String questionId;
    
    /**
     * Indicates if this answer is the correct one for the question.
     */
    private boolean isCorrect;
    
    /**
     * Index determining the order of answers within a question.
     */
    private int answerOrder;
}
