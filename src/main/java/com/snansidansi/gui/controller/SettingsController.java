package com.snansidansi.gui.controller;

import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.gui.uielements.settingsrow.SettingsRow;
import com.snansidansi.gui.uielements.settingsrow.SpinnerSettingsRow;
import com.snansidansi.gui.util.SceneManager;
import com.snansidansi.settings.BackupSetting;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        createSettingsRows();
        displaySettingsRows();
    }

    private void createSettingsRows() {
        var widthProperty = this.settingsScrollPane.widthProperty();
        final int FONTSIZE = 14;
        this.availableSettingsRows.clear();

        this.availableSettingsRows.add(new SpinnerSettingsRow(
                BackupSetting.MAX_ERROR_LOGS,
                SettingsManagerInstance.settingsManager.getSetting(BackupSetting.MAX_ERROR_LOGS),
                "Max number of error logs:",
                FONTSIZE,
                widthProperty));

        this.availableSettingsRows.add(new SpinnerSettingsRow(
                BackupSetting.MAX_BACKUP_LOGS,
                SettingsManagerInstance.settingsManager.getSetting(BackupSetting.MAX_BACKUP_LOGS),
                "Max number of backup logs:",
                FONTSIZE,
                widthProperty));
    }

    private void displaySettingsRows() {
        this.settingsVBox.getChildren().clear();
        this.settingsVBox.getChildren().addAll(this.availableSettingsRows);
    }

    public void backToConfigureBackupScene() {
        if (settingsChanged() &&
                !showConfirmAlert("Unsaved Changes", "Are you sure to discard the changed settings?")) {
            return;
        }

        try {
            SceneManager.setConfigureBackupsScene((Stage) this.mainContainer.getScene().getWindow());
        } catch (IOException unused) {
        }
    }

    public void restoreDefaultSettings() {
        if (!showConfirmAlert("Restore default Settings", "Are you sure to restore the default settings?"))
            return;

        SettingsManagerInstance.settingsManager.restoreDefaults();
        createSettingsRows();
        displaySettingsRows();
    }

    public void discardChanges() {
        for (SettingsRow settingsRow : this.availableSettingsRows)
            settingsRow.restoreStandardValue();
    }

    public void saveChanges() {
        for (SettingsRow settingsRow : this.availableSettingsRows) {
            SettingsManagerInstance.settingsManager.changeSetting(settingsRow.getSetting(), settingsRow.getValue());
            settingsRow.setInitValue(settingsRow.getValue());
        }

        if (!SettingsManagerInstance.settingsManager.applyChanges()) return;
        SettingsManagerInstance.reloadSettings();
        createSettingsRows();
        displaySettingsRows();
    }

    private boolean settingsChanged() {
        for (SettingsRow settingsRow : this.availableSettingsRows) {
            if (!settingsRow.getValue().equals(settingsRow.getInitValue()))
                return true;
        }
        return false;
    }

    private boolean showConfirmAlert(String title, String contentText) {
        Alert unsavedChangesAlert = new Alert(Alert.AlertType.CONFIRMATION);
        unsavedChangesAlert.setTitle(title);
        unsavedChangesAlert.setHeaderText(contentText);

        Optional<ButtonType> result = unsavedChangesAlert.showAndWait();
        return result.get() == ButtonType.OK;
    }
}
