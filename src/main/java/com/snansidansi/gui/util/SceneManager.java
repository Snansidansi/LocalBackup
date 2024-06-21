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
        changeScene(stage, "/scenes/configureBackup.fxml");
        changeStageSizeBounds(stage, 520, 350, -1, -1);
    }

    public static void setAboutScene(Stage stage) throws IOException {
        Parent root = changeScene(stage, "/scenes/about.fxml");

        stage.setWidth(root.prefWidth(-1) + 20);
        stage.setHeight(root.prefHeight(-1) + 40);
    }

    public static void setLogScene(Stage stage) throws IOException {
        changeScene(stage, "/scenes/log.fxml");
        changeStageSizeBounds(stage, 370, 160, -1, -1);
    }

    public static void setSettingsScene(Stage stage) throws IOException {
        changeScene(stage, "/scenes/settings.fxml");
        changeStageSizeBounds(stage, 330, 200, -1, -1);
    }

    public static void changeStageSizeBounds(Stage stage, double minWidth, double minHeight, double maxWidth, double maxHeight) {
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setMaxWidth(maxWidth == -1 ? Double.MAX_VALUE : maxWidth);
        stage.setMaxHeight(maxHeight == -1 ? Double.MAX_VALUE : maxHeight);
    }

    private static Parent changeScene(Stage stage, String resource) throws IOException {
        Parent root = new FXMLLoader((SceneManager.class.getResource(resource))).load();
        Scene currentScene = stage.getScene();
        if (currentScene == null) stage.setScene(new Scene(root));
        else currentScene.setRoot(root);
        return root;
    }
}
