package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizAttemptEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.QuizEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.QuizAttemptResource;
import com.cenfotec.p3.neuralforge_api.repository.QuizRepository;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting between {@link QuizAttemptEntity} and {@link QuizAttemptResource}.
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Component
public class QuizAttemptMapper {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private QuizUserAnswerMapper userAnswerMapper;
    
    /**
     * Converts a quiz attempt entity into its corresponding resource.
     *
     * @param entity The quiz attempt entity to be mapped.
     * @return A resource containing the mapped quiz attempt data.
     */
    public QuizAttemptResource mapToResource(QuizAttemptEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return QuizAttemptResource.builder()
                .id(entity.getId())
                .quizId(entity.getQuiz().getId())
                .quizTitle(entity.getQuiz().getTitle())
                .userId(entity.getUser().getId())
                .score(entity.getScore())
                .totalQuestions(entity.getTotalQuestions())
                .userAnswers(entity.getUserAnswers().stream()
                        .map(userAnswerMapper::mapToResource)
                        .collect(Collectors.toList()))
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }
    
    /**
     * Converts a quiz attempt resource into its corresponding entity.
     *
     * @param resource The quiz attempt resource to be mapped.
     * @return An entity containing the mapped quiz attempt data.
     */
    public QuizAttemptEntity mapToEntity(QuizAttemptResource resource) {
        if (resource == null) {
            return null;
        }
        
        QuizEntity quiz = quizRepository.findById(resource.getQuizId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Quiz not found with id: " + resource.getQuizId()));
        
        UserEntity user = userRepository.findById(resource.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "User not found with id: " + resource.getUserId()));
        
        QuizAttemptEntity entity = QuizAttemptEntity.builder()
                .id(resource.getId())
                .quiz(quiz)
                .user(user)
                .score(resource.getScore())
                .totalQuestions(resource.getTotalQuestions())
                .startedAt(resource.getStartedAt())
                .completedAt(resource.getCompletedAt())
                .build();
        
        return entity;
    }
}
