package com.snansidansi.gui.uielements.settingsrow;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public abstract class SettingsRow extends HBox {
    private final HBox controlHBox = new HBox();
    private final String settingID;

    public SettingsRow(String settingName, int fontSize, String settingID, ReadOnlyDoubleProperty widthProperty) {
        super();
        super.setPadding(new Insets(5));
        this.settingID = settingID;

        Font fontSizeFont = new Font(fontSize);
        Label nameLabel = new Label(settingName);
        nameLabel.setFont(fontSizeFont);

        HBox nameHBox = new HBox();
        nameHBox.setAlignment(Pos.CENTER_LEFT);
        nameHBox.getChildren().add(nameLabel);

        nameHBox.minWidthProperty().bind(widthProperty.divide(2).subtract(9));
        this.controlHBox.minWidthProperty().bind(widthProperty.divide(2).subtract(9));

        super.setStyle("-fx-border-color: c3c3c3");
        super.getChildren().addAll(nameHBox, this.controlHBox);
    }

    public String getSetting() {
        return settingID;
    }

    protected HBox getControlHBox() {
        return this.controlHBox;
    }

    public abstract String getValue();

    public abstract void restoreStandardValue();

    public abstract void setStandardValue(String value);
}
