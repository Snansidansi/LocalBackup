package com.snansidansi.gui.controller;

import com.snansidansi.app.instances.BackupServiceInstance;
import com.snansidansi.app.singletons.RunBackupThreadSingleton;
import com.snansidansi.backup.exceptions.DestinationNoDirException;
import com.snansidansi.backup.exceptions.DestinationPathIsInSourcePathException;
import com.snansidansi.backup.exceptions.SourceDoesNotExistException;
import com.snansidansi.backup.exceptions.StringsAreEqualException;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.util.SrcDestPair;
import com.snansidansi.gui.util.SceneManager;
import com.snansidansi.gui.util.TableEntry;
import com.snansidansi.gui.windows.AboutWindow;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigureBackupSceneController {
    private boolean deletePressedOnce = false;
    private int numberOfTableElements = 0;
    private String lastSelectedSrcDirPath = "";
    private String lastSelectedDestDirPath = "";

    @FXML
    private BorderPane mainContainer;

    @FXML
    private Line middleLine;

    @FXML
    private TableView<TableEntry> tableView;

    @FXML
    private TableColumn<TableEntry, HBox> sourceTableCol;

    @FXML
    private TableColumn<TableEntry, String> destinationTableCol;

    @FXML
    private TableColumn<TableEntry, HBox> removeTableCol;

    @FXML
    private TableColumn<TableEntry, HBox> tagTableCol;

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
    private Label backupRunningIndicatorLabel;

    @FXML
    private Label backupFinishedLabel;

    @FXML
    public void initialize() {
        bindMiddleLineToWindowWidth();

        this.deleteConfirmLabel.setVisible(false);
        this.invalidSrcPathLabel.setVisible(false);
        this.invalidDestPathLabel.setVisible(false);
        this.backupRunningIndicatorLabel.setVisible(false);
        this.backupFinishedLabel.setVisible(false);

        setupTable();
        Platform.runLater(this::setupTableColumnSizeProperties);

        RotateTransition loadingAnimation = createLoadingAnimation();
        RunBackupThreadSingleton.setAnimation(loadingAnimation, this.backupRunningIndicatorLabel);
        RunBackupThreadSingleton.setFinishedLabel(this.backupFinishedLabel);
        RunBackupThreadSingleton.setConfigureBackupSceneController(this);
    }

    private void bindMiddleLineToWindowWidth() {
        this.middleLine.endXProperty().bind(this.mainContainer.widthProperty());
    }

    private void setupTableColumnSizeProperties() {
        this.sourceTableCol.setReorderable(false);
        this.destinationTableCol.setReorderable(false);
        this.tagTableCol.setReorderable(false);
        this.removeTableCol.setReorderable(false);
    }

    private void setupTable() {
        this.tableView.setPlaceholder(new Label("No backups found"));

        this.sourceTableCol.setCellValueFactory(
                new PropertyValueFactory<>("srcHBox"));

        this.destinationTableCol.setCellValueFactory(
                new PropertyValueFactory<>("destPath"));

        this.removeTableCol.setCellValueFactory(
                new PropertyValueFactory<>("checkBoxHBox"));

        this.tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                this.tableView.refresh();
            }
        });

        refillTable(true);
    }

    public void refillTable(boolean changedValues) {
        this.numberOfTableElements = 0;
        List<Integer> checkedElements = null;

        if (!changedValues) {
            checkedElements = getCheckedElementsFromTable();
        }
        this.tableView.getItems().clear();

        for (SrcDestPair pathPair : BackupServiceInstance.backupService.getAllBackups()) {
            boolean srcIsDir = Files.isDirectory(Path.of(pathPair.srcPath()));
            pathPair = adjustSrcDestPairToPathMode(pathPair);
            boolean checked = false;

            if (!changedValues && !checkedElements.isEmpty() && this.numberOfTableElements == checkedElements.getFirst()) {
                checkedElements.removeFirst();
                checked = true;
            }

            this.tableView.getItems().add(
                    new TableEntry(pathPair.srcPath(), pathPair.destPath(), this.numberOfTableElements, checked, srcIsDir));
            this.numberOfTableElements++;
        }
    }

    private SrcDestPair adjustSrcDestPairToPathMode(SrcDestPair pathPair) {
        Path srcPath = Path.of(pathPair.srcPath());
        Path destPath = Path.of(pathPair.destPath());

        if (!this.showFullPathsCheckBox.isSelected()) {
            if (srcPath.getFileName() != null) {
                srcPath = srcPath.getFileName();
            }
            if (destPath.getFileName() != null) {
                destPath = destPath.getFileName();
            }
        }

        return new SrcDestPair(srcPath.toString(), destPath.toString());
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

    private RotateTransition createLoadingAnimation() {
        RotateTransition loadingAnimation = new RotateTransition();
        loadingAnimation.setNode(this.backupRunningIndicatorLabel);
        loadingAnimation.setCycleCount(Animation.INDEFINITE);
        loadingAnimation.setToAngle(360);
        loadingAnimation.setDuration(Duration.seconds(1.25));
        loadingAnimation.setInterpolator(Interpolator.LINEAR);

        return loadingAnimation;
    }

    public void openSrcFileSelection() {
        FileChooser srcSelection = new FileChooser();
        srcSelection.setTitle("Select source file");
        if (!this.lastSelectedSrcDirPath.isEmpty()) {
            srcSelection.setInitialDirectory(new File(lastSelectedSrcDirPath));
        }

        File selectedFile = srcSelection.showOpenDialog(new Stage());
        if (selectedFile == null) {
            return;
        }

        this.lastSelectedSrcDirPath = selectedFile.getParent();
        this.srcPathTextField.setText(selectedFile.getAbsolutePath());
        this.invalidSrcPathLabel.setVisible(false);
    }

    public void openSrcFolderSelection() {
        DirectoryChooser srcSelection = new DirectoryChooser();
        srcSelection.setTitle("Select source folder");
        if (!this.lastSelectedSrcDirPath.isEmpty()) {
            srcSelection.setInitialDirectory(new File(lastSelectedSrcDirPath));
        }

        File selectedFolder = srcSelection.showDialog(new Stage());
        if (selectedFolder == null) {
            return;
        }

        this.lastSelectedSrcDirPath = selectedFolder.getPath();
        this.srcPathTextField.setText(selectedFolder.getAbsolutePath());
        this.invalidSrcPathLabel.setVisible(false);
    }

    public void openDestSelection() {
        DirectoryChooser destSelection = new DirectoryChooser();
        destSelection.setTitle("Select destination folder");
        if (!this.lastSelectedDestDirPath.isEmpty()) {
            destSelection.setInitialDirectory(new File(lastSelectedDestDirPath));
        }

        File selectedFolder = destSelection.showDialog(new Stage());
        if (selectedFolder == null) {
            return;
        }

        this.lastSelectedDestDirPath = selectedFolder.getPath();
        this.destPathTextField.setText(selectedFolder.getAbsolutePath());
        this.invalidDestPathLabel.setVisible(false);
    }

    public void runBackup() {
        if (RunBackupThreadSingleton.isAlive()) {
            return;
        }

        this.backupFinishedLabel.setVisible(false);
        this.backupRunningIndicatorLabel.setVisible(true);
        RunBackupThreadSingleton.start();
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

        if (RunBackupThreadSingleton.isAlive()) {
            this.invalidSrcPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setText("Please wait for running backup to finish.");
            this.invalidDestPathLabel.setVisible(false);
            return;
        }

        if (!BackupServiceInstance.backupService.addBackup(pathPair)) {
            this.invalidSrcPathLabel.setVisible(true);
            this.invalidSrcPathLabel.setText("Error: Backup could not be added (view log)");
            return;
        }

        boolean srcIsDir = Files.isDirectory(Path.of(pathPair.srcPath()));
        pathPair = adjustSrcDestPairToPathMode(pathPair);
        this.tableView.getItems().add(
                new TableEntry(pathPair.srcPath(), pathPair.destPath(), this.numberOfTableElements, false, srcIsDir));
        numberOfTableElements++;

        this.invalidSrcPathLabel.setVisible(false);
        this.invalidDestPathLabel.setVisible(false);
    }

    public void deleteBackup() {
        if (RunBackupThreadSingleton.isAlive()) {
            this.deleteConfirmLabel.setVisible(true);
            this.deleteConfirmLabel.setText("Please wait for running backup to finish.");
            return;
        }

        if (!this.deletePressedOnce) {
            this.deleteConfirmLabel.setVisible(true);
            this.deleteConfirmLabel.setText("Are you sure to remove all the selected backups? Press again to confirm.");
            this.deletePressedOnce = true;
            return;
        }

        List<Integer> indicesToRemove = getCheckedElementsFromTable();
        BackupServiceInstance.backupService.removeBackup(indicesToRemove.stream().mapToInt(i -> i).toArray());

        refillTable(true);

        this.deleteConfirmLabel.setVisible(false);
        this.deletePressedOnce = false;
    }

    public void toggleFullPath() {
        refillTable(false);
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

    public void switchToLogScene() {
        try {
            SceneManager.setLogScene((Stage) this.mainContainer.getScene().getWindow());
        } catch (IOException unused) {
        }
    }

    public void switchToSettingsScene() {
        try {
            SceneManager.setSettingsScene((Stage) this.mainContainer.getScene().getWindow());
        } catch (IOException unused) {
        }
    }
}
