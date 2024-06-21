package com.snansidansi.settings;

import javafx.util.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    private static final Path settingsFilePath = Path.of("data/settings.txt");
    private static final Map<String, String> defaultSettingsMap = new HashMap<>();
    private static final Map<String, String> settingsMap = new HashMap<>();
    private static final Map<String, String> tempSettingsMap = new HashMap<>();

    static {
        // Add new settings here.
    }

    private SettingsManager() {
    }

    /**
     * Loads the settings from the settings file
     *
     * @return True if the load process was successful. Also returns true if the settings file was not found and
     * default values were loaded. Returns false if an IOException occurred when reading the file and default values
     * were used.
     */
    public static boolean load() {
        if (Files.notExists(settingsFilePath)) {
            saveToFile(); // Writes default settings to file
            return true;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(settingsFilePath.toFile()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                addToSettingsMap(line);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static void addToSettingsMap(String line) {
        String[] splitLine = line.split(":", 2);
        if (splitLine.length != 2) return;

        String settingID = splitLine[0].strip().toLowerCase();
        if (!defaultSettingsMap.containsKey(settingID)) return;

        String value = splitLine[1].strip();

        if (isValidSettingsValue(Setting.getEnumFromID(settingID), value))
            settingsMap.put(settingID, value);
    }

    public static void restoreDefaults() {
        settingsMap.clear();
        saveToFile();
    }

    /**
     * @return Boolean value if writing the settings to the settings file happened without an IOException.
     */
    private static boolean saveToFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(settingsFilePath.toFile());
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {

            for (var defaultSetting : defaultSettingsMap.entrySet()) {
                String key = defaultSetting.getKey();

                if (settingsMap.containsKey(key)) {
                    bufferedWriter.write(key + ":" + settingsMap.get(key));
                    bufferedWriter.newLine();
                    continue;
                }
                bufferedWriter.write(key + ":" + defaultSetting.getValue());
                bufferedWriter.newLine();
            }
        } catch (IOException unused) {
            return false;
        }
        return true;
    }

    public static boolean applyChanges() {
        Map<String, String> backupSettingsMap = new HashMap<>(settingsMap);

        for (var setting : tempSettingsMap.entrySet()) {
            if (defaultSettingsMap.containsKey(setting.getKey())
                    && isValidSettingsValue(Setting.getEnumFromID(setting.getKey()), setting.getValue())) {

                settingsMap.put(setting.getKey(), setting.getValue());
            }
        }

        if (saveToFile()) return true;
        settingsMap.clear();
        settingsMap.putAll(backupSettingsMap);
        saveToFile();
        return false;
    }

    public static void discardChanges() {
        tempSettingsMap.clear();
    }

    public static void changeSetting(String settingID, String value) {
        tempSettingsMap.put(settingID, value);
    }

    public static Pair<String, ?> getSetting(Setting setting) {
        String settingValue;
        if ((settingValue = settingsMap.get(setting.getID())) == null)
            settingValue = defaultSettingsMap.get(setting.getID());

        return switch (setting.getType()) {
            case INTEGER -> new Pair<>(setting.getID(), Integer.parseInt(settingValue));
            case BOOLEAN -> new Pair<>(setting.getID(), Boolean.parseBoolean(settingValue));
            case STRING -> new Pair<>(setting.getID(), settingValue);
        };
    }

    private static boolean isValidSettingsValue(Setting setting, String value) {
        if (setting == null) return false;

        switch (setting.getType()) {
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

    private static void newDefaultSetting(Setting setting, String defaultValue) {
        defaultSettingsMap.put(setting.toString(), defaultValue);
    }
}
