package com.snansidansi.gui.controller;

import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.gui.uielements.settingsrow.SettingsRow;
import com.snansidansi.gui.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsController {
    private final List<SettingsRow> availableSettingsRows = new ArrayList<>();

    @FXML
    VBox mainContainer;

    @FXML
    ScrollPane settingsScrollPane;

    @FXML
    VBox settingsVBox;

    @FXML
    public void initialize() {
        displaySettings();
    }

    private void displaySettings() {
        this.settingsVBox.getChildren().clear();
        var widthProperty = this.settingsScrollPane.widthProperty();
    }

    private <T extends SettingsRow> void addSettingsRow(T settingsRow) {
        this.settingsVBox.getChildren().add(settingsRow);
        this.availableSettingsRows.add(settingsRow);
    }

    public void backToConfigureBackupScene() {
        try {
            SceneManager.setConfigureBackupsScene((Stage) this.mainContainer.getScene().getWindow());
        } catch (IOException unused) {
        }
    }

    public void restoreDefaultSettings() {
        SettingsManagerInstance.settingsManager.restoreDefaults();
        displaySettings();
    }

    public void discardChanges() {
        for (SettingsRow settingsRow : this.availableSettingsRows)
            settingsRow.restoreStandardValue();
    }

    public void saveChanges() {
        for (SettingsRow settingsRow : this.availableSettingsRows) {
            SettingsManagerInstance.settingsManager.changeSetting(settingsRow.getSetting(), settingsRow.getValue());
            settingsRow.setStandardValue(settingsRow.getValue());
        }

        if (SettingsManagerInstance.settingsManager.applyChanges()) return;
        displaySettings();
    }
}
