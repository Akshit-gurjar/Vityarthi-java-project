package com.studyplanner.util;

import com.studyplanner.model.StudySession;
import com.studyplanner.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility responsible for exporting data to CSV format using Java NIO.2.
 */
public class CsvExporter {

    private final Path exportDirectory;
    private static final DateTimeFormatter FILE_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public CsvExporter(String exportDir) {
        this.exportDirectory = Paths.get(exportDir);
        ensureDirectoryExists();
    }

    public CsvExporter() {
        this("data/exports");
    }

    private void ensureDirectoryExists() {
        try {
            if (!Files.exists(exportDirectory)) {
                Files.createDirectories(exportDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize export directory: " + exportDirectory, e);
        }
    }

    /**
     * Exports a list of tasks to CSV with subject code mapping.
     */
    public Path exportTasks(List<Task> tasks, Map<Integer, String> subjectCodeMap) throws IOException {
        ensureDirectoryExists();
        String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMAT);
        Path targetFile = exportDirectory.resolve("tasks_export_" + timestamp + ".csv");

        List<String> lines = new ArrayList<>();
        // CSV Header
        lines.add("TaskID,SubjectCode,Title,Description,Priority,Deadline,Status,EstimatedMinutes,IsOverdue");

        for (Task t : tasks) {
            String subjectCode = subjectCodeMap.getOrDefault(t.getSubjectId(), "N/A");
            String line = String.format("%d,\"%s\",\"%s\",\"%s\",%s,%s,%s,%d,%b",
                    t.getId(),
                    escapeCsv(subjectCode),
                    escapeCsv(t.getTitle()),
                    escapeCsv(t.getDescription() == null ? "" : t.getDescription()),
                    t.getPriority(),
                    t.getDeadline() == null ? "" : t.getDeadline().toString(),
                    t.getStatus(),
                    t.getEstimatedMinutes(),
                    t.isOverdue()
            );
            lines.add(line);
        }

        Files.write(targetFile, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return targetFile;
    }

    /**
     * Exports scheduled study sessions to CSV with subject code mapping.
     */
    public Path exportSchedule(List<StudySession> sessions, Map<Integer, String> subjectCodeMap) throws IOException {
        ensureDirectoryExists();
        String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMAT);
        Path targetFile = exportDirectory.resolve("schedule_export_" + timestamp + ".csv");

        List<String> lines = new ArrayList<>();
        // CSV Header
        lines.add("SessionID,SubjectCode,Date,StartTime,EndTime,DurationMinutes,Topic,Status");

        for (StudySession s : sessions) {
            String subjectCode = subjectCodeMap.getOrDefault(s.getSubjectId(), "N/A");
            String line = String.format("%d,\"%s\",%s,%s,%s,%d,\"%s\",%s",
                    s.getId(),
                    escapeCsv(subjectCode),
                    s.getSessionDate(),
                    s.getStartTime(),
                    s.getEndTime(),
                    s.getDurationMinutes(),
                    escapeCsv(s.getTopic() == null ? "" : s.getTopic()),
                    s.getStatus()
            );
            lines.add(line);
        }

        Files.write(targetFile, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return targetFile;
    }

    public Path getExportDirectory() {
        return exportDirectory;
    }

    private static String escapeCsv(String field) {
        if (field == null) return "";
        return field.replace("\"", "\"\"");
    }
}
