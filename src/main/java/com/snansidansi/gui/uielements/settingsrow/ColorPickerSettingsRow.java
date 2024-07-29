package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.gui.util.Utility;
import com.snansidansi.settings.BackupSetting;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class ColorPickerSettingsRow extends SettingsRow {
    private final ColorPicker colorPicker = new ColorPicker();

    public ColorPickerSettingsRow(BackupSetting setting,
                                  String displayText,
                                  int fontSize) {

        super(setting, displayText, fontSize);
        super.getControlHBox().getChildren().add(this.colorPicker);

        restoreStandardValue();
    }

    @Override
    public String getValue() {
        Color color = this.colorPicker.getValue();
        return Utility.convertJavaFXColorToHexColor(color);
    }

    @Override
    public void restoreStandardValue() {
        this.colorPicker.setValue(Color.web(this.initValue));
    }

    @Override
    public void setInitValue(String value) {
        this.initValue = value;
        restoreStandardValue();
    }
}
