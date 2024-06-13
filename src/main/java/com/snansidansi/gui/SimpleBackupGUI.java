package com.snansidansi.gui;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SimpleBackupGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Simple Backup");

        Image appIcon = new Image("/icons/appIcon.png");
        primaryStage.getIcons().add(appIcon);

        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.setConfigureBackupsScene();

        primaryStage.show();
    }
}
