package com.snansidansi.gui.util;

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

        String setting = splitLine[0].strip().toLowerCase();
        if (!defaultSettingsMap.containsKey(setting)) return;

        String value = splitLine[1].strip();
        settingsMap.put(setting, value);
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
            if (defaultSettingsMap.containsKey(setting.getKey()))
                settingsMap.put(setting.getKey(), setting.getValue());
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
}
