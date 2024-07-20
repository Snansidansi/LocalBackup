package com.snansidansi.gui.controller;

import com.snansidansi.gui.uielements.WindowBar;
import com.snansidansi.gui.util.Utility;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AboutSceneController {
    @FXML
    private Label githubLinkLabel;
    @FXML
    private Label icons8LinkLabel;
    @FXML
    private VBox mainContainer;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            WindowBar windowBar = new WindowBar(this.mainContainer);
            windowBar.setStageResizable(false);
            this.mainContainer.getChildren().addFirst(windowBar);
        });
    }

    public void copyGithubLinkToClipboard() {
        Utility.copyTextToClipboard(githubLinkLabel.getText());
        githubLinkLabel.setTextFill(Utility.getRandomJavaFXColor());
    }

    public void copyIcons8LinkToClipboard() {
        Utility.copyTextToClipboard(icons8LinkLabel.getText());
        icons8LinkLabel.setTextFill(Utility.getRandomJavaFXColor());
    }
}
