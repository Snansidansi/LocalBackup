package com.snansidansi.gui.controller;

import com.snansidansi.gui.util.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutSceneController {
    @FXML
    private Label githubLinkLabel;

    @FXML
    private Label icons8LinkLabel;

    public void copyGithubLinkToClipboard() {
        Utility.copyTextToClipboard(githubLinkLabel.getText());
        githubLinkLabel.setTextFill(Utility.getRandomJavaFXColor());
    }

    public void copyIcons8LinkToClipboard() {
        Utility.copyTextToClipboard(icons8LinkLabel.getText());
        icons8LinkLabel.setTextFill(Utility.getRandomJavaFXColor());
    }
}
