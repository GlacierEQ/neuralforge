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
import java.util.List;

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
     * Timestamp indicating when the user's password was last changed.
     * Can be null if password has never been changed or if the user is 
     * using external authentication methods.
     */
    private LocalDateTime lastPasswordChangeAt;

    /**
     * User role associated with the user.
     * Defines the user's permissions and access levels.
     */
    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id", nullable = false)
    private UserRoleEntity role;

    /**
     * Status of access for the user.
     * Defaults to {@code true} when a new user is created.
     */
    private Boolean status;

    /**
     * Status of verification for the user.
     * Defaults to {@code false} when a new user is created.
     */
    private Boolean verified;

    /**
     * List of validation records associated with this user.
     * When this user is deleted, all validation records will be automatically removed.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserValidationEntity> validations;

    /**
     * List of notifications associated with this user.
     * When this user is deleted, all notifications will be automatically removed.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationEntity> notifications;

    /**
     * List of quiz attempts associated with this user.
     * When this user is deleted, all quiz attempts will be automatically removed.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttemptEntity> quizAttempts;

    /**
     * List of virtual student records associated with this user.
     * When this user is deleted, all related virtual student records will be automatically removed.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VirtualStudentEntity> virtualStudents;

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
     */
    @PrePersist
    private void setDefaultValues() {
        this.status = Boolean.TRUE;
        this.verified = Boolean.FALSE;
    }
}
