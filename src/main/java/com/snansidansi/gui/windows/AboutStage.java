package com.snansidansi.gui.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutStage {
    public static void showWindow() throws IOException {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About the program");
        aboutStage.setResizable(false);

        Image windowIcon = new Image("/icons/info.png");
        aboutStage.getIcons().add(windowIcon);

        FXMLLoader loader = new FXMLLoader(AboutStage.class.getResource("/scenes/about.fxml"));
        Parent root = loader.load();

        Scene aboutScene = new Scene(root);
        aboutStage.setScene(aboutScene);

        aboutStage.show();
    }
}
