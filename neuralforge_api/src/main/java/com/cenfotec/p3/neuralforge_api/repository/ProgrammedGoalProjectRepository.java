package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.ProgrammedGoalProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link ProgrammedGoalProjectEntity} persistence.
 * Extends {@link JpaRepository} to provide CRUD operations on learning project entities.
 *
 * This repository provides specialized methods for retrieving learning projects.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Repository
public interface ProgrammedGoalProjectRepository extends JpaRepository<ProgrammedGoalProjectEntity, String> {
    
    /**
     * Finds all learning projects created by a specific user.
     *
     * @param creatorUserId The ID of the creator user
     * @return A list of learning projects created by the specified user
     */
    List<ProgrammedGoalProjectEntity> findByCreatorUserId(String creatorUserId);

    List<ProgrammedGoalProjectEntity> findAllByNotifyTrue();
}