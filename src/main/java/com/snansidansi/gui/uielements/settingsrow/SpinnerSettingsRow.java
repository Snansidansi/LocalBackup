package com.snansidansi.gui.uielements.settingsrow;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class SpinnerSettingsRow extends SettingsRow {
    private Spinner<Integer> spinner = new Spinner<>();
    private String standardValue = "0";

    public SpinnerSettingsRow(String settingName, int fontSize, String settingID, ReadOnlyDoubleProperty widthProperty) {
        super(settingName, fontSize, settingID, widthProperty);
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

        this.standardValue = String.valueOf(standardValue);
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
        this.spinner.getEditor().setText(this.standardValue);
    }

    @Override
    public void setStandardValue(String value) {
        this.standardValue = value;
        restoreStandardValue();
    }
}
