package com.snansidansi.tag;

import com.snansidansi.csv.CsvReader;
import com.snansidansi.csv.CsvWriter;
import javafx.util.Pair;

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
 * {@link #saveChangesToFile()}. Otherwise, the changes will not be saved to the tag file. An instance of this class
 * will not be reading the tags from the tag file (specified in the constructor) unless the method
 * {@link #getTagsFromFile()} is used.
 */
public class TagManager {
    private final Path tagsFilePath;
    private Map<String, Pair<String, List<Integer>>> tagsMap = new HashMap<>();
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
                String tagColor = line[1];
                List<Integer> indicesWithTag = new ArrayList<>(line.length - 1);

                for (int i = 2; i < line.length; i++) {
                    try {
                        indicesWithTag.add(Integer.parseInt(line[i]));
                    } catch (NumberFormatException unused) {
                        success = false;
                    }
                }

                this.tagsMap.put(tagName, new Pair<>(tagColor, indicesWithTag));
            }
        } catch (FileNotFoundException unused) {
        }

        return success;
    }

    /**
     * Adds a tag name to the {@code TagManager}. Only adds the tag if it does not exist already.
     * @param tagName Name of the tag as string.
     * @return Boolean value if the tag was added.
     */
    public boolean addTagName(String tagName) {
        if (this.tagsMap.containsKey(tagName)) {
            return false;
        }
        this.tagsMap.put(tagName, new Pair<>("", new ArrayList<>()));
        return true;
    }

    /**
     * Deletes a tag from the {@code TagManager}.
     * @param tagName The name of the tag that should be deleted as string.
     */
    public void deleteTag(String tagName) {
        this.tagsMap.remove(tagName);
    }

    /**
     * Changes the name of a tag in the {@code TagManager}. Only changes the tag name if the tag exists.
     * @param oldTagName The name of the tag which name should be changed as string.
     * @param newTagName The new name of the {@code oldTagName} tag as string.
     * @return Boolean value if the tag name was changed successfully.
     */
    public boolean changeTagName(String oldTagName, String newTagName) {
        if (!this.tagsMap.containsKey(oldTagName)) {
            return false;
        }
        this.tagsMap.put(newTagName, this.tagsMap.remove(oldTagName));
        return true;
    }

    /**
     * Changes the content of a tag in the {@code TagManager}. Only changes the tag content if the tag exists.
     * @param tagName The name of the tag which content should be changed as string.
     * @param newContent The new content of the {@code tagName} tag as {@code List<Integer>}.
     * @return Boolean value if the content of the tag was changed successfully.
     */
    public boolean changeTagContent(String tagName, List<Integer> newContent) {
        if (!this.tagsMap.containsKey(tagName)) {
            return false;
        }
        String color = this.tagsMap.get(tagName).getKey();
        this.tagsMap.put(tagName, new Pair<>(color, newContent));
        return true;
    }

    /**
     * Changes the color of a tag in the {@code TagManager}. Only changes the color of a tag if the tag exists.
     * @param tagName The name of the tag which color should be changed as string.
     * @param colorValue The new color of the {@code tagName} as string.
     * @return Boolean value if the color of the tag was changed successfully.
     */
    public boolean changeColor(String tagName, String colorValue) {
        if (!this.tagsMap.containsKey(tagName)) {
            return false;
        }
        List<Integer> content = this.tagsMap.get(tagName).getValue();
        this.tagsMap.put(tagName, new Pair<>(colorValue, content));
        return true;
    }

    /**
     * Adds a tag to {@code TagManager}. Only adds the tag if it does not exist already.
     *
     * @param tag The new tag as {@link Tag}.
     * @return Boolean value if the tag was added successfully.
     */
    public boolean addTag(Tag tag) {
        if (this.tagsMap.containsKey(tag.name)) {
            return false;
        }
        this.tagsMap.put(tag.name, new Pair<>(tag.color, tag.content));
        return true;
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
            for (Map.Entry<String, Pair<String, List<Integer>>> entry : this.tagsMap.entrySet()) {
                String[] inputLine = new String[entry.getValue().getValue().size() + 2];
                inputLine[0] = entry.getKey();
                inputLine[1] = entry.getValue().getKey();

                for (int i = 0; i < entry.getValue().getValue().size(); i++) {
                    inputLine[i + 2] = String.valueOf(entry.getValue().getValue().get(i));
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
     * @return The content of the {@code tagName} tag as {@code List<Integer>}. If the given {@code tagName} does not
     * exist then method returns an empty {@code ArrayList<Integer>}.
     */
    public List<Integer> getTagContent(String tagName) {
        if (!this.tagsMap.containsKey(tagName)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.tagsMap.get(tagName).getValue());
    }

    /**
     * Gets all tag names from the {@code TagManager}.
     * @return All tag names as {@code String[]}.
     */
    public String[] getAllTagNames() {
        String[] tags = new String[this.tagsMap.size()];
        int i = 0;
        for (Map.Entry<String, Pair<String, List<Integer>>> entry : this.tagsMap.entrySet()) {
            tags[i] = entry.getKey();
            i++;
        }
        return tags;
    }

    /**
     * Gets the color of a given tag from the {@code TagManager}.
     * @param tagName The name of a tag as string.
     * @return The color of the tag as string. If the {@code tagName} does not exist then the method returns an empty
     * string.
     */
    public String getTagColor(String tagName) {
        if (!this.tagsMap.containsKey(tagName)) {
            return "";
        }
        return this.tagsMap.get(tagName).getKey();
    }

    /**
     * Gets a tag from the {@code TagManager}.
     *
     * @param tagName The name of the tag as string.
     * @return The full tag data as {@link Tag}. If the {@code tagName} does not exist then the method will return null.
     */
    public Tag getTag(String tagName) {
        if (!this.tagsMap.containsKey(tagName)) {
            return null;
        }
        Pair<String, List<Integer>> tagBody = this.tagsMap.get(tagName);
        return new Tag(tagName, tagBody.getKey(), tagBody.getValue());
    }
}
