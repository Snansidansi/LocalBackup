package com.snansidansi.gui.controller;

import com.snansidansi.backup.exceptions.DestinationNoDirException;
import com.snansidansi.backup.exceptions.DestinationPathIsInSourcePathException;
import com.snansidansi.backup.exceptions.SourceDoesNotExistException;
import com.snansidansi.backup.exceptions.StringsAreEqualException;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.util.SrcDestPair;
import com.snansidansi.gui.util.TableEntry;
import com.snansidansi.gui.windows.AboutWindow;
import com.snansidansi.singletons.BackupServiceInstance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
    private CheckBox showFullPathsCheckBox;

    @FXML
    public void initialize() {
        bindMiddleLineToWindowWidth();
        bindRemoveColToRight();

        this.deleteConfirmLabel.setVisible(false);
        this.invalidSrcPathLabel.setVisible(false);
        this.invalidDestPathLabel.setVisible(false);

        setupTable();
    }

    private void bindMiddleLineToWindowWidth() {
        this.middleLine.endXProperty().bind(this.mainContainer.widthProperty());
    }

    private void bindRemoveColToRight() {
        this.destinationTableCol.prefWidthProperty().bind(
                this.tableView.widthProperty()
                        .subtract(this.removeTableCol.getWidth())
                        .subtract(this.sourceTableCol.widthProperty())
                        .subtract(2)
        );
    }

    private void setupTable() {
        this.tableView.setPlaceholder(new Label("No backups found"));

        this.sourceTableCol.setCellValueFactory(
                new PropertyValueFactory<>("srcPath"));

        this.destinationTableCol.setCellValueFactory(
                new PropertyValueFactory<>("destPath"));

        this.removeTableCol.setCellValueFactory(
                new PropertyValueFactory<>("checkBoxHBox"));

        refillTable(true, true);
    }

    private void refillTable(boolean fullPath, boolean changedValues) {
        this.numberOfTableElements = 0;
        List<Integer> checkedElements = null;

        if (!changedValues) checkedElements = getCheckedElementsFromTable();
        this.tableView.getItems().clear();

        for (SrcDestPair pathPair : BackupServiceInstance.backupService.getAllBackups()) {
            Path srcPath = Path.of(pathPair.srcPath());
            Path destPath = Path.of(pathPair.destPath());

            if (!fullPath) {
                if (srcPath.getFileName() != null) srcPath = srcPath.getFileName();
                if (destPath.getFileName() != null) destPath = destPath.getFileName();
            }

            boolean checked = false;
            if (!changedValues && !checkedElements.isEmpty() && this.numberOfTableElements == checkedElements.getFirst()) {
                checkedElements.removeFirst();
                checked = true;
            }

            this.tableView.getItems().add(
                    new TableEntry(srcPath.toString(), destPath.toString(), this.numberOfTableElements, checked));
            this.numberOfTableElements++;
        }
    }

    private List<Integer> getCheckedElementsFromTable() {
        List<Integer> indicesToRemove = new ArrayList<>();
        for (TableEntry entry : this.tableView.getItems()) {
            if (entry.getCheckBox().isSelected()) {
                indicesToRemove.add(entry.getIndex());
            }
        }
        return indicesToRemove;
    }

    public void openSrcFileSelection() {
        FileChooser srcSelection = new FileChooser();
        srcSelection.setTitle("Select source file");

        File selectedFile = srcSelection.showOpenDialog(new Stage());
        this.srcPathTextField.setText(selectedFile.getAbsolutePath());
        this.invalidSrcPathLabel.setVisible(false);
    }

    public void openSrcFolderSelection() {
        DirectoryChooser srcSelection = new DirectoryChooser();
        srcSelection.setTitle("Select source folder");

        File selectedFolder = srcSelection.showDialog(new Stage());
        this.srcPathTextField.setText(selectedFolder.getAbsolutePath());
        this.invalidSrcPathLabel.setVisible(false);
    }

    public void openDestSelection() {
        DirectoryChooser destSelection = new DirectoryChooser();
        destSelection.setTitle("Select destination folder");

        File selectedFolder = destSelection.showDialog(new Stage());
        this.destPathTextField.setText(selectedFolder.getAbsolutePath());
        this.invalidDestPathLabel.setVisible(false);
    }

    public void runBackup() {
        BackupServiceInstance.backupService.runBackup();
    }

    public void addBackup() {
        SrcDestPair pathPair = new SrcDestPair(this.srcPathTextField.getText(), this.destPathTextField.getText());

        try {
            BackupService.validateBackupPaths(pathPair);
        } catch (StringsAreEqualException unused) {
            this.invalidSrcPathLabel.setVisible(true);
            this.invalidDestPathLabel.setVisible(false);
            this.invalidSrcPathLabel.setText("Source and destination can't be the same.");
            return;
        } catch (SourceDoesNotExistException unused) {
            this.invalidSrcPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setText("Source path does not exist.");
            return;
        } catch (DestinationNoDirException unused) {
            this.invalidDestPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setVisible((false));
            this.invalidDestPathLabel.setText("Destination path is no directory.");
            return;
        } catch (DestinationPathIsInSourcePathException unused) {
            this.invalidDestPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setVisible(false);
            this.invalidDestPathLabel.setText("Destination directory can't be in the source directory.");
        }

        if (srcPathTextField.getText().isBlank()) {
            this.invalidSrcPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setText("Source path can't be empty.");
            return;
        } else if (destPathTextField.getText().isBlank()) {
            this.invalidDestPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setVisible(false);
            this.invalidDestPathLabel.setText("Destination path can't be empty.");
            return;
        }

        if (BackupServiceInstance.backupService.checkIfBackupAlreadyExists(pathPair)) {
            this.invalidSrcPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setText("Backup already exists.");
            this.invalidDestPathLabel.setVisible(false);
            return;
        }

        if (!BackupServiceInstance.backupService.addBackup(pathPair)) {
            this.invalidSrcPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setText("Error: Backup could not be added (view log)");
            return;
        }
        this.tableView.getItems().add(new TableEntry(pathPair.srcPath(), pathPair.destPath(), this.numberOfTableElements, false));
        numberOfTableElements++;

        this.invalidSrcPathLabel.setVisible(false);
        this.invalidDestPathLabel.setVisible(false);
    }

    public void deleteBackup() {
        if (!this.deletePressedOnce) {
            this.deleteConfirmLabel.setVisible(true);
            this.deletePressedOnce = true;
            return;
        }

        List<Integer> indicesToRemove = getCheckedElementsFromTable();
        BackupServiceInstance.backupService.removeBackup(indicesToRemove.stream().mapToInt(i -> i).toArray());

        refillTable(this.showFullPathsCheckBox.isSelected(), true);

        this.deleteConfirmLabel.setVisible(false);
        this.deletePressedOnce = false;
    }

    public void toggleFullPath() {
        refillTable(this.showFullPathsCheckBox.isSelected(), false);
    }

    public void showAboutMessageBox() throws IOException {
        AboutWindow.showWindow();
    }

    public void checkSrcPathInput() {
        if (!BackupService.validateSrcPath(this.srcPathTextField.getText())) {
            this.invalidSrcPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setText("Source path does not exist");
            return;
        }
        this.invalidSrcPathLabel.setVisible(false);
    }

    public void checkDestPathInput() {
        if (!BackupService.validateDestPath(this.destPathTextField.getText())) {
            this.invalidDestPathLabel.setVisible(true);
            this.invalidDestPathLabel.setText("Destination path is no directory");
            return;
        }
        this.invalidDestPathLabel.setVisible(false);
    }
}
