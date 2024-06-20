package com.snansidansi.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class LogController {
    @FXML
    BorderPane mainBorderPane;

    @FXML
    ToggleButton backupLogTButton;

    @FXML
    ToggleButton errorLogTButton;

    @FXML
    HBox toolButtonHBox;

    @FXML
    Spinner<Integer> textSizeSpinner;

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

        System.out.println("Show backup logs");
    }

    public void showErrorLogs() {
        if (!this.errorLogTButton.isSelected()) this.errorLogTButton.setSelected(true); // Disables deselection

        System.out.println("Show error logs");
    }
}
