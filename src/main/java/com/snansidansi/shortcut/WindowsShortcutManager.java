package com.snansidansi.shortcut;

import com.snansidansi.app.LocalBackupApp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class to create and delete windows shortcuts ({@code .lnk} files).
 */
public class WindowsShortcutManager {
    private final Path targetPath;
    private final Path destinationPath;
    private String lauchParameters = "";

    /**
     * Creates a new {@code WindowsShortcutManager} instance.
     *
     * @param targetPath      The path to the file that the shortcut should point to.
     * @param destinationPath The path where the shortcut file should be created (without the file name).
     * @param shortcutName    The name of the shortcut file.
     * @throws OsIsNotWindowsException Gets thrown if the os is not windows.
     */
    public WindowsShortcutManager(Path targetPath, Path destinationPath, String shortcutName) throws OsIsNotWindowsException {
        if (!LocalBackupApp.osIsWindows) {
            throw new OsIsNotWindowsException("The operation system has to be windows to use this class!");
        }

        this.targetPath = targetPath.toAbsolutePath();
        this.destinationPath = destinationPath.resolve(shortcutName + ".lnk").toAbsolutePath();
    }

    /**
     * Sets lauch parameters for the file that the shortcut points to.
     * @param parameters Lauch parameters as separate strings.
     */
    public void setLaunchParameters(String... parameters) {
        this.lauchParameters = String.join(" ", parameters);
    }

    /**
     * Creates the shortcut file.
     * @return A boolean value if the creation was successful.
     */
    public boolean create() {
        if (exist()) {
            return true;
        }

        try {
            ProcessBuilder second = new ProcessBuilder(generateShortcutCreationCommand());
            Process process = second.start();
            process.waitFor();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    private String[] generateShortcutCreationCommand() {
        String[] commandParts = new String[3];
        commandParts[0] = "powershell";
        commandParts[1] = "-command";
        commandParts[2] = " \"" +
                "$ws = New-Object -ComObject WScript.Shell; " +
                "$sLinkFile = '" + this.destinationPath + "'; " +
                "$oLink = $ws.CreateShortcut($sLinkFile); " +
                "$oLink.Targetpath = '" + this.targetPath + "'; " +
                "$oLink.Arguments = '" + this.lauchParameters + "';" +
                "$oLink.Save()\"";

        return commandParts;
    }

    /**
     * Deletes the shortcut file.
     * @return A boolean value if the deletion was successful.
     */
    public boolean delete() {
        if (!exist()) {
            return true;
        }

        try {
            return Files.deleteIfExists(destinationPath);
        } catch (IOException unused) {
            return false;
        }
    }

    /**
     * Checks if the shortcut file already exists.
     * @return Boolean value if the shortcut file exists.
     */
    public boolean exist() {
        return Files.exists(destinationPath);
    }

    /**
     * Get the absolute path of the shortcut file.
     * @return Path to the shortcut file as {@code Path}.
     */
    public Path getFullDestinationPath() {
        return this.destinationPath;
    }
}
