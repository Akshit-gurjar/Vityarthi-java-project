package com.studyplanner.model;

/**
 * Enumeration representing the execution state of an academic task.
 */
public enum TaskStatus {
    PENDING,
    COMPLETED;

    /**
     * Parses a string into a TaskStatus enum, case-insensitively.
     *
     * @param value the string value
     * @return matching TaskStatus or throws IllegalArgumentException
     */
    public static TaskStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Task status cannot be empty.");
        }
        return TaskStatus.valueOf(value.trim().toUpperCase());
    }
}
