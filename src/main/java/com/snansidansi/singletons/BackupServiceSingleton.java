package com.snansidansi.singletons;

import com.snansidansi.backup.service.BackupService;

public class BackupServiceSingleton {
    static final public BackupService backupService = new BackupService("data/backupData.csv");

    private BackupServiceSingleton() {
    }
}
