package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.AuthenticationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service responsible for handling user authentication.
 * Verifies user credentials and generates authentication tokens.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class AuthenticationService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    protected JwtService jwtService;

    protected final UserMapper userMapper = new UserMapper();

    /**
     * Authenticates a user based on provided credentials.
     * If authentication is successful, returns an authentication token.
     *
     * @param input The {@link UserResource} containing the user's login credentials.
     * @return An {@link AuthenticationResource} containing the authentication token and user details.
     * @throws ResponseStatusException if the user is not found or authentication fails.
     */
    public AuthenticationResource authenticate(UserResource input) {
        UserEntity user = userRepository
                .findByEmail(input.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid email or password"));

        if (!user.getVerified()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account verification pending");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return AuthenticationResource.builder()
                .token(jwtService.generateToken(user))
                .expiresIn(jwtService.getExpirationTime())
                .authUser(userMapper.mapToResource(user))
                .build();
    }
}
