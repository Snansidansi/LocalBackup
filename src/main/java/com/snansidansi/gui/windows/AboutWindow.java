package com.snansidansi.gui.windows;

import com.snansidansi.gui.util.SceneManager;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutWindow {
    public static void showWindow() throws IOException {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About the program");
        aboutStage.setAlwaysOnTop(true);

        Image windowIcon = new Image("/icons/info.png");
        aboutStage.getIcons().add(windowIcon);

        SceneManager.setAboutScene(aboutStage);

        aboutStage.show();
    }
}
