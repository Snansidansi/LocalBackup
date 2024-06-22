package com.snansidansi.settings;

public enum BackupSetting implements Settings {
    NONE("none", "Nothing", "null", SettingType.BOOLEAN, false);

    private final String id;
    private final String displayText;
    private final String standardValue;
    private final SettingType settingType;
    private final Boolean isUserChangeable;

    BackupSetting(String id,
                  String displayText,
                  String standardValue,
                  SettingType settingType,
                  Boolean isUserChangeable) {

        this.id = id;
        this.displayText = displayText;
        this.standardValue = standardValue;
        this.settingType = settingType;
        this.isUserChangeable = isUserChangeable;
    }

    public String getID() {
        return this.id;
    }

    public String getDisplayText() {
        return this.displayText;
    }

    public String getStandardValue() {
        return this.standardValue;
    }

    public SettingType getType() {
        return this.settingType;
    }

    public Boolean getIsUserChangeable() {
        return this.isUserChangeable;
    }
}
