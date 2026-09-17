package com.studyplanner.service;

import com.studyplanner.model.Priority;
import com.studyplanner.model.Subject;
import com.studyplanner.model.Task;
import com.studyplanner.model.TaskStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service providing progress analytics, calculations, and reporting metrics using Java Stream API.
 */
public class AnalyticsService {

    private final TaskService taskService;
    private final SubjectService subjectService;
    private final ScheduleService scheduleService;

    public AnalyticsService(TaskService taskService, SubjectService subjectService, ScheduleService scheduleService) {
        this.taskService = taskService;
        this.subjectService = subjectService;
        this.scheduleService = scheduleService;
    }

    /**
     * Calculates overall task completion percentage across all subjects.
     */
    public double getOverallCompletionRate() {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            return 0.0;
        }
        long completedCount = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();
        return ((double) completedCount / tasks.size()) * 100.0;
    }

    /**
     * Calculates subject-wise completion percentage mapping: Subject Code -> Completion Rate (0-100%).
     */
    public Map<String, Double> getSubjectWiseProgress() {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, String> subjectCodeMap = subjectService.getSubjectCodeMap();

        // Group tasks by subject ID using Stream API
        Map<Integer, List<Task>> tasksBySubject = tasks.stream()
                .collect(Collectors.groupingBy(Task::getSubjectId));

        return tasksBySubject.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> subjectCodeMap.getOrDefault(entry.getKey(), "SUB-" + entry.getKey()),
                        entry -> {
                            List<Task> subTasks = entry.getValue();
                            long completed = subTasks.stream()
                                    .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                                    .count();
                            return ((double) completed / subTasks.size()) * 100.0;
                        }
                ));
    }

    public long getTotalTasksCount() {
        return taskService.getAllTasks().size();
    }

    public long getCompletedTasksCount() {
        return taskService.getAllTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();
    }

    public long getPendingTasksCount() {
        return taskService.getAllTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING)
                .count();
    }

    public long getOverdueTasksCount() {
        return taskService.getAllTasks().stream()
                .filter(Task::isOverdue)
                .count();
    }

    public long getHighPriorityPendingCount() {
        return taskService.getAllTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING && t.getPriority() == Priority.HIGH)
                .count();
    }

    /**
     * Computes total estimated study hours from all tasks.
     */
    public double getTotalEstimatedTaskHours() {
        int totalMinutes = taskService.getAllTasks().stream()
                .mapToInt(Task::getEstimatedMinutes)
                .sum();
        return totalMinutes / 60.0;
    }

    /**
     * Computes total planned study session hours from the schedule.
     */
    public double getTotalScheduledStudyHours() {
        long totalMinutes = scheduleService.getAllSessions().stream()
                .mapToLong(session -> session.getDurationMinutes())
                .sum();
        return totalMinutes / 60.0;
    }
}
