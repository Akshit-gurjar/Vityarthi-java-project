package com.studyplanner.service;

import com.studyplanner.exception.InvalidInputException;
import com.studyplanner.exception.SubjectNotFoundException;
import com.studyplanner.exception.TaskNotFoundException;
import com.studyplanner.model.Priority;
import com.studyplanner.model.Task;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.repository.TaskRepository;
import com.studyplanner.util.InputValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service encapsulating business logic, workflow, and queries for tasks.
 */
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubjectService subjectService;

    public TaskService(TaskRepository taskRepository, SubjectService subjectService) {
        this.taskRepository = taskRepository;
        this.subjectService = subjectService;
    }

    public Task createTask(int subjectId, String title, String description,
                           Priority priority, LocalDate deadline, int estimatedMinutes)
            throws InvalidInputException, SubjectNotFoundException {
        // Validate subject exists
        subjectService.getSubjectById(subjectId);

        String validTitle = InputValidator.requireNonEmpty(title, "Task Title");
        if (deadline == null) {
            throw new InvalidInputException("Deadline cannot be null.");
        }
        if (estimatedMinutes < 0) {
            throw new InvalidInputException("Estimated study minutes cannot be negative.");
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }

        Task task = new Task(subjectId, validTitle, description, priority, deadline, TaskStatus.PENDING, estimatedMinutes);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(int id) throws TaskNotFoundException {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
    }

    public List<Task> getTasksBySubject(int subjectId) {
        return taskRepository.findBySubjectId(subjectId);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getOverdueTasks() {
        return getAllTasks().stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksDueToday() {
        return getAllTasks().stream()
                .filter(Task::isDueToday)
                .collect(Collectors.toList());
    }

    public List<Task> getHighPriorityPendingTasks() {
        return getAllTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING && t.getPriority() == Priority.HIGH)
                .collect(Collectors.toList());
    }

    public List<Task> searchTasks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTasks();
        }
        return taskRepository.searchByTitle(query);
    }

    public boolean markTaskComplete(int id) throws TaskNotFoundException {
        getTaskById(id);
        return taskRepository.updateStatus(id, TaskStatus.COMPLETED);
    }

    public boolean markTaskPending(int id) throws TaskNotFoundException {
        getTaskById(id);
        return taskRepository.updateStatus(id, TaskStatus.PENDING);
    }

    public boolean updateTask(int id, int subjectId, String title, String description,
                              Priority priority, LocalDate deadline, TaskStatus status, int estimatedMinutes)
            throws TaskNotFoundException, SubjectNotFoundException, InvalidInputException {
        Task existing = getTaskById(id);
        subjectService.getSubjectById(subjectId);

        String validTitle = InputValidator.requireNonEmpty(title, "Task Title");
        if (deadline == null) {
            throw new InvalidInputException("Deadline cannot be null.");
        }
        if (estimatedMinutes < 0) {
            throw new InvalidInputException("Estimated study minutes cannot be negative.");
        }

        existing.setSubjectId(subjectId);
        existing.setTitle(validTitle);
        existing.setDescription(description);
        existing.setPriority(priority != null ? priority : Priority.MEDIUM);
        existing.setDeadline(deadline);
        existing.setStatus(status != null ? status : TaskStatus.PENDING);
        existing.setEstimatedMinutes(estimatedMinutes);

        return taskRepository.update(existing);
    }

    public boolean deleteTask(int id) throws TaskNotFoundException {
        getTaskById(id);
        return taskRepository.delete(id);
    }
}
