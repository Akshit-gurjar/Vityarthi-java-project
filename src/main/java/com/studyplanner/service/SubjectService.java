package com.studyplanner.service;

import com.studyplanner.exception.InvalidInputException;
import com.studyplanner.exception.SubjectNotFoundException;
import com.studyplanner.model.Subject;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.util.InputValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service encapsulating business logic and validation for academic subjects.
 */
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject createSubject(String name, String code, int credits, String description) throws InvalidInputException {
        String validName = InputValidator.requireNonEmpty(name, "Subject Name");
        String validCode = InputValidator.requireNonEmpty(code, "Subject Code").toUpperCase();

        if (credits <= 0) {
            throw new InvalidInputException("Credits must be a positive integer greater than 0.");
        }

        // Check for duplicate code
        Optional<Subject> existing = subjectRepository.findByCode(validCode);
        if (existing.isPresent()) {
            throw new InvalidInputException("Subject code '" + validCode + "' already exists.");
        }

        Subject subject = new Subject(validName, validCode, credits, description);
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(int id) throws SubjectNotFoundException {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found with ID: " + id));
    }

    public Subject getSubjectByCode(String code) throws SubjectNotFoundException {
        return subjectRepository.findByCode(code)
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found with code: " + code));
    }

    public List<Subject> searchSubjects(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSubjects();
        }
        return subjectRepository.searchByNameOrCode(keyword);
    }

    public boolean updateSubject(int id, String name, String code, int credits, String description)
            throws SubjectNotFoundException, InvalidInputException {
        Subject existing = getSubjectById(id);

        String validName = InputValidator.requireNonEmpty(name, "Subject Name");
        String validCode = InputValidator.requireNonEmpty(code, "Subject Code").toUpperCase();

        if (credits <= 0) {
            throw new InvalidInputException("Credits must be a positive integer greater than 0.");
        }

        // If code changed, check that new code isn't taken by another subject
        if (!existing.getCode().equalsIgnoreCase(validCode)) {
            Optional<Subject> withNewCode = subjectRepository.findByCode(validCode);
            if (withNewCode.isPresent() && withNewCode.get().getId() != id) {
                throw new InvalidInputException("Subject code '" + validCode + "' is already assigned to another subject.");
            }
        }

        existing.setName(validName);
        existing.setCode(validCode);
        existing.setCredits(credits);
        existing.setDescription(description);

        return subjectRepository.update(existing);
    }

    public boolean deleteSubject(int id) throws SubjectNotFoundException {
        getSubjectById(id); // Ensure exists
        return subjectRepository.delete(id);
    }

    /**
     * Returns a lookup map of Subject ID -> Subject entity.
     */
    public Map<Integer, Subject> getSubjectMap() {
        return getAllSubjects().stream()
                .collect(Collectors.toMap(Subject::getId, s -> s));
    }

    /**
     * Returns a lookup map of Subject ID -> Subject code.
     */
    public Map<Integer, String> getSubjectCodeMap() {
        return getAllSubjects().stream()
                .collect(Collectors.toMap(Subject::getId, Subject::getCode));
    }
}
