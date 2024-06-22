package com.snansidansi.settings.settingsmanager;

import javafx.util.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager<T extends Enum<T> & Settings> {
    private final Path settingsFilePath;
    private final Class<T> enumClass;
    private final Map<T, String> settingsMap = new HashMap<>();
    private final Map<T, String> tempSettingsMap = new HashMap<>();

    public SettingsManager(String filePath, Class<T> settingEnum) throws InvalidPathException {
        this.settingsFilePath = Path.of(filePath);
        this.enumClass = settingEnum;
    }

    /**
     * Loads the settings from the settings file
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

    public void discardChanges() {
        this.tempSettingsMap.clear();
    }

    public void changeSetting(T setting, String value) {
        this.tempSettingsMap.put(setting, value);
    }

    public Pair<String, ?> getSetting(T setting) {
        String settingValue;
        if ((settingValue = this.settingsMap.get(setting)) == null)
            settingValue = setting.getStandardValue();

        return switch (setting.getType()) {
            case INTEGER -> new Pair<>(setting.getID(), Integer.parseInt(settingValue));
            case BOOLEAN -> new Pair<>(setting.getID(), Boolean.parseBoolean(settingValue));
            case STRING -> new Pair<>(setting.getID(), settingValue);
        };
    }

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

    private T getEnumFromID(String id) {
        for (T setting : this.enumClass.getEnumConstants()) {
            if (id.equals(setting.getID())) return setting;
        }
        return null;
    }
}
