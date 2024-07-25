package com.snansidansi.gui.util;

import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.settings.BackupSetting;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private static double lastWidth = -1;
    private static double lastHeight = -1;
    public static final String darkModeButtonImageColor = "#c21014";
    private static final List<Stage> activeStage = new ArrayList<>();

    private SceneManager() {
    }

    public static void setConfigureBackupsScene(Stage stage) throws IOException {
        changeScene(stage, "/scenes/configureBackup.fxml");
        loadSafedStageSize(stage);
        changeStageSizeBounds(stage, 600, 580, -1, -1);
    }

    public static void setAboutScene(Stage stage) throws IOException {
        Parent root = changeScene(stage, "/scenes/about.fxml");

        stage.setWidth(root.prefWidth(-1) + 20);
        stage.setHeight(root.prefHeight(-1) + 40);
    }

    public static void setLogScene(Stage stage) throws IOException {
        changeScene(stage, "/scenes/log.fxml");
        changeStageSizeBounds(stage, 600, 160, -1, -1);
    }

    public static void setSettingsScene(Stage stage) throws IOException {
        changeScene(stage, "/scenes/settings.fxml");
        safeCurrentStageSize(stage);
    }

    public static void changeStageSizeBounds(Stage stage, double minWidth, double minHeight, double maxWidth, double maxHeight) {
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setMaxWidth(maxWidth == -1 ? Double.MAX_VALUE : maxWidth);
        stage.setMaxHeight(maxHeight == -1 ? Double.MAX_VALUE : maxHeight);
    }

    public static void changeStageSize(Stage stage, double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
    }

    private static Parent changeScene(Stage stage, String resource) throws IOException {
        Parent root = new FXMLLoader((SceneManager.class.getResource(resource))).load();
        Scene currentScene = stage.getScene();
        if (currentScene == null) {
            stage.setScene(new Scene(root));
        } else {
            currentScene.setRoot(root);
        }

        root.getScene().setFill(Color.TRANSPARENT);
        root.getScene().getStylesheets().clear();

        String styleSheet = getCorrectStyleSheet();
        root.getScene().getStylesheets().add(styleSheet);

        Platform.runLater(() -> setCorrectButtonColor(root.getScene()));
        return root;
    }

    private static void safeCurrentStageSize(Stage stage) {
        lastWidth = stage.getWidth();
        lastHeight = stage.getHeight();
    }

    /**
     * Sets the width and height of a given Stage to the safed values if they are greater than 0. Also sets the stored
     * sizes to -1.
     * @param stage The current stage.
     */
    private static void loadSafedStageSize(Stage stage) {
        if (lastWidth < 1 && lastHeight < 1) {
            return;
        }
        changeStageSize(stage, lastWidth, lastHeight);
        lastWidth = -1;
        lastHeight = -1;
    }

    public static void setCorrectButtonColor(Scene currentScene) {
        Color buttonColor;
        if (SettingsManagerInstance.settingsManager.getSetting(BackupSetting.COLOR_SCHEME).equals("light mode")) {
            buttonColor = Color.BLACK;
        }
        else {
            buttonColor = Color.web(darkModeButtonImageColor);
        }

        for (Node node : currentScene.getRoot().lookupAll(".dynamic-image")) {
            if (node instanceof ImageView imageView) {
                Image newImage = Utility.changeColorOfTransparentBackgroundImage(
                        imageView.getImage(), buttonColor);

                imageView.setImage(newImage);
            }
        }
    }

    private static String getCorrectStyleSheet() {
        if (SettingsManagerInstance.settingsManager.getSetting(BackupSetting.COLOR_SCHEME).equals("dark mode")) {
            return getStyleSheetFromFile("darkMode");
        }
        else {
            return getStyleSheetFromFile("lightMode");
        }
    }

    private static String getStyleSheetFromFile(String fileName) {
        return SceneManager.class.getResource("/css/" + fileName + ".css").toExternalForm();
    }

    public static void addActiveStage(Stage stage) {
        activeStage.add(stage);
    }

    public static void removeActiveStage(Stage stage) {
        activeStage.remove(stage);
    }

    public static void updateStyle() {
        String styleSheet = getCorrectStyleSheet();
        for (Stage stage : activeStage) {
            stage.getScene().getStylesheets().clear();
            stage.getScene().getStylesheets().add(styleSheet);
            setCorrectButtonColor(stage.getScene());
        }
    }

    public static void closeActiveStages() {
        for (Stage stage : activeStage) {
            stage.close();
        }
    }

    public static List<Stage> getActiveStages() {
        return new ArrayList<>(activeStage);
    }
}
