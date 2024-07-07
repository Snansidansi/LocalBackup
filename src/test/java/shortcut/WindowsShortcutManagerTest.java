package shortcut;

import com.snansidansi.shortcut.OsIsNotWindowsException;
import com.snansidansi.shortcut.WindowsShortcutManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

public class WindowsShortcutManagerTest {
    private static final Path existingFile = Path.of("src/test/resources/shortcut/existingFile.txt");

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path tempDir;

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @Tag("slow")
    void createShortcutWithoutParameters() throws OsIsNotWindowsException {
        String shortcutName = "shortcutWithoutParameters";

        WindowsShortcutManager shortcutManager = new WindowsShortcutManager(
                existingFile, tempDir, shortcutName);
        shortcutManager.create();

        Assertions.assertTrue(Files.exists(shortcutManager.getFullDestinationPath()));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @Tag("slow")
    void createShortcutWithParameters() throws OsIsNotWindowsException {
        String shortcutName = "shortcutWithParameters";

        WindowsShortcutManager shortcutManager = new WindowsShortcutManager(
                existingFile, tempDir, shortcutName);
        shortcutManager.setLaunchParameters("test", "parameters");
        shortcutManager.create();

        Assertions.assertTrue(Files.exists(shortcutManager.getFullDestinationPath()));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @Tag("slow")
    void createShortcutAndDeleteIt() throws OsIsNotWindowsException {
        String shortcutName = "createAndDeleteShortcut";

        WindowsShortcutManager shortcutManager = new WindowsShortcutManager(
                existingFile, tempDir, shortcutName);
        shortcutManager.create();
        Assertions.assertTrue(Files.exists(shortcutManager.getFullDestinationPath()));

        shortcutManager.delete();
        Assertions.assertTrue(Files.notExists(shortcutManager.getFullDestinationPath()));
    }
}
