package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.CheckBox;

public class CheckBoxSettingsRow extends SettingsRow {
    private final CheckBox checkBox = new CheckBox();
    private String intValue;

    public CheckBoxSettingsRow(BackupSetting setting,
                               String intValue,
                               int fontSize,
                               ReadOnlyDoubleProperty widthProperty) {

        super(setting, intValue, fontSize, widthProperty);
        super.getControlHBox().getChildren().add(this.checkBox);
    }

    @Override
    public String getValue() {
        return String.valueOf(this.checkBox.isSelected());
    }

    @Override
    public void restoreStandardValue() {
        this.checkBox.setSelected(Boolean.parseBoolean(this.intValue));
    }

    @Override
    public void setInitValue(String value) {
        this.intValue = value;
        restoreStandardValue();
    }
}
