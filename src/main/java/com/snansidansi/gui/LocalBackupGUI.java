package com.snansidansi.gui;

import com.snansidansi.app.singletons.RunBackupThreadSingleton;
import com.snansidansi.gui.util.SceneManager;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

public class LocalBackupGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Local Backup");

        Image appIcon = new Image("/icons/appIcon.png");
        primaryStage.getIcons().add(appIcon);

        SceneManager.setConfigureBackupsScene(primaryStage);
        primaryStage.show();
        SceneManager.addActiveStage(primaryStage);

        primaryStage.setOnCloseRequest(windowEvent -> {
            if (!warnIfBackupIsRunning()) {
                System.exit(0);
            }

            SceneManager.closeActiveStages();
        });
    }

    private boolean warnIfBackupIsRunning() {
        if (!RunBackupThreadSingleton.isAlive()) {
            return true;
        }

        Alert confirmCloseAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmCloseAlert.getDialogPane().getScene().getStylesheets().add(SceneManager.getCorrectStyleSheet());
        confirmCloseAlert.setTitle("Run backup in background");
        confirmCloseAlert.setHeaderText("A backup is still running.\nShould the backup finish in the background?");

        Optional<ButtonType> result = confirmCloseAlert.showAndWait();
        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
    }
}
