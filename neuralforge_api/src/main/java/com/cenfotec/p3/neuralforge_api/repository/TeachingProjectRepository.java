package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.TeachingProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing teaching projects.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Repository
public interface TeachingProjectRepository extends JpaRepository<TeachingProjectEntity, String> {
    List<TeachingProjectEntity> findByCreatorUserId(String creatorUserId);
} 