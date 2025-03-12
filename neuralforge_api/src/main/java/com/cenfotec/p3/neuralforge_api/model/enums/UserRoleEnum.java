package com.cenfotec.p3.neuralforge_api.model.enums;

/**
 * Enum representing the different user roles in the system.
 * Defines role-based access control for users.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public enum UserRoleEnum {

    /**
     * Role assigned to students.
     * Grants access to student-related functionalities.
     */
    ROLE_STUDENT,

    /**
     * Role assigned to teachers.
     * Grants access to teaching and course management functionalities.
     */
    ROLE_TEACHER,

    /**
     * Role assigned to administrators.
     * Grants full system access, including user and role management.
     */
    ROLE_ADMINISTRATOR
}
