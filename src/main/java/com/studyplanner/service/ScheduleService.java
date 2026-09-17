package com.studyplanner.service;

import com.studyplanner.exception.InvalidInputException;
import com.studyplanner.exception.SubjectNotFoundException;
import com.studyplanner.model.SessionStatus;
import com.studyplanner.model.StudySession;
import com.studyplanner.repository.StudySessionRepository;
import com.studyplanner.util.InputValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service managing timetable schedules and detecting session timing conflicts.
 */
public class ScheduleService {

    private final StudySessionRepository sessionRepository;
    private final SubjectService subjectService;

    public ScheduleService(StudySessionRepository sessionRepository, SubjectService subjectService) {
        this.sessionRepository = sessionRepository;
        this.subjectService = subjectService;
    }

    public StudySession createSession(int subjectId, LocalDate date,
                                      LocalTime startTime, LocalTime endTime,
                                      String topic)
            throws InvalidInputException, SubjectNotFoundException {
        subjectService.getSubjectById(subjectId);

        if (date == null) {
            throw new InvalidInputException("Session date cannot be null.");
        }
        InputValidator.validateTimeRange(startTime, endTime);
        String validTopic = InputValidator.requireNonEmpty(topic, "Topic");

        StudySession session = new StudySession(subjectId, date, startTime, endTime, validTopic, SessionStatus.SCHEDULED);
        return sessionRepository.save(session);
    }

    /**
     * Checks if a session has any timing conflict with existing scheduled sessions.
     */
    public List<StudySession> findConflicts(StudySession candidate) {
        List<StudySession> daySessions = sessionRepository.findByDate(candidate.getSessionDate());
        return daySessions.stream()
                .filter(candidate::conflictsWith)
                .collect(Collectors.toList());
    }

    public List<StudySession> getAllSessions() {
        return sessionRepository.findAll();
    }

    public StudySession getSessionById(int id) throws InvalidInputException {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Study session not found with ID: " + id));
    }

    public List<StudySession> getSessionsByDate(LocalDate date) {
        return sessionRepository.findByDate(date);
    }

    public List<StudySession> getSessionsBySubject(int subjectId) {
        return sessionRepository.findBySubjectId(subjectId);
    }

    public boolean updateSession(int id, int subjectId, LocalDate date,
                                 LocalTime startTime, LocalTime endTime,
                                 String topic, SessionStatus status)
            throws InvalidInputException, SubjectNotFoundException {
        StudySession existing = getSessionById(id);
        subjectService.getSubjectById(subjectId);

        if (date == null) {
            throw new InvalidInputException("Session date cannot be null.");
        }
        InputValidator.validateTimeRange(startTime, endTime);
        String validTopic = InputValidator.requireNonEmpty(topic, "Topic");

        existing.setSubjectId(subjectId);
        existing.setSessionDate(date);
        existing.setStartTime(startTime);
        existing.setEndTime(endTime);
        existing.setTopic(validTopic);
        existing.setStatus(status != null ? status : SessionStatus.SCHEDULED);

        return sessionRepository.update(existing);
    }

    public boolean deleteSession(int id) throws InvalidInputException {
        getSessionById(id);
        return sessionRepository.delete(id);
    }
}
