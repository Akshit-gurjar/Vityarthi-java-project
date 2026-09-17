package com.studyplanner.app;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.exception.InvalidInputException;
import com.studyplanner.exception.SubjectNotFoundException;
import com.studyplanner.exception.TaskNotFoundException;
import com.studyplanner.model.*;
import com.studyplanner.repository.StudySessionRepository;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.repository.TaskRepository;
import com.studyplanner.service.*;
import com.studyplanner.util.BackupManager;
import com.studyplanner.util.CsvExporter;
import com.studyplanner.util.InputValidator;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main entry point and interactive console user interface for Study Planner CLI.
 */
public class Main {

    private static final String DB_PATH = "data/studyplanner.dat";

    private final DatabaseManager dbManager;
    private final SubjectService subjectService;
    private final TaskService taskService;
    private final ScheduleService scheduleService;
    private final AnalyticsService analyticsService;
    private final ReportService reportService;
    private final ReminderService reminderService;
    private final Scanner scanner;

    public Main() {
        this(DB_PATH);
    }

    public Main(String dbPath) {
        this.dbManager = new DatabaseManager(dbPath);
        SubjectRepository subjectRepo = new SubjectRepository(dbManager);
        TaskRepository taskRepo = new TaskRepository(dbManager);
        StudySessionRepository sessionRepo = new StudySessionRepository(dbManager);

        this.subjectService = new SubjectService(subjectRepo);
        this.taskService = new TaskService(taskRepo, subjectService);
        this.scheduleService = new ScheduleService(sessionRepo, subjectService);
        this.analyticsService = new AnalyticsService(taskService, subjectService, scheduleService);

        CsvExporter csvExporter = new CsvExporter("data/exports");
        BackupManager backupManager = new BackupManager("data/backups");
        this.reportService = new ReportService(analyticsService, taskService, subjectService, scheduleService, csvExporter, backupManager);

        this.reminderService = new ReminderService(taskService);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();

        // Register clean JVM shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.shutdown();
        }));

        app.start();
    }

    public void start() {
        // Start background reminder service (runs every 10 seconds)
        reminderService.start(1, 10);

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = readChoice();
            if (choice.isEmpty()) {
                if (!scanner.hasNextLine()) break;
                continue;
            }
            switch (choice) {
                case "1" -> handleSubjectMenu();
                case "2" -> handleTaskMenu();
                case "3" -> handleScheduleMenu();
                case "4" -> handleAnalyticsMenu();
                case "5" -> handleReportsMenu();
                case "6" -> seedDemoData();
                case "7" -> {
                    System.out.println("\nThank you for using Study Planner CLI. Goodbye!");
                    running = false;
                }
                default -> System.out.println("\n[!] Invalid choice. Please select an option between 1 and 7.");
            }
        }

        shutdown();
    }

    private String readChoice() {
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().replaceAll("[\\p{Cntrl}\\uFEFF]", "").trim();
    }

    private void shutdown() {
        reminderService.shutdown();
        dbManager.close();
    }

    private void printMainMenu() {
        String alert = reminderService.getActiveAlert();
        System.out.println("\n========================================================");
        System.out.println("                 STUDY PLANNER CLI                      ");
        System.out.println("========================================================");
        if (!alert.isEmpty()) {
            System.out.println(" >> " + alert);
            System.out.println("--------------------------------------------------------");
        }
        System.out.println(" 1. Subject Management");
        System.out.println(" 2. Task Management");
        System.out.println(" 3. Study Schedule");
        System.out.println(" 4. Progress & Analytics");
        System.out.println(" 5. Reports & Backup");
        System.out.println(" 6. Demo Data Seeder (Quick Sample Setup)");
        System.out.println(" 7. Exit");
        System.out.println("========================================================");
        System.out.print("Enter choice: ");
    }

    // ==========================================
    // MODULE 1: SUBJECT MANAGEMENT
    // ==========================================
    private void handleSubjectMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----------------- SUBJECT MANAGEMENT -----------------");
            System.out.println(" 1. Add Subject");
            System.out.println(" 2. View All Subjects");
            System.out.println(" 3. Search Subject");
            System.out.println(" 4. Update Subject");
            System.out.println(" 5. Delete Subject");
            System.out.println(" 6. Back to Main Menu");
            System.out.println("------------------------------------------------------");
            System.out.print("Enter choice: ");

            String choice = readChoice();
            if (choice.isEmpty() && !scanner.hasNextLine()) break;
            switch (choice) {
                case "1" -> addSubject();
                case "2" -> viewAllSubjects();
                case "3" -> searchSubjects();
                case "4" -> updateSubject();
                case "5" -> deleteSubject();
                case "6" -> back = true;
                default -> System.out.println("\n[!] Invalid choice. Enter 1-6.");
            }
        }
    }

    private void addSubject() {
        System.out.println("\n--- Add New Subject ---");
        try {
            System.out.print("Subject Name: ");
            String name = scanner.nextLine();
            System.out.print("Subject Code (e.g. CS101): ");
            String code = scanner.nextLine();
            System.out.print("Credits: ");
            int credits = InputValidator.parsePositiveInt(scanner.nextLine(), "Credits");
            System.out.print("Description (optional): ");
            String desc = scanner.nextLine();

            Subject created = subjectService.createSubject(name, code, credits, desc);
            System.out.printf("[✓] Subject '%s' created successfully with ID: %d\n", created.getCode(), created.getId());
        } catch (InvalidInputException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private void viewAllSubjects() {
        System.out.println("\n--- All Enrolled Subjects ---");
        List<Subject> subjects = subjectService.getAllSubjects();
        if (subjects.isEmpty()) {
            System.out.println("No subjects found. Add a subject first.");
            return;
        }
        System.out.printf("%-5s | %-10s | %-30s | %-8s | %s\n", "ID", "Code", "Name", "Credits", "Description");
        System.out.println("----------------------------------------------------------------------------------");
        for (Subject s : subjects) {
            System.out.printf("%-5d | %-10s | %-30s | %-8d | %s\n",
                    s.getId(), s.getCode(), s.getName(), s.getCredits(),
                    s.getDescription() == null ? "" : s.getDescription());
        }
    }

    private void searchSubjects() {
        System.out.print("\nEnter keyword to search (name or code): ");
        String keyword = scanner.nextLine();
        List<Subject> list = subjectService.searchSubjects(keyword);
        if (list.isEmpty()) {
            System.out.println("No matching subjects found.");
            return;
        }
        System.out.printf("\nFound %d matching subject(s):\n", list.size());
        for (Subject s : list) {
            System.out.printf("  * [#%d] %s - %s (%d credits)\n", s.getId(), s.getCode(), s.getName(), s.getCredits());
        }
    }

    private void updateSubject() {
        System.out.println("\n--- Update Subject ---");
        try {
            System.out.print("Enter Subject ID to update: ");
            int id = InputValidator.parsePositiveInt(scanner.nextLine(), "Subject ID");
            Subject existing = subjectService.getSubjectById(id);

            System.out.printf("Current Name [%s]: ", existing.getName());
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) name = existing.getName();

            System.out.printf("Current Code [%s]: ", existing.getCode());
            String code = scanner.nextLine();
            if (code.trim().isEmpty()) code = existing.getCode();

            System.out.printf("Current Credits [%d]: ", existing.getCredits());
            String creditsInput = scanner.nextLine();
            int credits = creditsInput.trim().isEmpty() ? existing.getCredits()
                    : InputValidator.parsePositiveInt(creditsInput, "Credits");

            System.out.printf("Current Description [%s]: ", existing.getDescription());
            String desc = scanner.nextLine();
            if (desc.trim().isEmpty()) desc = existing.getDescription();

            boolean ok = subjectService.updateSubject(id, name, code, credits, desc);
            if (ok) {
                System.out.println("[✓] Subject updated successfully.");
            }
        } catch (SubjectNotFoundException | InvalidInputException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private void deleteSubject() {
        System.out.println("\n--- Delete Subject ---");
        try {
            System.out.print("Enter Subject ID to delete: ");
            int id = InputValidator.parsePositiveInt(scanner.nextLine(), "Subject ID");
            System.out.print("Are you sure? All linked tasks and sessions will be deleted (y/n): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                boolean ok = subjectService.deleteSubject(id);
                if (ok) {
                    System.out.println("[✓] Subject deleted successfully.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (SubjectNotFoundException | InvalidInputException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    // ==========================================
    // MODULE 2: TASK MANAGEMENT
    // ==========================================
    private void handleTaskMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------------ TASK MANAGEMENT -------------------");
            System.out.println(" 1. Add Task");
            System.out.println(" 2. View All Tasks");
            System.out.println(" 3. Search Tasks");
            System.out.println(" 4. Filter Tasks (By Status / Priority)");
            System.out.println(" 5. Update Task");
            System.out.println(" 6. Delete Task");
            System.out.println(" 7. Mark Task Complete");
            System.out.println(" 8. Mark Task Pending");
            System.out.println(" 9. Back to Main Menu");
            System.out.println("------------------------------------------------------");
            System.out.print("Enter choice: ");

            String choice = readChoice();
            if (choice.isEmpty() && !scanner.hasNextLine()) break;
            switch (choice) {
                case "1" -> addTask();
                case "2" -> viewAllTasks();
                case "3" -> searchTasks();
                case "4" -> filterTasks();
                case "5" -> updateTask();
                case "6" -> deleteTask();
                case "7" -> markTaskStatus(TaskStatus.COMPLETED);
                case "8" -> markTaskStatus(TaskStatus.PENDING);
                case "9" -> back = true;
                default -> System.out.println("\n[!] Invalid choice. Enter 1-9.");
            }
        }
    }

    private void addTask() {
        System.out.println("\n--- Add New Task ---");
        try {
            viewAllSubjects();
            System.out.print("\nSubject ID: ");
            int subjectId = InputValidator.parsePositiveInt(scanner.nextLine(), "Subject ID");
            System.out.print("Task Title: ");
            String title = scanner.nextLine();
            System.out.print("Description (optional): ");
            String desc = scanner.nextLine();
            System.out.print("Priority (LOW / MEDIUM / HIGH) [default: MEDIUM]: ");
            String prioStr = scanner.nextLine();
            Priority priority = prioStr.trim().isEmpty() ? Priority.MEDIUM : InputValidator.parsePriority(prioStr);
            System.out.print("Deadline (YYYY-MM-DD): ");
            LocalDate deadline = InputValidator.parseDate(scanner.nextLine(), "Deadline");
            System.out.print("Estimated Study Minutes (e.g. 60): ");
            int minutes = InputValidator.parseNonNegativeInt(scanner.nextLine(), "Estimated Minutes");

            Task created = taskService.createTask(subjectId, title, desc, priority, deadline, minutes);
            System.out.printf("[✓] Task #%d '%s' added successfully.\n", created.getId(), created.getTitle());
            reminderService.refreshAlerts();
        } catch (InvalidInputException | SubjectNotFoundException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private void viewAllTasks() {
        System.out.println("\n--- All Academic Tasks ---");
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        printTasksTable(tasks);
    }

    private void printTasksTable(List<Task> tasks) {
        Map<Integer, String> codeMap = subjectService.getSubjectCodeMap();
        System.out.printf("%-4s | %-8s | %-25s | %-8s | %-12s | %-10s | %-8s | %s\n",
                "ID", "Subject", "Title", "Priority", "Deadline", "Status", "Est(min)", "Overdue?");
        System.out.println("---------------------------------------------------------------------------------------------------");
        for (Task t : tasks) {
            String subjectCode = codeMap.getOrDefault(t.getSubjectId(), "N/A");
            System.out.printf("%-4d | %-8s | %-25s | %-8s | %-12s | %-10s | %-8d | %s\n",
                    t.getId(),
                    subjectCode,
                    truncate(t.getTitle(), 25),
                    t.getPriority(),
                    t.getDeadline(),
                    t.getStatus(),
                    t.getEstimatedMinutes(),
                    t.isOverdue() ? "[OVERDUE]" : (t.isDueToday() ? "[TODAY]" : "No")
            );
        }
    }

    private void searchTasks() {
        System.out.print("\nEnter keyword to search in task titles: ");
        String query = scanner.nextLine();
        List<Task> tasks = taskService.searchTasks(query);
        if (tasks.isEmpty()) {
            System.out.println("No tasks match query.");
            return;
        }
        System.out.printf("\nFound %d task(s):\n", tasks.size());
        printTasksTable(tasks);
    }

    private void filterTasks() {
        System.out.println("\nFilter Options:");
        System.out.println(" 1. Pending Tasks");
        System.out.println(" 2. Completed Tasks");
        System.out.println(" 3. Overdue Tasks");
        System.out.println(" 4. Tasks Due Today");
        System.out.println(" 5. High Priority Pending Tasks");
        System.out.print("Select filter: ");
        String opt = scanner.nextLine().trim();

        List<Task> filtered = switch (opt) {
            case "1" -> taskService.getTasksByStatus(TaskStatus.PENDING);
            case "2" -> taskService.getTasksByStatus(TaskStatus.COMPLETED);
            case "3" -> taskService.getOverdueTasks();
            case "4" -> taskService.getTasksDueToday();
            case "5" -> taskService.getHighPriorityPendingTasks();
            default -> {
                System.out.println("[!] Invalid filter choice.");
                yield List.of();
            }
        };

        if (!filtered.isEmpty()) {
            System.out.println("\nFiltered Results:");
            printTasksTable(filtered);
        } else if (opt.matches("[1-5]")) {
            System.out.println("No matching tasks found for the selected filter.");
        }
    }

    private void markTaskStatus(TaskStatus targetStatus) {
        System.out.printf("\n--- Mark Task as %s ---\n", targetStatus);
        try {
            System.out.print("Enter Task ID: ");
            int id = InputValidator.parsePositiveInt(scanner.nextLine(), "Task ID");
            if (targetStatus == TaskStatus.COMPLETED) {
                taskService.markTaskComplete(id);
            } else {
                taskService.markTaskPending(id);
            }
            System.out.printf("[✓] Task #%d marked as %s.\n", id, targetStatus);
            reminderService.refreshAlerts();
        } catch (TaskNotFoundException | InvalidInputException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private void updateTask() {
        System.out.println("\n--- Update Task ---");
        try {
            System.out.print("Enter Task ID to update: ");
            int id = InputValidator.parsePositiveInt(scanner.nextLine(), "Task ID");
            Task existing = taskService.getTaskById(id);

            System.out.printf("Subject ID [%d]: ", existing.getSubjectId());
            String subInput = scanner.nextLine();
            int subId = subInput.trim().isEmpty() ? existing.getSubjectId()
                    : InputValidator.parsePositiveInt(subInput, "Subject ID");

            System.out.printf("Title [%s]: ", existing.getTitle());
            String title = scanner.nextLine();
            if (title.trim().isEmpty()) title = existing.getTitle();

            System.out.printf("Description [%s]: ", existing.getDescription());
            String desc = scanner.nextLine();
            if (desc.trim().isEmpty()) desc = existing.getDescription();

            System.out.printf("Priority (LOW/MEDIUM/HIGH) [%s]: ", existing.getPriority());
            String prioInput = scanner.nextLine();
            Priority priority = prioInput.trim().isEmpty() ? existing.getPriority()
                    : InputValidator.parsePriority(prioInput);

            System.out.printf("Deadline (YYYY-MM-DD) [%s]: ", existing.getDeadline());
            String dateInput = scanner.nextLine();
            LocalDate deadline = dateInput.trim().isEmpty() ? existing.getDeadline()
                    : InputValidator.parseDate(dateInput, "Deadline");

            System.out.printf("Status (PENDING/COMPLETED) [%s]: ", existing.getStatus());
            String statusInput = scanner.nextLine();
            TaskStatus status = statusInput.trim().isEmpty() ? existing.getStatus()
                    : InputValidator.parseTaskStatus(statusInput);

            System.out.printf("Estimated Minutes [%d]: ", existing.getEstimatedMinutes());
            String minsInput = scanner.nextLine();
            int mins = minsInput.trim().isEmpty() ? existing.getEstimatedMinutes()
                    : InputValidator.parseNonNegativeInt(minsInput, "Estimated Minutes");

            boolean ok = taskService.updateTask(id, subId, title, desc, priority, deadline, status, mins);
            if (ok) {
                System.out.println("[✓] Task updated successfully.");
                reminderService.refreshAlerts();
            }
        } catch (TaskNotFoundException | SubjectNotFoundException | InvalidInputException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private void deleteTask() {
        System.out.println("\n--- Delete Task ---");
        try {
            System.out.print("Enter Task ID: ");
            int id = InputValidator.parsePositiveInt(scanner.nextLine(), "Task ID");
            boolean ok = taskService.deleteTask(id);
            if (ok) {
                System.out.println("[✓] Task deleted successfully.");
                reminderService.refreshAlerts();
            }
        } catch (TaskNotFoundException | InvalidInputException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    // ==========================================
    // MODULE 3: STUDY SCHEDULE
    // ==========================================
    private void handleScheduleMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----------------- STUDY SCHEDULE --------------------");
            System.out.println(" 1. Add Study Session");
            System.out.println(" 2. View Full Schedule");
            System.out.println(" 3. View Sessions by Date");
            System.out.println(" 4. Update Study Session");
            System.out.println(" 5. Delete Study Session");
            System.out.println(" 6. Back to Main Menu");
            System.out.println("-----------------------------------------------------");
            System.out.print("Enter choice: ");

            String choice = readChoice();
            if (choice.isEmpty() && !scanner.hasNextLine()) break;
            switch (choice) {
                case "1" -> addStudySession();
                case "2" -> viewSchedule();
                case "3" -> viewScheduleByDate();
                case "4" -> updateStudySession();
                case "5" -> deleteStudySession();
                case "6" -> back = true;
                default -> System.out.println("\n[!] Invalid choice. Enter 1-6.");
            }
        }
    }

    private void addStudySession() {
        System.out.println("\n--- Add Study Session ---");
        try {
            viewAllSubjects();
            System.out.print("\nSubject ID: ");
            int subjectId = InputValidator.parsePositiveInt(scanner.nextLine(), "Subject ID");
            System.out.print("Session Date (YYYY-MM-DD): ");
            LocalDate date = InputValidator.parseDate(scanner.nextLine(), "Session Date");
            System.out.print("Start Time (HH:mm 24-hr): ");
            LocalTime start = InputValidator.parseTime(scanner.nextLine(), "Start Time");
            System.out.print("End Time (HH:mm 24-hr): ");
            LocalTime end = InputValidator.parseTime(scanner.nextLine(), "End Time");
            System.out.print("Study Topic: ");
            String topic = scanner.nextLine();

            // Check for conflict before saving
            StudySession candidate = new StudySession(subjectId, date, start, end, topic, SessionStatus.SCHEDULED);
            List<StudySession> conflicts = scheduleService.findConflicts(candidate);
            if (!conflicts.isEmpty()) {
                System.out.println("\n[!] WARNING: Schedule conflict detected with existing session(s):");
                for (StudySession c : conflicts) {
                    System.out.printf("    Session #%d [%s - %s]: %s\n", c.getId(), c.getStartTime(), c.getEndTime(), c.getTopic());
                }
                System.out.print("Do you still want to schedule this session? (y/n): ");
                String confirm = scanner.nextLine();
                if (!confirm.equalsIgnoreCase("y")) {
                    System.out.println("Scheduling cancelled.");
                    return;
                }
            }

            StudySession created = scheduleService.createSession(subjectId, date, start, end, topic);
            System.out.printf("[✓] Study Session #%d scheduled successfully (%d mins).\n",
                    created.getId(), created.getDurationMinutes());
        } catch (InvalidInputException | SubjectNotFoundException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private void viewSchedule() {
        System.out.println("\n--- Timetable & Scheduled Study Sessions ---");
        List<StudySession> sessions = scheduleService.getAllSessions();
        if (sessions.isEmpty()) {
            System.out.println("No study sessions scheduled.");
            return;
        }
        printSessionsTable(sessions);
    }

    private void viewScheduleByDate() {
        try {
            System.out.print("\nEnter Date (YYYY-MM-DD): ");
            LocalDate date = InputValidator.parseDate(scanner.nextLine(), "Date");
            List<StudySession> sessions = scheduleService.getSessionsByDate(date);
            if (sessions.isEmpty()) {
                System.out.println("No sessions found for date: " + date);
                return;
            }
            printSessionsTable(sessions);
        } catch (InvalidInputException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private void printSessionsTable(List<StudySession> sessions) {
        Map<Integer, String> codeMap = subjectService.getSubjectCodeMap();
        System.out.printf("%-4s | %-8s | %-12s | %-13s | %-8s | %-12s | %s\n",
                "ID", "Subject", "Date", "Time", "Duration", "Status", "Topic");
        System.out.println("-------------------------------------------------------------------------------------------");
        for (StudySession s : sessions) {
            String subCode = codeMap.getOrDefault(s.getSubjectId(), "N/A");
            String timeWindow = s.getStartTime() + " - " + s.getEndTime();
            System.out.printf("%-4d | %-8s | %-12s | %-13s | %-4d min | %-12s | %s\n",
                    s.getId(), subCode, s.getSessionDate(), timeWindow, s.getDurationMinutes(), s.getStatus(), s.getTopic());
        }
    }

    private void updateStudySession() {
        System.out.println("\n--- Update Study Session ---");
        try {
            System.out.print("Enter Session ID: ");
            int id = InputValidator.parsePositiveInt(scanner.nextLine(), "Session ID");
            StudySession existing = scheduleService.getSessionById(id);

            System.out.printf("Subject ID [%d]: ", existing.getSubjectId());
            String subStr = scanner.nextLine();
            int subId = subStr.trim().isEmpty() ? existing.getSubjectId()
                    : InputValidator.parsePositiveInt(subStr, "Subject ID");

            System.out.printf("Date (YYYY-MM-DD) [%s]: ", existing.getSessionDate());
            String dateStr = scanner.nextLine();
            LocalDate date = dateStr.trim().isEmpty() ? existing.getSessionDate()
                    : InputValidator.parseDate(dateStr, "Date");

            System.out.printf("Start Time (HH:mm) [%s]: ", existing.getStartTime());
            String startStr = scanner.nextLine();
            LocalTime start = startStr.trim().isEmpty() ? existing.getStartTime()
                    : InputValidator.parseTime(startStr, "Start Time");

            System.out.printf("End Time (HH:mm) [%s]: ", existing.getEndTime());
            String endStr = scanner.nextLine();
            LocalTime end = endStr.trim().isEmpty() ? existing.getEndTime()
                    : InputValidator.parseTime(endStr, "End Time");

            System.out.printf("Topic [%s]: ", existing.getTopic());
            String topic = scanner.nextLine();
            if (topic.trim().isEmpty()) topic = existing.getTopic();

            System.out.printf("Status (SCHEDULED/COMPLETED/CANCELLED) [%s]: ", existing.getStatus());
            String statusStr = scanner.nextLine();
            SessionStatus status = statusStr.trim().isEmpty() ? existing.getStatus()
                    : InputValidator.parseSessionStatus(statusStr);

            boolean ok = scheduleService.updateSession(id, subId, date, start, end, topic, status);
            if (ok) {
                System.out.println("[✓] Study session updated successfully.");
            }
        } catch (InvalidInputException | SubjectNotFoundException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    private void deleteStudySession() {
        System.out.println("\n--- Delete Study Session ---");
        try {
            System.out.print("Enter Session ID: ");
            int id = InputValidator.parsePositiveInt(scanner.nextLine(), "Session ID");
            boolean ok = scheduleService.deleteSession(id);
            if (ok) {
                System.out.println("[✓] Study session deleted.");
            }
        } catch (InvalidInputException e) {
            System.out.println("[!] Error: " + e.getMessage());
        }
    }

    // ==========================================
    // MODULE 4: PROGRESS & ANALYTICS
    // ==========================================
    private void handleAnalyticsMenu() {
        System.out.println("\n========================================================");
        System.out.println("               PROGRESS & STUDY ANALYTICS               ");
        System.out.println("========================================================");

        double overall = analyticsService.getOverallCompletionRate();
        System.out.printf("Overall Progress   : [%-20s] %.1f%%\n", getProgressBar(overall), overall);
        System.out.println("--------------------------------------------------------");

        System.out.println("Subject-Wise Progress:");
        Map<String, Double> progress = analyticsService.getSubjectWiseProgress();
        if (progress.isEmpty()) {
            System.out.println("  (No subjects or tasks enrolled)");
        } else {
            progress.forEach((code, rate) -> {
                System.out.printf("  * %-10s [%-15s] %.1f%%\n", code, getProgressBar(rate, 15), rate);
            });
        }

        System.out.println("\nTask Breakdown:");
        System.out.printf("  * Total Tasks        : %d\n", analyticsService.getTotalTasksCount());
        System.out.printf("  * Completed Tasks    : %d\n", analyticsService.getCompletedTasksCount());
        System.out.printf("  * Pending Tasks      : %d\n", analyticsService.getPendingTasksCount());
        System.out.printf("  * Overdue Tasks      : %d\n", analyticsService.getOverdueTasksCount());
        System.out.printf("  * High Priority Due  : %d\n", analyticsService.getHighPriorityPendingCount());

        System.out.println("\nStudy Hours:");
        System.out.printf("  * Estimated Task Workload  : %.1f hours\n", analyticsService.getTotalEstimatedTaskHours());
        System.out.printf("  * Scheduled Study Sessions : %.1f hours\n", analyticsService.getTotalScheduledStudyHours());
        System.out.println("========================================================");

        System.out.print("Press ENTER to return to main menu...");
        scanner.nextLine();
    }

    private String getProgressBar(double percentage) {
        return getProgressBar(percentage, 20);
    }

    private String getProgressBar(double percentage, int length) {
        int filled = (int) Math.round((percentage / 100.0) * length);
        filled = Math.max(0, Math.min(length, filled));
        return "#".repeat(filled) + "-".repeat(length - filled);
    }

    // ==========================================
    // MODULE 5: REPORTS & BACKUP
    // ==========================================
    private void handleReportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----------------- REPORTS & BACKUP ------------------");
            System.out.println(" 1. View Formatted Progress Report");
            System.out.println(" 2. Export Tasks to CSV");
            System.out.println(" 3. Export Study Schedule to CSV");
            System.out.println(" 4. Create Database Backup Snapshot");
            System.out.println(" 5. Show Export & Backup Locations");
            System.out.println(" 6. Back to Main Menu");
            System.out.println("-----------------------------------------------------");
            System.out.print("Enter choice: ");

            String choice = readChoice();
            if (choice.isEmpty() && !scanner.hasNextLine()) break;
            switch (choice) {
                case "1" -> System.out.println("\n" + reportService.generateProgressReport());
                case "2" -> {
                    try {
                        Path p = reportService.exportTasksToCsv();
                        System.out.println("[✓] Tasks exported successfully to: " + p.toAbsolutePath());
                    } catch (Exception e) {
                        System.out.println("[!] Failed to export tasks: " + e.getMessage());
                    }
                }
                case "3" -> {
                    try {
                        Path p = reportService.exportScheduleToCsv();
                        System.out.println("[✓] Schedule exported successfully to: " + p.toAbsolutePath());
                    } catch (Exception e) {
                        System.out.println("[!] Failed to export schedule: " + e.getMessage());
                    }
                }
                case "4" -> {
                    try {
                        Path p = reportService.backupDatabase(DB_PATH);
                        System.out.println("[✓] Backup created successfully: " + p.toAbsolutePath());
                    } catch (Exception e) {
                        System.out.println("[!] Failed to create backup: " + e.getMessage());
                    }
                }
                case "5" -> {
                    System.out.println("\nData Directories (Java NIO.2):");
                    System.out.println("  * Exports : " + reportService.getExportDirectory().toAbsolutePath());
                    System.out.println("  * Backups : " + reportService.getBackupDirectory().toAbsolutePath());
                }
                case "6" -> back = true;
                default -> System.out.println("\n[!] Invalid choice. Enter 1-6.");
            }
        }
    }

    // ==========================================
    // MODULE 6: DEMO DATA SEEDER
    // ==========================================
    private void seedDemoData() {
        System.out.println("\n--- Seeding Demo Academic Data ---");
        try {
            // 1. Subjects (retrieve or create)
            Subject s1 = subjectService.getAllSubjects().stream()
                    .filter(s -> s.getCode().equalsIgnoreCase("CS101")).findFirst()
                    .orElse(null);
            if (s1 == null) {
                s1 = subjectService.createSubject("Java Programming", "CS101", 4, "Core Java, OOP, Streams, JDBC");
            }

            Subject s2 = subjectService.getAllSubjects().stream()
                    .filter(s -> s.getCode().equalsIgnoreCase("CS201")).findFirst()
                    .orElse(null);
            if (s2 == null) {
                s2 = subjectService.createSubject("Database Management Systems", "CS201", 4, "Relational modeling, SQL, Normalization");
            }

            Subject s3 = subjectService.getAllSubjects().stream()
                    .filter(s -> s.getCode().equalsIgnoreCase("CS301")).findFirst()
                    .orElse(null);
            if (s3 == null) {
                s3 = subjectService.createSubject("Operating Systems", "CS301", 3, "Processes, Threads, CPU Scheduling, Memory");
            }

            // 2. Tasks (only add if no tasks present)
            if (taskService.getAllTasks().isEmpty()) {
                LocalDate today = LocalDate.now();
                taskService.createTask(s1.getId(), "Complete Java OOP Lab", "Implement inheritance and polymorphic classes", Priority.HIGH, today.minusDays(1), 120); // Overdue
                Task t2 = taskService.createTask(s1.getId(), "Practice JDBC & PreparedStatements", "Build CRUD repository queries", Priority.MEDIUM, today, 90); // Due today
                taskService.createTask(s2.getId(), "Normalize DBMS Schema to 3NF", "Review functional dependencies and BCNF", Priority.HIGH, today.plusDays(3), 150);
                taskService.createTask(s3.getId(), "Revise Round-Robin Scheduling", "Simulate process burst times", Priority.LOW, today.plusDays(5), 60);

                // Mark one completed
                taskService.markTaskComplete(t2.getId());
            }

            // 3. Study Sessions (only add if no sessions present)
            if (scheduleService.getAllSessions().isEmpty()) {
                LocalDate today = LocalDate.now();
                scheduleService.createSession(s1.getId(), today, LocalTime.of(14, 0), LocalTime.of(15, 30), "Concurrency and ExecutorService");
                scheduleService.createSession(s2.getId(), today.plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 30), "ER Modeling & Normalization");
            }

            reminderService.refreshAlerts();
            System.out.println("[✓] Demo academic data loaded successfully!");
            System.out.println("    - Subjects ready: CS101, CS201, CS301");
            System.out.println("    - Tasks ready: Overdue lab, completed today, upcoming assignments");
            System.out.println("    - Study sessions ready");
        } catch (Exception e) {
            System.out.println("[!] Failed to seed demo data: " + e.getMessage());
        }
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen - 3) + "...";
    }
}
