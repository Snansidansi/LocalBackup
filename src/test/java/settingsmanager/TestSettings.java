package settingsmanager;

import com.snansidansi.settings.settingsmanager.SettingType;
import com.snansidansi.settings.settingsmanager.Settings;

enum TestSettings implements Settings {
    AINTEGER("ainteger", SettingType.INTEGER, "10"),
    ABOOLEAN("aboolean", SettingType.BOOLEAN, "true"),
    ASTRING("astring", SettingType.STRING, "default");

    private final String id;
    private final SettingType type;
    private final String standardValue;

    TestSettings(String id, SettingType type, String standardValue) {
        this.id = id;
        this.type = type;
        this.standardValue = standardValue;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public SettingType getType() {
        return this.type;
    }

    @Override
    public String getStandardValue() {
        return this.standardValue;
    }
}
