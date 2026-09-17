package com.studyplanner.exception;

/**
 * Exception thrown when a requested task cannot be found in the system.
 */
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
