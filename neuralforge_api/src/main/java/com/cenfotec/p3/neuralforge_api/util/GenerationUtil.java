package com.cenfotec.p3.neuralforge_api.util;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Utility class responsible for generating random values used within the application.
 * Provides functionality for generating random verification codes.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Component
public class GenerationUtil {

    /**
     * Generates a random six-digit verification code.
     *
     * @return A randomly generated six-digit integer verification code.
     */
    public int generateRandomVerificationCode() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
}
