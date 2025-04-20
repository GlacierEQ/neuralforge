package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.VirtualStudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualStudentRepository extends JpaRepository<VirtualStudentEntity, String> {
}
