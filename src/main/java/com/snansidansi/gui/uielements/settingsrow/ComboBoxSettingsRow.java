package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.scene.control.ComboBox;

public class ComboBoxSettingsRow  extends SettingsRow {
    private final ComboBox<String> comboBox = new ComboBox<>();

    public ComboBoxSettingsRow(BackupSetting setting,
                               String displayText,
                               int fontSize) {

        super(setting, displayText, fontSize);
        super.getControlHBox().getChildren().add(comboBox);

        this.comboBox.getItems().addAll(setting.getOptions());

        restoreStandardValue();
    }

    public void setComboBoxWidth(int width) {
        this.comboBox.setMinWidth(width);
        this.comboBox.setMaxWidth(width);
    }

    @Override
    public String getValue() {
        return this.comboBox.getValue();
    }

    @Override
    public void restoreStandardValue() {
        this.comboBox.setValue(this.initValue);
    }

    @Override
    public void setInitValue(String value) {
        this.initValue = value;
        restoreStandardValue();
    }
}
