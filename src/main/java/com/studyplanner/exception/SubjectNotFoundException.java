package com.studyplanner.exception;

/**
 * Exception thrown when an expected subject cannot be found by ID or code.
 */
public class SubjectNotFoundException extends Exception {
    public SubjectNotFoundException(String message) {
        super(message);
    }
}
