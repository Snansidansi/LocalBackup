package csv;

import com.snansidansi.csv.CsvReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderTest {
    private final String exampleDataCsvFile = "src/test/resources/csv/example-data.csv";
    private final Path separatorFilesPath = Path.of("src/test/resources/csv/separator");

    @Test
    void openFile() {
        Assertions.assertDoesNotThrow(() -> {
            try (CsvReader csvReader = new CsvReader(exampleDataCsvFile)) {}
        });
    }

    @Test
    void fileNotFoundTest() {
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            CsvReader csvReader = new CsvReader("");
            csvReader.close();
        });
    }

    @Test
    void readSingleLineFromCsvFile() {
        String[] expected = {"a", "aa"};
        String[] result = readSingleLineFromCsv(Path.of(exampleDataCsvFile));

        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void readAllLinesFromCsvFile() {
        try (CsvReader csvReader = new CsvReader(exampleDataCsvFile)) {
            List<String[]> expected = new ArrayList<>();
            expected.add(new String[]{"a", "aa"});
            expected.add(new String[]{"b", "bb"});
            expected.add(new String[]{"c", "cc"});

            List<String[]> result = csvReader.readAllLines();
            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertArrayEquals(expected.get(i), result.get(i));
            }
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void dataContainsSeparator() {
        String[] expected = {"a;b", "c"};
        String[] result = readSingleLineFromCsv(this.separatorFilesPath.resolve("containsSeparator.csv"));

        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void dataContainsSeparatorMarker() {
        String[] expected = {"#a#", "###"};
        String[] result = readSingleLineFromCsv(this.separatorFilesPath.resolve("containsSeparatorMarker.csv"));

        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void dataContainsSeparatorMarkerAndSeparator() {
        String[] expected = {"#;a", ";;#"};
        String[] result = readSingleLineFromCsv(
                this.separatorFilesPath.resolve("containsSeparatorMarkerAndSeparator.csv"));

        Assertions.assertArrayEquals(expected, result);
    }

    private String[] readSingleLineFromCsv(Path filePath) {
        try (CsvReader csvReader = new CsvReader(filePath.toString())) {
            return csvReader.readLine();
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }

        return null;
    }
}
