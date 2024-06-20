package com.snansidansi.gui.controller;

import com.snansidansi.gui.uielements.LogFileButton;
import com.snansidansi.gui.util.SceneManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LogController {
    File currentFile = null;

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
    public void initialize() {
        ToggleGroup logTypeTGroup = new ToggleGroup();
        this.errorLogTButton.setToggleGroup(logTypeTGroup);
        this.backupLogTButton.setToggleGroup(logTypeTGroup);

        setupTextSizeSpinner();
    }

    private void setupTextSizeSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 14);
        this.textSizeSpinner.setValueFactory(valueFactory);

        this.textSizeSpinner.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException unused) {
                textSizeSpinner.getEditor().setText(oldValue);
            }
        });
    }

    public void showBackupLogs() {
        if (!this.backupLogTButton.isSelected()) this.backupLogTButton.setSelected(true); // Disables deselection
        listLogsAsLogFileButton("backup");
    }

    public void showErrorLogs() {
        if (!this.errorLogTButton.isSelected()) this.errorLogTButton.setSelected(true); // Disables deselection
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

        for (File file : files) {
            LogFileButton logFileButton = new LogFileButton(file.getName(), 14, 32);

            logFileButton.setOnMouseClicked((event) -> {
                if (this.currentFile != null && this.currentFile.equals(file)) return;

                displayFileContent(file.toPath());
                this.currentFile = file;
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
}
