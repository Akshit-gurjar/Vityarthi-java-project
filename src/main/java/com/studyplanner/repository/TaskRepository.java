package com.studyplanner.repository;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.Task;
import com.studyplanner.model.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Data access repository for Task entities using native DatabaseManager.
 */
public class TaskRepository {

    private final DatabaseManager dbManager;

    public TaskRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Task save(Task task) {
        return dbManager.insertTask(task);
    }

    public Optional<Task> findById(int id) {
        return dbManager.findTaskById(id);
    }

    public List<Task> findAll() {
        return dbManager.findAllTasks();
    }

    public List<Task> findBySubjectId(int subjectId) {
        return dbManager.findTasksBySubjectId(subjectId);
    }

    public List<Task> findByStatus(TaskStatus status) {
        return dbManager.findAllTasks().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> searchByTitle(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        String pattern = query.trim().toLowerCase();
        return dbManager.findAllTasks().stream()
                .filter(t -> t.getTitle() != null && t.getTitle().toLowerCase().contains(pattern))
                .collect(Collectors.toList());
    }

    public boolean update(Task task) {
        return dbManager.updateTask(task);
    }

    public boolean updateStatus(int taskId, TaskStatus status) {
        Optional<Task> opt = dbManager.findTaskById(taskId);
        if (opt.isPresent()) {
            Task task = opt.get();
            task.setStatus(status);
            return dbManager.updateTask(task);
        }
        return false;
    }

    public boolean delete(int id) {
        return dbManager.deleteTask(id);
    }
}
