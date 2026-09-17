package com.studyplanner.util;

import com.studyplanner.exception.InvalidInputException;
import com.studyplanner.model.Priority;
import com.studyplanner.model.SessionStatus;
import com.studyplanner.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Reusable utility class for validating user input across the CLI.
 */
public final class InputValidator {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private InputValidator() {
        // Prevent instantiation
    }

    /**
     * Validates that a string is non-null and not empty.
     */
    public static String requireNonEmpty(String input, String fieldName) throws InvalidInputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
        return input.trim();
    }

    /**
     * Validates and parses a positive integer (> 0).
     */
    public static int parsePositiveInt(String input, String fieldName) throws InvalidInputException {
        try {
            int value = Integer.parseInt(requireNonEmpty(input, fieldName));
            if (value <= 0) {
                throw new InvalidInputException(fieldName + " must be a positive number greater than 0.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid whole number.");
        }
    }

    /**
     * Validates and parses a non-negative integer (>= 0).
     */
    public static int parseNonNegativeInt(String input, String fieldName) throws InvalidInputException {
        try {
            int value = Integer.parseInt(requireNonEmpty(input, fieldName));
            if (value < 0) {
                throw new InvalidInputException(fieldName + " cannot be negative.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid whole number.");
        }
    }

    /**
     * Validates and parses a date in YYYY-MM-DD format.
     */
    public static LocalDate parseDate(String input, String fieldName) throws InvalidInputException {
        String trimmed = requireNonEmpty(input, fieldName);
        try {
            return LocalDate.parse(trimmed, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(fieldName + " must match format YYYY-MM-DD (e.g., 2026-10-15).");
        }
    }

    /**
     * Validates and parses a time in HH:mm 24-hour format.
     */
    public static LocalTime parseTime(String input, String fieldName) throws InvalidInputException {
        String trimmed = requireNonEmpty(input, fieldName);
        try {
            return LocalTime.parse(trimmed, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(fieldName + " must match 24-hour format HH:mm (e.g., 14:30).");
        }
    }

    /**
     * Validates that end time is strictly after start time.
     */
    public static void validateTimeRange(LocalTime start, LocalTime end) throws InvalidInputException {
        if (start == null || end == null) {
            throw new InvalidInputException("Start time and end time must be specified.");
        }
        if (!end.isAfter(start)) {
            throw new InvalidInputException("End time (" + end + ") must be strictly after start time (" + start + ").");
        }
    }

    /**
     * Validates and parses a Priority (LOW, MEDIUM, HIGH).
     */
    public static Priority parsePriority(String input) throws InvalidInputException {
        String trimmed = requireNonEmpty(input, "Priority");
        try {
            return Priority.fromString(trimmed);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid priority '" + input + "'. Must be one of: LOW, MEDIUM, HIGH.");
        }
    }

    /**
     * Validates and parses a TaskStatus (PENDING, COMPLETED).
     */
    public static TaskStatus parseTaskStatus(String input) throws InvalidInputException {
        String trimmed = requireNonEmpty(input, "Task Status");
        try {
            return TaskStatus.fromString(trimmed);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid status '" + input + "'. Must be one of: PENDING, COMPLETED.");
        }
    }

    /**
     * Validates and parses a SessionStatus (SCHEDULED, COMPLETED, CANCELLED).
     */
    public static SessionStatus parseSessionStatus(String input) throws InvalidInputException {
        String trimmed = requireNonEmpty(input, "Session Status");
        try {
            return SessionStatus.fromString(trimmed);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid status '" + input + "'. Must be: SCHEDULED, COMPLETED, CANCELLED.");
        }
    }
}
