package com.snansidansi.gui.controller;

import com.snansidansi.BackupServiceInstance;
import com.snansidansi.gui.util.TableEntry;
import com.snansidansi.gui.windows.AboutStage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigureBackupSceneController {
    private boolean deletePressedOnce = false;

    @FXML
    private Line middleLine;

    @FXML
    private TableView<TableEntry> tableView;

    @FXML
    private TableColumn<TableEntry, String> sourceTableCol;

    @FXML
    private TableColumn<TableEntry, String> destinationTableCol;

    @FXML
    private TableColumn<TableEntry, HBox> removeTableCol;

    @FXML
    private Label deleteConfirmLabel;

    public Label getDeleteConfirmLabel() {
        return deleteConfirmLabel;
    }

    public TableView<TableEntry> getTableView() {
        return tableView;
    }

    public TableColumn<TableEntry, String> getSourceTableCol() {
        return sourceTableCol;
    }

    public TableColumn<TableEntry, String> getDestinationTableCol() {
        return destinationTableCol;
    }

    public TableColumn<TableEntry, HBox> getRemoveTableCol() {
        return removeTableCol;
    }

    public void bindMiddleLineToWindowWidth(Scene scene) {
        middleLine.endXProperty().bind(scene.widthProperty());
    }

    public void bindRemoveColToRight() {
        destinationTableCol.prefWidthProperty().bind(
                tableView.widthProperty()
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
        if (!deletePressedOnce) {
            deleteConfirmLabel.setVisible(true);
            deletePressedOnce = true;
            return;
        }

        List<Integer> indicesToRemove = new ArrayList<>();
        ObservableList<TableEntry> selectedItems = FXCollections.observableArrayList();
        for (TableEntry entry : tableView.getItems()) {
            if (entry.getCheckBox().isSelected()) {
                indicesToRemove.add(entry.getIndex());
                selectedItems.add(entry);
            }
        }

        BackupServiceInstance.backupService.removeBackup(indicesToRemove.stream().mapToInt(i -> i).toArray());
        tableView.getItems().removeAll(selectedItems);

        deleteConfirmLabel.setVisible(false);
        deletePressedOnce = false;
    }

    public void toggleFullPath(ActionEvent e) {
        System.out.println("Toggle full path");
    }

    public void showAboutMessageBox(ActionEvent e) throws IOException {
        AboutStage.showWindow();
    }
}
