package com.snansidansi.gui.controller;

import com.snansidansi.app.instances.BackupServiceInstance;
import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.app.instances.TagManagerInstance;
import com.snansidansi.app.singletons.RunBackupThreadSingleton;
import com.snansidansi.backup.exceptions.DestinationNoDirException;
import com.snansidansi.backup.exceptions.DestinationPathIsInSourcePathException;
import com.snansidansi.backup.exceptions.SourceDoesNotExistException;
import com.snansidansi.backup.exceptions.StringsAreEqualException;
import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.util.SrcDestPair;
import com.snansidansi.gui.uielements.WindowBar;
import com.snansidansi.gui.util.SceneManager;
import com.snansidansi.gui.util.TableEntry;
import com.snansidansi.gui.util.Utility;
import com.snansidansi.gui.windows.AboutWindow;
import com.snansidansi.settings.BackupSetting;
import com.snansidansi.tag.Tag;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfigureBackupSceneController {
    private boolean deletePressedOnce = false;
    private int numberOfTableElements = 0;
    private String lastSelectedSrcDirPath = "";
    private String lastSelectedDestDirPath = "";
    private final ObservableList<Tag> tagsInComboboxObservableList = FXCollections.observableArrayList();
    private boolean applyTagMode = false;

    @FXML
    private VBox mainContainer;
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
    private TextField addTagTextField;
    @FXML
    private ColorPicker addTagColorPicker;
    @FXML
    private VBox tagsVBox;
    @FXML
    private VBox tagsVBoxWrapperVBox;
    @FXML
    private ComboBox<Tag> editTagComboBox;
    @FXML
    private TextField editTagTextField;
    @FXML
    private ColorPicker editTagColorPicker;
    @FXML
    private ComboBox<Tag> applyTagComboBox;
    @FXML
    private Button applyTagButton;
    @FXML
    private Button deleteBackupButton;
    @FXML
    private HBox deleteBackupButtonWrapperHBox;
    @FXML
    private Label selectTagsInfoLabel;
    @FXML
    private BorderPane searchAndFilterBorderPane;
    @FXML
    private TextField backupSearchTextField;
    @FXML
    private ComboBox<Tag> filterTagComboBox;
    @FXML
    private Button deleteTagButton;
    @FXML
    private Button applyTagChangesButton;
    @FXML
    private HBox deleteTagButtonWrapperHBox;
    @FXML
    private HBox applyTagChangesButtonWrapperHBox;
    @FXML
    private Button settingsMenuButton;
    @FXML
    private HBox filterTagsHBox;
    @FXML
    private VBox filterTagsHBoxWrapperVBox;

    @FXML
    public void initialize() {
        WindowBar windowBar = new WindowBar(this.mainContainer);
        this.mainContainer.getChildren().addFirst(windowBar);

        setupTagControls();
        bindMiddleLineToWindowWidth();

        this.deleteConfirmLabel.setVisible(false);
        this.invalidSrcPathLabel.setVisible(false);
        this.invalidDestPathLabel.setVisible(false);
        this.backupRunningIndicatorLabel.setVisible(false);
        this.backupFinishedLabel.setVisible(false);
        this.selectTagsInfoLabel.setVisible(false);

        setupTable();
        Platform.runLater(this::setupTableColumnProperties);
        setupSearchAndFilterBorderPane();
        setupSearchBar();
        setupFilterTagCombobox();

        RotateTransition loadingAnimation = createLoadingAnimation();
        RunBackupThreadSingleton.setAnimation(loadingAnimation, this.backupRunningIndicatorLabel);
        RunBackupThreadSingleton.setFinishedLabel(this.backupFinishedLabel);
        RunBackupThreadSingleton.setConfigureBackupSceneController(this);
        RunBackupThreadSingleton.setSettingsMenuButton(this.settingsMenuButton);

        if (RunBackupThreadSingleton.isAlive()) {
            this.backupRunningIndicatorLabel.setVisible(true);
            loadingAnimation.playFromStart();
            this.settingsMenuButton.setDisable(true);
        }
    }

    private void setupFilterTagCombobox() {
        this.filterTagComboBox.setCellFactory(getTagComboboxCallback());
        this.filterTagComboBox.setButtonCell(getTagComboboxListCell());
        this.filterTagComboBox.setItems(this.tagsInComboboxObservableList);


        this.filterTagComboBox.setOnAction(event -> {
            refillTable(false);
            if (this.filterTagComboBox.getValue() == null) {
                return;
            }

            Iterator<TableEntry> tabelIterator = this.tableView.getItems().iterator();
            String selectedTag = this.filterTagComboBox.getValue().name;

            while (tabelIterator.hasNext()) {
                var currentElement = tabelIterator.next();
                if (!selectedTag.equals(currentElement.getTagName())) {
                    tabelIterator.remove();
                }
            }
        });
    }

    private void setupSearchAndFilterBorderPane() {
        this.searchAndFilterBorderPane.prefWidthProperty().bind(
                this.tableView.widthProperty()
                        .subtract(10) // For the HBox margin to the left.
        );
    }

    private void setupSearchBar() {
        this.backupSearchTextField.prefWidthProperty().bind(
                this.searchAndFilterBorderPane.widthProperty().multiply(0.5));

        this.backupSearchTextField.setPromptText("Search backup source");
        this.backupSearchTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            for (TableEntry entry : tableView.getItems()) {
                if (newVal.isBlank() || !entry.getSrcPath().contains(newVal)) {
                    entry.getSrcHBox().setStyle("-fx-background-color: transparent");
                    continue;
                }
                entry.getSrcHBox().setStyle("-fx-background-color: #d99338");
            }
        });
    }

    private void setupTagControls() {
        if (!Boolean.parseBoolean(SettingsManagerInstance.settingsManager.getSetting(BackupSetting.ENABLE_TAGS))) {
            this.tagsVBoxWrapperVBox.getChildren().clear();
            this.filterTagsHBoxWrapperVBox.getChildren().clear();
        }
        if (TagManagerInstance.tagManager == null) {
            disableTagControls();
            return;
        }

        this.tagsInComboboxObservableList.addAll(TagManagerInstance.tagManager.getAllTags());

        this.editTagComboBox.setOnAction(event -> {
            Tag tag = this.editTagComboBox.getValue();
            if (tag == null) {
                return;
            }
            this.editTagTextField.setText(tag.name);
            this.editTagColorPicker.setValue(Color.web(tag.color));
        });
        this.editTagComboBox.setCellFactory(getTagComboboxCallback());
        this.editTagComboBox.setButtonCell(getTagComboboxListCell());
        this.editTagComboBox.setItems(this.tagsInComboboxObservableList);

        this.applyTagComboBox.setCellFactory(getTagComboboxCallback());
        this.applyTagComboBox.setButtonCell(getTagComboboxListCell());
        this.applyTagComboBox.setItems(this.tagsInComboboxObservableList);
    }

    private Callback<ListView<Tag>, ListCell<Tag>> getTagComboboxCallback() {
        return stringComboBox -> getTagComboboxListCell();
    }

    private ListCell<Tag> getTagComboboxListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("");
                    setStyle("");
                } else {
                    setText(item.name);
                    setStyle("-fx-text-fill: " + item.color + ";");
                }
            }
        };
    }

    private void disableTagControls() {
        this.tagsVBox.setDisable(true);
        this.filterTagsHBox.setDisable(true);

        Tooltip disabledTagsTooltip = new Tooltip("Tags are disabled because of an Error while loading" +
                "the tags.\n Error: " + TagManagerInstance.loadingException);
        disabledTagsTooltip.setShowDelay(Duration.ZERO);
        disabledTagsTooltip.setHideDelay(Duration.ZERO);
        disabledTagsTooltip.setFont(new Font(14));

        Tooltip.install(this.tagsVBoxWrapperVBox, disabledTagsTooltip);
        Tooltip.install(this.filterTagsHBoxWrapperVBox, disabledTagsTooltip);
    }

    private void bindMiddleLineToWindowWidth() {
        this.middleLine.endXProperty().bind(this.mainContainer.widthProperty());
    }

    private void setupTableColumnProperties() {
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

        this.tagTableCol.setCellValueFactory(
                new PropertyValueFactory<>("tagHBox"));

        this.tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                this.tableView.refresh();
            }
        });

        if (!Boolean.parseBoolean(SettingsManagerInstance.settingsManager.getSetting(BackupSetting.ENABLE_TAGS))
                || TagManagerInstance.tagManager == null) {
            this.tagTableCol.setVisible(false);
        }
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

        if (TagManagerInstance.tagManager == null) {
            return;
        }
        for (Tag tag : TagManagerInstance.tagManager.getAllTags()) {
            for (int identifier : tag.content) {
                int index = BackupServiceInstance.backupService.getIndexFromIdentifier(identifier);
                if (index == -1) {
                    continue;
                }
                this.tableView.getItems().get(index).setTag(tag.name, tag.color);
            }
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
        for (int index : indicesToRemove) {
            String tagName = this.tableView.getItems().get(index).getTagName();
            Integer backupIdentifier = BackupServiceInstance.backupService.getBackupIdentifier(index);

            if (tagName == null) {
                continue;
            }

            TagManagerInstance.tagManager.getTagContent(tagName).remove(backupIdentifier);
        }

        if (!BackupServiceInstance.backupService.removeBackup(indicesToRemove.stream().mapToInt(i -> i).toArray()) ||
                !TagManagerInstance.tagManager.saveChangesToFile()) {
            TagManagerInstance.tagManager.revertChanges();
        }

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

    public void addTag() {
        String tagName = this.addTagTextField.getText();
        if (tagName.isBlank()) {
            return;
        }
        String hexTagColor = Utility.convertJavaFXColorToHexColor(this.addTagColorPicker.getValue());

        if (!TagManagerInstance.tagManager.addTagName(tagName)) {
            return;
        }

        TagManagerInstance.tagManager.changeColor(tagName, hexTagColor);
        if (!TagManagerInstance.tagManager.saveChangesToFile()) {
            TagManagerInstance.tagManager.deleteTag(tagName);
            return;
        }
        this.tagsInComboboxObservableList.add(new Tag(tagName, hexTagColor, new ArrayList<>()));
        this.addTagTextField.setText("");
    }

    public void deleteTag() {
        Tag selectedTag = this.editTagComboBox.getValue();
        if (selectedTag == null) {
            return;
        }

        Tag oldTag = TagManagerInstance.tagManager.getTag(selectedTag.name);
        if (oldTag == null) {
            return;
        }

        TagManagerInstance.tagManager.deleteTag(selectedTag.name);
        if (!TagManagerInstance.tagManager.saveChangesToFile()) {
            TagManagerInstance.tagManager.addTag(oldTag);
            return;
        }
        this.editTagComboBox.setValue(null);
        this.editTagTextField.setText("");
        this.editTagColorPicker.setValue(Color.WHITE);
        this.tagsInComboboxObservableList.remove(oldTag);
    }

    public void editTag() {
        Tag selectedTag = this.editTagComboBox.getValue();
        if (selectedTag == null) {
            return;
        }

        String newTagName = this.editTagTextField.getText();
        if (newTagName.isBlank()) {
            return;
        }

        String newHexColor = Utility.convertJavaFXColorToHexColor(this.editTagColorPicker.getValue());
        if (newTagName.equals(selectedTag.name) && newHexColor.equals(selectedTag.color)) {
            return;
        }

        TagManagerInstance.tagManager.changeTagName(selectedTag.name, newTagName);
        TagManagerInstance.tagManager.changeColor(newTagName, newHexColor);
        if (!TagManagerInstance.tagManager.saveChangesToFile()) {
            TagManagerInstance.tagManager.changeColor(newTagName, selectedTag.color);
            TagManagerInstance.tagManager.changeTagName(newTagName, selectedTag.name);
            return;
        }

        for (TableEntry entry : this.tableView.getItems()) {
            if (entry.getTagName().equals(selectedTag.name)) {
                entry.setTag(newTagName, newHexColor);
            }
        }

        this.editTagTextField.setText("");
        this.editTagComboBox.setValue(null);
        this.editTagColorPicker.setValue(Color.WHITE);

        if (this.filterTagComboBox.getValue() != null && this.filterTagComboBox.getValue().equals(selectedTag)) {
            this.filterTagComboBox.setValue(null);
        }
        if (this.applyTagComboBox.getValue() != null && this.applyTagComboBox.getValue().equals(selectedTag)) {
            this.applyTagComboBox.setValue(null);
        }
        this.filterTagComboBox.setValue(null);

        this.tagsInComboboxObservableList.remove(selectedTag);
        this.tagsInComboboxObservableList.add(TagManagerInstance.tagManager.getTag(newTagName));
    }

    public void applyTag() {
        Tag selectedTag = this.applyTagComboBox.getValue();
        if (selectedTag == null) {
            return;
        }

        if (!startTagApply(selectedTag)) {
            finishTagApply(selectedTag);
        }
    }

    private boolean startTagApply(Tag selectedTag) {
        if (this.applyTagMode) {
            return false;
        }

        this.applyTagMode = true;
        this.applyTagButton.setText("Finish");
        this.removeTableCol.setText("Apply tag");
        this.deleteBackupButton.setDisable(true);
        this.deleteTagButton.setDisable(true);
        this.applyTagChangesButton.setDisable(true);
        this.applyTagComboBox.setDisable(true);
        this.selectTagsInfoLabel.setVisible(true);

        Tooltip disabledElementDuringTagApplyTooltip = new Tooltip("This operation is unavailable while applying tags.");
        disabledElementDuringTagApplyTooltip.setFont(new Font(12));
        disabledElementDuringTagApplyTooltip.setShowDelay(Duration.ZERO);
        disabledElementDuringTagApplyTooltip.setHideDelay(Duration.ZERO);
        Tooltip.install(this.deleteBackupButtonWrapperHBox, disabledElementDuringTagApplyTooltip);
        Tooltip.install(this.deleteTagButtonWrapperHBox, disabledElementDuringTagApplyTooltip);
        Tooltip.install(this.applyTagChangesButtonWrapperHBox, disabledElementDuringTagApplyTooltip);

        for (TableEntry entry : this.tableView.getItems()) {
            entry.getCheckBox().setSelected(false);
        }
        for (int identifier : TagManagerInstance.tagManager.getTagContent(selectedTag.name)) {
            int index = BackupServiceInstance.backupService.getIndexFromIdentifier(identifier);
            if (index == -1) {
                continue;
            }
            this.tableView.getItems().get(index).getCheckBox().setSelected(true);
        }
        return true;
    }

    private void finishTagApply(Tag selectedTag) {
        List<Integer> newContent = new ArrayList<>();
        for (TableEntry entry : this.tableView.getItems()) {
            if (!entry.getCheckBox().isSelected()) {
                if (selectedTag.name.equals(entry.getTagName())) {
                    entry.clearTag();
                }
                continue;
            }

            Integer entryBackupIdentifier = BackupServiceInstance.backupService.getBackupIdentifier(entry.getIndex());
            if (entry.getTagName() != null && !entry.getTagName().equals(selectedTag.name)) {
                TagManagerInstance.tagManager.getTagContent(entry.getTagName()).remove(entryBackupIdentifier);
            }

            entry.setTag(selectedTag.name, selectedTag.color);
            newContent.add(entryBackupIdentifier);
            entry.getCheckBox().setSelected(false);
        }

        TagManagerInstance.tagManager.changeTagContent(selectedTag.name, newContent);
        if (!TagManagerInstance.tagManager.saveChangesToFile()) {
            TagManagerInstance.tagManager.revertChanges();
        }

        this.applyTagMode = false;
        this.applyTagButton.setText("Apply tag");
        this.removeTableCol.setText("Remove");
        this.applyTagComboBox.setDisable(false);
        this.deleteBackupButton.setDisable(false);
        this.deleteTagButton.setDisable(false);
        this.applyTagChangesButton.setDisable(false);
        this.selectTagsInfoLabel.setVisible(false);

        Tooltip.uninstall(this.deleteBackupButtonWrapperHBox, null);
        Tooltip.uninstall(this.deleteTagButtonWrapperHBox, null);
        Tooltip.uninstall(this.applyTagChangesButtonWrapperHBox, null);
    }

    public void clearTagFilter() {
        this.filterTagComboBox.setValue(null);
    }
}
