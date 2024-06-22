package com.snansidansi.app.instances;

import com.snansidansi.settings.BackupSetting;
import com.snansidansi.settings.settingsmanager.SettingsManager;

public class SettingsManagerInstance {
    public static final SettingsManager<BackupSetting> settingsManager =
            new SettingsManager<>("data/settings.txt", BackupSetting.class);

    static {
        settingsManager.load();
    }

    private SettingsManagerInstance() {
    }
}
