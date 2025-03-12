package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Entity representing a user in the system.
 * Implements {@link UserDetails} to integrate with Spring Security authentication.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Table(name = "users")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * First name of the user.
     */
    private String name;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Email address of the user, used as the username.
     * Must be unique and cannot be null.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Encrypted password of the user.
     * Cannot be null.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Timestamp indicating when the user account was created.
     * This value is automatically generated and cannot be updated.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * User role associated with the user.
     * Defines the user's permissions and access levels.
     */
    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id", nullable = false)
    private UserRoleEntity role;

    /**
     * Status of the user account.
     * Defaults to {@code true} when a new user is created.
     */
    private Boolean status;

    /**
     * Retrieves the authorities granted to the user.
     *
     * @return A collection of {@link GrantedAuthority} representing the user's permissions.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.getAuthority();
    }

    /**
     * Retrieves the username, which is the user's email.
     *
     * @return The email address of the user.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Sets default values before persisting the entity.
     * Ensures the user status is set to active upon creation.
     */
    @PrePersist
    private void setDefaultValues() {
        this.status = Boolean.TRUE;
    }
}
