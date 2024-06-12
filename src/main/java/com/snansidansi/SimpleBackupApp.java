package com.snansidansi;

import com.snansidansi.gui.SimpleBackupGUI;
import javafx.application.Application;

public class SimpleBackupApp {
    public static void main(String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equals("gui")))
            Application.launch(SimpleBackupGUI.class);
        else if (args.length == 1 && args[0].equals("run")) {
            BackupServiceInstance.backupService.runBackup();
        } else
            System.out.println("Possible options: gui, run");
    }
}
