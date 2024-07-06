package com.snansidansi.backup.service;

import com.snansidansi.backup.exceptions.DestinationNoDirException;
import com.snansidansi.backup.exceptions.DestinationPathIsInSourcePathException;
import com.snansidansi.backup.exceptions.SourceDoesNotExistException;
import com.snansidansi.backup.exceptions.StringsAreEqualException;
import com.snansidansi.backup.util.SrcDestPair;
import com.snansidansi.csv.CsvReader;
import com.snansidansi.csv.CsvWriter;
import com.snansidansi.log.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A class for different features for creating backups of files and directories.
 * Features:
 * <ul>
 *     <li>Adding a backup to a list</li>
 *     <li>Deleting a backup from a list</li>
 *     <li>Get all backups from a list</li>
 *     <li>Creating a backup of all entries of a list</li>
 *     <li>Validating filepath as backup source and destination</li>
 *     <li>Backup a file or complete directory</li>
 * </ul>
 * <p>
 * Every kind of backup only backs up the data if the corresponding data (file or directory) does not exist or has been
 * modified since the last backup. This applies also to directories (each subfile gets handelt individually).
 */
public class BackupService {
    private final String backupListPath;
    private List<SrcDestPair> allBackups;
    private final Logger backupLog;
    private final Logger errorLog;
    private int retryTime = 5;
    private int maxRetries = 1;

    private int copiedFilesDuringDirBackup = 0;

    /**
     * Creates a new {@code BackupService} object with a {@link Logger} in debug mode.
     *
     * @param backupListPath Path to the backup-list file as string.
     */
    public BackupService(String backupListPath) {
        this.backupLog = new Logger("log/backup", false);
        this.errorLog = new Logger("log/error", false);
        this.backupLog.setLogHeader("---Backup log file from local backup program: " + this.backupLog.getFilename() + "---");
        this.errorLog.setLogHeader("---Error log file form local backup program: " + this.errorLog.getFilename() + "---");
        this.backupLog.setEnabled(false);
        this.errorLog.setEnabled(false);

        this.backupListPath = Paths.get(backupListPath).toString();
        this.allBackups = readBackups();
    }

    /**
     * Creates a new {@code BackupService} object.
     *
     * @param backupListPath Path to the backup-list file as string.
     * @param debugMode      Boolean value if the {@link Logger} should run in debug mode.
     */
    public BackupService(String backupListPath, boolean debugMode) {
        this.backupLog = new Logger("log/backup", debugMode);
        this.errorLog = new Logger("log/error", debugMode);

        this.backupLog.setLogHeader("---Backup log file from local backup program: " + this.backupLog.getFilename() + "---");
        this.errorLog.setLogHeader("---Error log file form local backup program: " + this.errorLog.getFilename() + "---");

        this.backupListPath = Paths.get(backupListPath).toString();
        this.allBackups = readBackups();
    }

    /**
     * Runs a backup with from the backup-list file.
     */
    public void runBackup() {
        int i = 0;
        List<Integer> missingRootIndices = new ArrayList<>();
        List<Integer> missingBackupIndices = new ArrayList<>();

        for (SrcDestPair pathPair : this.allBackups) {
            Path srcPath = Path.of(pathPair.srcPath());

            if (Files.notExists(srcPath.getRoot())) {
                missingRootIndices.add(i);
                continue;
            } else if (Files.notExists(srcPath)) {
                missingBackupIndices.add(i);
                continue;
            }

            Path destPath = Path.of(pathPair.destPath()).resolve(srcPath.getFileName());

            if (Files.isDirectory(srcPath)) {
                backupDirLogged(srcPath, destPath);
            } else if (Files.isRegularFile(srcPath)) {
                backupFileLogged(srcPath, destPath);
            }

            i++;
        }

        try {
            retryBackups(missingRootIndices, missingBackupIndices);
        } catch (InterruptedException unused) {
        }

        removeBackup(convertIntListToArray(missingBackupIndices));

        if (this.errorLog.logCreated()) {
            this.backupLog.log("----Waring----",
                    "An error log was created during this backup: " + this.errorLog.getFilename());
        }
        this.backupLog.finishLog();
        this.backupLog.setLogHeader("---Backup log file from local backup program: " + this.backupLog.getFilename() + "---");

        this.errorLog.finishLog();
        this.errorLog.setLogHeader("---Error log file form local backup program: " + this.errorLog.getFilename() + "---");
    }

