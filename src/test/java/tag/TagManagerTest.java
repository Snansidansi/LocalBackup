package tag;

import com.snansidansi.tag.Tag;
import com.snansidansi.tag.TagManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagManagerTest {
    private final Path resourcePath = Path.of("src/test/resources/tag");
    private final Path tagsWithValidConfigPath = resourcePath.resolve("tagsWithValidContent.csv");
    private final String secondTagName = "Second tag";
    private final Tag[] expectedTagsInValidConfigPath = {
            new Tag("Tag1", "colorA", List.of(1, 2)),
            new Tag("Tag2", "colorB", List.of(3, 4))
    };

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path tempDir;

    @Test
    void addTagNameTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("addTagNameTest.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        String firstTagName = "Added tag";
        tagManager.addTagName(firstTagName);
        tagManager.addTagName(this.secondTagName);
        Assertions.assertTrue(tagManager.saveChangesToFile());
        asserFileContent(tagFilePath, 2, this.secondTagName + ";", firstTagName + ";");
    }

    @Test
    void deleteTagTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("deleteTagTest.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        String firstTagName = "Deleted tag";
        tagManager.addTagName(firstTagName);
        tagManager.addTagName(this.secondTagName);
        tagManager.deleteTag(firstTagName);
        Assertions.assertTrue(tagManager.saveChangesToFile());
        asserFileContent(tagFilePath, 1, this.secondTagName + ";");
    }

    @Test
    void changeTagNameTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("changeTagNameTest.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        String oldTagName = "Old tag name";
        String newTagName = "New tag name";
        tagManager.addTagName(oldTagName);
        tagManager.addTagName(this.secondTagName);
        tagManager.changeTagName(oldTagName, newTagName);
        Assertions.assertTrue(tagManager.saveChangesToFile());
        asserFileContent(tagFilePath, 2, this.secondTagName + ";", newTagName + ";");
    }

    @Test
    void getAllTagNamesWithValidContentFromFileTest() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        Assertions.assertTrue(tagManager.getTagsFromFile());
    }

    @Test
    void getTagContentTest() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        tagManager.getTagsFromFile();
        List<Integer> expectedFirstTagContent = List.of(1, 2);
        List<Integer> expectedSecondTagContent = List.of(3, 4);
        Assertions.assertEquals(expectedFirstTagContent, tagManager.getTagContent("Tag1"));
        Assertions.assertEquals(expectedSecondTagContent, tagManager.getTagContent("Tag2"));
    }

    @Test
    void getAllTagNamesTest() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        tagManager.getTagsFromFile();
        String[] expectedTags = {"Tag1", "Tag2"};
        Assertions.assertArrayEquals(expectedTags, tagManager.getAllTagNames());
    }

    @Test
    void getAllTagNamesWithSomeInvalidContentFromFileTest() throws IOException {
        Path tagFilePath = this.resourcePath.resolve("tagsWithSomeInvalidContent.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        Assertions.assertFalse(tagManager.getTagsFromFile());
        String[] expectedTags = {"Tag1", "Tag2"};
        List<Integer> expectedSecondTagContent = List.of(1, 2);
        Assertions.assertArrayEquals(expectedTags, tagManager.getAllTagNames());
        Assertions.assertEquals(List.of(), tagManager.getTagContent("Tag1"));
        Assertions.assertEquals(expectedSecondTagContent, tagManager.getTagContent("Tag2"));
    }

    @Test
    void changeTagContentTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("changeTagContentTest.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        String tagName = "change tag content";
        tagManager.addTagName(tagName);
        tagManager.addTagName(this.secondTagName);
        List<Integer> tagContent = List.of(1, 2, 3);
        tagManager.changeTagContent(tagName, tagContent);
        Assertions.assertTrue(tagManager.saveChangesToFile());
        Assertions.assertEquals(tagContent, tagManager.getTagContent(tagName));
        Assertions.assertEquals(List.of(), tagManager.getTagContent(secondTagName));
    }

    @Test
    void getTagColorTest() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        tagManager.getTagsFromFile();
        Assertions.assertEquals("colorA", tagManager.getTagColor("Tag1"));
        Assertions.assertEquals("colorB", tagManager.getTagColor("Tag2"));
    }

    @Test
    void changeTagColorTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("changeTagColorTest.csv");
        TagManager writeTagManager = new TagManager(tagFilePath);
        String tagName = "Tag with color";
        String tagColor = "hexColor";
        writeTagManager.addTagName(tagName);
        writeTagManager.addTagName(secondTagName);
        writeTagManager.changeColor(tagName, tagColor);
        writeTagManager.saveChangesToFile();

        TagManager loadTagManager = new TagManager(tagFilePath);
        loadTagManager.getTagsFromFile();
        Assertions.assertEquals(tagColor, loadTagManager.getTagColor(tagName));
    }

    @Test
    void getTagTest() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        tagManager.getTagsFromFile();
        String tagName = "Tag1";
        Tag expected = new Tag(tagName, "colorA", List.of(1, 2));
        Assertions.assertEquals(expected, tagManager.getTag(tagName));
    }


    @Test
    void getAllTagsTest() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        tagManager.getTagsFromFile();
        Assertions.assertArrayEquals(this.expectedTagsInValidConfigPath, tagManager.getAllTags());
    }

    @Test
    void addTagTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("addTagTest.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        String tagName = "tag";
        Tag tag = new Tag(tagName, "color", List.of(1, 2, 3));
        Assertions.assertTrue(tagManager.addTag(tag));
        Assertions.assertEquals(tag, tagManager.getTag(tagName));
    }

    @Test
    void usingTheGetTagsFromFileMethodTwiceDoesNotCreateDuplicates() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        tagManager.getTagsFromFile();
        tagManager.getTagsFromFile();
        Assertions.assertArrayEquals(this.expectedTagsInValidConfigPath, tagManager.getAllTags());
    }

    @Test
    void usingTheGetTagsFromFileMethodOverwritesTheOldTags() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        tagManager.addTagName("New tag");
        tagManager.getTagsFromFile();
        Assertions.assertArrayEquals(this.expectedTagsInValidConfigPath, tagManager.getAllTags());
        tagManager.changeColor("Tag1", "newColor");
        tagManager.getTagsFromFile();
        Assertions.assertArrayEquals(this.expectedTagsInValidConfigPath, tagManager.getAllTags());
    }

    @Test
    void revertChangesWorksWhenNotSavedTest() throws IOException {
        Path tagFilePath = tempDir.resolve("revertChangesWorksWhenNotSaved.csv");
        Files.copy(this.tagsWithValidConfigPath, tagFilePath);
        TagManager tagManager = new TagManager(tagFilePath);
        tagManager.getTagsFromFile();
        tagManager.addTagName("newTag");
        tagManager.deleteTag("Tag1");
        tagManager.changeColor("Tag2", "newColor");
        tagManager.revertChanges();
        Assertions.assertArrayEquals(this.expectedTagsInValidConfigPath, tagManager.getAllTags());
        tagManager.changeTagContent("Tag2", List.of(3, 4));
        Assertions.assertArrayEquals(this.expectedTagsInValidConfigPath, tagManager.getAllTags());
    }

    @Test
    void notRevertChangesSavedTest() throws IOException {
        Path tagFilePath = tempDir.resolve("notRevertChangesWhenSavedTest.csv");
        Files.copy(this.tagsWithValidConfigPath, tagFilePath);
        TagManager tagManager = new TagManager(tagFilePath);
        tagManager.getTagsFromFile();
        tagManager.addTagName("newTag");
        tagManager.deleteTag("Tag1");
        tagManager.changeColor("Tag2", "newColor");
        tagManager.saveChangesToFile();
        tagManager.revertChanges();
        Tag[] expectedTags = {
                new Tag("newTag", "", new ArrayList<>()),
                new Tag("Tag2", "newColor", List.of(3, 4))
        };
        System.out.println(Arrays.toString(expectedTags));
        System.out.println(Arrays.toString(tagManager.getAllTags()));
        Assertions.assertArrayEquals(expectedTags, tagManager.getAllTags());
    }

    private void asserFileContent(Path filePath, int expectedNumberOfLines, String... expectedLines) throws IOException {
        List<String> fileContent = Files.readAllLines(filePath);
        Assertions.assertEquals(expectedNumberOfLines, fileContent.size());
        for (int i = 0; i < fileContent.size(); i++) {
            Assertions.assertEquals(expectedLines[i], fileContent.get(i));
        }
    }
}
