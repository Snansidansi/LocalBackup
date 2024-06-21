package com.snansidansi.gui.uielements.settingsrow;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.CheckBox;

public class CheckBoxSettingsRow extends SettingsRow {
    private CheckBox checkBox = new CheckBox();
    private String standardValue = "false";

    public CheckBoxSettingsRow(String settingName, int fontSize, String settingID, ReadOnlyDoubleProperty widthProperty) {
        super(settingName, fontSize, settingID, widthProperty);
        super.getControlHBox().getChildren().add(this.checkBox);
    }

    public void setStandardValue(boolean checked) {
        this.checkBox.setSelected(checked);
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
