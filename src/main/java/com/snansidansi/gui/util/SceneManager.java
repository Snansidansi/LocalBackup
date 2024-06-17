package com.snansidansi.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private SceneManager() {
    }

    public static void setConfigureBackupsScene(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/scenes/configureBackup.fxml"));
        Parent root = loader.load();
        Scene configureBackupScene = new Scene(root);

        stage.setScene(configureBackupScene);
        stage.setMinWidth(520);
        stage.setMinHeight(350);
    }

    public static void setAboutScene(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/scenes/about.fxml"));
        Parent root = loader.load();
        Scene aboutScene = new Scene(root);

        stage.setScene(aboutScene);
        stage.setMinWidth(root.prefWidth(-1) + 20);
        stage.setMaxWidth(root.prefWidth(-1) + 20);
        stage.setMinHeight(root.prefHeight(-1) + 40);
        stage.setMaxHeight(root.prefHeight(-1) + 40);
    }

    public static void setLogScene(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/scenes/log.fxml"));
        Parent root = loader.load();
        Scene logScene = new Scene(root);

        stage.setScene(logScene);
    }
}
