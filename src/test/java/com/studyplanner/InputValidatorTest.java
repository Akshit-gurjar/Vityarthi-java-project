package com.studyplanner;

import com.studyplanner.exception.InvalidInputException;
import com.studyplanner.model.Priority;
import com.studyplanner.model.SessionStatus;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.util.InputValidator;
import com.studyplanner.test.DisplayName;
import com.studyplanner.test.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.studyplanner.test.Assertions.*;

/**
 * Unit tests for InputValidator.
 */
public class InputValidatorTest {

    @Test
    @DisplayName("requireNonEmpty should return trimmed string on valid input")
    void testRequireNonEmptySuccess() throws InvalidInputException {
        assertEquals("Valid Text", InputValidator.requireNonEmpty("  Valid Text  ", "Field"));
    }

    @Test
    @DisplayName("requireNonEmpty should throw InvalidInputException on null or blank input")
    void testRequireNonEmptyFailure() {
        assertThrows(InvalidInputException.class, () -> InputValidator.requireNonEmpty(null, "Name"));
        assertThrows(InvalidInputException.class, () -> InputValidator.requireNonEmpty("   ", "Name"));
    }

    @Test
    @DisplayName("parsePositiveInt should parse numbers greater than 0")
    void testParsePositiveIntSuccess() throws InvalidInputException {
        assertEquals(4, InputValidator.parsePositiveInt("4", "Credits"));
    }

    @Test
    @DisplayName("parsePositiveInt should throw exception on zero, negative numbers, or non-digits")
    void testParsePositiveIntFailure() {
        assertThrows(InvalidInputException.class, () -> InputValidator.parsePositiveInt("0", "Credits"));
        assertThrows(InvalidInputException.class, () -> InputValidator.parsePositiveInt("-3", "Credits"));
        assertThrows(InvalidInputException.class, () -> InputValidator.parsePositiveInt("abc", "Credits"));
    }

    @Test
    @DisplayName("parseDate should parse valid ISO YYYY-MM-DD format")
    void testParseDateSuccess() throws InvalidInputException {
        LocalDate date = InputValidator.parseDate("2026-11-20", "Deadline");
        assertEquals(LocalDate.of(2026, 11, 20), date);
    }

    @Test
    @DisplayName("parseDate should throw on malformed dates")
    void testParseDateFailure() {
        assertThrows(InvalidInputException.class, () -> InputValidator.parseDate("20-11-2026", "Deadline"));
        assertThrows(InvalidInputException.class, () -> InputValidator.parseDate("not-a-date", "Deadline"));
    }

    @Test
    @DisplayName("parseTime should parse valid 24-hr HH:mm format")
    void testParseTimeSuccess() throws InvalidInputException {
        LocalTime time = InputValidator.parseTime("14:45", "Start Time");
        assertEquals(LocalTime.of(14, 45), time);
    }

    @Test
    @DisplayName("validateTimeRange should throw if end time is before or equal to start time")
    void testValidateTimeRange() {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(9, 30);
        assertThrows(InvalidInputException.class, () -> InputValidator.validateTimeRange(start, end));
        assertThrows(InvalidInputException.class, () -> InputValidator.validateTimeRange(start, start));
        assertDoesNotThrow(() -> InputValidator.validateTimeRange(start, LocalTime.of(11, 0)));
    }

    @Test
    @DisplayName("parsePriority should handle case-insensitive valid strings")
    void testParsePriority() throws InvalidInputException {
        assertEquals(Priority.HIGH, InputValidator.parsePriority("high"));
        assertEquals(Priority.LOW, InputValidator.parsePriority("LoW"));
        assertThrows(InvalidInputException.class, () -> InputValidator.parsePriority("CRITICAL"));
    }
}
