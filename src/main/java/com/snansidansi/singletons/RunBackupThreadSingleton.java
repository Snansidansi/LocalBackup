package com.snansidansi.singletons;

public class RunBackupThreadSingleton extends Thread {
    static private final RunBackupThreadSingleton thread = new RunBackupThreadSingleton();

    private RunBackupThreadSingleton() {
    }

    public void run() {
        BackupServiceSingleton.backupService.runBackup();
    }

    static public RunBackupThreadSingleton getThread() {
        return thread;
    }
}
