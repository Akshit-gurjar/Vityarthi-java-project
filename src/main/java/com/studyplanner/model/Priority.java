package com.studyplanner.model;

/**
 * Enumeration representing the priority level of a study task.
 */
public enum Priority {
    LOW,
    MEDIUM,
    HIGH;

    /**
     * Parses a string into a Priority enum, case-insensitively.
     *
     * @param value the string value
     * @return matching Priority or throws IllegalArgumentException
     */
    public static Priority fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Priority cannot be empty.");
        }
        return Priority.valueOf(value.trim().toUpperCase());
    }
}
