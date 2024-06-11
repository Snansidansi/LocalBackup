package com.snansidansi.gui.scenes;

import com.snansidansi.gui.controller.ConfigureBackupSceneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfigureBackupScene {
    public static void setScene(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader((ConfigureBackupScene.class.getResource("/scenes/configureBackup.fxml")));
        Parent root = loader.load();

        Scene configureBackupScene = new Scene(root);
        primaryStage.setScene(configureBackupScene);
        primaryStage.setMinWidth(520);
        primaryStage.setMinHeight(350);

        ConfigureBackupSceneController controller = loader.getController();
        controller.bindMiddleLineToWindowWidth(configureBackupScene);
        controller.bindRemoveColToRight();
    }
}
