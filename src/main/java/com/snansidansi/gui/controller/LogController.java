package com.snansidansi.gui.controller;

import com.snansidansi.gui.uielements.LogFileButton;
import com.snansidansi.gui.util.SceneManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

public class LogController {
    File currendDisplayedFile = null;
    Boolean deletePressedOnce = false;
    String lastSelectedExportPath = "";

    @FXML
    BorderPane mainBorderPane;

    @FXML
    ToggleButton backupLogTButton;

    @FXML
    ToggleButton errorLogTButton;

    @FXML
    Spinner<Integer> textSizeSpinner;

    @FXML
    VBox logFileListVBox;

    @FXML
    TextFlow logFileContentTextFlow;

    @FXML
    Label noLogFilesLabel;

    @FXML
    Label confirmDeleteLabel;

    @FXML
    public void initialize() {
        ToggleGroup logTypeTGroup = new ToggleGroup();
        this.errorLogTButton.setToggleGroup(logTypeTGroup);
        this.backupLogTButton.setToggleGroup(logTypeTGroup);

        setupTextSizeSpinner();

        this.noLogFilesLabel.setVisible(false);
        this.confirmDeleteLabel.setVisible(false);
    }

    private void setupTextSizeSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 14);
        this.textSizeSpinner.setValueFactory(valueFactory);

        this.textSizeSpinner.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException unused) {
                this.textSizeSpinner.getEditor().setText(oldValue);
            }
        });
    }

    public void showBackupLogs() {
        if (!this.backupLogTButton.isSelected()) {
            this.backupLogTButton.setSelected(true); // Disables deselection
        }
        listLogsAsLogFileButton("backup");
    }

    public void showErrorLogs() {
        if (!this.errorLogTButton.isSelected()) {
            this.errorLogTButton.setSelected(true); // Disables deselection
        }
        listLogsAsLogFileButton("error");
    }

    public void backToBackupConfigScene() throws IOException {
        Stage stage = (Stage) this.mainBorderPane.getScene().getWindow();
        SceneManager.setConfigureBackupsScene(stage);
    }

    private void listLogsAsLogFileButton(String logDir) {
        Path logDirPath = Path.of("log").resolve(logDir);
        FilenameFilter txtFilter = (dir, name) -> name.endsWith(".txt");
        File[] files = logDirPath.toFile().listFiles(txtFilter);

        var container = this.logFileListVBox.getChildren();
        container.clear();

        if (files.length == 0) {
            this.noLogFilesLabel.setVisible(true);
            container.add(this.noLogFilesLabel);
            return;
        }

        Arrays.sort(files, Comparator.comparing(File::getName).reversed());

        for (File file : files) {
            LogFileButton logFileButton = new LogFileButton(file.getName(), 14, 32);

            logFileButton.setOnMouseClicked((event) -> {
                if (this.currendDisplayedFile != null && this.currendDisplayedFile.equals(file)) {
                    return;
                }

                displayFileContent(file.toPath());
                this.currendDisplayedFile = file;
            });

            container.add(logFileButton);
        }
    }

    private void displayFileContent(Path filePath) {
        var container = this.logFileContentTextFlow.getChildren();
        container.clear();

        try {
            if (Files.size(filePath) > 2_000_000) {
                container.add(newTextWithSpinnerFontSize("File is to large to display."));
                return;
            }

            byte[] content = Files.readAllBytes(filePath);
            container.add(newTextWithSpinnerFontSize(new String(content, StandardCharsets.UTF_8)));
        } catch (OutOfMemoryError unused) {
            container.add(newTextWithSpinnerFontSize("File is too large to display."));
        } catch (IOException unused) {
            container.add(newTextWithSpinnerFontSize("Error when opening file."));
        }
    }

    private Text newTextWithSpinnerFontSize(String text) {
        Text message = new Text(text);
        message.styleProperty().bind(
                Bindings.concat("-fx-font-size: ", this.textSizeSpinner.valueProperty(), "px"));
        return message;
    }

    public void deleteLog() {
        if (this.currendDisplayedFile == null) {
            return;
        }
        this.confirmDeleteLabel.setVisible(false);

        if (!this.deletePressedOnce) {
            this.confirmDeleteLabel.setText("Press again to confirm deletion.");
            this.confirmDeleteLabel.setVisible(true);
            this.deletePressedOnce = true;
            return;
        }

        if (this.currendDisplayedFile.delete()) {
            this.logFileContentTextFlow.getChildren().clear();
            this.currendDisplayedFile = null;
            this.deletePressedOnce = false;

            if (this.backupLogTButton.isSelected()) {
                listLogsAsLogFileButton("backup");
            } else {
                listLogsAsLogFileButton("error");
            }
            return;
        }

        this.confirmDeleteLabel.setText("Could not delete file.");
        this.confirmDeleteLabel.setVisible(true);
        this.deletePressedOnce = false;
    }

    public void exportLog() {
        if (this.currendDisplayedFile == null) {
            return;
        }
        this.confirmDeleteLabel.setVisible(false);

        FileChooser destSelection = new FileChooser();
        destSelection.setTitle("Select an export location");
        if (!this.lastSelectedExportPath.isEmpty()) {
            destSelection.setInitialDirectory(new File(this.lastSelectedExportPath));
        }
        destSelection.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt-files (*.txt)", "*.txt"));
        destSelection.setInitialFileName(this.currendDisplayedFile.getName());

        File result = destSelection.showSaveDialog(new Stage());
        if (result == null) {
            return;
        }

        String exportPath = result.getAbsolutePath();
        if (!exportPath.endsWith(".txt")) {
            exportPath = exportPath + ".txt";
        }

        try {
            Files.copy(this.currendDisplayedFile.toPath(), Path.of(exportPath));
        } catch (IOException unused) {
            this.confirmDeleteLabel.setText("Could not export file.");
            this.confirmDeleteLabel.setVisible(true);
            return;
        }

        this.lastSelectedExportPath = Path.of(exportPath).getParent().toString();
    }
}
