package com.cenfotec.p3.neuralforge_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private String secretKey = "mysecretkeymysecretkeymysecretkeymysecretkey";
    private long expirationTime = 1000 * 60 * 60;

    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock environment variables manually
        jwtService = new JwtService();
        jwtService.jwtExpiration = expirationTime;
        jwtService.secretKey = secretKey;

        when(userDetails.getUsername()).thenReturn("testUser");

        // Generate a token for testing
        token = jwtService.generateToken(userDetails);
    }

    @Test
    void givenUserDetails_whenGenerateToken_thenTokenIsCreated() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void givenToken_whenExtractUsername_thenReturnCorrectUsername() {
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testUser", extractedUsername);
    }

    @Test
    void givenValidToken_whenValidateToken_thenReturnTrue() {
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void givenToken_whenExtractExpirationTime_thenReturnValidExpirationDate() {
        Date expirationDate = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date())); // Expiration should be in the future
    }

    @Test
    void givenToken_whenExtractClaims_thenReturnCorrectClaims() {
        Claims claims = jwtService.extractAllClaims(token);
        assertNotNull(claims);
        assertEquals("testUser", claims.getSubject());
    }
}
