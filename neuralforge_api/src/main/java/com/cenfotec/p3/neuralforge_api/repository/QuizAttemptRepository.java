package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link QuizAttemptEntity} operations.
 * Provides database access methods for quiz attempt-related functionality.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttemptEntity, String> {
    
    /**
     * Finds all attempts for a specific quiz.
     *
     * @param quizId The ID of the quiz to find attempts for.
     * @return A list of attempts for the quiz.
     */
    List<QuizAttemptEntity> findByQuizId(String quizId);
    
    /**
     * Finds all attempts made by a specific user.
     *
     * @param userId The ID of the user to find attempts for.
     * @return A list of attempts made by the user.
     */
    List<QuizAttemptEntity> findByUserId(String userId);
    
    /**
     * Finds all attempts for a specific quiz made by a specific user.
     *
     * @param quizId The ID of the quiz.
     * @param userId The ID of the user.
     * @return A list of attempts for the quiz made by the user.
     */
    List<QuizAttemptEntity> findByQuizIdAndUserId(String quizId, String userId);
    
    /**
     * Finds all attempts for a specific quiz made by a specific user, ordered by completion time.
     *
     * @param quizId The ID of the quiz.
     * @param userId The ID of the user.
     * @return A list of attempts for the quiz made by the user, ordered by completion time.
     */
    List<QuizAttemptEntity> findByQuizIdAndUserIdOrderByCompletedAtDesc(String quizId, String userId);
}
