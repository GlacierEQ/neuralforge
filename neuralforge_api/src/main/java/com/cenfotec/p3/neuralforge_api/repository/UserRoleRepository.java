package com.cenfotec.p3.neuralforge_api.repository;

import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserRoleEntity} persistence.
 * Extends {@link JpaRepository} to provide CRUD operations on the user role entity.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, String> {

    /**
     * Finds a user role by its name.
     *
     * @param role The {@link UserRoleEnum} representing the role to search for.
     * @return An {@link Optional} containing the role if found, or empty otherwise.
     */
    Optional<UserRoleEntity> findByName(UserRoleEnum role);
}
