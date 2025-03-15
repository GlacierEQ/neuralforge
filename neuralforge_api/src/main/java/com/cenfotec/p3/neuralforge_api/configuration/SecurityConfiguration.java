package com.cenfotec.p3.neuralforge_api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration class for the application.
 * Defines the authentication and authorization settings using Spring Security.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthenticationFilterConfiguration jwtAuthenticationFilterConfiguration;

    /**
     * Configures the security filter chain for handling authentication and authorization.
     *
     * @param http The {@link HttpSecurity} object for configuring security settings.
     * @return A {@link SecurityFilterChain} instance with defined security rules.
     * @throws Exception If an error occurs during security configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .csrf(csrf -> csrf.disable()) // Disables CSRF protection for stateless authentication
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll() // Allows public access to authentication endpoints
                        .anyRequest().authenticated() // Requires authentication for all other requests
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configures stateless session management
                )
                .authenticationProvider(authenticationProvider) // Sets the authentication provider
                .addFilterBefore(jwtAuthenticationFilterConfiguration, UsernamePasswordAuthenticationFilter.class); // Adds JWT authentication filter

        return http.build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings.
     * Allows communication between frontend applications running on different domains.
     *
     * @return {@link UrlBasedCorsConfigurationSource} containing CORS policies.
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8080", "https://neuralforge.ealpizar.com")); // Defines allowed origins
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Defines allowed HTTP methods
        config.setAllowedHeaders(List.of("*")); // Allows all headers
        config.setAllowCredentials(true); // Allows credentials in requests
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Applies CORS settings globally
        return source;
    }
}