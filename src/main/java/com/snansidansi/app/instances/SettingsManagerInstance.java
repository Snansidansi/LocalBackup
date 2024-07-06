package com.snansidansi.app.instances;

import com.snansidansi.app.LocalBackupApp;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.settings.BackupSetting;
import com.snansidansi.settings.settingsmanager.SettingsManager;
import com.snansidansi.shortcut.OsIsNotWindowsException;
import com.snansidansi.shortcut.WindowsShortcutManager;

import java.nio.file.Path;

public class SettingsManagerInstance {
    public static final SettingsManager<BackupSetting> settingsManager =
            new SettingsManager<>("data/settings.txt", BackupSetting.class);

    static {
        settingsManager.load();
    }

    public static void reloadSettings() {
        final BackupService backupService = BackupServiceInstance.backupService;

        backupService.getErrorLog().setMaxNumberOfLogs(
                Integer.parseInt(settingsManager.getSetting(BackupSetting.MAX_ERROR_LOGS)));

        backupService.getBackupLog().setMaxNumberOfLogs(
                Integer.parseInt(settingsManager.getSetting(BackupSetting.MAX_BACKUP_LOGS)));

        backupService.setMaxRetries(
                Integer.parseInt(settingsManager.getSetting(BackupSetting.NUMBER_OF_BACKUP_RETRIES)));

        backupService.setRetryTime(
                Integer.parseInt(settingsManager.getSetting(BackupSetting.DELAY_BETWEEN_BACKUP_RETRIES)));

        reloadAutostartSetting();
    }

    private static void reloadAutostartSetting() {
        if (!LocalBackupApp.runsFromExeFile) {
            return;
        }

        WindowsShortcutManager shortcutManager;
        try {
            shortcutManager = new WindowsShortcutManager(
                    Path.of("C:/Dev/Java/LocalBackup/build/launch4j/LocalBackup.exe"),
                    Path.of(settingsManager.getSetting(BackupSetting.AUTOSTART_DIR_PATH)),
                    "runBackup-LocalBackup");
        } catch (OsIsNotWindowsException unused) {
            return;
        }

        boolean addToAutostart = Boolean.parseBoolean(settingsManager.getSetting(BackupSetting.ADDED_TO_AUTOSTART));
        if (addToAutostart) {
            shortcutManager.setLaunchParameters("run");
            shortcutManager.create();
            return;
        }
        shortcutManager.delete();
    }

    private SettingsManagerInstance() {
    }
}
