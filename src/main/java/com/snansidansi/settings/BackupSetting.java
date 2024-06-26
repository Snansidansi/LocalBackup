package com.snansidansi.settings;

import com.snansidansi.settings.settingsmanager.SettingType;
import com.snansidansi.settings.settingsmanager.Settings;

public enum BackupSetting implements Settings {
    MAX_BACKUP_LOGS("max backup logs", "14", SettingType.INTEGER),
    MAX_ERROR_LOGS("max error logs", "14", SettingType.INTEGER);

    private final String id;
    private final String standardValue;
    private final SettingType settingType;

    BackupSetting(String id,
                  String standardValue,
                  SettingType settingType) {

        this.id = id;
        this.standardValue = standardValue;
        this.settingType = settingType;
    }

    public String getID() {
        return this.id;
    }

    public String getStandardValue() {
        return this.standardValue;
    }

    public SettingType getType() {
        return this.settingType;
    }
}
