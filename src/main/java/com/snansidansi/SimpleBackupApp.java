package com.snansidansi;

import com.snansidansi.backup.service.BackupService;
import com.snansidansi.gui.SimpleBackupGUI;
import javafx.application.Application;

public class SimpleBackupApp {
    private static final String backupListPath = "data/backupList.csv";

    public static void main(String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equals("gui")))
            Application.launch(SimpleBackupGUI.class);
        else if (args.length == 1 && args[0].equals("run")) {
            BackupService runBackupService = new BackupService(backupListPath);
            runBackupService.runBackup();
        } else
            System.out.println("Possible options: gui, run");
    }
}
