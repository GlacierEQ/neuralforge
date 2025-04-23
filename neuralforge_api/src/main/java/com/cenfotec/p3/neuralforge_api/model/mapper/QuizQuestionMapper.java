package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.QuizQuestionEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.QuizQuestionResource;
import com.cenfotec.p3.neuralforge_api.repository.QuizAnswerRepository;
import com.cenfotec.p3.neuralforge_api.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting between {@link QuizQuestionEntity} and {@link QuizQuestionResource}.
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class QuizQuestionMapper {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuizAnswerRepository answerRepository;
    
    @Autowired
    private QuizAnswerMapper answerMapper;
    
    /**
     * Converts a quiz question entity into its corresponding resource.
     *
     * @param entity The quiz question entity to be mapped.
     * @return A resource containing the mapped quiz question data.
     */
    public QuizQuestionResource mapToResource(QuizQuestionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return QuizQuestionResource.builder()
                .id(entity.getId())
                .questionText(entity.getQuestionText())
                .quizId(entity.getQuiz().getId())
                .answers(entity.getAnswers().stream()
                        .map(answerMapper::mapToResource)
                        .collect(Collectors.toList()))
                .questionOrder(entity.getQuestionOrder())
                .explanation(entity.getExplanation())
                .build();
    }
    
    /**
     * Converts a quiz question resource into its corresponding entity.
     *
     * @param resource The quiz question resource to be mapped.
     * @return An entity containing the mapped quiz question data.
     */
    public QuizQuestionEntity mapToEntity(QuizQuestionResource resource) {
        if (resource == null) {
            return null;
        }
        
        // Only attempt to find the quiz if a quizId is provided
        QuizEntity quiz = null;
        if (resource.getQuizId() != null) {
            quiz = quizRepository.findById(resource.getQuizId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Quiz not found with id: " + resource.getQuizId()));
        }
        
        return QuizQuestionEntity.builder()
                .id(resource.getId())
                .questionText(resource.getQuestionText())
                .quiz(quiz)
                .questionOrder(resource.getQuestionOrder())
                .explanation(resource.getExplanation())
                .build();
    }
}
