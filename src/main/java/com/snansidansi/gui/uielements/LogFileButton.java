package com.snansidansi.gui.uielements;

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
    private final ImageView logImage = new ImageView(new Image(
            LogFileButton.class.getResource("/icons/document.png").toString()));

    public LogFileButton(String text, int fontSize, int imageSize) {
        super();
        this.setAlignment(Pos.CENTER_LEFT);
        this.setBorder(Border.stroke(Color.GRAY));

        this.logImage.setFitHeight(imageSize);
        this.logImage.setFitWidth(imageSize);
        this.getChildren().add(this.logImage);

        int indexOfUnderscore = text.indexOf('_');
        if (indexOfUnderscore == -1) {
            Label singleButtonLabel = createLabel(text, fontSize);
            this.getChildren().add(singleButtonLabel);
            return;
        }

        Label doubleUpperButtonLabel = createLabel(text.substring(0, indexOfUnderscore), fontSize);
        Label doubleDownButtonLabel = createLabel(text.substring(indexOfUnderscore + 1), fontSize);
        this.getChildren().add(new VBox(doubleUpperButtonLabel, doubleDownButtonLabel));
    }

    private Label createLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setFont(new Font(fontSize));
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);

        return label;
    }
}
