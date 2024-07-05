package com.snansidansi.app;

import com.snansidansi.app.instances.BackupServiceInstance;
import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.gui.LocalBackupGUI;
import javafx.application.Application;

import java.io.File;
import java.nio.file.Path;

public class LocalBackupApp {
    public static final Path applicationPath = new File(LocalBackupApp.class.getProtectionDomain().getCodeSource()
            .getLocation().getPath()).toPath();
    public static final boolean runsFromExeFile = applicationPath.toString().contains(".exe");

    public static void main(String[] args) {
        SettingsManagerInstance.reloadSettings();

        if (args.length == 0 || (args.length == 1 && args[0].equals("gui"))) {
            Application.launch(LocalBackupGUI.class);
        } else if (args.length == 1 && args[0].equals("run")) {
            BackupServiceInstance.backupService.runBackup();
        } else {
            System.out.println("Possible options: gui, run");
        }
    }
}
