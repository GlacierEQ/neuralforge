package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link SelectedDaysEntity} persistence.
 * Provides CRUD operations on selected days.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Repository
public interface SelectedDaysRepository extends JpaRepository<SelectedDaysEntity, String> {
}
