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
    private final Path outputDir;
    private final File logFile;
    private final boolean debugMode;
    private boolean successfulSetup = false;
    private boolean firstLogMessage = true;
    private String logHeader = null;

    /**
     * Creates a {@code Logger} object. The name of the logfile is the current date and time
     * (format: {@code yyyy-MM-dd_HH-mm-ss}). Also creates any missing directories in the {@code outputDir} path.
     *
     * @param outputDir The output directory for the log file as string.
     * @param debugMode Boolean value if the program runs in debug mode. When true: error messages from the
     *                  {@code Logger} class itself will be printed to the console.
     */
    public Logger(String outputDir, boolean debugMode) {
        this.outputDir = Path.of(outputDir);
        this.debugMode = debugMode;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        this.logFile = this.outputDir.resolve(now.format(dateTimeFormatter) + ".txt").toFile();

        setup();
    }

    private void setup() {
        try {
            Files.createDirectories(this.outputDir);
            Files.deleteIfExists(logFile.toPath());
            this.successfulSetup = true;
        } catch (IOException e) {
            if (this.debugMode) {
                System.out.println("-----");
                System.out.println("Error when setting up the logger:");
                System.out.println("Output directory: " + this.outputDir);
                System.out.println();
                System.out.println("Error:");
                System.out.println(e.getMessage());
                System.out.println("-----");
            }
        }
    }

    public void setLogHeader(String input) {
        this.logHeader = input;
    }

    /**
     * Logs the input to the logfile. Does not work if the root directory of the {@code outputDir} does not exist.
     * @param input Log message as string.
     */
    public void log(String... input) {
        if (!this.successfulSetup) {
            System.out.println("Cannot log message without successful setup of the Logger.");
            return;
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.logFile, true))) {
            if (this.firstLogMessage && this.logHeader != null) {
                bufferedWriter.write(this.logHeader + "\n\n");
                this.firstLogMessage = false;
            }

            for (String line : input)
                bufferedWriter.write(line + "\n");

            bufferedWriter.newLine();
        } catch (IOException e) {
            if (this.debugMode) {
                System.out.println("-----");
                System.out.println("Error when logging a message:");
                System.out.println("Message:");
                for (String line : input)
                    System.out.println(line);
                System.out.println();

                System.out.println("Error:");
                System.out.println(e.getMessage());
                System.out.println("-----");
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
