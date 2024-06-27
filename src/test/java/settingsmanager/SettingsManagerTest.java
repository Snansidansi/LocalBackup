package settingsmanager;

import com.snansidansi.settings.settingsmanager.SettingsManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

//The tests use the TestSettings.java enum class in the same package as this file.
public class SettingsManagerTest {
    private int settingsFileID = 0;
    private SettingsManager<TestSettings> settingsManager;
    private Path testResourcesPath = Path.of("src/test/resources/settingsmanager");

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path tempdir;

    @BeforeEach
    public void createSettingsManager() {
        this.settingsManager = new SettingsManager<>(
                tempdir.resolve(String.valueOf(this.settingsFileID)).toString(),
                TestSettings.class);
        this.settingsFileID++;
    }

    @Test
    void validPathForSettingsFileDoesNotThrowsInvalidPathException() {
        Assertions.assertDoesNotThrow(
                () -> new SettingsManager<>(this.testResourcesPath.toString(), TestSettings.class));
    }

    @ParameterizedTest
    @EnumSource(TestSettings.class)
    void getEnumFromIdTest(TestSettings setting) {
        String id = setting.getID();
        TestSettings result = this.settingsManager.getEnumFromID(id);
        Assertions.assertEquals(result.getID(), id);
    }

    @Test
    void getEnumFromNotExistingIdTest() {
        TestSettings result = settingsManager.getEnumFromID("not existing id");
        Assertions.assertNull(result);
    }

    @ParameterizedTest
    @EnumSource(TestSettings.class)
    void getDefaultSettingTest(TestSettings setting) {
        Assertions.assertEquals(setting.getStandardValue(), settingsManager.getSetting(setting));
    }

    @ParameterizedTest
    @EnumSource(TestSettings.class)
    void areDefaultValuesValidValuesTest(TestSettings setting) {
        Assertions.assertTrue(settingsManager.isValidSettingsValue(setting, setting.getStandardValue()));
    }

    @Test
    void invalidValueForInteger() {
        Assertions.assertFalse(settingsManager.isValidSettingsValue(
                TestSettings.AINTEGER, "invalid value"));
    }

    @Test
    void invalidValueForBoolean() {
        Assertions.assertFalse(settingsManager.isValidSettingsValue(
                TestSettings.ABOOLEAN, "invalid value"));
    }

    @Test
    void tooLargeIntegerAsValueIsInvalid() {
        Assertions.assertFalse(settingsManager.isValidSettingsValue(
                TestSettings.AINTEGER, "99999999999999999"));
    }

    @Test
    void doubleValueForIntegerValueIsInvalid() {
        Assertions.assertFalse(settingsManager.isValidSettingsValue(
                TestSettings.AINTEGER, "10.0"));
    }

    @Test
    void applyValidChangedSettingsTest() {
        changeSettingsValues("20", "false", "changed");
        settingsManager.applyChanges();
        assertSettingsValues("20", "false", "changed", settingsManager);
    }

    @Test
    void ignoreInvalidSettingsWhenApplyingChangedSettings() {
        changeSettingsValues("invalid", "invalid", "valid");
        settingsManager.applyChanges();
        assertSettingsValues(
                TestSettings.AINTEGER.getStandardValue(),
                TestSettings.ABOOLEAN.getStandardValue(),
                "valid",
                settingsManager);
    }

    @Test
    void discardChangesTest() {
        settingsManager.changeSetting(TestSettings.ABOOLEAN, "changed");
        settingsManager.discardChanges();
        settingsManager.applyChanges();

        Assertions.assertEquals(
                settingsManager.getSetting(TestSettings.ABOOLEAN), TestSettings.ABOOLEAN.getStandardValue());
    }

    @Test
    void saveDefaultAndChangedSettingsToFile() {
        settingsManager.changeSetting(TestSettings.ABOOLEAN, "false");
        settingsManager.changeSetting(TestSettings.ASTRING, "changed");
        settingsManager.applyChanges();

        List<String> result = null;
        try {
            result = Files.readAllLines(Path.of(settingsManager.getSettingsFilePath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }

        List<String> expected = List.of("ainteger:10", "aboolean:false", "astring:changed");
        Assertions.assertEquals(result, expected);
    }

    @Test
    void loadValidNoneDefaultSettingsFromFile() {
        var loadSettingsManager = new SettingsManager<>(
                this.testResourcesPath.resolve("validNoneDefaultSettings.txt").toString(),
                TestSettings.class);

        loadSettingsManager.load();
        assertSettingsValues("55", "false", "manual", loadSettingsManager);
    }

    @Test
    void loadDefaultValuesForInvalidValuesFromSettingsFile() {
        var loadSettingsManager = new SettingsManager<>(
                this.testResourcesPath.resolve("someInvalidNoneDefaultSettings.txt").toString(),
                TestSettings.class);

        loadSettingsManager.load();
        assertSettingsValues(
                TestSettings.AINTEGER.getStandardValue(),
                TestSettings.ABOOLEAN.getStandardValue(),
                "valid",
                loadSettingsManager);
    }

    @Test
    void ignoreUnknownSettingIDsInSettingsFileAndUseDefaultValuesInstead() {
        var loadSettingsManager = new SettingsManager<>(
                this.testResourcesPath.resolve("unknownSettings.txt").toString(),
                TestSettings.class);

        loadSettingsManager.load();
        assertDefaultSettingsValues(loadSettingsManager);
    }

    @Test
    void useDefaultSettingsIfSettingsFileDoesNotExist() {
        assertDefaultSettingsValues(settingsManager);
    }

    private void changeSettingsValues(String integerValue, String booleanValue, String stringValue) {
        settingsManager.changeSetting(TestSettings.AINTEGER, integerValue);
        settingsManager.changeSetting(TestSettings.ABOOLEAN, booleanValue);
        settingsManager.changeSetting(TestSettings.ASTRING, stringValue);
    }

    private void assertSettingsValues(String integerValue, String booleanValue, String stringValue,
                                      SettingsManager<TestSettings> sManager) {

        Assertions.assertEquals(integerValue, sManager.getSetting(TestSettings.AINTEGER));
        Assertions.assertEquals(booleanValue, sManager.getSetting(TestSettings.ABOOLEAN));
        Assertions.assertEquals(stringValue, sManager.getSetting(TestSettings.ASTRING));
    }

    private void assertDefaultSettingsValues(SettingsManager<TestSettings> sManager) {
        Assertions.assertEquals(TestSettings.AINTEGER.getStandardValue(), sManager.getSetting(TestSettings.AINTEGER));
        Assertions.assertEquals(TestSettings.ABOOLEAN.getStandardValue(), sManager.getSetting(TestSettings.ABOOLEAN));
        Assertions.assertEquals(TestSettings.ASTRING.getStandardValue(), sManager.getSetting(TestSettings.ASTRING));
    }
}
