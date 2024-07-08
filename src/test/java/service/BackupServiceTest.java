package service;

import com.snansidansi.backup.exceptions.DestinationNoDirException;
import com.snansidansi.backup.exceptions.DestinationPathIsInSourcePathException;
import com.snansidansi.backup.exceptions.SourceDoesNotExistException;
import com.snansidansi.backup.exceptions.StringsAreEqualException;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.util.SrcDestPair;
import com.snansidansi.csv.CsvWriter;
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
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class BackupServiceTest {
    private final String exampleBackupDataPath = Paths.get("src/test/resources/service/example-backup-data.csv").toAbsolutePath().toString();
    private final String existingFilePath = Paths.get("src/test/resources/service/ExistingFile.txt").toAbsolutePath().toString();
    private final String existingDirPath = Paths.get("src/test/resources/service").toAbsolutePath().toString();

    private final Path backupExSrc = Path.of("src/test/resources/service/backupExamples").toAbsolutePath();

    private final SrcDestPair[] exampleData = {
            new SrcDestPair(Paths.get("aa").toAbsolutePath().toString(),
                    Paths.get("ab").toAbsolutePath().toString()),
            new SrcDestPair(Paths.get("ba").toAbsolutePath().toString(),
                    Paths.get("bb").toAbsolutePath().toString()),
            new SrcDestPair(Paths.get("ca").toAbsolutePath().toString(),
                    Paths.get("cb").toAbsolutePath().toString())
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
        assertFileContent(filePath, 1, exampleData[0].srcPath() + ";" + exampleData[0].destPath());
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
                () -> BackupService.validateBackupPaths(new SrcDestPair(existingFilePath, "fksdajsdfsvd.txt")));
    }

    @Test
    void validateBackupPathsSourceAndDestinationAreEqual() {
        Assertions.assertThrows(StringsAreEqualException.class,
                () -> BackupService.validateBackupPaths(new SrcDestPair(existingDirPath, existingDirPath)));
    }

    @Test
    void validateBackupPathsSourceAndDestinationAreValid() {
        Assertions.assertDoesNotThrow(
                () -> BackupService.validateBackupPaths(new SrcDestPair(existingFilePath, existingDirPath)));
    }

    @Test
    void validateBackupsPathsDestinationIsDirButDoesNotExist() {
        Assertions.assertDoesNotThrow(
                () -> BackupService.validateBackupPaths(new SrcDestPair(existingFilePath,
                        Path.of(existingFilePath).getParent().resolve("newDir").toFile().getAbsolutePath())));
    }

    @Test
    void validateBackupPathsDestinationPathIsSubPathOfSourcePath() {
        Assertions.assertThrows(DestinationPathIsInSourcePathException.class,
                () -> BackupService.validateBackupPaths(new SrcDestPair(existingDirPath,
                        Path.of(existingFilePath).resolve("SubDir").toString())));
    }

    @Test
    void backupFileToNotExistingDestinationWithAttributesAndContent() {
        Path srcPath = backupExSrc.resolve("exampleFile.txt");
        Path destPath = tempDir.resolve("backupFileToNotExistingDest/a.txt");
        BackupService backupService = new BackupService("");

        Assertions.assertTrue(backupService.backupFile(srcPath, destPath));

        try {
            Assertions.assertTrue(Files.exists(srcPath));
            Assertions.assertEquals(Files.getLastModifiedTime(srcPath), Files.getLastModifiedTime(destPath));
            Assertions.assertEquals(Files.readAllLines(srcPath), Files.readAllLines(destPath));
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void backupUnmodifiedFileToExistingDestination() {
        Path srcPath = tempDir.resolve("backupUnmodifiedFileSrc/a.txt");
        Path destpath = tempDir.resolve("backupUnmodifiedFileDest/a.txt");
        BackupService backupService = new BackupService("");

        try {
            Files.createDirectory(srcPath.getParent());
            Files.createDirectory(destpath.getParent());
            Files.createFile(srcPath);
            Files.createFile(destpath);
            Files.setLastModifiedTime(destpath, Files.getLastModifiedTime(srcPath));

            Assertions.assertFalse(backupService.backupFile(srcPath, destpath));
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void backupModifiedFileToExistingDestination() {
        Path srcPath = backupExSrc.resolve("ExampleFile.txt");
        Path destPath = tempDir.resolve("backupModifiedFileDest/ExampleFile.txt");
        BackupService backupService = new BackupService("");

        try {
            Files.createDirectory(destPath.getParent());
            Files.createFile(destPath);
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        Assertions.assertTrue(backupService.backupFile(srcPath, destPath));
    }

    @Test
    void backupDirWithFilesToNotExistingDestination() {
        Path srcPath = backupExSrc.resolve("dirWithFiles");
        Path destPath = tempDir.resolve("dirWithFilesToNotExistingDest/dirWithFiles");
        BackupService backupService = new BackupService("");

        Assertions.assertTrue(backupService.backupDir(srcPath, destPath));
        assertDirTree(srcPath, destPath);
    }

    @Test
    void backupDirWithSubDirAndFilesToNotExistingDestination() {
        Path srcpath = backupExSrc.resolve("dirWithSubDirsAndFiles");
        Path destPath = tempDir.resolve("dirWithSubDirsAndFilesToNotExistingDestination/dirWithSubDirsAndFiles");
        BackupService backupService = new BackupService("");

        Assertions.assertTrue(backupService.backupDir(srcpath, destPath));
        assertDirTree(srcpath, destPath);
    }

    @Test
    void backupUnchangedDir() {
        Path srcPath = backupExSrc.resolve("dirWithFiles");
        Path destPath = tempDir.resolve("backupUnchangedDir/dirWithFiles");
        BackupService backupService = new BackupService("");

        backupService.backupDir(srcPath, destPath);
        Assertions.assertFalse(backupService.backupDir(srcPath, destPath));
    }

    @Test
    void backupDirToExistingButChangedDir() {
        Path srcPath = backupExSrc.resolve("dirWithFiles");
        Path destPath = tempDir.resolve("backupDirToExistingButChangedDir/dirWithFiles");
        BackupService backupService = new BackupService("");

        backupService.backupDir(srcPath, destPath);
        try {
            Assertions.assertTrue(Files.deleteIfExists(destPath.resolve("1.txt")));
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertTrue(backupService.backupDir(srcPath, destPath));
        assertDirTree(srcPath, destPath);
    }

    @Test
    void runBackupTest() {
        Path destPath = tempDir.resolve("runBackupTest");
        String backupConfigPath = createBackupConfigFile("runBackupTest.csv",
                new SrcDestPair(backupExSrc.resolve("separateBackups/first").toString(), destPath.toString()),
                new SrcDestPair(backupExSrc.resolve("separateBackups/second").toString(), destPath.toString()),
                new SrcDestPair(backupExSrc.resolve("separateBackups/3.txt").toString(), destPath.toString()));

        BackupService backupService = new BackupService(backupConfigPath);
        backupService.runBackup();

        assertDirTree(backupExSrc.resolve("separateBackups"), destPath);
    }

    @Test
    void removeMissingBackupsFromBackupListWhenRunningBackup() {
        Path destPath = tempDir.resolve("removeMissingBackupsFromBackupList");
        SrcDestPair existingFilePair = new SrcDestPair(exampleBackupDataPath, destPath.toString());

        String backupConfigPath = createBackupConfigFile("removeMissingBackups.csv",
                existingFilePair,
                new SrcDestPair(Path.of(existingDirPath).resolve("not-existing.txt").toString(), destPath.toString()),
                existingFilePair);

        BackupService backupService = new BackupService(backupConfigPath);
        backupService.runBackup();
        List<SrcDestPair> expectedBackupList = List.of(existingFilePair, existingFilePair);

        Assertions.assertEquals(destPath.toFile().listFiles().length, 1);
        Assertions.assertTrue(Files.exists(destPath.resolve(Path.of(exampleBackupDataPath).getFileName())));
        Assertions.assertEquals(expectedBackupList, backupService.getAllBackups());
    }

    @Test
    void deleteBackupFilesOfDeletedBackupSourcefiles() throws IOException {
        Path destPath = tempDir.resolve("deleteBackupFilesOfDeletedBackupSourcefiles");
        String fileName = "notExistingFile.txt";
        String backupConfigPath = createBackupConfigFile("deleteBackupFilesOfDeletedSourcefiles.csv",
                new SrcDestPair(Path.of(this.existingDirPath).resolve(fileName).toString(),
                        destPath.toString()));

        Files.createDirectory(destPath);
        Files.createFile(destPath.resolve(fileName));

        Assertions.assertTrue(Files.exists(destPath.resolve(fileName)));
        BackupService backupService = new BackupService(backupConfigPath);
        backupService.runBackup();

        Assertions.assertTrue(Files.notExists(destPath.resolve(fileName)));
    }

    @Test
    void deleteBackupDirectoryOfDeletedSourceDirectory() throws IOException {
        Path destPath = tempDir.resolve("deleteBackupDirOfDeletedSrcDir");
        String dirName = "shouldBeDeleted";
        String backupConfigPath = createBackupConfigFile("deleteBackupDirOfDeletedSrcDir.csv",
                new SrcDestPair(Path.of(this.existingDirPath).resolve(dirName).toString(),
                        destPath.toString()));

        Files.createDirectory(destPath);
        Files.createDirectory(destPath.resolve(dirName));

        Assertions.assertTrue(Files.exists(destPath.resolve(dirName)));
        BackupService backupService = new BackupService(backupConfigPath);
        backupService.runBackup();

        Assertions.assertTrue(Files.notExists(destPath.resolve(dirName)));
    }

    // Helper methods
    private String createBackupConfigFile(String fileName, SrcDestPair... pathPairs) {
        Path filePath = tempDir.resolve(fileName);
        try (CsvWriter csvWriter = new CsvWriter(filePath.toString())) {
            for (SrcDestPair pathPair : pathPairs) {
                csvWriter.writeLine(pathPair.srcPath(), pathPair.destPath());
            }
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        return filePath.toString();
    }

    private void assertDirTree(Path srcPath, Path destPath) {
        Assertions.assertTrue(Files.exists(destPath));
        Stream.of(srcPath.toFile().listFiles()).forEach(subFile -> {
            Path destFilePath = destPath.resolve(subFile.getName());

            if (subFile.isFile()) {
                Assertions.assertTrue(Files.exists(destFilePath));
                try {
                    Assertions.assertEquals(Files.readAllLines(subFile.toPath()), Files.readAllLines(destFilePath));
                } catch (IOException e) {
                    Assertions.fail(e.getMessage());
                }
            } else {
                assertDirTree(subFile.toPath(), destFilePath);
            }
        });
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
        for (int i = 0; i < expectedData.length; i++) {
            Assertions.assertEquals(expectedData[i], result.get(i));
        }
    }
}
