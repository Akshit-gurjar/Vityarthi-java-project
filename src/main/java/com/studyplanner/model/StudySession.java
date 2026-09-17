package com.studyplanner.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Domain model representing a scheduled study session or timetable block.
 */
public class StudySession {
    private int id;
    private int subjectId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String topic;
    private SessionStatus status;

    public StudySession() {
        this.status = SessionStatus.SCHEDULED;
    }

    public StudySession(int id, int subjectId, LocalDate sessionDate,
                        LocalTime startTime, LocalTime endTime, String topic, SessionStatus status) {
        this.id = id;
        this.subjectId = subjectId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.topic = topic;
        this.status = status;
    }

    public StudySession(int subjectId, LocalDate sessionDate,
                        LocalTime startTime, LocalTime endTime, String topic, SessionStatus status) {
        this(0, subjectId, sessionDate, startTime, endTime, topic, status);
    }

    /**
     * Calculates duration of session in minutes.
     */
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) return 0;
        return Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Checks if this session overlaps in time with another session on the same date.
     */
    public boolean conflictsWith(StudySession other) {
        if (other == null || this.id == other.id) return false;
        if (this.status == SessionStatus.CANCELLED || other.status == SessionStatus.CANCELLED) return false;
        if (!Objects.equals(this.sessionDate, other.sessionDate)) return false;

        // An overlap occurs when: this.start < other.end AND this.end > other.start
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
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

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudySession that = (StudySession) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Session #%d [%s %s-%s] Topic: %s (%s)",
                id, sessionDate, startTime, endTime, topic, status);
    }
}
