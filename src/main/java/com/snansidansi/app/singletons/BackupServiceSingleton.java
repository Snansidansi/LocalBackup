package com.snansidansi.app.singletons;

import com.snansidansi.backup.service.BackupService;

public class BackupServiceSingleton {
    public static final BackupService backupService = new BackupService("data/backupData.csv", true);

    static {
        backupService.setLoggerEnabled(true);
    }

    private BackupServiceSingleton() {
    }
}
