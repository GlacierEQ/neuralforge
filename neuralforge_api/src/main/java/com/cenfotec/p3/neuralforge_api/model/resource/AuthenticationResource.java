package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the authentication response resource.
 * Contains authentication details including the JWT token, authenticated user, and token expiration time.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResource {

    /**
     * JWT token assigned to the authenticated user.
     */
    private String token;

    /**
     * The authenticated user details.
     */
    private UserResource authUser;

    /**
     * Token expiration time in milliseconds.
     */
    private long expiresIn;
}
