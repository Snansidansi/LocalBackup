package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class SpinnerSettingsRow extends SettingsRow {
    private final Spinner<Integer> spinner = new Spinner<>();
    private String initValue;

    public SpinnerSettingsRow(BackupSetting setting,
                              String standardValue,
                              int fontSize,
                              ReadOnlyDoubleProperty widthProperty) {

        super(setting, standardValue, fontSize, widthProperty);
        super.getControlHBox().getChildren().add(this.spinner);

        this.spinner.setEditable(true);
        setBounds(0, 100, 0);
        this.spinner.getEditor().textProperty().addListener(((observableValue, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException unused) {
                this.spinner.getEditor().setText(oldValue);
            }
        }));
    }

    public void setBounds(int minValue, int maxValue, int standardValue) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory
                .IntegerSpinnerValueFactory(minValue, maxValue, standardValue);
        this.spinner.setValueFactory(valueFactory);

        this.initValue = String.valueOf(standardValue);
    }

    public void setSpinnerWidth(int width) {
        this.spinner.setMinWidth(width);
        this.spinner.setMaxWidth(width);
    }

    @Override
    public String getValue() {
        return this.spinner.getValue().toString();
    }

    @Override
    public void restoreStandardValue() {
        this.spinner.getEditor().setText(this.initValue);
    }

    @Override
    public void setInitValue(String value) {
        this.initValue = value;
        restoreStandardValue();
    }
}
