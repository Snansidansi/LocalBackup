package com.snansidansi.gui.util;

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

        this.stage.setScene(configureBackupScene);
        this.stage.setMinWidth(520);
        this.stage.setMinHeight(350);
    }

    public void setAboutScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/about.fxml"));
        Parent root = loader.load();
        Scene aboutScene = new Scene(root);

        this.stage.setScene(aboutScene);
        this.stage.setMinWidth(root.prefWidth(-1) + 20);
        this.stage.setMaxWidth(root.prefWidth(-1) + 20);
        this.stage.setMinHeight(root.prefHeight(-1) + 40);
        this.stage.setMaxHeight(root.prefHeight(-1) + 40);
    }

    public void setLogScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/log.fxml"));
        Parent root = loader.load();
        Scene logScene = new Scene(root);

        this.stage.setScene(logScene);
    }
}
