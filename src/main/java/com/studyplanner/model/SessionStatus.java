package com.studyplanner.model;

/**
 * Enumeration representing the status of a planned study session.
 */
public enum SessionStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED;

    public static SessionStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Session status cannot be empty.");
        }
        return SessionStatus.valueOf(value.trim().toUpperCase());
    }
}
