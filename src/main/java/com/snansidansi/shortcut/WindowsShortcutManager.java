package com.snansidansi.shortcut;

import com.snansidansi.app.LocalBackupApp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WindowsShortcutManager {
    private final Path targetPath;
    private final Path destinationPath;
    private String lauchParameters = "";

    public WindowsShortcutManager(Path targetpath, Path destinationPath, String shortcutName) throws OsIsNotWindowsException {
        if (!LocalBackupApp.osIsWindows) {
            throw new OsIsNotWindowsException("The operation system has to be windows to use this class!");
        }

        this.targetPath = targetpath.toAbsolutePath();
        this.destinationPath = destinationPath.resolve(shortcutName + ".lnk").toAbsolutePath();
    }

    public void setLaunchParameters(String... parameters) {
        this.lauchParameters = String.join(" ", parameters);
    }

    public boolean create() {
        if (exist()) {
            return true;
        }

        try {
            ProcessBuilder second = new ProcessBuilder(generateShortCutCreationCommand());
            Process process = second.start();
            process.waitFor();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    private String[] generateShortCutCreationCommand() {
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

    public boolean exist() {
        return Files.exists(destinationPath);
    }
}
