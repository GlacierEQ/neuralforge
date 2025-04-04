package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.resource.AuthenticationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;

/**
 * Mapper class responsible for converting authentication-related data into an {@link AuthenticationResource}.
 * Used to structure the response for user authentication requests.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public class LoginResponseMapper {

    /**
     * Maps user authentication details to an {@link AuthenticationResource}.
     *
     * @param user The authenticated user information.
     * @param token The JWT token generated for authentication.
     * @param expirationTime The token's expiration time in milliseconds.
     * @return An {@link AuthenticationResource} containing authentication details.
     */
    public AuthenticationResource mapToResource(UserResource user, String token, Long expirationTime) {
        return AuthenticationResource.builder()
                .authUser(user)
                .token(token)
                .expiresIn(expirationTime)
                .build();
    }
}
