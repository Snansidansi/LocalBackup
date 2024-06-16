package com.snansidansi.gui;

import com.snansidansi.app.singletons.PrimaryStageSceneMangerSingleton;
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

        PrimaryStageSceneMangerSingleton.init(primaryStage);
        PrimaryStageSceneMangerSingleton.getSceneManager().setConfigureBackupsScene();

        primaryStage.show();
    }
}
