package com.cenfotec.p3.neuralforge_api.configuration;

import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Main configuration class for authentication and security.
 * Defines Spring beans related to user management and authentication.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Configuration
public class ApplicationConfiguration {

    @Autowired
    private UserRepository userRepository;

    /**
     * Bean that provides the user details service.
     * Retrieves users from the database based on their email.
     *
     * @return {@link UserDetailsService} that fetches users from {@link UserRepository}.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Bean
    UserDetailsService userDetailsService() {
        return email -> userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Bean for password encoding using BCrypt.
     * Ensures secure storage of user passwords.
     *
     * @return An instance of {@link BCryptPasswordEncoder}.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean that provides the authentication manager.
     * Manages user authentication within the system.
     *
     * @param config Spring authentication configuration.
     * @return Configured {@link AuthenticationManager}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean that configures the authentication provider.
     * Uses {@link DaoAuthenticationProvider} to authenticate users with database-stored credentials.
     *
     * @return An instance of {@link AuthenticationProvider}.
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

}
