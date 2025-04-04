package com.cenfotec.p3.neuralforge_api.model.enums;

/**
 * Enum representing different types of projects within the NeuralForge system.
 * Defines categories of projects that can be created and managed by users.
 * 
 * This enum is used as a discriminator for the project entity inheritance hierarchy,
 * allowing the system to differentiate between various project implementations.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
public enum ProjectTypeEnum {    
   
    /**
     * Represents a learning-focused project used for educational purposes.
     * Learning projects allow users to practice and develop their skills
     * in a structured environment with specific learning objectives.
     */
    LEARNING,
    PROGRAMMED_GOAL
}