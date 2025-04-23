package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizUserAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link QuizUserAnswerEntity} operations.
 * Provides database access methods for quiz user answer-related functionality.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Repository
public interface QuizUserAnswerRepository extends JpaRepository<QuizUserAnswerEntity, String> {
    
    /**
     * Finds all user answers for a specific attempt.
     *
     * @param attemptId The ID of the attempt to find user answers for.
     * @return A list of user answers for the attempt.
     */
    List<QuizUserAnswerEntity> findByAttemptId(String attemptId);
    
    /**
     * Finds the user answer for a specific question in a specific attempt.
     *
     * @param attemptId The ID of the attempt.
     * @param questionId The ID of the question.
     * @return The user answer for the question in the attempt.
     */
    Optional<QuizUserAnswerEntity> findByAttemptIdAndQuestionId(String attemptId, String questionId);
}
