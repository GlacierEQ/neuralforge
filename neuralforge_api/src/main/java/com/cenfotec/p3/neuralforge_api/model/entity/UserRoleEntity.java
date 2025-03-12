package com.cenfotec.p3.neuralforge_api.model.entity;

import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Entity representing a user role in the system.
 * Defines role-based access control for users.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Table(name = "user_roles")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleEntity {

    /**
     * Unique identifier for the user role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;

    /**
     * Name of the user role.
     * This is stored as an enumerated value.
     */
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum name;

    /**
     * Description of the user role.
     */
    private String description;

    /**
     * Retrieves the granted authority for this role.
     * Used by Spring Security to determine user permissions.
     *
     * @return A collection containing a single {@link GrantedAuthority} representing the role name.
     */
    public Collection<? extends GrantedAuthority> getAuthority() {
        return Collections.singletonList((GrantedAuthority) () -> name.name());
    }
}
