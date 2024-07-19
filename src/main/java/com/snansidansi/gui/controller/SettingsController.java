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
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
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
    private static final SimpleDoubleProperty settingsRowNameHBoxWidth = new SimpleDoubleProperty(0);
    private static double stageWidth = 0;

    private final int SMALL_SPINNER_WIDTH = 80;
    private final int SETTINGS_NAME_FONTSIZE = 14;
    private final int TOOLTIP_FONTSIZE = 14;

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

        Platform.runLater(() -> {
            if (stageWidth == 0) {
                stageWidth = this.settingsVBox.getWidth() + 250;
            }

            Stage stage = (Stage) this.mainContainer.getScene().getWindow();
            SceneManager.changeStageSize(stage, stageWidth, 520);
            SceneManager.changeStageSizeBounds(stage, stageWidth, 0, stageWidth, -1);
        });
    }

    private void createSettingsContent() {
        final int HEADER_TOP_PADDING = 12;
        final int HEADER_BOTTOM_PADDING = 0;

        this.displayedSettings.clear();

        addSettingsHeader("Logging:", SETTINGS_NAME_FONTSIZE, true, true, 0, HEADER_BOTTOM_PADDING);
        addMaxNumberOfErrorLogsRow();
        addMaxNumberOfBackupLogsRow();

        addSettingsHeader("Retry failed backups:", SETTINGS_NAME_FONTSIZE, true, true, HEADER_TOP_PADDING, HEADER_BOTTOM_PADDING);
        addMaxNumberOfBackupRetriesRow();
        addDelayBetweenBackupRetriesRow();

        if (LocalBackupApp.runsFromExeFile) {
            addSettingsHeader("Automate backup:", SETTINGS_NAME_FONTSIZE, true, true, HEADER_TOP_PADDING, HEADER_BOTTOM_PADDING);
            SettingsRow addBackupExecutionToAutostartRow = addBackupExecutionToAutoStartRow();
            addAutostartPathRow(addBackupExecutionToAutostartRow);
        }

        addSettingsHeader("Backup:", SETTINGS_NAME_FONTSIZE, true, true, HEADER_TOP_PADDING, HEADER_BOTTOM_PADDING);
        addDeleteMissingFilesRow();

        addSettingsHeader("Tags:", SETTINGS_NAME_FONTSIZE, true, true,HEADER_TOP_PADDING,HEADER_BOTTOM_PADDING);
        addEnableTagsRow();
        addShowTagImageRow();
        addShowTagNameRow();
    }

    private void addShowTagNameRow() {
        CheckBoxSettingsRow showTagNameRow = new CheckBoxSettingsRow(
                BackupSetting.SHOW_TAG_NAME,
                "Show tag name:",
                this.SETTINGS_NAME_FONTSIZE);

        this.displayedSettings.add(showTagNameRow);
    }

    private void addShowTagImageRow() {
        CheckBoxSettingsRow showTagImageRow = new CheckBoxSettingsRow(
                BackupSetting.SHOW_TAG_IMAGE,
                "Show tag image:",
                this.SETTINGS_NAME_FONTSIZE);

        this.displayedSettings.add(showTagImageRow);
    }

    private void addEnableTagsRow() {
        CheckBoxSettingsRow enableTagsRow = new CheckBoxSettingsRow(
                BackupSetting.ENABLE_TAGS,
                "Enable tags:",
                this.SETTINGS_NAME_FONTSIZE);

        this.displayedSettings.add(enableTagsRow);
    }

    private void addDeleteMissingFilesRow() {
        CheckBoxSettingsRow deleteMissingFilesRow = new CheckBoxSettingsRow(
                BackupSetting.DELETE_BACKUP_FILES_WITH_MISSING_SRC,
                "Delete backup files with missing source files:",
                this.SETTINGS_NAME_FONTSIZE);

        deleteMissingFilesRow.addTooltip("This setting determines if a file at the backup location should be " +
                "deleted if the original file is deleted.\n" +
                "This counts for individual files, directories and files in directories.", this.TOOLTIP_FONTSIZE);

        this.displayedSettings.add(deleteMissingFilesRow);
    }

    private void addAutostartPathRow(SettingsRow enableAutostartRow) {
        TextFieldSettingsRow autostartPathRow = new TextFieldSettingsRow(
                BackupSetting.AUTOSTART_DIR_PATH,
                "Path of the autostart directory:",
                this.SETTINGS_NAME_FONTSIZE);
        autostartPathRow.setTextFieldWidth(250);

        TextField rowTextField = autostartPathRow.getTextField();

        ImageButton selectDirButton = new ImageButton("add_folder.png");
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

        autostartPathRow.addTooltip("""
                Only change this setting if the default version does not work!

                Here is how you can change this setting is necessary:
                Paste or select the path to the windows autostart directory in the text field.
                To find the autostart dir press "windows key + r ", write "shell:startup" and press enter.
                Then copy the path to the directory that the explorer opened.""", this.TOOLTIP_FONTSIZE);

    }

    private CheckBoxSettingsRow addBackupExecutionToAutoStartRow() {
        CheckBoxSettingsRow autostartRow = new CheckBoxSettingsRow(
                BackupSetting.ADDED_TO_AUTOSTART,
                "Add backup execution to autostart:",
                this.SETTINGS_NAME_FONTSIZE);

        this.displayedSettings.add(autostartRow);
        return autostartRow;
    }

    private void addDelayBetweenBackupRetriesRow() {
        SpinnerSettingsRow delayBetweenRetriesRow = new SpinnerSettingsRow(
                BackupSetting.DELAY_BETWEEN_BACKUP_RETRIES,
                "Delay between backup retries (seconds):",
                this.SETTINGS_NAME_FONTSIZE);
        delayBetweenRetriesRow.setSpinnerWidth(this.SMALL_SPINNER_WIDTH);

        delayBetweenRetriesRow.addTooltip("How long should be waited between the backup retries if the root" +
                " directory of a backup is missing (e.g. a drive is not connected)", TOOLTIP_FONTSIZE);

        this.displayedSettings.add(delayBetweenRetriesRow);
    }

    private void addMaxNumberOfBackupRetriesRow() {
        SpinnerSettingsRow numberOfRetriesRow = new SpinnerSettingsRow(
                BackupSetting.NUMBER_OF_BACKUP_RETRIES,
                "Max number of backup retries:",
                this.SETTINGS_NAME_FONTSIZE);
        numberOfRetriesRow.setSpinnerWidth(this.SMALL_SPINNER_WIDTH);

        numberOfRetriesRow.addTooltip("How often should the program retry to backup a file or directory where the" +
                " root directory is missing (e.g. a drive is not connected).", TOOLTIP_FONTSIZE);

        this.displayedSettings.add(numberOfRetriesRow);
    }

    private void addMaxNumberOfBackupLogsRow() {
        SpinnerSettingsRow maxBackupLogsRow = new SpinnerSettingsRow(
                BackupSetting.MAX_BACKUP_LOGS,
                "Max number of backup logs:",
                this.SETTINGS_NAME_FONTSIZE);
        maxBackupLogsRow.setSpinnerWidth(this.SMALL_SPINNER_WIDTH);

        this.displayedSettings.add(maxBackupLogsRow);
    }

    private void addMaxNumberOfErrorLogsRow() {
        SpinnerSettingsRow maxErrorLogsRow = new SpinnerSettingsRow(
                BackupSetting.MAX_ERROR_LOGS,
                "Max number of error logs:",
                this.SETTINGS_NAME_FONTSIZE);
        maxErrorLogsRow.setSpinnerWidth(this.SMALL_SPINNER_WIDTH);

        this.displayedSettings.add(maxErrorLogsRow);
    }

    private void updateSettingsNameLabelWidth(SettingsRow settingsRow) {
        double labelWidth = settingsRow.getNameLabel().getWidth();
        if (labelWidth > settingsRowNameHBoxWidth.get()) {
            settingsRowNameHBoxWidth.set(labelWidth + 10);
        }
        settingsRow.getNameLabel().minWidthProperty().bind(settingsRowNameHBoxWidth);
    }

    private void displaySettingsRows() {
        this.settingsVBox.getChildren().clear();
        this.settingsVBox.getChildren().addAll(this.displayedSettings);

        for (Node node : this.displayedSettings) {
            if (node instanceof SettingsRow row) {
                Platform.runLater(() -> {
                    updateSettingsNameLabelWidth(row);
                });
            }
        }
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
        for (Node node : this.displayedSettings) {
            if (node instanceof SettingsRow row) {
                row.setInitValue(SettingsManagerInstance.settingsManager.getSetting(row.getSetting()));
                row.restoreStandardValue();
            }
        }
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
        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
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
