package com.cenfotec.p3.neuralforge_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the NeuralForge API application.
 * Bootstraps the Spring Boot application.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@SpringBootApplication
public class Application {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command-line arguments passed during application startup.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
