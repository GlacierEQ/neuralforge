package com.cenfotec.p3.neuralforge_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for handling JWT (JSON Web Token) operations.
 * Includes token generation, validation, and claims extraction.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    protected String secretKey;

    @Value("${security.jwt.expiration-time}")
    protected long jwtExpiration;

    /**
     * Extracts the username (subject) from a given JWT token.
     *
     * @param token The JWT token.
     * @return The username contained within the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a given JWT token.
     *
     * @param token The JWT token.
     * @param claimsResolver A function to extract the desired claim.
     * @param <T> The type of the claim.
     * @return The extracted claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a new JWT token for the given user.
     *
     * @param userDetails The user details for whom the token is generated.
     * @return A JWT token string.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a new JWT token with additional claims.
     *
     * @param extraClaims Additional claims to include in the token.
     * @param userDetails The user details for whom the token is generated.
     * @return A JWT token string.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Retrieves the expiration time for the JWT token.
     *
     * @return The JWT token expiration time in milliseconds.
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Validates a JWT token by checking if it belongs to the given user and is not expired.
     *
     * @param token The JWT token to validate.
     * @param userDetails The user details to compare with the token.
     * @return {@code true} if the token is valid, otherwise {@code false}.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token to check.
     * @return {@code true} if the token is expired, otherwise {@code false}.
     */
    protected boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    protected Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token.
     * @return A {@link Claims} object containing all claims.
     */
    Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Builds a JWT token with the specified claims, user details, and expiration time.
     *
     * @param extraClaims Additional claims to include.
     * @param userDetails The user details for whom the token is generated.
     * @param expiration The expiration time for the token.
     * @return A JWT token string.
     */
    protected String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Retrieves the signing key used to sign the JWT token.
     *
     * @return A {@link Key} object representing the signing key.
     */
    protected Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a password reset JWT token with a short expiration time.
     *
     * @param userId The ID of the user requesting a password reset.
     * @return A JWT token that can be used for password reset.
     */
    public String generatePasswordResetToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        // Set expiration time to 15 minutes (900,000 milliseconds)
        long expirationTime = 15 * 60 * 1000;

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
