package com.snansidansi.backup.service;

import com.snansidansi.backup.csv.CsvReader;
import com.snansidansi.backup.csv.CsvWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // #TODO runBackup Methode erstellen.

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

        if (Files.notExists(Path.of(pathPair.srcPath())))
            throw new SourceDoesNotExistException();

        if (!Files.isDirectory(Path.of(pathPair.destPath())))
            throw new DestinationNoDirException();

        return true;
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

        for (int i = index.length - 1; i >= 0; i--) {
            this.allBackups.remove(index[i]);
        }
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
