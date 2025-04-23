package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.VirtualClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VirtualClassRepository extends JpaRepository<VirtualClassEntity, String> {
    List<VirtualClassEntity> findByOwnerId(String ownerId);

}
