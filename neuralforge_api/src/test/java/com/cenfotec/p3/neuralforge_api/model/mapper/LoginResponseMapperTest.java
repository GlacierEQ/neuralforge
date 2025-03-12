package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.resource.AuthenticationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseMapperTest {

    private LoginResponseMapper loginResponseMapper;

    @BeforeEach
    void setUp() {
        loginResponseMapper = new LoginResponseMapper();
    }

    @Test
    void givenValidUserTokenAndExpiration_whenMapToResource_thenReturnAuthenticationResource() {
        // Given
        UserResource mockUser = new UserResource();
        String mockToken = "mockJwtToken";
        Long mockExpirationTime = 3600000L;

        // When
        AuthenticationResource result = loginResponseMapper.mapToResource(mockUser, mockToken, mockExpirationTime);

        // Then
        assertNotNull(result);
        assertEquals(mockUser, result.getAuthUser());
        assertEquals(mockToken, result.getToken());
        assertEquals(mockExpirationTime, result.getExpiresIn());
    }
}
