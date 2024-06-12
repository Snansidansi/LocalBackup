package com.snansidansi.gui.scenes;

import com.snansidansi.backup.service.BackupService;
import com.snansidansi.backup.service.SrcDestPair;
import com.snansidansi.gui.controller.ConfigureBackupSceneController;
import com.snansidansi.gui.util.TableEntry;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfigureBackupScene {
    static private int tableElementsNum = 0;

    static public void setScene(Stage primaryStage, BackupService backupService) throws IOException {
        FXMLLoader loader = new FXMLLoader((ConfigureBackupScene.class.getResource("/scenes/configureBackup.fxml")));
        Parent root = loader.load();

        Scene configureBackupScene = new Scene(root);
        primaryStage.setScene(configureBackupScene);
        primaryStage.setMinWidth(520);
        primaryStage.setMinHeight(350);

        ConfigureBackupSceneController controller = loader.getController();
        controller.bindMiddleLineToWindowWidth(configureBackupScene);
        controller.bindRemoveColToRight();
        controller.getDeleteConfirmLabel().setVisible(false);
        controller.getInvalidSrcPathLabel().setVisible(false);
        controller.getInvalidDestPathLabel().setVisible(false);

        setupTable(controller, backupService);
    }

    static public int addTableElementsNum() {
        tableElementsNum++;
        return tableElementsNum - 1;
    }

    static private void setupTable(ConfigureBackupSceneController controller, BackupService backupService) {
        TableView<TableEntry> tableView = controller.getTableView();
        tableView.setPlaceholder(new Label("No backups found"));

        controller.getSourceTableCol().setCellValueFactory(
                new PropertyValueFactory<>("srcPath"));

        controller.getDestinationTableCol().setCellValueFactory(
                new PropertyValueFactory<>("destPath"));

        controller.getRemoveTableCol().setCellValueFactory(
                new PropertyValueFactory<>("checkBoxBox"));

        for (SrcDestPair pathPair : backupService.getAllBackups()) {
            tableView.getItems().add(new TableEntry(pathPair.srcPath(), pathPair.destPath(), tableElementsNum));
            tableElementsNum++;
        }
    }
}
