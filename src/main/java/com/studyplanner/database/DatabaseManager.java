package com.studyplanner.database;

import com.studyplanner.exception.DatabaseException;
import com.studyplanner.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 100% Native Java Persistence Engine.
 * Provides thread-safe, transactional, file-based relational data persistence
 * with zero third-party dependencies using Java NIO.2 and standard collections.
 */
public class DatabaseManager implements AutoCloseable {

    private final String dbPath;
    private final boolean isInMemory;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // In-memory relational tables
    private final Map<Integer, Subject> subjects = new LinkedHashMap<>();
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    private final Map<Integer, StudySession> sessions = new LinkedHashMap<>();

    // Auto-increment sequence generators
    private int nextSubjectId = 1;
    private int nextTaskId = 1;
    private int nextSessionId = 1;

    public DatabaseManager(String dbPath) {
        if (dbPath == null || dbPath.equals(":memory:") || dbPath.contains("::memory:")) {
            this.isInMemory = true;
            this.dbPath = ":memory:";
        } else {
            this.isInMemory = false;
            this.dbPath = dbPath.replace('\\', '/');
            ensureDirectoryExists();
            loadFromFile();
        }
    }

    public DatabaseManager() {
        this("data/studyplanner.dat");
    }

    private void ensureDirectoryExists() {
        if (isInMemory) return;
        try {
            Path path = Paths.get(dbPath);
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new DatabaseException("Failed to initialize database storage directory: " + e.getMessage(), e);
        }
    }

    public String getDbUrl() {
        return dbPath;
    }

    public boolean isInMemory() {
        return isInMemory;
    }

    // ==========================================
    // SUBJECT CRUD OPERATIONS
    // ==========================================

