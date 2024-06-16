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
    private boolean setupDone = false;

    private Logger(String outputDir, boolean debugMode) throws NoSuchRootException {
        this.outputDir = Path.of(outputDir);
        if (Files.notExists(this.outputDir.getRoot()))
            throw new NoSuchRootException(this.outputDir.getRoot().toString());

        this.debugMode = debugMode;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        this.logFile = this.outputDir.resolve(now.format(dateTimeFormatter) + ".txt").toFile();

        setup();
    }

    /**
     * Creates a {@code Logger} object. The name of the logfile is the current date and time
     * (format: {@code yyyy-MM-dd_hh-mm-ss}). Also creates any missing directories in the {@code outputDir} path.
     *
     * @param outputDir The output directory for the log file as string.
     * @param debugMode Boolean value if the program runs in debug mode. When true: error messages from the
     *                  {@code Logger} class itself will be printed to the console.
     * @throws NoSuchRootException Gets thrown when the root of the given {@code outputDir} in the constructor of the
     * {@code Logger} object does not exist.
     * @return The created {@code Logger} instance.
     */
    public static Logger init(String outputDir, boolean debugMode) throws NoSuchRootException {
        return new Logger(outputDir, debugMode);
    }

    private void setup() {
        try {
            Files.createDirectories(this.outputDir);
            Files.deleteIfExists(logFile.toPath());
            this.setupDone = true;
        } catch (IOException e) {
            if (this.debugMode) {
                System.out.println("-----");
                System.out.println("Error when setting up the logger:");
                System.out.println(e.getMessage());
                System.out.println("-----");
            }
        }
    }

    /**
     * Logs the input to the logfile.
     * @param input Log message as string.
     */
    public void log(String... input) {
        if (!this.setupDone) {
            System.out.println("Cannot log message without successful setup of the Logger.");
            return;
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.logFile, true))) {
            for (String line : input)
                bufferedWriter.write(line);
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
