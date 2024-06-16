package com.snansidansi.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Logger {
    private Path outputDir;
    private File logFile;
    private boolean debugMode;

    public Logger(String outputDir, String logName, boolean debugMode) {
        this.outputDir = Path.of(outputDir);
        this.logFile = this.outputDir.resolve(logName + ".txt").toFile();
        this.debugMode = debugMode;
    }

    private void setup() throws NoSuchRootException {
        if (Files.notExists(this.outputDir.getRoot()))
            throw new NoSuchRootException(this.outputDir.getRoot().toString());

        try {
            Files.createDirectories(this.outputDir);
            Files.deleteIfExists(logFile.toPath());
            logFile.createNewFile();
        } catch (IOException e) {
            if (this.debugMode) {
                System.out.println("Error when setting up the logger:");
                System.out.println(e.getMessage());
            }
        }
    }

    public void log(String input) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.logFile, true))) {
            bufferedWriter.write(input);
            bufferedWriter.newLine();
        } catch (IOException e) {
            if (this.debugMode) {
                System.out.println("Error when logging a message:");
                System.out.println("Message: " + input);
                System.out.println(e.getMessage());
            }
        }
    }
}
