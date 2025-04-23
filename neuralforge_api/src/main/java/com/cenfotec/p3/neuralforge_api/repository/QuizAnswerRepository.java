package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link QuizAnswerEntity} operations.
 * Provides database access methods for quiz answer-related functionality.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswerEntity, String> {
    
    /**
     * Finds all answers associated with a specific question.
     *
     * @param questionId The ID of the question to find answers for.
     * @return A list of answers associated with the question.
     */
    List<QuizAnswerEntity> findByQuestionId(String questionId);
    
    /**
     * Finds all answers associated with a specific question, ordered by answer order.
     *
     * @param questionId The ID of the question to find answers for.
     * @return A list of answers associated with the question, ordered by answer order.
     */
    List<QuizAnswerEntity> findByQuestionIdOrderByAnswerOrder(String questionId);
    
    /**
     * Finds the correct answer for a specific question.
     *
     * @param questionId The ID of the question to find the correct answer for.
     * @return The correct answer for the question.
     */
    QuizAnswerEntity findByQuestionIdAndIsCorrectTrue(String questionId);
}
