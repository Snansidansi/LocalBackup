package com.snansidansi.app.singletons;

import com.snansidansi.app.instances.BackupServiceInstance;

public class RunBackupThreadSingleton extends Thread {
    private static final RunBackupThreadSingleton thread = new RunBackupThreadSingleton();

    private RunBackupThreadSingleton() {
    }

    public void run() {
        BackupServiceInstance.backupService.runBackup();
    }

    public static RunBackupThreadSingleton getThread() {
        return thread;
    }
}
