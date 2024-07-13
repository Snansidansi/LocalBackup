package tag;

import com.snansidansi.tag.TagManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TagManagerTest {
    private final Path resourcePath = Path.of("src/test/resources/tag");
    private final Path tagsWithValidConfigPath = resourcePath.resolve("tagsWithValidContent.csv");
    private final String secondTagName = "Second tag";

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path tempDir;

    @Test
    void addTagTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("addTagTest.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        String firstTagName = "Added tag";
        tagManager.addTag(firstTagName);
        tagManager.addTag(this.secondTagName);
        Assertions.assertTrue(tagManager.saveChangesToFile());
        asserFileContent(tagFilePath, 2, this.secondTagName + ";", firstTagName + ";");
    }

    @Test
    void deleteTagTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("deleteTagTest.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        String firstTagName = "Deleted tag";
        tagManager.addTag(firstTagName);
        tagManager.addTag(this.secondTagName);
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
        tagManager.addTag(oldTagName);
        tagManager.addTag(this.secondTagName);
        tagManager.changeTagName(oldTagName, newTagName);
        Assertions.assertTrue(tagManager.saveChangesToFile());
        asserFileContent(tagFilePath, 2, this.secondTagName + ";", newTagName + ";");
    }

    @Test
    void getTagsWithValidContentFromFileTest() throws IOException {
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
    void getTagsTest() throws IOException {
        TagManager tagManager = new TagManager(this.tagsWithValidConfigPath);
        tagManager.getTagsFromFile();
        String[] expectedTags = {"Tag1", "Tag2"};
        Assertions.assertArrayEquals(expectedTags, tagManager.getTags());
    }

    @Test
    void getTagsWithSomeInvalidContentFromFileTest() throws IOException {
        Path tagFilePath = this.resourcePath.resolve("tagsWithSomeInvalidContent.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        Assertions.assertFalse(tagManager.getTagsFromFile());
        String[] expectedTags = {"Tag1", "Tag2"};
        List<Integer> expectedSecondTagContent = List.of(1, 2);
        Assertions.assertArrayEquals(expectedTags, tagManager.getTags());
        Assertions.assertEquals(List.of(), tagManager.getTagContent("Tag1"));
        Assertions.assertEquals(expectedSecondTagContent, tagManager.getTagContent("Tag2"));
    }

    @Test
    void changeTagContentTest() throws IOException {
        final Path tagFilePath = tempDir.resolve("changeTagContentTest.csv");
        TagManager tagManager = new TagManager(tagFilePath);
        String tagName = "change tag content";
        tagManager.addTag(tagName);
        tagManager.addTag(this.secondTagName);
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
        writeTagManager.addTag(tagName);
        writeTagManager.addTag(secondTagName);
        writeTagManager.changeColor(tagName, tagColor);
        writeTagManager.saveChangesToFile();

        TagManager loadTagManager = new TagManager(tagFilePath);
        loadTagManager.getTagsFromFile();
        Assertions.assertEquals(tagColor, loadTagManager.getTagColor(tagName));
    }

    private void asserFileContent(Path filePath, int expectedNumberOfLines, String... expectedLines) throws IOException {
        List<String> fileContent = Files.readAllLines(filePath);
        Assertions.assertEquals(expectedNumberOfLines, fileContent.size());
        for (int i = 0; i < fileContent.size(); i++) {
            Assertions.assertEquals(expectedLines[i], fileContent.get(i));
        }
    }
}
