package com.snansidansi.backup.csv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter implements AutoCloseable{
    BufferedWriter bufferedWriter;

    public CsvWriter(String filePath) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(filePath));
    }

    public CsvWriter(String filePath, boolean append) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(filePath, append));
    }

    public void writeLine(String... data) throws IOException {
        if (data.length > 0)
            bufferedWriter.write(String.join(";", data));
        bufferedWriter.newLine();
    }

    @Override
    public void close() throws IOException{
        bufferedWriter.close();
    }
}
