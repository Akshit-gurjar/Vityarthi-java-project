package com.studyplanner.service;

import com.studyplanner.model.StudySession;
import com.studyplanner.model.Subject;
import com.studyplanner.model.Task;
import com.studyplanner.util.BackupManager;
import com.studyplanner.util.CsvExporter;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service coordinating report generation, CSV data export, and database backup.
 */
public class ReportService {

    private final AnalyticsService analyticsService;
    private final TaskService taskService;
    private final SubjectService subjectService;
    private final ScheduleService scheduleService;
    private final CsvExporter csvExporter;
    private final BackupManager backupManager;

    public ReportService(AnalyticsService analyticsService,
                         TaskService taskService,
                         SubjectService subjectService,
                         ScheduleService scheduleService,
                         CsvExporter csvExporter,
                         BackupManager backupManager) {
        this.analyticsService = analyticsService;
        this.taskService = taskService;
        this.subjectService = subjectService;
        this.scheduleService = scheduleService;
        this.csvExporter = csvExporter;
        this.backupManager = backupManager;
    }

    /**
     * Builds an in-depth formatted text summary of current academic progress.
     */
    public String generateProgressReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("             ACADEMIC PROGRESS REPORT\n");
        sb.append("   Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        sb.append("====================================================\n\n");

        double overallProgress = analyticsService.getOverallCompletionRate();
        sb.append(String.format("Overall Completion : %.1f%%\n\n", overallProgress));

        sb.append("--- Subject-Wise Breakdown ---\n");
        Map<String, Double> subjectProgress = analyticsService.getSubjectWiseProgress();
        if (subjectProgress.isEmpty()) {
            sb.append("No active subjects or tasks.\n");
        } else {
            subjectProgress.forEach((code, rate) -> {
                sb.append(String.format("  * %-15s : %.1f%%\n", code, rate));
            });
        }
        sb.append("\n");

        sb.append("--- Task Statistics ---\n");
        sb.append("  * Total Tasks        : ").append(analyticsService.getTotalTasksCount()).append("\n");
        sb.append("  * Completed Tasks    : ").append(analyticsService.getCompletedTasksCount()).append("\n");
        sb.append("  * Pending Tasks      : ").append(analyticsService.getPendingTasksCount()).append("\n");
        sb.append("  * Overdue Tasks      : ").append(analyticsService.getOverdueTasksCount()).append("\n");
        sb.append("  * High Priority Due  : ").append(analyticsService.getHighPriorityPendingCount()).append("\n");
        sb.append(String.format("  * Est. Task Hours    : %.2f hrs\n", analyticsService.getTotalEstimatedTaskHours()));
        sb.append(String.format("  * Planned Study Time : %.2f hrs\n", analyticsService.getTotalScheduledStudyHours()));
        sb.append("====================================================\n");

        return sb.toString();
    }

    public Path exportTasksToCsv() throws IOException {
        List<Task> tasks = taskService.getAllTasks();
        Map<Integer, String> codeMap = subjectService.getSubjectCodeMap();
        return csvExporter.exportTasks(tasks, codeMap);
    }

    public Path exportScheduleToCsv() throws IOException {
        List<StudySession> sessions = scheduleService.getAllSessions();
        Map<Integer, String> codeMap = subjectService.getSubjectCodeMap();
        return csvExporter.exportSchedule(sessions, codeMap);
    }

    public Path backupDatabase(String dbFilePath) throws IOException {
        return backupManager.createBackup(dbFilePath);
    }

    public Path getExportDirectory() {
        return csvExporter.getExportDirectory();
    }

    public Path getBackupDirectory() {
        return backupManager.getBackupDirectory();
    }
}
