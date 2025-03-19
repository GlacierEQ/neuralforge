package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.AuthenticationResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service responsible for handling Google OAuth2 authentication.
 * This service verifies Google ID tokens and authenticates users accordingly.
 *
 * @author Fabian Vargas
 * @version 1.0
 */
@Service
public class GoogleOAuth2Service {

    private final RestTemplate restTemplate;
    private final AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final UserMapper userMapper = new UserMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructor that initializes OAuth2 client manager and REST template.
     *
     * @param clientRegistrationRepository Repository for OAuth2 client registration.
     */
    @Autowired
    public GoogleOAuth2Service(ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
        );
        this.restTemplate = new RestTemplate();
    }

    /**
     * Authenticates a user using a Google ID token.
     *
     * @param token Google ID token to be verified.
     * @return {@link AuthenticationResource} containing authentication details.
     * @throws ResponseStatusException if the token is invalid or the user is not found.
     */
    public AuthenticationResource authenticate(String token) {
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token cannot be null or empty");
        }

        // Google API endpoint for validating ID tokens
        String userInfoEndpointUri = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + token;
        String response;

        try {
            // Call Google API to validate token
            response = restTemplate.getForObject(userInfoEndpointUri, String.class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to validate Google token", e);
        }

        // Extract email from Google response
        String email = extractEmailFromResponse(response);

        // Find user in the database
        UserEntity user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid email or password"));

        // Ensure the user has a verified account
        if (!user.getVerified()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account verification pending");
        }

        // Return authentication resource with JWT token
        return AuthenticationResource.builder()
                .token(jwtService.generateToken(user))
                .expiresIn(jwtService.getExpirationTime())
                .authUser(userMapper.mapToResource(user))
                .build();
    }

    /**
     * Extracts the email from Google's token response.
     *
     * @param response JSON response from Google containing user info.
     * @return Extracted email address.
     * @throws ResponseStatusException if the response is invalid or cannot be parsed.
     */
    private String extractEmailFromResponse(String response) {
        if (response == null || response.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token response");
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("email").asText();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error parsing Google response", e);
        }
    }
}
