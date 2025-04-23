package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link QuizEntity} operations.
 * Provides database access methods for quiz-related functionality.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, String> {
    
    /**
     * Finds all non-deleted quizzes associated with a specific project.
     *
     * @param projectId The ID of the project to find quizzes for.
     * @return A list of quizzes associated with the project.
     */
    List<QuizEntity> findByProjectIdAndIsDeletedFalse(String projectId);
    
    /**
     * Finds all non-deleted quizzes created by a specific user.
     *
     * @param creatorUserId The ID of the user who created the quizzes.
     * @return A list of quizzes created by the user.
     */
    List<QuizEntity> findByCreatorUserIdAndIsDeletedFalse(String creatorUserId);
    
    /**
     * Finds a non-deleted quiz by its ID.
     *
     * @param id The ID of the quiz to find.
     * @return The quiz if found and not deleted, or null otherwise.
     */
    @Query("SELECT q FROM QuizEntity q WHERE q.id = :id AND q.isDeleted = false")
    QuizEntity findByIdAndNotDeleted(@Param("id") String id);
}
