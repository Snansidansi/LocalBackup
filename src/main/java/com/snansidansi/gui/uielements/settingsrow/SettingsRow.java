package com.snansidansi.gui.uielements.settingsrow;

import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.settings.BackupSetting;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public abstract class SettingsRow extends HBox {
    private final HBox controlHBox = new HBox();
    private final HBox nameHBox = new HBox();
    private final Label nameLabel;
    private final BackupSetting setting;
    protected String initValue;

    public SettingsRow(BackupSetting setting,
                       String displayText,
                       int fontSize) {

        super();
        this.setting = setting;
        this.initValue = SettingsManagerInstance.settingsManager.getSetting(setting);

        Font fontSizeFont = new Font(fontSize);
        this.nameLabel = new Label(displayText);
        this.nameLabel.setFont(fontSizeFont);

        this.nameHBox.setAlignment(Pos.CENTER_LEFT);
        this.nameHBox.getChildren().add(this.nameLabel);

        super.setPadding(new Insets(5));
        super.getChildren().addAll(this.nameHBox, this.controlHBox);
    }

    public BackupSetting getSetting() {
        return this.setting;
    }

    public HBox getControlHBox() {
        return this.controlHBox;
    }

    public abstract String getValue();

    public abstract void restoreStandardValue();

    public abstract void setInitValue(String value);

    public String getInitValue() {
        return this.initValue;
    }

    public void addTooltip(String text, int fontSize) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setFont(new Font(fontSize));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.ZERO);

        Tooltip.install(this.nameHBox, tooltip);
    }

    public Label getNameLabel() {
        return  this.nameLabel;
    }
}
