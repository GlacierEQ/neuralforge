package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.CourseTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CourseTopicEntity.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Repository
public interface CourseTopicRepository extends JpaRepository<CourseTopicEntity, String> {
}
