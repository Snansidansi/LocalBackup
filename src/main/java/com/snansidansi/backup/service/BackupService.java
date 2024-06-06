package com.snansidansi.backup.service;

import com.snansidansi.backup.csv.CsvReader;
import com.snansidansi.backup.csv.CsvWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BackupService {
    private final String backupConfigFilePath;

    public BackupService(String backupConfigFilePath) {
        this.backupConfigFilePath = Paths.get(backupConfigFilePath).toString();
    }

    // #TODO runBackup Methode erstellen.

    public void addBackup(String sourcePath, String destinationPath)
            throws SourceDoesNotExistException, NotDirectoryException {
        if (sourcePath.equals(destinationPath)) return;

        if (Files.notExists(Path.of(sourcePath)))
            throw new SourceDoesNotExistException("Source path could not be found: " + sourcePath);

        if (!Files.isDirectory(Path.of(destinationPath)))
            throw new NotDirectoryException("Destination path is no directory: " + destinationPath);

        if (checkIfBackupAlreadyExists(sourcePath, destinationPath)) return;

        try (CsvWriter csvWriter = new CsvWriter(this.backupConfigFilePath, true)) {
            csvWriter.writeLine(sourcePath, destinationPath);
        } catch (IOException e) {
            System.out.println("Unexpected IOException while adding entry to \"" + this.backupConfigFilePath + "\".");
            System.out.println(e.getMessage());
        }
    }

    // #TODO Create deleteBackup()

    public boolean checkIfBackupAlreadyExists(String sourcePath, String destinationPath) {
        try (CsvReader csvReader = new CsvReader(backupConfigFilePath)) {
            String[] lineData;
            while ((lineData = csvReader.readLine()) != null) {
                if (lineData[0].equals(sourcePath) && lineData[1].equals(destinationPath)) return true;
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            return true;
        }
        return false;
    }
}
