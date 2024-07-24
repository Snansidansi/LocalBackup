package com.snansidansi.gui.uielements.buttons;

import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.gui.util.SceneManager;
import com.snansidansi.gui.util.Utility;
import com.snansidansi.settings.BackupSetting;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LogFileButton extends HBox {
    private static final Image logImage = new Image(
            LogFileButton.class.getResource("/icons/document.png").toString());

    public LogFileButton(String text, int fontSize, int imageSize) {
        super();
        this.setAlignment(Pos.CENTER_LEFT);
        this.setBorder(Border.stroke(Color.GRAY));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(imageSize);
        imageView.setFitHeight(imageSize);
        if (SettingsManagerInstance.settingsManager.getSetting(BackupSetting.COLOR_SCHEME).equals("dark mode")) {
            imageView.setImage(Utility.changeColorOfTransparentBackgroundImage(
                    logImage, Color.web(SceneManager.darkModeButtonImageColor)));
        }
        else {
            imageView.setImage(logImage);
        }
        this.getChildren().add(imageView);

        int indexOfUnderscore = text.indexOf('_');
        if (indexOfUnderscore == -1) {
            Label singleButtonLabel = createLabel(text, fontSize);
            this.getChildren().add(singleButtonLabel);
            return;
        }

        Label doubleUpperButtonLabel = createLabel(text.substring(0, indexOfUnderscore), fontSize);
        Label doubleDownButtonLabel = createLabel(text.substring(indexOfUnderscore + 1), fontSize);
        this.getChildren().add(new VBox(doubleUpperButtonLabel, doubleDownButtonLabel));
        this.getStyleClass().add("log-file-button");
    }

    private Label createLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setFont(new Font(fontSize));
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);

        return label;
    }
}
