package csv;

import com.snansidansi.csv.CsvReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderTest {
    private final String exampleDataCsvFile = "src/test/resources/csv/example-data.csv";

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
        });
    }

    @Test
    void readSingleLineFromCsvFile() {
        try (CsvReader csvReader = new CsvReader(exampleDataCsvFile)) {
            String[] expected = {"a", "aa"};
            String[] readerOutput = csvReader.readLine();
            Assertions.assertArrayEquals(expected, readerOutput);
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void readAllLinesFromCsvFile() {
        try (CsvReader csvReader = new CsvReader(exampleDataCsvFile)) {
            List<String[]> expected = new ArrayList<>();
            expected.add(new String[]{"a", "aa"});
            expected.add(new String[]{"b", "bb"});
            expected.add(new String[]{"c", "cc"});

            List<String[]> result = csvReader.readAllLines();
            for (int i = 0; i < expected.size(); i++)
                Assertions.assertArrayEquals(expected.get(i), result.get(i));
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }
}
