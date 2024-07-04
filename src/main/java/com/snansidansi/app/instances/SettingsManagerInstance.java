package com.snansidansi.app.instances;

import com.snansidansi.backup.service.BackupService;
import com.snansidansi.settings.BackupSetting;
import com.snansidansi.settings.settingsmanager.SettingsManager;

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
    }

    private SettingsManagerInstance() {
    }
}
