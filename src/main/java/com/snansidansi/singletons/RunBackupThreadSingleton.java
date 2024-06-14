package com.snansidansi.singletons;

public class RunBackupThreadSingleton extends Thread {
    private static final RunBackupThreadSingleton thread = new RunBackupThreadSingleton();

    private RunBackupThreadSingleton() {
    }

    public void run() {
        BackupServiceSingleton.backupService.runBackup();
    }

    public static RunBackupThreadSingleton getThread() {
        return thread;
    }
}
