package com.snansidansi.settings.settingsmanager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code SettingsManager} class can load, change and save settings to a file. The class uses an enum to determine
 * all available settings and default values.
 *
 * @param <T> An enum that implements the {@link Settings} interface. This enum should represent the different settings.
 */
public class SettingsManager<T extends Enum<T> & Settings> {
    private final Path settingsFilePath;
    private final Class<T> enumClass;
    private final Map<T, String> settingsMap = new HashMap<>();
    private final Map<T, String> tempSettingsMap = new HashMap<>();

    /**
     * Creates a new {@code SettingsManager} instance.
     *
     * @param filePath    Path to an existing settings file (or where the settings file should be created) as string.
     * @param settingEnum The enum {@code class} that belongs to the generic parameter of the class.
     * @throws InvalidPathException Throws if the given {@code filePath} is an invalid path.
     */
    public SettingsManager(String filePath, Class<T> settingEnum) throws InvalidPathException {
        this.settingsFilePath = Path.of(filePath);
        this.enumClass = settingEnum;
    }

    /**
     * Loads the settings from the settings file. This method can also be used if the settings file has invalid values,
     * not the right amount of settings or does not exist. If something like this is the case, default values will be
     * used as the settings for the invalid or missing setting.
     *
     * @return True if the load process was successful. Also returns true if the settings file was not found and
     * default values were loaded. Returns false if an IOException occurred when reading the file and default values
     * were used.
     */
    public boolean load() {
        if (Files.notExists(this.settingsFilePath)) {
            return true;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.settingsFilePath.toFile()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                addToSettingsMap(line);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void addToSettingsMap(String line) {
        String[] splitLine = line.split(":", 2);
        if (splitLine.length != 2) return;

        String settingID = splitLine[0].strip().toLowerCase();
        T defaultSetting = getEnumFromID(settingID);
        if (defaultSetting == null) return;

        String value = splitLine[1].strip();
        if (isValidSettingsValue(defaultSetting, value))
            this.settingsMap.put(defaultSetting, value);
    }

    /**
     * Restores the default settings to the loaded settings and also to the settings file.
     */
    public void restoreDefaults() {
        this.settingsMap.clear();
        saveToFile();
    }

    /**
     * @return Boolean value if writing the settings to the settings file happened without an IOException.
     */
    private boolean saveToFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(this.settingsFilePath.toFile());
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {

            for (T defaultSetting : this.enumClass.getEnumConstants()) {
                String key = defaultSetting.getID();

                if (this.settingsMap.containsKey(defaultSetting)) {
                    bufferedWriter.write(key + ":" + this.settingsMap.get(defaultSetting));
                    bufferedWriter.newLine();
                    continue;
                }
                bufferedWriter.write(key + ":" + defaultSetting.getStandardValue());
                bufferedWriter.newLine();
            }
        } catch (IOException unused) {
            return false;
        }
        return true;
    }

    /**
     * Applies any changes made (by using the {@link #changeSetting} method) to the settings since the last use of
     * {@link #load} or {@code applyChanges}. Only applies changes whose type is valid for the setting it belongs to.
     * Invalid changes will be discarded. Also saves the changes in the settings file.
     *
     * @return A boolean value if saving the changes was successful. If it returns false, the loaded settings will
     * not be changed. The settings file should also stay the same but can contain some of the changes. The method only
     * returns false if an {@code IOException} occurs during the settings file update.
     */
    public boolean applyChanges() {
        Map<T, String> backupSettingsMap = new HashMap<>(this.settingsMap);

        for (var setting : this.tempSettingsMap.entrySet()) {
            if (isValidSettingsValue(setting.getKey(), setting.getValue()))
                this.settingsMap.put(setting.getKey(), setting.getValue());
        }

        if (saveToFile()) return true;
        this.settingsMap.clear();
        this.settingsMap.putAll(backupSettingsMap);
        saveToFile();
        return false;
    }

    /**
     * Discards all changes made by using the {@link #changeSetting} method since the last use of {@link #load} or
     * {@link #applyChanges}.
     */
    public void discardChanges() {
        this.tempSettingsMap.clear();
    }

    /**
     * Changes a setting temporary. To confirm/discard all temporary changes us {@link #applyChanges}/{@link #load}.
     * @param setting The setting that should be changed as enum value.
     * @param value The new settings value as String.
     */
    public void changeSetting(T setting, String value) {
        this.tempSettingsMap.put(setting, value);
    }

    /**
     * Gets the value of a loaded setting.
     *
     * @param setting The setting whose value should be returned as enum value.
     * @return The value of the requested setting as string.
     */
    public String getSetting(T setting) {
        String settingValue;
        if ((settingValue = this.settingsMap.get(setting)) == null)
            settingValue = setting.getStandardValue();

        return settingValue;
    }

    /**
     * @return Boolean value if it is a valid settings value. Returns also false if the type {@link SettingType} of the
     * setting is not implemented in this method.
     */
    private boolean isValidSettingsValue(T backupSetting, String value) {
        if (backupSetting == null) return false;

        switch (backupSetting.getType()) {
            case INTEGER:
                try {
                    Integer.parseInt(value);
                    return true;
                } catch (NumberFormatException unused) {
                    return false;
                }
            case BOOLEAN:
                return value.equals("true") || value.equals("false");
            case STRING:
                return true;
        }
        return false;
    }

    /**
     * @return Returns the enum value to a settings id. Returns null if no enum value is associated with this id.
     */
    private T getEnumFromID(String id) {
        for (T setting : this.enumClass.getEnumConstants()) {
            if (id.equals(setting.getID())) return setting;
        }
        return null;
    }
}
