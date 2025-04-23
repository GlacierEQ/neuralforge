package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link QuizQuestionEntity} operations.
 * Provides database access methods for quiz question-related functionality.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestionEntity, String> {
    
    /**
     * Finds all questions associated with a specific quiz.
     *
     * @param quizId The ID of the quiz to find questions for.
     * @return A list of questions associated with the quiz.
     */
    List<QuizQuestionEntity> findByQuizId(String quizId);
    
    /**
     * Finds all questions associated with a specific quiz, ordered by question order.
     *
     * @param quizId The ID of the quiz to find questions for.
     * @return A list of questions associated with the quiz, ordered by question order.
     */
    List<QuizQuestionEntity> findByQuizIdOrderByQuestionOrder(String quizId);
}
