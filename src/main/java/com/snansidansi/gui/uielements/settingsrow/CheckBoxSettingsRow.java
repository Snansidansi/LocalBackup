package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.scene.control.CheckBox;

public class CheckBoxSettingsRow extends SettingsRow {
    private final CheckBox checkBox = new CheckBox();

    public CheckBoxSettingsRow(BackupSetting setting,
                               String displayText,
                               int fontSize) {

        super(setting, displayText, fontSize);
        super.getControlHBox().getChildren().add(this.checkBox);

        restoreStandardValue();
    }

    @Override
    public String getValue() {
        return String.valueOf(this.checkBox.isSelected());
    }

    @Override
    public void restoreStandardValue() {
        this.checkBox.setSelected(Boolean.parseBoolean(this.initValue));
    }

    @Override
    public void setInitValue(String value) {
        this.initValue = value;
        restoreStandardValue();
    }
}
