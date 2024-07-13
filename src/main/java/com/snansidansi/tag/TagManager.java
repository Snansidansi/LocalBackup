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

/**
 * A class to manage tags for backups. To save a change like adding or deleting a tag, it is important to use the method
 * {@link #saveChangesToFile()}. Otherwise, the changes will not be saved to the tag file.
 */
public class TagManager {
    private final Path tagsFilePath;
    private Map<String, List<Integer>> tagsMap = new HashMap<>();
    private final Path tagFileModPath;

    /**
     * Creates a new {@code TagManager} instance.
     *
     * @param tagsFilePath Filepath to the tag data ({@code .csv}) as path. The file does not have to exist already.
     * @throws IOException Throws an IOException if the directories to the {@code tagsFilePath} or the tag file could
     * not be created if they are missing.
     */
    public TagManager(Path tagsFilePath) throws IOException {
        this.tagsFilePath = tagsFilePath;
        this.tagFileModPath = this.tagsFilePath.getParent().resolve("mod_" + this.tagsFilePath.getFileName());

        if (Files.notExists(this.tagsFilePath.getParent())) {
            Files.createDirectories(this.tagsFilePath.getParent());
        }
        if (Files.notExists(this.tagsFilePath)) {
            Files.createFile(this.tagsFilePath);
        }
    }

    /**
     * Reads the tags and tag content form the tag file. Invalid content gets ignored/skipped.
     *
     * @return Boolean value if evey content for a tag was a valid {@code Integer} value.
     * @throws IOException Gets thrown if an {@code IOException} occurs during the file reading process.
     */
    public boolean getTagsFromFile() throws IOException {
        if (Files.notExists(this.tagsFilePath)) {
            return true;
        }

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

    /**
     * Adds a tag to the {@code TagManager}.
     * @param tagName Name of the tag as string.
     */
    public void addTag(String tagName) {
        if (this.tagsMap.containsKey(tagName)) {
            return;
        }
        this.tagsMap.put(tagName, new ArrayList<>());
    }

    /**
     * Deletes a tag from the {@code TagManager}.
     * @param tagName The name of the tag that should be deleted as string.
     */
    public void deleteTag(String tagName) {
        this.tagsMap.remove(tagName);
    }

    /**
     * Changes the name of a tag in the {@code TagManager}.
     * @param oldTagName The name of the tag which name should be changed as string.
     * @param newTagName The new name of the {@code oldTagName} tag as string.
     */
    public void changeTagName(String oldTagName, String newTagName) {
        if (!this.tagsMap.containsKey(oldTagName)) {
            return;
        }
        this.tagsMap.put(newTagName, this.tagsMap.remove(oldTagName));
    }

    /**
     * Changes the content of a tag in the {@code TagManager}.
     * @param tagName The name of the tag which content should be changed as string.
     * @param newContent The new content of the {@code tagName} tag as {@code List<Integer>}.
     */
    public void changeTagContent(String tagName, List<Integer> newContent) {
        if (!this.tagsMap.containsKey(tagName)) {
            return;
        }
        this.tagsMap.put(tagName, newContent);
    }

    /**
     * Saves all changes, like adding or deleting a tag, to the tag file. The tag file will be unmodified if the save
     * fails at some point.
     *
     * @return Boolean value if the save was successful.
     */
    public boolean saveChangesToFile() {
        if (Files.exists(this.tagFileModPath)) {
            try {
                Files.delete(this.tagFileModPath);
            } catch (IOException unused) {
                return false;
            }
        }

        try {
            Files.copy(this.tagsFilePath, this.tagFileModPath);
        } catch (IOException unused) {
            return false;
        }

        try (CsvWriter csvWriter = new CsvWriter(this.tagFileModPath.toString())) {
            for (Map.Entry<String, List<Integer>> entry : this.tagsMap.entrySet()) {
                String[] inputLine = new String[entry.getValue().size() + 1];
                inputLine[0] = entry.getKey();

                for (int i = 0; i < entry.getValue().size(); i++) {
                    inputLine[i + 1] = String.valueOf(entry.getValue().get(i));
                }

                csvWriter.writeLine(inputLine);
            }
        } catch (IOException unused1) {
            try {
                Files.delete(this.tagFileModPath);
            } catch (IOException unused2) {
            }
            return false;
        }

        try {
            Files.move(this.tagFileModPath, this.tagsFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException unused) {
            return false;
        }

        return true;
    }

    /**
     * Gets the content of a given tag from the {@code TagManager}.
     * @param tagName The name of the tag which content should be returned.
     * @return The content of the {@code tagName} tag as {@code List<Integer>}.
     */
    public List<Integer> getTagContent(String tagName) {
        if (!this.tagsMap.containsKey(tagName)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.tagsMap.get(tagName));
    }

    /**
     * Gets all tags from the {@code TagManager}.
     * @return All tags as {@code String[]}.
     */
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
