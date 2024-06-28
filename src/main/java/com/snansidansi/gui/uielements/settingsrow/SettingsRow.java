package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.settings.BackupSetting;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public abstract class SettingsRow extends HBox {
    private final HBox controlHBox = new HBox();
    private final BackupSetting setting;
    protected String initValue;

    public SettingsRow(BackupSetting setting,
                       String initValue,
                       String displayText,
                       int fontSize,
                       ReadOnlyDoubleProperty widthProperty) {

        super();
        this.setting = setting;
        this.initValue = initValue;

        Font fontSizeFont = new Font(fontSize);
        Label nameLabel = new Label(displayText);
        nameLabel.setFont(fontSizeFont);

        HBox nameHBox = new HBox();
        nameHBox.setAlignment(Pos.CENTER_LEFT);
        nameHBox.getChildren().add(nameLabel);

        nameHBox.minWidthProperty().bind(widthProperty.divide(2).subtract(9)); // -9 because of padding
        this.controlHBox.minWidthProperty().bind(widthProperty.divide(2).subtract(9)); // -9 because of padding

        super.setPadding(new Insets(5));
        super.setStyle("-fx-border-color: c3c3c3");
        super.getChildren().addAll(nameHBox, this.controlHBox);
    }

    public BackupSetting getSetting() {
        return this.setting;
    }

    protected HBox getControlHBox() {
        return this.controlHBox;
    }

    public abstract String getValue();

    public abstract void restoreStandardValue();

    public abstract void setInitValue(String value);

    public String getInitValue() {
        return this.initValue;
    }
}
