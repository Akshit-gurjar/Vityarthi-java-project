package com.studyplanner.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 100% Native Java Test Runner.
 * Executes test suites without any external framework or third-party JARs.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            STUDY PLANNER CLI - NATIVE JAVA AUTOMATED TEST RUNNER              ");
        System.out.println("               (Zero External Dependencies / Pure Java SE JDK)                  ");
        System.out.println("================================================================================");

        List<Class<?>> testClasses = List.of(
                com.studyplanner.InputValidatorTest.class,
                com.studyplanner.DatabaseCrudTest.class,
                com.studyplanner.TaskServiceTest.class,
                com.studyplanner.AnalyticsServiceTest.class,
                com.studyplanner.ScheduleServiceTest.class
        );

        int totalPassed = 0;
        int totalFailed = 0;
        long startTime = System.currentTimeMillis();

        for (Class<?> clazz : testClasses) {
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println("Suite: " + clazz.getSimpleName());
            System.out.println("--------------------------------------------------------------------------------");

            List<Method> beforeMethods = new ArrayList<>();
            List<Method> afterMethods = new ArrayList<>();
            List<Method> testMethods = new ArrayList<>();

            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(BeforeEach.class)) {
                    beforeMethods.add(m);
                } else if (m.isAnnotationPresent(AfterEach.class)) {
                    afterMethods.add(m);
                } else if (m.isAnnotationPresent(Test.class) || m.getName().startsWith("test")) {
                    testMethods.add(m);
                }
            }

            for (Method testMethod : testMethods) {
                String testName = testMethod.getName();
                if (testMethod.isAnnotationPresent(DisplayName.class)) {
                    testName = testMethod.getAnnotation(DisplayName.class).value();
                }

                long methodStart = System.currentTimeMillis();
                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();

                    // Run @BeforeEach
                    for (Method b : beforeMethods) {
                        b.setAccessible(true);
                        b.invoke(instance);
                    }

                    // Run test
                    testMethod.setAccessible(true);
                    testMethod.invoke(instance);

                    // Run @AfterEach
                    for (Method a : afterMethods) {
                        a.setAccessible(true);
                        a.invoke(instance);
                    }

                    long elapsed = System.currentTimeMillis() - methodStart;
                    System.out.printf("  [✓ PASS] %-55s (%d ms)\n", testName, elapsed);
                    totalPassed++;
                } catch (Throwable t) {
                    long elapsed = System.currentTimeMillis() - methodStart;
                    Throwable cause = (t instanceof InvocationTargetException) ? t.getCause() : t;
                    System.out.printf("  [✗ FAIL] %-55s (%d ms)\n", testName, elapsed);
                    System.out.println("           Reason: " + cause.getMessage());
                    cause.printStackTrace(System.out);
                    totalFailed++;
                }
            }
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        int totalTests = totalPassed + totalFailed;
        double passRate = totalTests == 0 ? 0.0 : ((double) totalPassed / totalTests) * 100.0;

        System.out.println("\n================================================================================");
        System.out.println("                               TEST RESULTS SUMMARY                             ");
        System.out.println("================================================================================");
        System.out.printf("  Total Test Cases Executed : %d\n", totalTests);
        System.out.printf("  Passed                    : %d\n", totalPassed);
        System.out.printf("  Failed                    : %d\n", totalFailed);
        System.out.printf("  Pass Rate                 : %.1f%%\n", passRate);
        System.out.printf("  Execution Time            : %d ms\n", totalDuration);
        System.out.println("================================================================================");

        if (totalFailed == 0) {
            System.out.println("  BUILD & TEST STATUS: [SUCCESS] ALL NATIVE TESTS PASSED!");
            System.out.println("================================================================================\n");
            System.exit(0);
        } else {
            System.err.println("  BUILD & TEST STATUS: [FAILED] SOME TESTS ENCOUNTERED FAILURES.");
            System.out.println("================================================================================\n");
            System.exit(1);
        }
    }
}
