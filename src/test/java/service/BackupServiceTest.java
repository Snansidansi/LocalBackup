package service;

import com.snansidansi.backup.csv.CsvWriter;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.service.DestinationNoDirException;
import com.snansidansi.backup.service.SourceDoesNotExistException;
import com.snansidansi.backup.service.SrcDestPair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BackupServiceTest {
    private final String exampleBackupDataPath = "src/test/resources/service/example-backup-data.csv";
    private final String existingFilePath = "src/test/resources/service/ExistingFile.txt";
    private final String existingDirPath = "src/test/resources/service";
    private final SrcDestPair[] exampleData = {
            new SrcDestPair("a", "aa"),
            new SrcDestPair("b", "bb"),
            new SrcDestPair("c", "cc")
    };

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path tempDir;

    @Test
    void getCorrectBackupConfigFileContent() {
        BackupService backupService = new BackupService(exampleBackupDataPath);
        List<SrcDestPair> expected = List.of(
                new SrcDestPair("aa", "ab"),
                new SrcDestPair("ba", "bb"),
                new SrcDestPair("ca", "cb"));
        List<SrcDestPair> result = backupService.getAllBackups();

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), result.get(i));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "aa, ab",
            "ba, bb",
            "ca, cb"
    })
    void checkIfBackupAlreadyExits(String source, String destination) {
        BackupService backupService = new BackupService(exampleBackupDataPath);
        Assertions.assertTrue(backupService.checkIfBackupAlreadyExists(new SrcDestPair(source, destination)));
    }

    @Test
    void checkIfBackupNotAlreadyExits() {
        BackupService backupService = new BackupService(exampleBackupDataPath);
        Assertions.assertFalse(backupService.checkIfBackupAlreadyExists(new SrcDestPair("", "")));
    }

    @Test
    void addBackupToBackupsConfig() {
        String filePath = createBackupConfigFile(
                "addBackupToBackupConfig.csv", new SrcDestPair("a", "b"));
        BackupService backupService = new BackupService(filePath);
        backupService.addBackup(new SrcDestPair(filePath, tempDir.toString()));
        assertFileContent(filePath, 2, "a;b", filePath + ";" + tempDir);
    }

    @Test
    void removeTwoBackupsFromBackupConfig() {
        String filePath = createBackupConfigFile("deleteBackup.csv", exampleData);
        BackupService backupService = new BackupService(filePath);
        backupService.removeBackup(1, 2);
        assertFileContent(filePath, 1, "a;aa");
    }

    @ParameterizedTest
    @CsvSource({"1", "-1"})
    void removeBackupsWithInvalidIndexDoesNothing(int index) {
        String filePath = createBackupConfigFile(index + "-invalidIndexForRemove.csv",
                new SrcDestPair("a", "aa"));
        BackupService backupService = new BackupService(filePath);
        Assertions.assertFalse(backupService.removeBackup(index));
        assertFileContent(filePath, 1, "a;aa");
    }

    @Test
    void validateBackupPathsSourceDoesNotExistException() {
        Assertions.assertThrows(SourceDoesNotExistException.class,
                () -> BackupService.validateBackupPaths(new SrcDestPair("dfkslajsfkldsa", existingDirPath)));
    }

    @Test
    void validateBackupPathsDestinationNoDirException() {
        Assertions.assertThrows(DestinationNoDirException.class,
                () -> BackupService.validateBackupPaths(new SrcDestPair(existingFilePath, "fksdajsdfsvd")));
    }

    @Test
    void validateBackupPathsSourceAndDestinationAreEqual() {
        try {
            Assertions.assertFalse(BackupService.validateBackupPaths(new SrcDestPair(existingDirPath, existingDirPath)));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void validateBackupPathsSourceAndDestinationAreValid() {
        try {
            Assertions.assertTrue(BackupService.validateBackupPaths(new SrcDestPair(existingFilePath, existingDirPath)));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    private String createBackupConfigFile(String fileName, SrcDestPair... pathPairs) {
        Path filePath = tempDir.resolve(fileName);
        try (CsvWriter csvWriter = new CsvWriter(filePath.toString())) {
            for (SrcDestPair pathPair : pathPairs)
                csvWriter.writeLine(pathPair.srcPath(), pathPair.destPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        return filePath.toString();
    }

    private void assertFileContent(String filePath, int expectedSize, String... expectedData) {
        List<String> result;
        try {
            result = Files.readAllLines(Path.of(filePath), Charset.defaultCharset());
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
            return;
        }

        Assertions.assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedData.length; i++)
            Assertions.assertEquals(expectedData[i], result.get(i));
    }
}
