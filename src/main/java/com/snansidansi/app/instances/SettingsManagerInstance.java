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
        BackupService backupService = BackupServiceInstance.backupService;

        backupService.getErrorLog().setMaxNumberOfLogs(
                Integer.parseInt(settingsManager.getSetting(BackupSetting.MAX_ERROR_LOGS)));

        backupService.getBackupLog().setMaxNumberOfLogs(
                Integer.parseInt(settingsManager.getSetting(BackupSetting.MAX_BACKUP_LOGS)));
    }

    private SettingsManagerInstance() {
    }
}
