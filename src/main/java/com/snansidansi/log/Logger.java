package com.snansidansi.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class for creating simple logfiles with a {@code .txt} extension.
 */
public class Logger {
    private Path outputDir;
    private File logFile;
    private boolean debugMode;

    /**
     * Creates a {@code Logger} object. The name of the logfile is the current date and time
     * (format: yyyy-MM-dd hh-mm-ss)
     *
     * @param outputDir The output directory for the log file as string.
     * @param debugMode Boolean value if the program runs in debug mode. When true: error messages from the
     *                  {@code Logger} class itself will be printed to the console.
     */
    public Logger(String outputDir, boolean debugMode) {
        this.outputDir = Path.of(outputDir);
        this.debugMode = debugMode;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        this.logFile = this.outputDir.resolve(now.format(dateTimeFormatter) + ".txt").toFile();
    }

    /**
     * Does the necessary setup for the {@code Logger}. Creates all missing directories of the {@code outputDir} and the
     * log file.
     * @throws NoSuchRootException Gets thrown when the root of the given {@code outputDir} in the constructor of the
     * {@code Logger} object does not exist.
     */
    public void setup() throws NoSuchRootException {
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

    /**
     * Logs the input to the logfile.
     * @param input Log message as string.
     */
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

    /**
     * @return The name of the log file as string.
     */
    public String getFilename() {
        return this.logFile.getName();
    }

    /**
     * @return The path of the log file as string.
     */
    public String getFilePath() {
        return this.logFile.getPath();
    }
}
