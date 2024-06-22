package com.snansidansi.settings.settingsmanager;

public interface Settings {
    String getID();

    SettingType getType();

    String getStandardValue();
}
