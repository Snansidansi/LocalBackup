package com.snansidansi.backup.service;

import com.snansidansi.backup.csv.CsvReader;
import com.snansidansi.backup.csv.CsvWriter;
import com.snansidansi.backup.exceptions.DestinationNoDirException;
import com.snansidansi.backup.exceptions.DestinationPathIsInSourcePathException;
import com.snansidansi.backup.exceptions.SourceDoesNotExistException;
import com.snansidansi.backup.exceptions.StringsAreEqualException;
import com.snansidansi.backup.util.SrcDestPair;

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
import java.util.stream.Collectors;

public class BackupService {
    private final String backupConfigFilePath;
    private List<SrcDestPair> allBackups;

    public BackupService(String backupConfigFilePath) {
        this.backupConfigFilePath = Paths.get(backupConfigFilePath).toString();
        this.allBackups = readBackups();
    }

    public void runBackup() {
        for (SrcDestPair pathPair : this.allBackups) {
            Path srcPath = Path.of(pathPair.srcPath());
            if (Files.notExists(srcPath)) return;

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
        boolean append = Files.exists(Path.of(this.backupConfigFilePath));

        try {
            if (!append) Files.createDirectories(Path.of(this.backupConfigFilePath).getParent());
        } catch (IOException unused) {
            return false;
        }

        try (CsvWriter csvWriter = new CsvWriter(this.backupConfigFilePath, append)) {
            for (SrcDestPair data : newBackups) {
                csvWriter.writeLine(Path.of(data.srcPath()).toAbsolutePath().toString(),
                        Path.of(data.destPath()).toAbsolutePath().toString());
            }
            this.allBackups = getAllBackups();
        } catch (IOException unused) {
            return false;
        }
        return true;
    }

    public boolean addBackup(SrcDestPair pathPair) {
        boolean append = Files.exists(Path.of(this.backupConfigFilePath));

        try {
            if (!append) Files.createDirectories(Path.of(this.backupConfigFilePath).getParent());
        } catch (IOException unused) {
            return false;
        }

        try (CsvWriter csvWriter = new CsvWriter(this.backupConfigFilePath, append)) {
            csvWriter.writeLine(Path.of(pathPair.srcPath()).toAbsolutePath().toString(),
                    Path.of(pathPair.destPath()).toAbsolutePath().toString());
            this.allBackups.add(pathPair);
        } catch (IOException unused) {
            return false;
        }
        return true;
    }

    public static void validateBackupPaths(SrcDestPair pathPair)
            throws SourceDoesNotExistException, DestinationNoDirException, StringsAreEqualException,
            DestinationPathIsInSourcePathException {
        if (pathPair.srcPath().equals(pathPair.destPath())) throw new StringsAreEqualException();
        if (!validateSrcPath(pathPair.srcPath())) throw new SourceDoesNotExistException();
        if (!validateDestPath(pathPair.destPath())) throw new DestinationNoDirException();

        // Check if the destination path is a sub path of the source path
        Iterator<Path> srcPathIterator = Path.of(pathPair.srcPath()).iterator();
        Iterator<Path> destPathIterator = Path.of(pathPair.destPath()).iterator();

        while (srcPathIterator.hasNext() && destPathIterator.hasNext()) {
            if (!srcPathIterator.next().equals(destPathIterator.next())) return;
        }

        if (!srcPathIterator.hasNext()) throw new DestinationPathIsInSourcePathException();
    }

    public static boolean validateSrcPath(String srcPath) {
        return !Files.notExists(Path.of(srcPath));
    }

    public static boolean validateDestPath(String destPathString) {
        Path destPath = Path.of(destPathString);
        if (Files.isDirectory(destPath)) return true;
        try {
            if (Files.notExists(destPath.getRoot())) return false;
        } catch (NullPointerException unused) {
            return false;
        }
        return true;
    }

    public boolean removeBackup(int... index) {
        if (index.length == 0) return false;

        Arrays.sort(index);
        if (index[index.length - 1] > this.allBackups.size() - 1) return false;
        if (index[0] < 0) return false;

        Path backupConfigFileCopyPath = Path.of(this.backupConfigFilePath).getParent()
                .resolve(this.backupConfigFilePath.hashCode() + ".csv");
        try {
            //Copies the file to the same location but with the hashcode as name
            Files.copy(Path.of(this.backupConfigFilePath), backupConfigFileCopyPath, StandardCopyOption.REPLACE_EXISTING);

            Files.deleteIfExists(Path.of(this.backupConfigFilePath));
        } catch (IOException unused) {
            return false;
        }

        List<SrcDestPair> allBackupsCopy = new ArrayList<>(this.allBackups);
        for (int i = index.length - 1; i >= 0; i--)
            this.allBackups.remove(index[i]);

        boolean addSuccessful = addBackup((this.allBackups));
        if (!addSuccessful) {
            try {
                Files.deleteIfExists(Path.of(this.backupConfigFilePath));
                Files.move(backupConfigFileCopyPath, Path.of(this.backupConfigFilePath));

                this.allBackups.clear();
                this.allBackups.addAll(allBackupsCopy);
            } catch (IOException unused) {
            }
        } else {
            try {
                Files.delete(backupConfigFileCopyPath);
            } catch (IOException unused) {
            }
        }

        return addSuccessful;
    }

    private List<SrcDestPair> readBackups() {
        List<String[]> allBackupsFromFile;
        try (CsvReader csvReader = new CsvReader(this.backupConfigFilePath)) {
            allBackupsFromFile = csvReader.readAllLines();
        } catch (IOException unused) {
            return new ArrayList<>();
        }

        if (allBackupsFromFile.isEmpty()) return new ArrayList<>();

        return allBackupsFromFile.stream()
                .map(e -> new SrcDestPair(e[0], e[1]))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<SrcDestPair> getAllBackups() {
        return this.allBackups;
    }

    public boolean checkIfBackupAlreadyExists(SrcDestPair paths) {
        for (SrcDestPair backupPaths : this.allBackups) {
            if (backupPaths.srcPath().equals(paths.srcPath()) && backupPaths.destPath().equals(paths.destPath()))
                return true;
        }
        return false;
    }
}
