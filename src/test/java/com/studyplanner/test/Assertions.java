package com.studyplanner.test;

import java.util.Objects;

/**
 * 100% Native Java Assertion Library for zero-dependency automated testing.
 */
public final class Assertions {

    private Assertions() {}

    @FunctionalInterface
    public interface Executable {
        void execute() throws Throwable;
    }

    public static void assertTrue(boolean condition) {
        assertTrue(condition, "Expected true but was false");
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, "Expected false but was true");
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "Expected: " + expected + " but was: " + actual);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (expected instanceof Number && actual instanceof Number) {
            Number expNum = (Number) expected;
            Number actNum = (Number) actual;
            if (expected instanceof Double || actual instanceof Double ||
                expected instanceof Float || actual instanceof Float) {
                if (Double.compare(expNum.doubleValue(), actNum.doubleValue()) == 0) {
                    return;
                }
            } else if (expNum.longValue() == actNum.longValue()) {
                return;
            }
        }
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " [Expected: " + expected + ", Actual: " + actual + "]");
        }
    }

    public static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + " but was: " + actual);
        }
    }

    public static void assertEquals(double expected, double actual, double delta) {
        if (Double.isNaN(expected) || Double.isNaN(actual) || Math.abs(expected - actual) > delta) {
            throw new AssertionError("Expected: " + expected + " within delta " + delta + " but was: " + actual);
        }
    }

    public static void assertNotNull(Object actual) {
        assertNotNull(actual, "Expected non-null value but was null");
    }

    public static void assertNotNull(Object actual, String message) {
        if (actual == null) {
            throw new AssertionError(message);
        }
    }

    public static void assertNull(Object actual) {
        assertNull(actual, "Expected null value but was: " + actual);
    }

    public static void assertNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionError(message);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable actualThrown) {
            if (expectedType.isInstance(actualThrown)) {
                return (T) actualThrown;
            }
            throw new AssertionError("Unexpected exception type thrown. Expected: "
                    + expectedType.getName() + " but caught: " + actualThrown.getClass().getName(), actualThrown);
        }
        throw new AssertionError("Expected " + expectedType.getName() + " to be thrown, but nothing was thrown.");
    }

    public static void assertDoesNotThrow(Executable executable) {
        try {
            executable.execute();
        } catch (Throwable t) {
            throw new AssertionError("Expected no exception to be thrown, but caught: " + t.getClass().getName() + ": " + t.getMessage(), t);
        }
    }
}
