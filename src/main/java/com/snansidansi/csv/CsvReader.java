package com.snansidansi.csv;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to write data to file in a csv format. Uses ';' as field separator. Implements {@code AutoClosable}.
 */
public class CsvReader implements AutoCloseable{
    private final BufferedReader bufferedReader;

    /**
     * Creates a new {@code CsvReader} object.
     *
     * @param filename Path to the file as string.
     * @throws FileNotFoundException Gets thrown when the file does not exist.
     */
    public CsvReader (String filename) throws FileNotFoundException {
        File inputFile = new File(filename);
        if (!inputFile.exists())
            throw new FileNotFoundException("CSV file not found: " + filename);
        this.bufferedReader = new BufferedReader(new FileReader(inputFile));
    }

    /**
     * Reads a single line form the file.
     * @return String array of the separated values from the line.
     * @throws IOException
     */
    public String[] readLine() throws IOException {
        String line = this.bufferedReader.readLine();
        if (line == null) return null;
        return line.split(";");
    }

    /**
     * Reads all lines form the file.
     * @return List of string arrays. Each list entry represents a line of the file and each string array represents
     * the data from the line.
     * @throws IOException
     */
    public List<String[]> readAllLines() throws IOException {
        String[] line;
        List<String[]> allLines = new ArrayList<>();

        while ((line = readLine()) != null)
            allLines.add(line);
        return allLines;
    }

    /**
     * Closes the {@code CsvReader}. Can be used in try-catch with resources.
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        this.bufferedReader.close();
    }
}
