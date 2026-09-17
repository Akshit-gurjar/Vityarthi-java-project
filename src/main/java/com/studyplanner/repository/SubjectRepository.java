package com.studyplanner.repository;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.Subject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Data access repository for Subject entities using native DatabaseManager.
 */
public class SubjectRepository {

    private final DatabaseManager dbManager;

    public SubjectRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Subject save(Subject subject) {
        return dbManager.insertSubject(subject);
    }

    public Optional<Subject> findById(int id) {
        return dbManager.findSubjectById(id);
    }

    public Optional<Subject> findByCode(String code) {
        return dbManager.findSubjectByCode(code);
    }

    public List<Subject> findAll() {
        return dbManager.findAllSubjects();
    }

    public List<Subject> searchByNameOrCode(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String lowerPattern = keyword.trim().toLowerCase();
        return dbManager.findAllSubjects().stream()
                .filter(s -> (s.getName() != null && s.getName().toLowerCase().contains(lowerPattern))
                        || (s.getCode() != null && s.getCode().toLowerCase().contains(lowerPattern)))
                .collect(Collectors.toList());
    }

    public boolean update(Subject subject) {
        return dbManager.updateSubject(subject);
    }

    public boolean delete(int id) {
        return dbManager.deleteSubject(id);
    }
}
