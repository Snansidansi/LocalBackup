package service;

import com.snansidansi.backup.csv.CsvWriter;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.service.SourceDoesNotExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.List;

public class BackupServiceTest {
    private final String exampleBackupDataPath = "src/test/resources/service/example-backup-data.csv";
    private final String existingFilePath = "src/test/resources/service/ExistingFile.txt";
    private final String existingDirPath = "src/test/resources/service";

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path tempDir;

    @ParameterizedTest
    @CsvSource({
            "aa, ab",
            "ba, bb",
            "ca, cb"
    })
    void checkIfBackupAlreadyExits(String source, String destination) {
        BackupService backupService = new BackupService(exampleBackupDataPath);
        Assertions.assertTrue(backupService.checkIfBackupAlreadyExists(source, destination));
    }

    @Test
    void checkIfBackupNotAlreadyExits() {
        BackupService backupService = new BackupService(exampleBackupDataPath);
        Assertions.assertFalse(backupService.checkIfBackupAlreadyExists("", ""));
    }

    @Test
    void backupSourceDoesNotExist() {
        BackupService notExistingSourceBackupService = new BackupService("");
        Assertions.assertThrows(SourceDoesNotExistException.class, () ->
                notExistingSourceBackupService.addBackup("NoExistingPath", tempDir.toString()));
    }

    @Test
    void backupDestinationIsNoDirectory() {
        BackupService noDirDestinationBackupService = new BackupService("");
        Assertions.assertThrows(NotDirectoryException.class, () ->
                noDirDestinationBackupService.addBackup(existingFilePath, "file.txt"));
    }

    @Test
    void addBackupToBackupConfig() throws SourceDoesNotExistException, IOException {
        String filePath = createBackupConfigFile("addBackupToBackupConfig.csv", "a", "b");
        BackupService backupService = new BackupService(filePath);
        backupService.addBackup(filePath, tempDir.toString());
        assertFileContent(filePath, 2, "a;b", filePath + ";" + tempDir);
    }

    @Test
    void addExistingBackupToBackupConfigChangesNothing()
            throws SourceDoesNotExistException, IOException {
        String filePath = createBackupConfigFile("addExistingBackupToBackupConfig.csv",
                existingFilePath, existingDirPath);

        BackupService backupService = new BackupService(filePath);
        backupService.addBackup(existingFilePath, existingDirPath);
        assertFileContent(filePath, 1, existingFilePath + ";" + existingDirPath);
    }

    @Test
    void addDirectoryAsBackupSourceToBackupConfig()
            throws SourceDoesNotExistException, IOException {
        String filePath = createBackupConfigFile("addDirAsSource.csv");

        BackupService backupService = new BackupService(filePath);
        backupService.addBackup(existingDirPath, tempDir.toString());
        assertFileContent(filePath, 1, existingDirPath + ";" + tempDir);
    }

    @Test
    void addBackupToBackupConfigWithSameSourceAndDestination()
            throws SourceDoesNotExistException, IOException {
        String filePath = createBackupConfigFile("sameSourceAndDestination.csv");

        BackupService backupService = new BackupService(filePath);
        backupService.addBackup(existingFilePath, existingFilePath);
        assertFileContent(filePath, 0);
    }

    private String createBackupConfigFile(String fileName, String... data) {
        Path filePath = tempDir.resolve(fileName);
        try (CsvWriter csvWriter = new CsvWriter(filePath.toString())) {
            for (int i = 0; i < data.length; i += 2) {
                csvWriter.writeLine(data[i], data[i + 1]);
            }
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        return filePath.toString();
    }

    private void assertFileContent(String filePath, int expectedSize, String... expectedData) throws IOException {
        List<String> result = Files.readAllLines(Path.of(filePath), Charset.defaultCharset());
        Assertions.assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedData.length; i++) {
            Assertions.assertEquals(expectedData[i], result.get(i));
        }
    }
}
