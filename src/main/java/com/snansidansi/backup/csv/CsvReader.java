package com.snansidansi.backup.csv;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvReader implements AutoCloseable{
    private final BufferedReader bufferedReader;

    public CsvReader (String filename) throws FileNotFoundException {
        File inputFile = new File(filename);
        if (!inputFile.exists())
            throw new FileNotFoundException("CSV file not found: " + filename);
        this.bufferedReader = new BufferedReader(new FileReader(inputFile));
    }

    public String[] readLine() throws IOException {
        String line = this.bufferedReader.readLine();
        if (line == null) return null;
        return line.split(";");
    }

    public List<String[]> readAllLines() throws IOException {
        String[] line;
        List<String[]> allLines = new ArrayList<>();

        while ((line = readLine()) != null)
            allLines.add(line);
        return allLines;
    }

    @Override
    public void close() throws IOException {
        this.bufferedReader.close();
    }
}
