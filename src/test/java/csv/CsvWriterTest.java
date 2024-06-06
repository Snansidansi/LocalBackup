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
    private static Path tempDir;

    @Test
    @DisplayName("Create a new csv file and write one line to it")
    void writeLineAndCreateFile() throws IOException {
        Path filePath = tempDir.resolve("newFileOneLine.csv");
        writeToCsvFile(filePath, false, new String[]{"Hello","Test"});
        assertFileContent(filePath, 1, "Hello;Test");
    }

    @Test
    @DisplayName("Create a new csv file an write 3 lines to it")
    void writeThreeLinesAndCreateFile() throws IOException {
        Path filePath = tempDir.resolve("newFileFiveLines.csv");
        writeToCsvFile(filePath, false,
                new String[]{"a","a"},
                new String[]{"b","b"},
                new String[]{"c","c"});
        assertFileContent(filePath, 3, "a;a", "b;b", "c;c");
    }

    @Test
    void appendLineToCsvFile() throws IOException {
        Path filePath = tempDir.resolve("appendToFile.csv");
        writeToCsvFile(filePath, false, new String[]{"a", "b"});
        writeToCsvFile(filePath, true, new String[]{"c", "d"});
        assertFileContent(filePath, 2, "a;b", "c;d");
    }

    private void assertFileContent(Path filePath, int expectedSize, String... expectedData) throws IOException {
        List<String> result = Files.readAllLines(filePath, Charset.defaultCharset());
        Assertions.assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedData.length; i++) {
            Assertions.assertEquals(expectedData[i], result.get(i));
        }
    }

    private void writeToCsvFile(Path filePath, boolean append, String[]... data) {
        try (CsvWriter csvWriter = new CsvWriter(filePath.toString(), append)) {
            for (String[] line : data)
                csvWriter.writeLine(line);
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }
}