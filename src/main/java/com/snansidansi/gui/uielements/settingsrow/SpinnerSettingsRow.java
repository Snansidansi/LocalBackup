package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class SpinnerSettingsRow extends SettingsRow {
    private final Spinner<Integer> spinner = new Spinner<>();

    public SpinnerSettingsRow(BackupSetting setting,
                              String displayText,
                              int fontSize) {

        super(setting, displayText, fontSize);
        super.getControlHBox().getChildren().add(this.spinner);

        this.spinner.setEditable(true);
        setBounds(0, 100, Integer.parseInt(initValue));
        this.spinner.getEditor().textProperty().addListener(((observableValue, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException unused) {
                this.spinner.getEditor().setText(oldValue);
            }
        }));

        restoreStandardValue();
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
        this.spinner.getValueFactory().setValue(Integer.parseInt(this.initValue));
    }

    @Override
    public void setInitValue(String value) {
        this.initValue = value;
        restoreStandardValue();
    }
}
