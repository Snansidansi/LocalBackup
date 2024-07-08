package com.snansidansi.gui.controller;

import com.snansidansi.app.LocalBackupApp;
import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.gui.uielements.buttons.ImageButton;
import com.snansidansi.gui.uielements.settingsrow.CheckBoxSettingsRow;
import com.snansidansi.gui.uielements.settingsrow.SettingsRow;
import com.snansidansi.gui.uielements.settingsrow.SpinnerSettingsRow;
import com.snansidansi.gui.uielements.settingsrow.TextFieldSettingsRow;
import com.snansidansi.gui.util.SceneManager;
import com.snansidansi.settings.BackupSetting;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SettingsController {
    private final List<Node> displayedSettings = new ArrayList<>();
    private boolean invalidSettings = false;

    @FXML
    VBox mainContainer;

    @FXML
    ScrollPane settingsScrollPane;

    @FXML
    VBox settingsVBox;

    @FXML
    public void initialize() {
        createSettingsContent();
        displaySettingsRows();
    }

    private void createSettingsContent() {
        var widthProperty = this.settingsScrollPane.widthProperty();
        final int FONTSIZE = 14;
        final int TOOLTIP_FONTSIZE = 14;
        final int HEADER_TOP_PADDING = 12;
        final int HEADER_BOTTOM_PADDING = 0;

        this.displayedSettings.clear();

        addSettingsHeader("Logging:", FONTSIZE, true, true, 0, HEADER_BOTTOM_PADDING);
        addMaxNumberOfErrorLogsRow(FONTSIZE, widthProperty);
        addMaxNumberOfBackupLogsRow(FONTSIZE, widthProperty);

        addSettingsHeader("Retry failed backups:", FONTSIZE, true, true, HEADER_TOP_PADDING, HEADER_BOTTOM_PADDING);
        addMaxNumberOfBackupRetriesRow(FONTSIZE, widthProperty, TOOLTIP_FONTSIZE);
        addDelayBetweenBackupRetriesRow(FONTSIZE, widthProperty, TOOLTIP_FONTSIZE);

        if (LocalBackupApp.runsFromExeFile) {
            addSettingsHeader("Automate backup:", FONTSIZE, true, true, HEADER_TOP_PADDING, HEADER_BOTTOM_PADDING);
            SettingsRow addBackupExecutionToAutostartRow = addBackupExecutionToAutoStartRow(FONTSIZE, widthProperty);
            addAutostartPathRow(FONTSIZE, widthProperty, addBackupExecutionToAutostartRow, TOOLTIP_FONTSIZE);
        }

        addSettingsHeader("Backup:", FONTSIZE, true, true, HEADER_TOP_PADDING, HEADER_BOTTOM_PADDING);
        addDeleteMissingFilesRow(FONTSIZE, widthProperty, TOOLTIP_FONTSIZE);
    }

    private void addDeleteMissingFilesRow(int fontSize, ReadOnlyDoubleProperty widthProperty, int tooltipFontSize) {
        CheckBoxSettingsRow deleteMissingFilesRow = new CheckBoxSettingsRow(
                BackupSetting.DELETE_BACKUP_FILES_WITH_MISSING_SRC,
                "Delete backup files with missing source files:",
                fontSize,
                widthProperty);

        deleteMissingFilesRow.addTooltip("This setting determines if a file at the backup location should be " +
                "deleted if the original file is deleted.\n" +
                "This counts for individual files, directories and files in directories.", tooltipFontSize);

        this.displayedSettings.add(deleteMissingFilesRow);
    }

    private void addAutostartPathRow(int fontSize, ReadOnlyDoubleProperty widthProperty, SettingsRow enableAutostartRow,
                                     int tooltipFontSize) {
        TextFieldSettingsRow autostartPathRow = new TextFieldSettingsRow(
                BackupSetting.AUTOSTART_DIR_PATH,
                "Path of the autostart directory:",
                fontSize,
                widthProperty);
        autostartPathRow.setTextFieldWidth(250);

        TextField rowTextField = autostartPathRow.getTextField();

        ImageButton selectDirButton = new ImageButton("openedFolder.png");
        selectDirButton.setImageSize(17, 17);
        selectDirButton.setOnMouseClicked(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select autostart directory");
            directoryChooser.setInitialDirectory(new File(System.getenv("APPDATA")));

            File result = directoryChooser.showDialog(new Stage());
            if (result == null) {
                return;
            }

            rowTextField.setText(result.toString());
        });

        rowTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (Files.exists(Path.of(newValue))) {
                enableAutostartRow.setDisable(false);
                rowTextField.setStyle("-fx-text-fill: black");
                this.invalidSettings = false;
                return;
            }

            enableAutostartRow.setDisable(true);
            rowTextField.setStyle("-fx-text-fill: red");
            this.invalidSettings = true;
        }));

        autostartPathRow.getControlHBox().setSpacing(8);
        autostartPathRow.getControlHBox().getChildren().add(selectDirButton);

        autostartPathRow.addTooltip("Only change this setting if the default version does not work!\n\n" +
                "Here is how you can change this setting is necessary:\n" +
                "Paste or select the path to the windows autostart directory in the text field.\n" +
                "To find the autostart dir press \"windows key + r \", write \"shell:startup\" and press enter.\n" +
                "Then copy the path to the directory that the explorer opened.", tooltipFontSize);

        this.displayedSettings.add(autostartPathRow);
    }

    private CheckBoxSettingsRow addBackupExecutionToAutoStartRow(int fontSize, ReadOnlyDoubleProperty widthProperty) {
        CheckBoxSettingsRow autostartRow = new CheckBoxSettingsRow(
                BackupSetting.ADDED_TO_AUTOSTART,
                "Add backup execution to autostart:",
                fontSize,
                widthProperty);

        this.displayedSettings.add(autostartRow);
        return autostartRow;
    }

    private void addDelayBetweenBackupRetriesRow(int FONTSIZE, ReadOnlyDoubleProperty widthProperty, int TOOLTIP_FONTSIZE) {
        SpinnerSettingsRow delayBetweenRetriesRow = new SpinnerSettingsRow(
                BackupSetting.DELAY_BETWEEN_BACKUP_RETRIES,
                "Delay between backup retries (seconds):",
                FONTSIZE,
                widthProperty);
        delayBetweenRetriesRow.addTooltip("How long should be waited between the backup retries if the root" +
                " directory of a backup is missing (e.g. a drive is not connected)", TOOLTIP_FONTSIZE);
        this.displayedSettings.add(delayBetweenRetriesRow);
    }

    private void addMaxNumberOfBackupRetriesRow(int FONTSIZE, ReadOnlyDoubleProperty widthProperty, int TOOLTIP_FONTSIZE) {
        SpinnerSettingsRow numberOfRetriesRow = new SpinnerSettingsRow(
                BackupSetting.NUMBER_OF_BACKUP_RETRIES,
                "Max number of backup retries:",
                FONTSIZE,
                widthProperty);

        numberOfRetriesRow.addTooltip("How often should the program retry to backup a file or directory where the" +
                " root directory is missing (e.g. a drive is not connected).", TOOLTIP_FONTSIZE);
        this.displayedSettings.add(numberOfRetriesRow);
    }

    private void addMaxNumberOfBackupLogsRow(int FONTSIZE, ReadOnlyDoubleProperty widthProperty) {
        this.displayedSettings.add(new SpinnerSettingsRow(
                BackupSetting.MAX_BACKUP_LOGS,
                "Max number of backup logs:",
                FONTSIZE,
                widthProperty));
    }

    private void addMaxNumberOfErrorLogsRow(int FONTSIZE, ReadOnlyDoubleProperty widthProperty) {
        this.displayedSettings.add(new SpinnerSettingsRow(
                BackupSetting.MAX_ERROR_LOGS,
                "Max number of error logs:",
                FONTSIZE,
                widthProperty));
    }

    private void displaySettingsRows() {
        this.settingsVBox.getChildren().clear();
        this.settingsVBox.getChildren().addAll(this.displayedSettings);
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
        if (!showConfirmAlert("Restore default Settings", "Are you sure to restore the default settings?")) {
            return;
        }

        SettingsManagerInstance.settingsManager.restoreDefaults();
        SettingsManagerInstance.reloadSettings();
        createSettingsContent();
        displaySettingsRows();
    }

    public void discardChanges() {
        for (Node settingsContent : this.displayedSettings) {
            if (settingsContent instanceof SettingsRow settingsRow) {
                settingsRow.restoreStandardValue();
            }
        }
    }

    public void saveChanges() {
        if (!settingsChanged()) {
            return;
        }
        if (this.invalidSettings) {
            showErrorAlert("Invalid Settings", "Please fix the invalid settings (marked red) before" +
                    " saving.");
            return;
        }

        for (Node settingsContent : this.displayedSettings) {
            if (settingsContent instanceof SettingsRow settingsRow) {
                SettingsManagerInstance.settingsManager.changeSetting(settingsRow.getSetting(), settingsRow.getValue());
                settingsRow.setInitValue(settingsRow.getValue());
            }
        }

        if (!SettingsManagerInstance.settingsManager.applyChanges()) {
            return;
        }
        SettingsManagerInstance.reloadSettings();
        createSettingsContent();
        displaySettingsRows();
    }

    private boolean settingsChanged() {
        for (Node settingsContent : this.displayedSettings) {
            if (settingsContent instanceof SettingsRow settingsRow) {
                if (!settingsRow.getValue().equals(settingsRow.getInitValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean showConfirmAlert(String title, String contentText) {
        Alert confrimAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confrimAlert.setTitle(title);
        confrimAlert.setHeaderText(contentText);

        Optional<ButtonType> result = confrimAlert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    private void showErrorAlert(String title, String contentText) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(contentText);
        errorAlert.showAndWait();
    }

    private void addSettingsHeader(String text, int textSize, boolean underline, boolean bold,
                                   int topPadding, int bottomPadding) {

        Label label = new Label(text);
        String fxBold = bold ? "bold" : "normal";
        String style = String.format("-fx-underline: %b; " +
                        "-fx-font-weight: %s; " +
                        "-fx-padding: %d 0 %d 0; " +
                        "-fx-font-size: %d",
                underline, fxBold, topPadding, bottomPadding, textSize);
        label.setStyle(style);

        this.displayedSettings.add(label);
    }
}
