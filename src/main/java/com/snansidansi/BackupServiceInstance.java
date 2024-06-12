package com.snansidansi;

import com.snansidansi.backup.service.BackupService;

public class BackupServiceInstance {
    static final public BackupService backupService = new BackupService("data/backupData.csv");

    private BackupServiceInstance() {
    }
}
