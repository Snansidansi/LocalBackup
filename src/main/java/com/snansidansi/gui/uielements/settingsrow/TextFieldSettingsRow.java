package com.snansidansi.gui.uielements.settingsrow;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TextField;

public class TextFieldSettingsRow extends SettingsRow {
    private TextField textField = new TextField();
    private String standardValue = "";

    public TextFieldSettingsRow(String settingName, int fontSize, String settingID, ReadOnlyDoubleProperty widthProperty) {
        super(settingName, fontSize, settingID, widthProperty);
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
        this.textField.setText(standardValue);
    }

    @Override
    public void setStandardValue(String value) {
        this.standardValue = value;
        restoreStandardValue();
    }

}
