package com.snansidansi.tag;

import com.snansidansi.csv.CsvReader;
import com.snansidansi.csv.CsvWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagManager {
    private final Path tagsFilePath;
    private Map<String, List<Integer>> tagsMap = new HashMap<>();
    private final Path tagsBackupPath;

    public TagManager(Path tagsFilePath) {
        this.tagsFilePath = tagsFilePath;
        this.tagsBackupPath = this.tagsFilePath.getParent().resolve("tag_backup.csv");
    }

    public boolean getTagsFromFile() throws IOException {
        if (Files.notExists(this.tagsFilePath)) {
            return true;
        }

        restoreBackupIfExistent();
        boolean success = true;

        try (CsvReader csvReader = new CsvReader(this.tagsFilePath.toString())) {
            List<String[]> fileContent = csvReader.readAllLines();
            if (this.tagsMap.isEmpty()) {
                this.tagsMap = new HashMap<>(fileContent.size());
            }

            for (String[] line : fileContent) {
                String tagName = line[0];
                List<Integer> indicesWithTag = new ArrayList<>(line.length - 1);

                for (int i = 1; i < line.length; i++) {
                    try {
                        indicesWithTag.add(Integer.parseInt(line[i]));
                    } catch (NumberFormatException unused) {
                        success = false;
                    }
                }

                this.tagsMap.put(tagName, indicesWithTag);
            }
        } catch (FileNotFoundException unused) {
        }

        return success;
    }

    public boolean addTag(String tagName) {
        if (this.tagsMap.containsKey(tagName)) {
            return false;
        }
        if (!createTagFileBackup()) {
            return false;
        }

        this.tagsMap.put(tagName, new ArrayList<>());
        try (CsvWriter csvWriter = new CsvWriter(this.tagsFilePath.toString(), true)) {
            csvWriter.writeLine(tagName);
        } catch (IOException unused) {
            restoreBackupIfExistent();
            this.tagsMap.remove(tagName);
            return false;
        }
        return true;
    }

    private boolean createTagFileBackup() {
        if (Files.notExists(this.tagsBackupPath)) {
            return true;
        }
        try {
            Files.copy(this.tagsFilePath, tagsBackupPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException unused) {
            return false;
        }
        return true;
    }

    private void restoreBackupIfExistent() {
        if (Files.notExists(this.tagsBackupPath)) {
            return;
        }

        try {
            Files.move(tagsBackupPath, this.tagsFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException unused) {
        }
    }

    public boolean deleteTag(String tagName) {
        if (!this.tagsMap.containsKey(tagName)) {
            return false;
        }

        List<Integer> backupTagIndices = this.tagsMap.get(tagName);
        this.tagsMap.remove(tagName);
        if (!updateTagsFile()) {
            this.tagsMap.put(tagName, backupTagIndices);
            return false;
        }
        return true;
    }

    public boolean changeTagName(String oldTagName, String newTagName) {
        if (!this.tagsMap.containsKey(oldTagName)) {
            return false;
        }

        this.tagsMap.put(newTagName, this.tagsMap.remove(oldTagName));
        if (!updateTagsFile()) {
            this.tagsMap.put(oldTagName, this.tagsMap.remove(newTagName));
            return false;
        }
        return true;
    }

    public boolean changeTagContent(String tagName, List<Integer> newContent) {
        if (!this.tagsMap.containsKey(tagName)) {
            return false;
        }

        List<Integer> backupContent = this.tagsMap.get(tagName);
        this.tagsMap.put(tagName, newContent);
        if (!updateTagsFile()) {
            this.tagsMap.put(tagName, backupContent);
            return false;
        }
        return true;
    }

    private boolean updateTagsFile() {
        if (!createTagFileBackup()) {
            return false;
        }

        try (CsvWriter csvWriter = new CsvWriter(this.tagsFilePath.toString())) {
            for (Map.Entry<String, List<Integer>> entry : this.tagsMap.entrySet()) {
                String[] inputLine = new String[entry.getValue().size() + 1];
                inputLine[0] = entry.getKey();

                for (int i = 0; i < entry.getValue().size(); i++) {
                    inputLine[i + 1] = String.valueOf(entry.getValue().get(i));
                }

                csvWriter.writeLine(inputLine);
            }
        } catch (IOException unused) {
            restoreBackupIfExistent();
            return false;
        }
        return true;
    }

    public List<Integer> getTagContent(String tagName) {
        if (!this.tagsMap.containsKey(tagName)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.tagsMap.get(tagName));
    }

    public String[] getTags() {
        String[] tags = new String[this.tagsMap.size()];
        int i = 0;
        for (Map.Entry<String, List<Integer>> entry : this.tagsMap.entrySet()) {
            tags[i] = entry.getKey();
            i++;
        }
        return tags;
    }
}
