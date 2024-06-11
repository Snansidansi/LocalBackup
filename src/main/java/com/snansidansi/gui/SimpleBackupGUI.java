package com.snansidansi.gui;

import com.snansidansi.backup.service.BackupService;
import com.snansidansi.gui.scenes.ConfigureBackupScene;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SimpleBackupGUI extends Application {
    private BackupService backupService = new BackupService("data/backupData.csv");

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Simple Backup");

        Image appIcon = new Image("/icons/appIcon.png");
        primaryStage.getIcons().add(appIcon);

        ConfigureBackupScene.setScene(primaryStage, backupService);

        primaryStage.show();
    }
}