    private int[] convertIntListToArray(List<Integer> list) {
        int[] intArray = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            intArray[i] = list.get(i);
        }

        return intArray;
    }

    private void retryBackups(List<Integer> missingRootIndices, List<Integer> missingBackupIndices)
            throws InterruptedException {

        if (missingRootIndices.isEmpty()) {
            return;
        }

        for (int i = 0; i < this.maxRetries; i++) {
            Thread.sleep(this.retryTime * 1000L);
            Iterator<Integer> iterator = missingRootIndices.iterator();

            while (iterator.hasNext()) {
                int index = iterator.next();
                SrcDestPair pathPair = this.allBackups.get(index);
                Path srcPath = Path.of(pathPair.srcPath());

                if (Files.notExists(srcPath.getRoot())) {
                    continue;
                } else if (Files.notExists(srcPath)) {
                    missingBackupIndices.add(i);
                    iterator.remove();
                    continue;
                }

                Path destPath = Path.of(pathPair.destPath());

                if (Files.isDirectory(srcPath)) {
                    backupDirLogged(srcPath, destPath);
                } else if (Files.isRegularFile(srcPath)) {
                    backupFileLogged(srcPath, destPath);
                }

                iterator.remove();
            }
        }
    }

    private void backupDirLogged(Path srcPath, Path destPath) {
        this.copiedFilesDuringDirBackup = 0;
        if (backupDir(srcPath, destPath)) {
            this.backupLog.log("Directory backup.",
                    "Source: " + srcPath.toString(),
                    "Destination: " + destPath.toString(),
                    "Number of copied files: " + this.copiedFilesDuringDirBackup);
        }
    }

    private void backupFileLogged(Path srcPath, Path destPath) {
        if (backupFile(srcPath, destPath)) {
            this.backupLog.log("File backup.",
                    "Source: " + srcPath.toString(),
                    "Destination: " + destPath.toString());
        }
    }

    /**
     * Copies a directory with all subfiles and subdirectories. Creates the destination directory and all missing parent
     * directories if they do not exist. A subfile only is copied when the corresponding file at the destination does
     * not exist or has been modified since the last backup.
     *
     * @param srcPath  Source path as {@code path}.
     * @param destPath Destination path as {@code path}.
     * @return Returns true if any backups in the dir or the sub-dirs were successfully done.
     * Returns false if no backups were done.
     */
    public boolean backupDir(Path srcPath, Path destPath) {
        boolean changedAnything = false;

        if (!Files.exists(destPath)) {
            if (destPath.toFile().mkdirs()) {
                changedAnything = true;
            } else {
                return false;
            }
        }

        for (File subFile : srcPath.toFile().listFiles()) {
            if (subFile.isDirectory() && backupDir(subFile.toPath(), destPath.resolve(subFile.getName()))) {
                changedAnything = true;
            } else if (subFile.isFile() && backupFile(subFile.toPath(), destPath.resolve(subFile.getName()))) {
                changedAnything = true;
                this.copiedFilesDuringDirBackup++;
            }
        }

        return changedAnything;
    }

    /**
     * Copies a file if the corresponding file at the destination does not exist or has been modified since the last
     * backup.
     * @param srcPath Source path as {@code path}.
     * @param destPath Destination path as {@code path}.
     * @return Boolean value if the backup was successful.
     */
    public boolean backupFile(Path srcPath, Path destPath) {
        try {
            if (!Files.exists(destPath)
                    || srcPath.toFile().lastModified() != destPath.toFile().lastModified()) {
                Files.createDirectories(destPath.getParent());
                Files.copy(srcPath, destPath, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            this.errorLog.log("Error during file backup.",
                    "Source: " + srcPath,
                    "Destination: " + destPath,
                    "Error message. " + e.getMessage());
        }
        return false;
    }

    /**
     * Adds multiple backups to the backup-list.
     * @param newBackups A list of source and destination paths as {@link SrcDestPair}.
     * @return Boolean value if the method was successful.
     */
    public boolean addBackup(List<SrcDestPair> newBackups) {
        boolean append = Files.exists(Path.of(this.backupListPath));

        if (!append && !createBackupListDirs()) {
            return false;
        }

        try (CsvWriter csvWriter = new CsvWriter(this.backupListPath, append)) {
            for (SrcDestPair data : newBackups) {
                csvWriter.writeLine(Path.of(data.srcPath()).toAbsolutePath().toString(),
                        Path.of(data.destPath()).toAbsolutePath().toString());
            }
        } catch (IOException e) {
            this.errorLog.log("Error when adding a new backup to the backup list.",
                    "Error message. " + e.getMessage());
            return false;
        }
        this.allBackups = readBackups();
        return true;
    }

    /**
     * Adds a backup to the backup-list.
     * @param pathPair A source and destination path as {@link SrcDestPair}.
     * @return Boolean value if the method was successful.
     */
    public boolean addBackup(SrcDestPair pathPair) {
        boolean append = Files.exists(Path.of(this.backupListPath));

        if (!append && !createBackupListDirs()) {
            return false;
        }

        try (CsvWriter csvWriter = new CsvWriter(this.backupListPath, append)) {
            csvWriter.writeLine(Path.of(pathPair.srcPath()).toAbsolutePath().toString(),
                    Path.of(pathPair.destPath()).toAbsolutePath().toString());
            this.allBackups.add(pathPair);
        } catch (IOException e) {
            this.errorLog.log("Error when adding backup to the backup list.",
                    "New backup source: " + pathPair.srcPath(),
                    "New backup destination: " + pathPair.destPath(),
                    "Error message: " + e.getMessage());
            return false;
        }

        return true;
    }

    private boolean createBackupListDirs() {
        try {
            Files.createDirectories(Path.of(this.backupListPath).getParent());
        } catch (IOException e) {
            this.errorLog.log("Error when adding a backup to the backup list.",
                    "Could not create all missing directories in path: " + Path.of(this.backupListPath).getParent(),
                    "Error message: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Validates a source and a destination path as backup paths. Look at the possible exception names for different
     * kinds of validation.
     * @param pathPair A source and destination path as {@link SrcDestPair}.
     * @throws SourceDoesNotExistException
     * @throws DestinationNoDirException
     * @throws StringsAreEqualException Throws if the source and destination path are equal.
     * @throws DestinationPathIsInSourcePathException Throws if the destination path is a sub path of the source path.
     */
    public static void validateBackupPaths(SrcDestPair pathPair)
            throws SourceDoesNotExistException, DestinationNoDirException, StringsAreEqualException,
            DestinationPathIsInSourcePathException {
        if (pathPair.srcPath().equals(pathPair.destPath())) {
            throw new StringsAreEqualException();
        }
        if (!validateSrcPath(pathPair.srcPath())) {
            throw new SourceDoesNotExistException();
        }
        if (!validateDestPath(pathPair.destPath())) {
            throw new DestinationNoDirException();
        }

        // Check if the destination path is a sub path of the source path
        Iterator<Path> srcPathIterator = Path.of(pathPair.srcPath()).iterator();
        Iterator<Path> destPathIterator = Path.of(pathPair.destPath()).iterator();

        while (srcPathIterator.hasNext() && destPathIterator.hasNext()) {
            if (!srcPathIterator.next().equals(destPathIterator.next())) {
                return;
            }
        }

        if (!srcPathIterator.hasNext()) {
            throw new DestinationPathIsInSourcePathException();
        }
    }

    /**
     * Checks if a path exists.
     * @param srcPath Path as string.
     * @return A boolean value if the path exists.
     */
    public static boolean validateSrcPath(String srcPath) {
        return Files.exists(Path.of(srcPath));
    }

    /**
     * Checks if a path is a directory and if the root exist.
     * @param destPathString Path as string.
     * @return Boolean value if the path is a directory and the root exists.
     */
    public static boolean validateDestPath(String destPathString) {
        Path destPath = Path.of(destPathString);
        if (Files.isDirectory(destPath)) {
            return true;
        }
        try {
            if (Files.notExists(destPath.getRoot())) {
                return false;
            }
        } catch (NullPointerException unused) {
            return false;
        }
        return true;
    }

    /**
     * Removes a backup from the backup-list. Saves a temporary copy of the old backup-list in the same dir. It can
     * happen the methode fails to an unexpected internal {@code IOException} and the temporary file stays in the
     * directory but the original backup-list file is gone.
     * @param index Arbitrary number of indices that should be removed.
     * @return Boolean value if the method was successful.
     */
    public boolean removeBackup(int... index) {
        if (index.length == 0) {
            return false;
        }

        Arrays.sort(index);
        if (index[index.length - 1] > this.allBackups.size() - 1) {
            return false;
        }
        if (index[0] < 0) {
            return false;
        }

        // Copy of old backup-list if new backup-list can't be created.
        Path backupConfigFileCopyPath = Path.of(this.backupListPath).getParent()
                .resolve(this.backupListPath.hashCode() + ".csv");

        try {
            //Copies the file to the same location but with the hashcode as name
            Files.copy(Path.of(this.backupListPath), backupConfigFileCopyPath, StandardCopyOption.REPLACE_EXISTING);

            Files.deleteIfExists(Path.of(this.backupListPath));
        } catch (IOException e) {
            this.errorLog.log("Error when removing a backup from the backup list.",
                    "Error when creating a temporary copy of the old backup list.",
                    "Error message: " + e.getMessage());
            return false;
        }

        // Remove backups
        List<SrcDestPair> allBackupsCopy = new ArrayList<>(this.allBackups);
        for (int i = index.length - 1; i >= 0; i--) {
            this.allBackups.remove(index[i]);
        }

        boolean addSuccessful = addBackup((this.allBackups));

        // Cleanup and error handling
        if (!addSuccessful) {
            try {
                Files.deleteIfExists(Path.of(this.backupListPath));
                Files.move(backupConfigFileCopyPath, Path.of(this.backupListPath));

                this.allBackups.clear();
                this.allBackups.addAll(allBackupsCopy);
            } catch (IOException e) {
                this.errorLog.log("Error when removing a backup from the backup list.",
                        "Error during cleanup after failed remove operation.",
                        "Error message: " + e.getMessage());
            }
        } else {
            try {
                Files.delete(backupConfigFileCopyPath);
            } catch (IOException e) {
                this.errorLog.log("Error when removing a backup from the backup list.",
                        "Error during cleanup of the temporary copy of the backup list after successful remove operation",
                        "Error message: " + e.getMessage());
            }
        }

        return addSuccessful;
    }

    private List<SrcDestPair> readBackups() {
        if (Files.notExists(Path.of(this.backupListPath))) {
            return new ArrayList<>();
        }

        List<String[]> allBackupsFromFile;
        try (CsvReader csvReader = new CsvReader(this.backupListPath)) {
            allBackupsFromFile = csvReader.readAllLines();
        } catch (IOException e) {
            this.errorLog.log("Error when reading the content of the backup file.",
                    "An IOException occurred: " + e.getMessage());
            return new ArrayList<>();
        }

        if (allBackupsFromFile.isEmpty()) {
            return new ArrayList<>();
        }

        List<SrcDestPair> outputList = new ArrayList<>(allBackupsFromFile.size());
        for (String[] strings : allBackupsFromFile) {
            try {
                outputList.add(new SrcDestPair(strings[0], strings[1]));
            } catch (ArrayIndexOutOfBoundsException e) {
                this.errorLog.log("Error when reading a backup from the backup list file.",
                        "Array out of bounds exception occurred.",
                        "Content of the invalid line: " + Arrays.toString(strings));
            }
        }

        return outputList;
    }

    /**
     * @return The backup-list as list of {@link SrcDestPair}.
     */
    public List<SrcDestPair> getAllBackups() {
        return this.allBackups;
    }

    /**
     * Checks if a backup already exist in the backup-list.
     * @param paths A source and destination path as {@link SrcDestPair}.
     * @return Boolean value if the backup already exists.
     */
    public boolean checkIfBackupAlreadyExists(SrcDestPair paths) {
        for (SrcDestPair backupPaths : this.allBackups) {
            if (backupPaths.srcPath().equals(paths.srcPath()) && backupPaths.destPath().equals(paths.destPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enables or disables logging.
     *
     * @param enabled Boolean if logging should be enabled.
     */
    public void setLoggerEnabled(boolean enabled) {
        this.errorLog.setEnabled(enabled);
        this.backupLog.setEnabled(enabled);
    }

    /**
     * Gets the error logger.
     * @return Error logger as {@link Logger}.
     */
    public Logger getErrorLog() {
        return this.errorLog;
    }

    /**
     * Gets the backup logger.
     * @return Backup logger as {@link Logger}.
     */
    public Logger getBackupLog() {
        return this.backupLog;
    }

    /**
     * Sets the time in seconds how long after the call of {@link #runBackup()} should be waited before the backup of
     * paths with a missing root is retried.
     *
     * @param seconds Time in seconds as int.
     */
    public void setRetryTime(int seconds) {
        this.retryTime = seconds;
    }

    /**
     * Sets the maximum number of retries for a backup with a missing root after the call of {@link #runBackup()}.
     *
     * @param maxRetries Maximum number of retires as int.
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}
