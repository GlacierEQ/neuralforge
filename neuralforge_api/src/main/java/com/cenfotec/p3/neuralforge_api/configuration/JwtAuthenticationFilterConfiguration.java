package com.cenfotec.p3.neuralforge_api.configuration;

import com.cenfotec.p3.neuralforge_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * JWT Authentication Filter that intercepts requests to authenticate users using JWT tokens.
 * Ensures that only valid tokens can access secured endpoints.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Component
public class JwtAuthenticationFilterConfiguration extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructs the JWT authentication filter.
     *
     * @param jwtService The service responsible for JWT token processing.
     * @param userDetailsService The service for loading user details.
     * @param handlerExceptionResolver The exception resolver for handling authentication errors.
     */
    public JwtAuthenticationFilterConfiguration(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Filters incoming requests to check for valid JWT tokens.
     * If a valid token is found, the user is authenticated and stored in the security context.
     *
     * @param request The incoming HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain for processing the request.
     * @throws ServletException If an error occurs while processing the request.
     * @throws IOException If an input or output error is detected.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // If there is no Authorization header or it does not contain a Bearer token, continue the filter chain.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            logger.info("Extracted user email from token: " + userEmail);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // If the email is extracted and there is no authentication in context, process authentication
            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                logger.info("User details loaded: " + userDetails.getUsername());
                logger.info("User authorities: " + userDetails.getAuthorities());

                // Validate JWT token before setting authentication
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("Authentication set in SecurityContextHolder");
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            logger.error("Error during JWT authentication", exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
