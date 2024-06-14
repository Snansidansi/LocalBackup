package com.snansidansi.backup.csv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * A class to write data into a csv file. Uses ';' as field separator. Implements {@code AutoClosable}.
 */
public class CsvWriter implements AutoCloseable{
    BufferedWriter bufferedWriter;

    /**
     * Creates a new {@code CsvWriter} object.
     *
     * @param filePath Path to the file as string.
     * @throws IOException
     */
    public CsvWriter(String filePath) throws IOException {
        this.bufferedWriter = new BufferedWriter(new FileWriter(filePath));
    }

    /**
     * Creates a new {@code CsvWriter} object.
     * @param filePath Path to the file as string.
     * @param append Boolean value that determines if the {@code CsvWriter} should append to the file or overwrite it.
     * @throws IOException
     */
    public CsvWriter(String filePath, boolean append) throws IOException {
        this.bufferedWriter = new BufferedWriter(new FileWriter(filePath, append));
    }

    /**
     * Writes one line to the file.
     * @param data Arbitrary number of strings or a string array.
     * @throws IOException
     */
    public void writeLine(String... data) throws IOException {
        if (data.length > 0)
            this.bufferedWriter.write(String.join(";", data));
        this.bufferedWriter.newLine();
    }

    /**
     * Writes multiple lines to the file.
     * @param data List of string arrays. Each list entry represents a line and each string array represents the data
     *             for the line.
     * @throws IOException
     */
    public void writeAllLines(List<String[]> data) throws IOException {
        if (data.isEmpty()) return;
        for (String[] line : data) this.writeLine(line);
    }

    /**
     * Closes the {@code CsvWriter}. Can be used in try-catch with resources.
     * @throws IOException
     */
    @Override
    public void close() throws IOException{
        this.bufferedWriter.close();
    }
}
