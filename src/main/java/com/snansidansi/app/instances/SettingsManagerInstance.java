package com.snansidansi.app.instances;

import com.snansidansi.app.LocalBackupApp;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.gui.uielements.WindowBar;
import com.snansidansi.gui.util.SceneManager;
import com.snansidansi.settings.BackupSetting;
import com.snansidansi.settings.settingsmanager.SettingsManager;
import com.snansidansi.shortcut.OsIsNotWindowsException;
import com.snansidansi.shortcut.WindowsShortcutManager;
import javafx.scene.Node;
import javafx.stage.Stage;

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

        backupService.setDeleteBackupsWithMissingSource(
                Boolean.parseBoolean(settingsManager.getSetting(BackupSetting.DELETE_BACKUP_FILES_WITH_MISSING_SRC))
        );

        updateUIStyle();
    }

    private static void updateUIStyle() {
        SceneManager.updateStyle();

        for (Stage stage : SceneManager.getActiveStages()) {
            for (Node node : stage.getScene().getRoot().lookupAll(".window-bar")) {
                if (node instanceof WindowBar windowBar) {
                    windowBar.updateImages();
                }
            }
        }
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
            if (!shortcutManager.create()) {
                settingsManager.changeSetting(BackupSetting.ADDED_TO_AUTOSTART, String.valueOf(!addToAutostart));
            }
            return;
        }
        if (!shortcutManager.delete()) {
            settingsManager.changeSetting(BackupSetting.ADDED_TO_AUTOSTART, String.valueOf(!addToAutostart));
        }
    }

    private SettingsManagerInstance() {
    }
}
