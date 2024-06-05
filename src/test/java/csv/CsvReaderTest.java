package csv;

import com.snansidansi.backup.csv.CsvReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

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
            String[] expected = {"1Source", "1Destination"};
            String[] readerOutput = csvReader.readLine();
            Assertions.assertArrayEquals(expected, readerOutput);
        } catch (Exception e) {
            Assertions.fail("Something unexpected happend: " + e.getMessage());
        }
    }

    @Test
    void readWholeCsvFile() {
        try (CsvReader csvReader = new CsvReader(exampleDataCsvFile)) {
            String[][] expected = {
                    {"1Source", "1Destination"},
                    {"2Source", "2Destination"},
                    {"3Source", "3Destination"}
            };

            String[] line;
            int i = 0;
            while ((line = csvReader.readLine()) != null) {
                Assertions.assertArrayEquals(line, expected[i]);
                i++;
            }
        } catch (IOException e) {
            Assertions.fail("IOException happened during the test: " + e.getMessage());
        } catch (Exception e) {
            Assertions.fail("Unexpected exception during test: " + e.getMessage());
        }
    }
}
