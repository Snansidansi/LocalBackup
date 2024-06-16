package logger;

import com.snansidansi.log.Logger;
import com.snansidansi.log.NoSuchRootException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LoggerTest {
    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path tempDir;

    @Test
    void setupLogCorrectly() {
        String destPath = tempDir.resolve("successfulSetup").toString();
        Logger logger = new Logger(destPath, false);

        Assertions.assertDoesNotThrow(logger::setup);
        Assertions.assertTrue(Files.exists(Path.of(logger.getFilePath())));
    }

    @Test
    void successfulLogMessage() {
        String destPath = tempDir.resolve("successfulLog").toString();
        Logger logger = new Logger(destPath, false);

        try {
            logger.setup();
        } catch (NoSuchRootException e) {
            Assertions.fail(e.getMessage());
        }

        String logMessage = "This is a log entry.";
        logger.log(logMessage);

        try {
            List<String> result = Files.readAllLines(Path.of(logger.getFilePath()));
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(logMessage, result.getFirst());
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }
}
