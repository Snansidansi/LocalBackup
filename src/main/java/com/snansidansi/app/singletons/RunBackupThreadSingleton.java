package com.snansidansi.app.singletons;

import com.snansidansi.app.instances.BackupServiceInstance;

public class RunBackupThreadSingleton {
    private static runBackupThread thread = null;

    private RunBackupThreadSingleton() {
    }

    private static class runBackupThread extends Thread {
        public void run() {
            BackupServiceInstance.backupService.runBackup();
        }
    }

    public static void start() {
        thread = new runBackupThread();
        thread.start();
    }

    public static boolean isAlive() {
        if (thread == null) {
            return false;
        }
        return thread.isAlive();
    }
}
