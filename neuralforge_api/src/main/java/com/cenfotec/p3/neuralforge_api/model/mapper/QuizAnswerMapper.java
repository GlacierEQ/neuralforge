package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizAnswerEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.QuizQuestionEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.QuizAnswerResource;
import com.cenfotec.p3.neuralforge_api.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Mapper class responsible for converting between {@link QuizAnswerEntity} and {@link QuizAnswerResource}.
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class QuizAnswerMapper {
    
    @Autowired
    private QuizQuestionRepository questionRepository;
    
    /**
     * Converts a quiz answer entity into its corresponding resource.
     *
     * @param entity The quiz answer entity to be mapped.
     * @return A resource containing the mapped quiz answer data.
     */
    public QuizAnswerResource mapToResource(QuizAnswerEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return QuizAnswerResource.builder()
                .id(entity.getId())
                .answerText(entity.getAnswerText())
                .questionId(entity.getQuestion().getId())
                .isCorrect(entity.isCorrect())
                .answerOrder(entity.getAnswerOrder())
                .build();
    }
    
    /**
     * Converts a quiz answer resource into its corresponding entity.
     *
     * @param resource The quiz answer resource to be mapped.
     * @return An entity containing the mapped quiz answer data.
     */
    public QuizAnswerEntity mapToEntity(QuizAnswerResource resource) {
        if (resource == null) {
            return null;
        }
        
        // Only attempt to find the question if a questionId is provided
        QuizQuestionEntity question = null;
        if (resource.getQuestionId() != null) {
            question = questionRepository.findById(resource.getQuestionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Question not found with id: " + resource.getQuestionId()));
        }
        
        return QuizAnswerEntity.builder()
                .id(resource.getId())
                .answerText(resource.getAnswerText())
                .question(question)
                .isCorrect(resource.isCorrect())
                .answerOrder(resource.getAnswerOrder())
                .build();
    }
}
