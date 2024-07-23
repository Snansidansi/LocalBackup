package com.snansidansi.settings;

import com.snansidansi.settings.settingsmanager.SettingType;
import com.snansidansi.settings.settingsmanager.Settings;

import java.nio.file.Path;

public enum BackupSetting implements Settings {
    MAX_BACKUP_LOGS("max backup logs", "14", SettingType.INTEGER),
    MAX_ERROR_LOGS("max error logs", "14", SettingType.INTEGER),
    NUMBER_OF_BACKUP_RETRIES("number of backup retries", "1", SettingType.INTEGER),
    DELAY_BETWEEN_BACKUP_RETRIES("delay between backup retries", "10", SettingType.INTEGER),
    AUTOSTART_DIR_PATH("autostart dir path", Path.of(System.getenv("APPDATA"))
            .resolve("Microsoft/Windows/Start Menu/Programs/Startup").toString(), SettingType.STRING),
    ADDED_TO_AUTOSTART("added to autostart", "false", SettingType.BOOLEAN),
    DELETE_BACKUP_FILES_WITH_MISSING_SRC("delete missing files", "true", SettingType.BOOLEAN),
    ENABLE_TAGS("enable tags", "false", SettingType.BOOLEAN),
    SHOW_TAG_IMAGE("show tag image", "true", SettingType.BOOLEAN),
    SHOW_TAG_NAME("show tag name", "true", SettingType.BOOLEAN),
    DARK_MODE("dark mode", "light mode", SettingType.STRING, "light mode", "dark mode");

    private final String id;
    private final String standardValue;
    private final SettingType settingType;
    private String[] options = null;

    BackupSetting(String id,
                  String standardValue,
                  SettingType settingType) {

        this.id = id;
        this.standardValue = standardValue;
        this.settingType = settingType;
    }

    BackupSetting(String id,
                  String standardValue,
                  SettingType settingType,
                  String... options) {

        this(id, standardValue, settingType);
        this.options = options;
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

    public String[] getOptions() {
        return this.options;
    }
}
