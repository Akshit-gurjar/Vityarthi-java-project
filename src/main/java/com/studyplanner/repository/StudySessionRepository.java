package com.studyplanner.repository;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.StudySession;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data access repository for StudySession entities using native DatabaseManager.
 */
public class StudySessionRepository {

    private final DatabaseManager dbManager;

    public StudySessionRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public StudySession save(StudySession session) {
        return dbManager.insertStudySession(session);
    }

    public Optional<StudySession> findById(int id) {
        return dbManager.findSessionById(id);
    }

    public List<StudySession> findAll() {
        return dbManager.findAllSessions();
    }

    public List<StudySession> findByDate(LocalDate date) {
        return dbManager.findSessionsByDate(date);
    }

    public List<StudySession> findBySubjectId(int subjectId) {
        return dbManager.findSessionsBySubjectId(subjectId);
    }

    public boolean update(StudySession session) {
        return dbManager.updateSession(session);
    }

    public boolean delete(int id) {
        return dbManager.deleteSession(id);
    }
}
