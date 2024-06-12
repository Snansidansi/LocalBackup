package com.snansidansi.backup.service;

import com.snansidansi.backup.csv.CsvReader;
import com.snansidansi.backup.csv.CsvWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BackupService {
    private final String backupConfigFilePath;
    private final List<SrcDestPair> allBackups;

    public BackupService(String backupConfigFilePath) {
        this.backupConfigFilePath = Paths.get(backupConfigFilePath).toString();
        this.allBackups = readBackups();
    }

    public void runBackup() {
        for (SrcDestPair pathPair : allBackups) {
            Path srcPath = Path.of(pathPair.srcPath());
            Path destPath = Path.of(pathPair.destPath()).resolve(srcPath.getFileName());
            if (Files.isDirectory(srcPath)) backupDir(srcPath, destPath);
            else if (Files.isRegularFile(srcPath)) backupFile(srcPath, destPath);
        }
    }

    // Returns true if any backups in the dir or the sub-dirs were successfully done.
    // Returns false if no backups were done.
    public static boolean backupDir(Path srcPath, Path destPath) {
        if (!Files.exists(destPath))
            if (!destPath.toFile().mkdirs()) return false;

        boolean changedAnything = false;
        for (File subFile : srcPath.toFile().listFiles()) {
            if (subFile.isDirectory() && backupDir(subFile.toPath(), destPath.resolve(subFile.getName())))
                changedAnything = true;
            else if (subFile.isFile() && backupFile(subFile.toPath(), destPath.resolve(subFile.getName())))
                changedAnything = true;
        }

        return changedAnything;
    }

    public static boolean backupFile(Path srcPath, Path destPath) {
        try {
            if (!Files.exists(destPath)
                    || srcPath.toFile().lastModified() != destPath.toFile().lastModified()) {
                Files.createDirectories(destPath.getParent());
                Files.copy(srcPath, destPath, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    public boolean addBackup(List<SrcDestPair> newBackups) {
        try (CsvWriter csvWriter = new CsvWriter(this.backupConfigFilePath, true)) {
            for (SrcDestPair data : newBackups)
                csvWriter.writeLine(data.srcPath(), data.destPath());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean addBackup(SrcDestPair pathPair) {
        try (CsvWriter csvWriter = new CsvWriter(this.backupConfigFilePath, true)) {
            csvWriter.writeLine(pathPair.srcPath(), pathPair.destPath());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean validateBackupPaths(SrcDestPair pathPair)
            throws SourceDoesNotExistException, DestinationNoDirException {
        if (pathPair.srcPath().equals(pathPair.destPath())) return false;
        if (validateSrcPath(pathPair.srcPath())) throw new SourceDoesNotExistException();
        if (validateDestPath(pathPair.destPath())) throw new DestinationNoDirException();
        return true;
    }

    public static boolean validateSrcPath(String srcPath) {
        return Files.notExists(Path.of(srcPath));
    }

    public static boolean validateDestPath(String destPath) {
        return !Files.isDirectory(Path.of(destPath));
    }

    public boolean removeBackup(int... index) {
        if (index.length == 0) return false;

        Arrays.sort(index);
        if (index[index.length - 1] > allBackups.size() - 1) return false;
        if (index[0] < 0) return false;

        try {
            Files.deleteIfExists(Path.of(this.backupConfigFilePath));
        } catch (IOException e) {
            return false;
        }

        for (int i = index.length - 1; i >= 0; i--)
            this.allBackups.remove(index[i]);

        return addBackup(this.allBackups);
    }

    private List<SrcDestPair> readBackups() {
        List<String[]> allBackups;
        try (CsvReader csvReader = new CsvReader(this.backupConfigFilePath)) {
            allBackups = csvReader.readAllLines();
        } catch (IOException e) {
            return new ArrayList<>();
        }

        if (allBackups.isEmpty()) return new ArrayList<>();

        return allBackups.stream()
                .map(e -> new SrcDestPair(e[0], e[1]))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<SrcDestPair> getAllBackups() {
        return this.allBackups;
    }

    public boolean checkIfBackupAlreadyExists(SrcDestPair paths) {
        for (SrcDestPair backupPaths : allBackups) {
            if (backupPaths.srcPath().equals(paths.srcPath()) && backupPaths.destPath().equals(paths.destPath()))
                return true;
        }
        return false;
    }
}
