package com.snansidansi.app.singletons;

import com.snansidansi.gui.util.SceneManager;
import javafx.stage.Stage;

public class PrimaryStageSceneMangerSingleton {
    private static SceneManager sceneManager = null;

    private PrimaryStageSceneMangerSingleton() {
    }

    public static void init(Stage primaryStage) {
        if (sceneManager == null) sceneManager = new SceneManager(primaryStage);
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }
}
