package com.snansidansi.gui.controller;

import com.snansidansi.BackupServiceInstance;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.service.DestinationNoDirException;
import com.snansidansi.backup.service.SourceDoesNotExistException;
import com.snansidansi.backup.service.SrcDestPair;
import com.snansidansi.gui.scenes.ConfigureBackupScene;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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

    @FXML
    private TextField srcPathTextField;

    @FXML
    private TextField destPathTextField;

    @FXML
    private Label invalidSrcPathLabel;

    @FXML
    private Label invalidDestPathLabel;

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

    public Label getInvalidSrcPathLabel() {
        return invalidSrcPathLabel;
    }

    public Label getInvalidDestPathLabel() {
        return invalidDestPathLabel;
    }

    public void bindMiddleLineToWindowWidth(Scene scene) {
        middleLine.endXProperty().bind(scene.widthProperty());
    }

    public void bindRemoveColToRight() {
        destinationTableCol.prefWidthProperty().bind(
                tableView.widthProperty()
                        .subtract(removeTableCol.getWidth())
                        .subtract(sourceTableCol.widthProperty())
                        .subtract(2)
        );
    }

    public void openSrcFileSelection(ActionEvent e) {
        FileChooser srcSelection = new FileChooser();
        srcSelection.setTitle("Select source file");

        File selectedFile = srcSelection.showOpenDialog(new Stage());
        srcPathTextField.setText(selectedFile.getAbsolutePath());
    }

    public void openSrcFolderSelection(ActionEvent e) {
        DirectoryChooser srcSelection = new DirectoryChooser();
        srcSelection.setTitle("Select source folder");

        File selectedFolder = srcSelection.showDialog(new Stage());
        srcPathTextField.setText(selectedFolder.getAbsolutePath());
    }

    public void openDestSelection(ActionEvent e) {
        DirectoryChooser destSelection = new DirectoryChooser();
        destSelection.setTitle("Select destination folder");

        File selectedFolder = destSelection.showDialog(new Stage());
        destPathTextField.setText(selectedFolder.getAbsolutePath());
    }

    public void runBackup(ActionEvent e) {
        System.out.println("Run backup");
    }

    public void addBackup(ActionEvent e) {
        SrcDestPair pathPair = new SrcDestPair(srcPathTextField.getText(), destPathTextField.getText());

        try {
            BackupService.validateBackupPaths(pathPair);
        } catch (SourceDoesNotExistException unused) {
            invalidSrcPathLabel.setVisible(true);
            invalidSrcPathLabel.setText("Source path does not exist");
            return;
        } catch (DestinationNoDirException unused) {
            invalidDestPathLabel.setVisible(true);
            invalidDestPathLabel.setText("Destination path is no directory");
            return;
        }

        BackupServiceInstance.backupService.addBackup(pathPair);
        tableView.getItems().add(new TableEntry(pathPair.srcPath(), pathPair.destPath(), ConfigureBackupScene.addTableElementsNum()));

        invalidSrcPathLabel.setVisible(false);
        invalidDestPathLabel.setVisible(false);
        srcPathTextField.setText("");
        destPathTextField.setText("");
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
