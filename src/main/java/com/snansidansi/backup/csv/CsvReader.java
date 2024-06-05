package com.snansidansi.backup.csv;

import java.io.*;

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

    @Override
    public void close() throws IOException {
        this.bufferedReader.close();
    }
}
