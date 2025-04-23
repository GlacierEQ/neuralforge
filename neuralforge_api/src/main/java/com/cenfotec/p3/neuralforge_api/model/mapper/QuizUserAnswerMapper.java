package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizAnswerEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.QuizAttemptEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.QuizQuestionEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.QuizUserAnswerEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.QuizUserAnswerResource;
import com.cenfotec.p3.neuralforge_api.repository.QuizAnswerRepository;
import com.cenfotec.p3.neuralforge_api.repository.QuizAttemptRepository;
import com.cenfotec.p3.neuralforge_api.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Mapper class responsible for converting between {@link QuizUserAnswerEntity} and {@link QuizUserAnswerResource}.
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class QuizUserAnswerMapper {
    
    @Autowired
    private QuizAttemptRepository attemptRepository;
    
    @Autowired
    private QuizQuestionRepository questionRepository;
    
    @Autowired
    private QuizAnswerRepository answerRepository;
    
    /**
     * Converts a quiz user answer entity into its corresponding resource.
     *
     * @param entity The quiz user answer entity to be mapped.
     * @return A resource containing the mapped quiz user answer data.
     */
    public QuizUserAnswerResource mapToResource(QuizUserAnswerEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return QuizUserAnswerResource.builder()
                .id(entity.getId())
                .attemptId(entity.getAttempt().getId())
                .questionId(entity.getQuestion().getId())
                .questionText(entity.getQuestion().getQuestionText())
                .selectedAnswerId(entity.getSelectedAnswer().getId())
                .selectedAnswerText(entity.getSelectedAnswer().getAnswerText())
                .isCorrect(entity.isCorrect())
                .build();
    }
    
    /**
     * Converts a quiz user answer resource into its corresponding entity.
     *
     * @param resource The quiz user answer resource to be mapped.
     * @return An entity containing the mapped quiz user answer data.
     */
    public QuizUserAnswerEntity mapToEntity(QuizUserAnswerResource resource) {
        if (resource == null) {
            return null;
        }
        
        QuizAttemptEntity attempt = attemptRepository.findById(resource.getAttemptId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Attempt not found with id: " + resource.getAttemptId()));
        
        QuizQuestionEntity question = questionRepository.findById(resource.getQuestionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Question not found with id: " + resource.getQuestionId()));
        
        QuizAnswerEntity selectedAnswer = answerRepository.findById(resource.getSelectedAnswerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Answer not found with id: " + resource.getSelectedAnswerId()));
        
        return QuizUserAnswerEntity.builder()
                .id(resource.getId())
                .attempt(attempt)
                .question(question)
                .selectedAnswer(selectedAnswer)
                .isCorrect(resource.isCorrect())
                .build();
    }
}
