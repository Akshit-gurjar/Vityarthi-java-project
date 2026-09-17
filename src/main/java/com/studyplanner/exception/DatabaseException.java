package com.studyplanner.exception;

/**
 * Exception thrown when a persistence or database error occurs.
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(String message) {
        super(message);
    }
}
