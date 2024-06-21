package com.snansidansi.gui.uielements;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class SettingsRow extends HBox {
    private HBox nameHBox = new HBox();
    private HBox valueHBox = new HBox();

    public SettingsRow(String settingName, int fontSize, ReadOnlyDoubleProperty widthProperty) {
        super();
        Font fontSizeFont = new Font(fontSize);

        Label nameLabel = new Label(settingName);
        nameLabel.setFont(fontSizeFont);

        this.nameHBox.getChildren().add(nameLabel);

        this.nameHBox.minWidthProperty().bind(widthProperty.divide(2).subtract(4));
        this.valueHBox.minWidthProperty().bind(widthProperty.divide(2).subtract(4));

        super.setStyle("-fx-border-color: c3c3c3");
        super.getChildren().addAll(this.nameHBox, this.valueHBox);
    }

    @Override
    public ObservableList<Node> getChildren() {
        return this.valueHBox.getChildren();
    }
}
