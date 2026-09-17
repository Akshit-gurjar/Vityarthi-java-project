package com.studyplanner.service;

import com.studyplanner.model.Task;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Background concurrency service that periodically scans for overdue tasks and tasks due today.
 * Uses an ExecutorService and shuts down cleanly on application termination.
 */
public class ReminderService {

    private final TaskService taskService;
    private final ScheduledExecutorService scheduler;
    private final AtomicReference<String> activeAlert = new AtomicReference<>("");

    public ReminderService(TaskService taskService) {
        this.taskService = taskService;
        // Daemon thread factory so it does not prevent JVM exit if needed
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "StudyPlanner-ReminderThread");
                t.setDaemon(true);
                return t;
            }
        });
    }

    /**
     * Starts periodic execution of the reminder scanner.
     */
    public void start(long initialDelaySeconds, long periodSeconds) {
        scheduler.scheduleAtFixedRate(this::refreshAlerts, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
    }

    /**
     * Synchronously evaluates active alerts.
     */
    public void refreshAlerts() {
        try {
            List<Task> overdue = taskService.getOverdueTasks();
            List<Task> dueToday = taskService.getTasksDueToday();

            if (overdue.isEmpty() && dueToday.isEmpty()) {
                activeAlert.set("");
                return;
            }

            StringBuilder sb = new StringBuilder();
            if (!overdue.isEmpty()) {
                sb.append("[ALERT: ").append(overdue.size()).append(" overdue task(s)!] ");
            }
            if (!dueToday.isEmpty()) {
                sb.append("[REMINDER: ").append(dueToday.size()).append(" task(s) due today!]");
            }
            activeAlert.set(sb.toString().trim());
        } catch (Exception e) {
            // Background thread must not crash
        }
    }

    /**
     * Retrieves the latest background alert message, if any.
     */
    public String getActiveAlert() {
        return activeAlert.get();
    }

    /**
     * Gracefully shuts down the background executor service.
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
