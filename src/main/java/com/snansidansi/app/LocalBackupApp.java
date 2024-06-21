package com.snansidansi.app;

import com.snansidansi.app.singletons.BackupServiceSingleton;
import com.snansidansi.gui.LocalBackupGUI;
import com.snansidansi.gui.util.SettingsManager;
import javafx.application.Application;

public class LocalBackupApp {
    public static void main(String[] args) {
        SettingsManager.load();

        if (args.length == 0 || (args.length == 1 && args[0].equals("gui")))
            Application.launch(LocalBackupGUI.class);
        else if (args.length == 1 && args[0].equals("run")) {
            BackupServiceSingleton.backupService.runBackup();
        } else
            System.out.println("Possible options: gui, run");
    }
}
