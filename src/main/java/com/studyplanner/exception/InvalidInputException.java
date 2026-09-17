package com.studyplanner.exception;

/**
 * Exception thrown when user input violates format, range, or validation constraints.
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
