package com.snansidansi.backup.csv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter implements AutoCloseable{
    BufferedWriter bufferedWriter;

    public CsvWriter(String filePath) throws IOException {
        this.bufferedWriter = new BufferedWriter(new FileWriter(filePath));
    }

    public CsvWriter(String filePath, boolean append) throws IOException {
        this.bufferedWriter = new BufferedWriter(new FileWriter(filePath, append));
    }

    public void writeLine(String... data) throws IOException {
        if (data.length > 0)
            this.bufferedWriter.write(String.join(";", data));
        this.bufferedWriter.newLine();
    }

    public void writeAllLines(List<String[]> data) throws IOException {
        if (data.isEmpty()) return;
        for (String[] line : data) this.writeLine(line);
    }

    @Override
    public void close() throws IOException{
        this.bufferedWriter.close();
    }
}
