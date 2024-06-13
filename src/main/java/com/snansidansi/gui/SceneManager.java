package com.snansidansi.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void setConfigureBackupsScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/configureBackup.fxml"));
        Parent root = loader.load();
        Scene configureBackupScene = new Scene(root);

        stage.setScene(configureBackupScene);
        stage.setMinWidth(520);
        stage.setMinHeight(350);
    }
}
