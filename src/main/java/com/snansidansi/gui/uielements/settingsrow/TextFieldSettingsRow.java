package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TextField;

public class TextFieldSettingsRow extends SettingsRow {
    private final TextField textField = new TextField();
    private String initValue;

    public TextFieldSettingsRow(BackupSetting setting,
                                String initValue,
                                int fontSize,
                                ReadOnlyDoubleProperty widthProperty) {
        super(setting, initValue, fontSize, widthProperty);
        super.getControlHBox().getChildren().add(textField);
    }

    public void setTextFieldWidth(int width) {
        this.textField.setMinWidth(width);
        this.textField.setMaxWidth(width);
    }

    @Override
    public String getValue() {
        return this.textField.getText();
    }

    @Override
    public void restoreStandardValue() {
        this.textField.setText(initValue);
    }

    @Override
    public void setInitValue(String value) {
        this.initValue = value;
        restoreStandardValue();
    }

}
