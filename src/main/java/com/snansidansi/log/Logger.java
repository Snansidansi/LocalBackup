package com.snansidansi.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A class for creating simple logfiles with a {@code .txt} extension.
 */
public class Logger {
    private int maxNumberOfLogs = 100;
    private final Path outputDir;
    private File logFile;
    private final boolean debugMode;
    private boolean successfulSetup = false;
    private boolean firstLogMessage = true;
    private String logHeader = null;
    private boolean enabled = true;

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
        setup();
    }

    public void finishLog() {
        this.firstLogMessage = true;
        this.logHeader = null;
        this.successfulSetup = false;
        setup();
    }

    private void setup() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        this.logFile = this.outputDir.resolve(now.format(dateTimeFormatter) + ".txt").toFile();

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
        if (!enabled) return;
        if (!this.successfulSetup) {
            System.out.println("Cannot log message without successful setup of the Logger.");
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(this.logFile, true);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {

            if (this.firstLogMessage) prepareFirstLogMessage(bufferedWriter);

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

    private void prepareFirstLogMessage(BufferedWriter bufferedWriter) throws IOException {
        FilenameFilter logFileFilter = (dir, name) -> name.endsWith(".txt");

        File[] logFiles = this.outputDir.toFile().listFiles(logFileFilter);
        if (logFiles == null) return;
        Arrays.sort(logFiles, Comparator.comparing(File::getName));

        int numberOfLogFiles = logFiles.length;
        if (numberOfLogFiles > this.maxNumberOfLogs) {
            for (int i = 0; i < numberOfLogFiles - this.maxNumberOfLogs; i++)
                logFiles[i].delete();
        }

        if (this.logHeader != null) bufferedWriter.write(this.logHeader + "\n\n");

        this.firstLogMessage = false;
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

    /**
     * Enables or disables the logger.
     *
     * @param enabled New state of the logger as boolean.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the maximum number of logs that can exists at the same time. If the maximum number of logs is reached, the
     * oldest log will be deleted (Oldest .txt file in log dir filtert by the date in the filename).
     *
     * @param maximumNumberOfLogs The maximum number of logs as int.
     */
    public void setMaxNumberOfLogs(int maximumNumberOfLogs) {
        this.maxNumberOfLogs = maximumNumberOfLogs;
    }
}
