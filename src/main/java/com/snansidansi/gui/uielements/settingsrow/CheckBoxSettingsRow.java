package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.CheckBox;

public class CheckBoxSettingsRow extends SettingsRow {
    private final CheckBox checkBox = new CheckBox();
    private String standardValue;

    public CheckBoxSettingsRow(BackupSetting setting,
                               String standardValue,
                               int fontSize,
                               ReadOnlyDoubleProperty widthProperty) {

        super(setting, standardValue, fontSize, widthProperty);
        super.getControlHBox().getChildren().add(this.checkBox);
    }

    @Override
    public String getValue() {
        return String.valueOf(this.checkBox.isSelected());
    }

    @Override
    public void restoreStandardValue() {
        this.checkBox.setSelected(Boolean.parseBoolean(this.standardValue));
    }

    @Override
    public void setStandardValue(String value) {
        this.standardValue = value;
        restoreStandardValue();
    }
}