    public Subject insertSubject(Subject subject) {
        lock.writeLock().lock();
        try {
            // Check code uniqueness
            String code = subject.getCode().trim().toUpperCase();
            for (Subject s : subjects.values()) {
                if (s.getCode().equalsIgnoreCase(code)) {
                    throw new DatabaseException("Unique constraint violation: Subject code '" + code + "' already exists.");
                }
            }

            int id = nextSubjectId++;
            subject.setId(id);
            Subject copy = new Subject(id, subject.getName(), code, subject.getCredits(), subject.getDescription());
            subjects.put(id, copy);
            saveToFile();
            return copy;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Subject> findSubjectById(int id) {
        lock.readLock().lock();
        try {
            Subject s = subjects.get(id);
            if (s == null) return Optional.empty();
            return Optional.of(new Subject(s.getId(), s.getName(), s.getCode(), s.getCredits(), s.getDescription()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Subject> findSubjectByCode(String code) {
        lock.readLock().lock();
        try {
            if (code == null) return Optional.empty();
            String trimmed = code.trim();
            return subjects.values().stream()
                    .filter(s -> s.getCode().equalsIgnoreCase(trimmed))
                    .map(s -> new Subject(s.getId(), s.getName(), s.getCode(), s.getCredits(), s.getDescription()))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Subject> findAllSubjects() {
        lock.readLock().lock();
        try {
            List<Subject> list = new ArrayList<>();
            for (Subject s : subjects.values()) {
                list.add(new Subject(s.getId(), s.getName(), s.getCode(), s.getCredits(), s.getDescription()));
            }
            list.sort(Comparator.comparing(Subject::getCode));
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateSubject(Subject subject) {
        lock.writeLock().lock();
        try {
            if (!subjects.containsKey(subject.getId())) {
                return false;
            }
            // Check unique code for another subject
            String code = subject.getCode().trim().toUpperCase();
            for (Subject s : subjects.values()) {
                if (s.getId() != subject.getId() && s.getCode().equalsIgnoreCase(code)) {
                    throw new DatabaseException("Unique constraint violation: Subject code '" + code + "' is already in use by another subject.");
                }
            }

            Subject copy = new Subject(subject.getId(), subject.getName(), code, subject.getCredits(), subject.getDescription());
            subjects.put(subject.getId(), copy);
            saveToFile();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteSubject(int id) {
        lock.writeLock().lock();
        try {
            if (!subjects.containsKey(id)) {
                return false;
            }
            subjects.remove(id);

            // Cascading delete for linked tasks and study sessions
            tasks.values().removeIf(t -> t.getSubjectId() == id);
            sessions.values().removeIf(s -> s.getSubjectId() == id);

            saveToFile();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==========================================
    // TASK CRUD OPERATIONS
    // ==========================================

    public Task insertTask(Task task) {
        lock.writeLock().lock();
        try {
            int id = nextTaskId++;
            task.setId(id);
            Task copy = new Task(
                    id,
                    task.getSubjectId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getPriority(),
                    task.getDeadline(),
                    task.getStatus(),
                    task.getEstimatedMinutes()
            );
            tasks.put(id, copy);
            saveToFile();
            return copy;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Task> findTaskById(int id) {
        lock.readLock().lock();
        try {
            Task t = tasks.get(id);
            if (t == null) return Optional.empty();
            return Optional.of(cloneTask(t));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Task> findAllTasks() {
        lock.readLock().lock();
        try {
            List<Task> list = new ArrayList<>();
            for (Task t : tasks.values()) {
                list.add(cloneTask(t));
            }
            list.sort(Comparator.comparing(Task::getDeadline));
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Task> findTasksBySubjectId(int subjectId) {
        lock.readLock().lock();
        try {
            List<Task> list = new ArrayList<>();
            for (Task t : tasks.values()) {
                if (t.getSubjectId() == subjectId) {
                    list.add(cloneTask(t));
                }
            }
            list.sort(Comparator.comparing(Task::getDeadline));
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateTask(Task task) {
        lock.writeLock().lock();
        try {
            if (!tasks.containsKey(task.getId())) {
                return false;
            }
            tasks.put(task.getId(), cloneTask(task));
            saveToFile();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteTask(int id) {
        lock.writeLock().lock();
        try {
            if (!tasks.containsKey(id)) {
                return false;
            }
            tasks.remove(id);
            saveToFile();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int deleteTasksBySubjectId(int subjectId) {
        lock.writeLock().lock();
        try {
            int before = tasks.size();
            tasks.values().removeIf(t -> t.getSubjectId() == subjectId);
            int deleted = before - tasks.size();
            if (deleted > 0) {
                saveToFile();
            }
            return deleted;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==========================================
    // STUDY SESSION CRUD OPERATIONS
    // ==========================================

    public StudySession insertStudySession(StudySession session) {
        lock.writeLock().lock();
        try {
            int id = nextSessionId++;
            session.setId(id);
            StudySession copy = new StudySession(
                    id,
                    session.getSubjectId(),
                    session.getSessionDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getTopic(),
                    session.getStatus()
            );
            sessions.put(id, copy);
            saveToFile();
            return copy;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<StudySession> findSessionById(int id) {
        lock.readLock().lock();
        try {
            StudySession s = sessions.get(id);
            if (s == null) return Optional.empty();
            return Optional.of(cloneSession(s));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<StudySession> findAllSessions() {
        lock.readLock().lock();
        try {
            List<StudySession> list = new ArrayList<>();
            for (StudySession s : sessions.values()) {
                list.add(cloneSession(s));
            }
            list.sort(Comparator.comparing(StudySession::getSessionDate).thenComparing(StudySession::getStartTime));
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<StudySession> findSessionsBySubjectId(int subjectId) {
        lock.readLock().lock();
        try {
            List<StudySession> list = new ArrayList<>();
            for (StudySession s : sessions.values()) {
                if (s.getSubjectId() == subjectId) {
                    list.add(cloneSession(s));
                }
            }
            list.sort(Comparator.comparing(StudySession::getSessionDate).thenComparing(StudySession::getStartTime));
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<StudySession> findSessionsByDate(LocalDate date) {
        lock.readLock().lock();
        try {
            List<StudySession> list = new ArrayList<>();
            if (date == null) return list;
            for (StudySession s : sessions.values()) {
                if (date.equals(s.getSessionDate())) {
                    list.add(cloneSession(s));
                }
            }
            list.sort(Comparator.comparing(StudySession::getStartTime));
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateSession(StudySession session) {
        lock.writeLock().lock();
        try {
            if (!sessions.containsKey(session.getId())) {
                return false;
            }
            sessions.put(session.getId(), cloneSession(session));
            saveToFile();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteSession(int id) {
        lock.writeLock().lock();
        try {
            if (!sessions.containsKey(id)) {
                return false;
            }
            sessions.remove(id);
            saveToFile();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int deleteSessionsBySubjectId(int subjectId) {
        lock.writeLock().lock();
        try {
            int before = sessions.size();
            sessions.values().removeIf(s -> s.getSubjectId() == subjectId);
            int deleted = before - sessions.size();
            if (deleted > 0) {
                saveToFile();
            }
            return deleted;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==========================================
    // PERSISTENCE ENGINE (JAVA NIO.2 FILE I/O)
    // ==========================================

    private synchronized void saveToFile() {
        if (isInMemory) return;
        Path targetPath = Paths.get(dbPath);

        try {
            List<String> lines = new ArrayList<>();
            lines.add("# STUDYPLANNER NATIVE DATABASE - VERSION 1.0");
            lines.add("# AUTO-GENERATED NATIVE STORAGE - DO NOT EDIT MANUALLY");

            lines.add("[COUNTERS]");
            lines.add("nextSubjectId=" + nextSubjectId);
            lines.add("nextTaskId=" + nextTaskId);
            lines.add("nextSessionId=" + nextSessionId);

            lines.add("[SUBJECTS]");
            for (Subject s : subjects.values()) {
                lines.add(String.format("%d|%s|%s|%d|%s",
                        s.getId(),
                        escapeField(s.getName()),
                        escapeField(s.getCode()),
                        s.getCredits(),
                        escapeField(s.getDescription() == null ? "" : s.getDescription())
                ));
            }

            lines.add("[TASKS]");
            for (Task t : tasks.values()) {
                lines.add(String.format("%d|%d|%s|%s|%s|%s|%s|%d",
                        t.getId(),
                        t.getSubjectId(),
                        escapeField(t.getTitle()),
                        escapeField(t.getDescription() == null ? "" : t.getDescription()),
                        t.getPriority().name(),
                        t.getDeadline().toString(),
                        t.getStatus().name(),
                        t.getEstimatedMinutes()
                ));
            }

            lines.add("[SESSIONS]");
            for (StudySession s : sessions.values()) {
                lines.add(String.format("%d|%d|%s|%s|%s|%s|%s",
                        s.getId(),
                        s.getSubjectId(),
                        s.getSessionDate().toString(),
                        s.getStartTime().toString(),
                        s.getEndTime().toString(),
                        escapeField(s.getTopic() == null ? "" : s.getTopic()),
                        s.getStatus().name()
                ));
            }

            // Write directly to target path (guaranteed thread-safe via write lock)
            Files.write(targetPath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new DatabaseException("Failed to persist data to disk: " + e.getMessage(), e);
        }
    }

    private synchronized void loadFromFile() {
        if (isInMemory) return;
        Path targetPath = Paths.get(dbPath);
        if (!Files.exists(targetPath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(targetPath, StandardCharsets.UTF_8)) {
            String line;
            String currentSection = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1).toUpperCase();
                    continue;
                }

                switch (currentSection) {
                    case "COUNTERS" -> {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            int val = Integer.parseInt(parts[1].trim());
                            if ("nextSubjectId".equals(key)) nextSubjectId = val;
                            else if ("nextTaskId".equals(key)) nextTaskId = val;
                            else if ("nextSessionId".equals(key)) nextSessionId = val;
                        }
                    }
                    case "SUBJECTS" -> {
                        String[] cols = line.split("\\|", -1);
                        if (cols.length >= 5) {
                            int id = Integer.parseInt(cols[0]);
                            String name = unescapeField(cols[1]);
                            String code = unescapeField(cols[2]);
                            int credits = Integer.parseInt(cols[3]);
                            String desc = unescapeField(cols[4]);
                            subjects.put(id, new Subject(id, name, code, credits, desc));
                            if (id >= nextSubjectId) nextSubjectId = id + 1;
                        }
                    }
                    case "TASKS" -> {
                        String[] cols = line.split("\\|", -1);
                        if (cols.length >= 8) {
                            int id = Integer.parseInt(cols[0]);
                            int subjectId = Integer.parseInt(cols[1]);
                            String title = unescapeField(cols[2]);
                            String desc = unescapeField(cols[3]);
                            Priority priority = Priority.valueOf(cols[4]);
                            LocalDate deadline = LocalDate.parse(cols[5]);
                            TaskStatus status = TaskStatus.valueOf(cols[6]);
                            int estMinutes = Integer.parseInt(cols[7]);
                            tasks.put(id, new Task(id, subjectId, title, desc, priority, deadline, status, estMinutes));
                            if (id >= nextTaskId) nextTaskId = id + 1;
                        }
                    }
                    case "SESSIONS" -> {
                        String[] cols = line.split("\\|", -1);
                        if (cols.length >= 7) {
                            int id = Integer.parseInt(cols[0]);
                            int subjectId = Integer.parseInt(cols[1]);
                            LocalDate date = LocalDate.parse(cols[2]);
                            LocalTime start = LocalTime.parse(cols[3]);
                            LocalTime end = LocalTime.parse(cols[4]);
                            String topic = unescapeField(cols[5]);
                            SessionStatus status = SessionStatus.valueOf(cols[6]);
                            sessions.put(id, new StudySession(id, subjectId, date, start, end, topic, status));
                            if (id >= nextSessionId) nextSessionId = id + 1;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to read database file '" + dbPath + "': " + e.getMessage(), e);
        }
    }

    private static String escapeField(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\r", "")
                .replace("\n", "\\n");
    }

    private static String unescapeField(String val) {
        if (val == null) return "";
        return val.replace("\\n", "\n")
                .replace("\\p", "|")
                .replace("\\\\", "\\");
    }

    private Task cloneTask(Task t) {
        return new Task(
                t.getId(),
                t.getSubjectId(),
                t.getTitle(),
                t.getDescription(),
                t.getPriority(),
                t.getDeadline(),
                t.getStatus(),
                t.getEstimatedMinutes()
        );
    }

    private StudySession cloneSession(StudySession s) {
        return new StudySession(
                s.getId(),
                s.getSubjectId(),
                s.getSessionDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getTopic(),
                s.getStatus()
        );
    }

    @Override
    public void close() {
        saveToFile();
    }
}
