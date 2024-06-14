package com.snansidansi;

import com.snansidansi.gui.LocalBackupGUI;
import com.snansidansi.singletons.BackupServiceSingleton;
import javafx.application.Application;

public class LocalBackupApp {
    public static void main(String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equals("gui")))
            Application.launch(LocalBackupGUI.class);
        else if (args.length == 1 && args[0].equals("run")) {
            BackupServiceSingleton.backupService.runBackup();
        } else
            System.out.println("Possible options: gui, run");
    }
}
