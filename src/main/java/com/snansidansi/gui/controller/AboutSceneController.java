package com.snansidansi.gui.controller;

import com.snansidansi.gui.util.utility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutSceneController {
    @FXML
    private Label githubLinkLabel;

    @FXML
    private Label icons8LinkLabel;

    public void copyGithubLinkToClipboard() {
        utility.copyTextToClipboard(githubLinkLabel.getText());
        githubLinkLabel.setTextFill(utility.getRandomJavaFXColor());
    }

    public void copyIcons8LinkToClipboard() {
        utility.copyTextToClipboard(icons8LinkLabel.getText());
        icons8LinkLabel.setTextFill(utility.getRandomJavaFXColor());
    }
}
