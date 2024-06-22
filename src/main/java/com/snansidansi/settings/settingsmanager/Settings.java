package com.snansidansi.settings.settingsmanager;

/**
 * Interface for enums that can be used as settings enums in the {@link SettingsManager} class.
 */
public interface Settings {
    String getID();

    SettingType getType();

    String getStandardValue();
}
