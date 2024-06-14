package com.snansidansi.gui;

import com.snansidansi.gui.util.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LocalBackupGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Local Backup");

        Image appIcon = new Image("/icons/appIcon.png");
        primaryStage.getIcons().add(appIcon);

        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.setConfigureBackupsScene();

        primaryStage.show();
    }
}
