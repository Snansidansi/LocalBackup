package com.snansidansi.app.instances;

import com.snansidansi.backup.service.BackupService;

public class BackupServiceInstance {
    public static final BackupService backupService = new BackupService("data/backupData.csv", true);

    static {
        backupService.setLoggerEnabled(true);
    }

    private BackupServiceInstance() {
    }
}
