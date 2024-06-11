package com.snansidansi.gui.controller;

import com.snansidansi.gui.windows.AboutStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.shape.Line;

import java.io.IOException;

public class ConfigureBackupSceneController {
    @FXML
    private Line middleLine;

    @FXML
    private TableView<?> table;

    @FXML
    private TableColumn<?, ?> sourceTableCol;

    @FXML
    private TableColumn<?, ?> destinationTableCol;

    @FXML
    private TableColumn<?, ?> removeTableCol;

    public void bindMiddleLineToWindowWidth(Scene scene) {
        middleLine.endXProperty().bind(scene.widthProperty());
    }

    public void bindRemoveColToRight() {
        destinationTableCol.prefWidthProperty().bind(
                table.widthProperty()
                        .subtract(removeTableCol.getWidth())
                        .subtract(sourceTableCol.widthProperty())
        );
    }

    public void openSrcSelection(ActionEvent e) {
        System.out.println("Open src selection");
    }

    public void openDestSelection(ActionEvent e) {
        System.out.println("Open dest selection");
    }

    public void runBackup(ActionEvent e) {
        System.out.println("Run backup");
    }

    public void addBackup(ActionEvent e) {
        System.out.println("Add backup");
    }

    public void deleteBackup(ActionEvent e) {
        System.out.println("Delete Backup");
    }

    public void toggleFullPath(ActionEvent e) {
        System.out.println("Toggle full path");
    }

    public void showAboutMessageBox(ActionEvent e) throws IOException {
        AboutStage.showWindow();
    }
}
