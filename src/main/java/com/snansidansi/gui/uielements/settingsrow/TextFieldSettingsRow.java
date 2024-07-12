package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.scene.control.TextField;

public class TextFieldSettingsRow extends SettingsRow {
    private final TextField textField = new TextField();

    public TextFieldSettingsRow(BackupSetting setting,
                                String displayText,
                                int fontSize) {

        super(setting, displayText, fontSize);
        super.getControlHBox().getChildren().add(textField);

        restoreStandardValue();
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

    public TextField getTextField() {
        return this.textField;
    }
}
