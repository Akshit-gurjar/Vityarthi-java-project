package com.studyplanner.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility responsible for automated database snapshot backup using Java NIO.2.
 */
public class BackupManager {

    private final Path backupDirectory;
    private static final DateTimeFormatter BACKUP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public BackupManager(String backupDir) {
        this.backupDirectory = Paths.get(backupDir);
        ensureDirectoryExists();
    }

    public BackupManager() {
        this("data/backups");
    }

    private void ensureDirectoryExists() {
        try {
            if (!Files.exists(backupDirectory)) {
                Files.createDirectories(backupDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize backup directory: " + backupDirectory, e);
        }
    }

    /**
     * Creates a timestamped snapshot of the database storage file.
     *
     * @param databasePath path to active database file
     * @return Path to generated backup file
     * @throws IOException if backup fails
     */
    public Path createBackup(String databasePath) throws IOException {
        ensureDirectoryExists();
        Path sourcePath = Paths.get(databasePath);

        if (!Files.exists(sourcePath)) {
            throw new IOException("Database source file does not exist at: " + sourcePath);
        }

        String timestamp = LocalDateTime.now().format(BACKUP_FORMATTER);
        String filename = sourcePath.getFileName().toString();
        String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf('.')) : ".dat";
        Path targetPath = backupDirectory.resolve("studyplanner_backup_" + timestamp + ext);

        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath;
    }

    public Path getBackupDirectory() {
        return backupDirectory;
    }
}
