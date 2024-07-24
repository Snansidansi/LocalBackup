package com.snansidansi.gui.windows;

import com.snansidansi.gui.util.SceneManager;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AboutWindow {
    public static void showWindow() throws IOException {
        Stage aboutStage = new Stage();
        aboutStage.initStyle(StageStyle.TRANSPARENT);

        aboutStage.setTitle("About the program");
        aboutStage.setAlwaysOnTop(true);

        Image windowIcon = new Image("/icons/info.png");
        aboutStage.getIcons().add(windowIcon);

        SceneManager.setAboutScene(aboutStage);
        aboutStage.setResizable(false);

        aboutStage.show();
        SceneManager.addActiveStage(aboutStage);

        aboutStage.setOnCloseRequest((event) -> {
            SceneManager.removeActiveStage(aboutStage);
            visible = false;
        });
    }
}
