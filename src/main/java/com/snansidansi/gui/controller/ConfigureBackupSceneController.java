package com.snansidansi.gui.controller;

import com.snansidansi.BackupServiceInstance;
import com.snansidansi.backup.exceptions.DestinationNoDirException;
import com.snansidansi.backup.exceptions.DestinationPathIsInSourcePathException;
import com.snansidansi.backup.exceptions.SourceDoesNotExistException;
import com.snansidansi.backup.exceptions.StringsAreEqualException;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.service.SrcDestPair;
import com.snansidansi.gui.util.TableEntry;
import com.snansidansi.gui.windows.AboutStage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
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
    private int numberOfTableElements = 0;

    @FXML
    private BorderPane mainContainer;

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

    @FXML
    public void initialize() {
        bindMiddleLineToWindowWidth();
        bindRemoveColToRight();

        deleteConfirmLabel.setVisible(false);
        invalidSrcPathLabel.setVisible(false);
        invalidDestPathLabel.setVisible(false);

        setupTable();
    }

    private void bindMiddleLineToWindowWidth() {
        middleLine.endXProperty().bind(mainContainer.widthProperty());
    }

    private void bindRemoveColToRight() {
        destinationTableCol.prefWidthProperty().bind(
                tableView.widthProperty()
                        .subtract(removeTableCol.getWidth())
                        .subtract(sourceTableCol.widthProperty())
                        .subtract(2)
        );
    }

    private void setupTable() {
        tableView.setPlaceholder(new Label("No backups found"));

        sourceTableCol.setCellValueFactory(
                new PropertyValueFactory<>("srcPath"));

        destinationTableCol.setCellValueFactory(
                new PropertyValueFactory<>("destPath"));

        removeTableCol.setCellValueFactory(
                new PropertyValueFactory<>("checkBoxBox"));

        for (SrcDestPair pathPair : BackupServiceInstance.backupService.getAllBackups()) {
            tableView.getItems().add(new TableEntry(pathPair.srcPath(), pathPair.destPath(), numberOfTableElements));
            numberOfTableElements++;
        }
    }

    public void openSrcFileSelection() {
        FileChooser srcSelection = new FileChooser();
        srcSelection.setTitle("Select source file");

        File selectedFile = srcSelection.showOpenDialog(new Stage());
        srcPathTextField.setText(selectedFile.getAbsolutePath());
        invalidSrcPathLabel.setVisible(false);
    }

    public void openSrcFolderSelection() {
        DirectoryChooser srcSelection = new DirectoryChooser();
        srcSelection.setTitle("Select source folder");

        File selectedFolder = srcSelection.showDialog(new Stage());
        srcPathTextField.setText(selectedFolder.getAbsolutePath());
        invalidSrcPathLabel.setVisible(false);
    }

    public void openDestSelection() {
        DirectoryChooser destSelection = new DirectoryChooser();
        destSelection.setTitle("Select destination folder");

        File selectedFolder = destSelection.showDialog(new Stage());
        destPathTextField.setText(selectedFolder.getAbsolutePath());
        invalidDestPathLabel.setVisible(false);
    }

    public void runBackup() {
        System.out.println("Run backup");
    }

    public void addBackup() {
        SrcDestPair pathPair = new SrcDestPair(srcPathTextField.getText(), destPathTextField.getText());

        try {
            BackupService.validateBackupPaths(pathPair);
        } catch (StringsAreEqualException unused) {
            invalidSrcPathLabel.setVisible(true);
            invalidDestPathLabel.setVisible(false);
            invalidSrcPathLabel.setText("Source and destination can't be the same.");
            return;
        } catch (SourceDoesNotExistException unused) {
            invalidSrcPathLabel.setVisible(true);
            invalidSrcPathLabel.setText("Source path does not exist.");
            return;
        } catch (DestinationNoDirException unused) {
            invalidDestPathLabel.setVisible(true);
            invalidSrcPathLabel.setVisible((false));
            invalidDestPathLabel.setText("Destination path is no directory.");
            return;
        } catch (DestinationPathIsInSourcePathException unused) {
            invalidDestPathLabel.setVisible(true);
            invalidSrcPathLabel.setVisible(false);
            invalidDestPathLabel.setText("Destination directory can't be in the source directory.");
        }

        if (srcPathTextField.getText().isBlank()) {
            invalidSrcPathLabel.setVisible(true);
            invalidSrcPathLabel.setText("Source path can't be empty.");
            return;
        } else if (destPathTextField.getText().isBlank()) {
            invalidDestPathLabel.setVisible(true);
            invalidSrcPathLabel.setVisible(false);
            invalidDestPathLabel.setText("Destination path can't be empty.");
            return;
        }

        if (BackupServiceInstance.backupService.checkIfBackupAlreadyExists(pathPair)) {
            invalidSrcPathLabel.setVisible(true);
            invalidSrcPathLabel.setText("Backup already exists.");
            invalidDestPathLabel.setVisible(false);
            return;
        }

        BackupServiceInstance.backupService.addBackup(pathPair);
        tableView.getItems().add(new TableEntry(pathPair.srcPath(), pathPair.destPath(), numberOfTableElements));
        numberOfTableElements++;

        invalidSrcPathLabel.setVisible(false);
        invalidDestPathLabel.setVisible(false);
    }

    public void deleteBackup() {
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

    public void toggleFullPath() {
        System.out.println("Toggle full path");
    }

    public void showAboutMessageBox() throws IOException {
        AboutStage.showWindow();
    }

    public void checkSrcPathInput() {
        if (!BackupService.validateSrcPath(srcPathTextField.getText())) {
            invalidSrcPathLabel.setVisible(true);
            invalidSrcPathLabel.setText("Source path does not exist");
            return;
        }
        invalidSrcPathLabel.setVisible(false);
    }

    public void checkDestPathInput() {
        if (!BackupService.validateDestPath(destPathTextField.getText())) {
            invalidDestPathLabel.setVisible(true);
            invalidDestPathLabel.setText("Destination path is no directory");
            return;
        }
        invalidDestPathLabel.setVisible(false);
    }
}
