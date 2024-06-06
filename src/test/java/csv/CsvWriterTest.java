package csv;

import com.snansidansi.backup.csv.CsvWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvWriterTest {
    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path path;

    @Test
    @DisplayName("Create a new csv file and write one line to it")
    void writeLineAndCreateFile() throws IOException {
        Path filePath = path.resolve("newFileOneLine.csv");
        try (CsvWriter csvWriter = new CsvWriter(filePath.toString())) {
            String[] data = {"Hello", "Test"};
            csvWriter.writeLine(data);
        } catch (IOException e) {
            Assertions.fail("Unexpected IOException: " + e.getMessage());
        }

        String expected = "Hello;Test";
        List<String> result = Files.readAllLines(filePath, Charset.defaultCharset());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expected, result.getFirst());
    }

    @Test
    @DisplayName("Create a new csv file an write 5 lines to it")
    void writeFiveLinesAndCreateFile() throws IOException {
        Path filePath = path.resolve("newFileFiveLines.csv");
        try (CsvWriter csvWriter = new CsvWriter(filePath.toString())) {
            for (int i = 0; i < 5; i++) {
                csvWriter.writeLine(i + "a", i + "b");
            }
        } catch (IOException e) {
            Assertions.fail("Unexpected IOException: " + e.getMessage());
        }

        List<String> result = Files.readAllLines(filePath, Charset.defaultCharset());
        Assertions.assertEquals(5, result.size());
        for (int i = 0; i < 5; i++) {
            Assertions.assertEquals(i + "a;" + i + "b", result.get(i));
        }
    }

    @Test
    void appendLineToCsvFile() throws IOException {
        Path filePath = path.resolve("appendToFile.csv");
        try (CsvWriter csvWriterFilePrepare = new CsvWriter(filePath.toString())) {
            csvWriterFilePrepare.writeLine("a", "b");
        } catch (IOException e) {
            Assertions.fail("Unexpected IOError during file preparation: " + e.getMessage());
        }

        try (CsvWriter csvWriterAppend = new CsvWriter(filePath.toString(), true)) {
            csvWriterAppend.writeLine("c", "d");
        } catch (IOException e) {
            Assertions.fail("Unexpected IOException when appending to file: " + e.getMessage());
        }

        List<String> result = Files.readAllLines(filePath, Charset.defaultCharset());
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("a;b", result.get(0));
        Assertions.assertEquals("c;d", result.get(1));
    }
}