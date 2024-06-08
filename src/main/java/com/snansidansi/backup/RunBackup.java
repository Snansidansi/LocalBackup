package com.snansidansi.backup;

import com.snansidansi.backup.service.BackupService;

public class RunBackup {
    public static void main(String[] args) {
        String backupConfigPath = "data/backupList.csv";
        BackupService backupService = new BackupService(backupConfigPath);
        backupService.runBackup();
    }
}
