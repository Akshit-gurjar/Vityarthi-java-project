package com.studyplanner.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain model representing an academic task or assignment.
 */
public class Task {
    private int id;
    private int subjectId;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate deadline;
    private TaskStatus status;
    private int estimatedMinutes;

    public Task() {
        this.priority = Priority.MEDIUM;
        this.status = TaskStatus.PENDING;
    }

    public Task(int id, int subjectId, String title, String description,
                Priority priority, LocalDate deadline, TaskStatus status, int estimatedMinutes) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
        this.estimatedMinutes = estimatedMinutes;
    }

    public Task(int subjectId, String title, String description,
                Priority priority, LocalDate deadline, TaskStatus status, int estimatedMinutes) {
        this(0, subjectId, title, description, priority, deadline, status, estimatedMinutes);
    }

    public boolean isOverdue() {
        return status == TaskStatus.PENDING && deadline != null && deadline.isBefore(LocalDate.now());
    }

    public boolean isDueToday() {
        return status == TaskStatus.PENDING && deadline != null && deadline.isEqual(LocalDate.now());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("#%d: %s [%s | %s] Due: %s (%d mins)",
                id, title, priority, status, deadline, estimatedMinutes);
    }
}
