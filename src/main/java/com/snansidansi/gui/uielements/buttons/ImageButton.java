package com.snansidansi.gui.uielements.buttons;


import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageButton extends Button {
    private final ImageView imageView;

    public ImageButton(String fileName) {
        super();
        super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        String filePath = getClass().getResource("/icons/" + fileName).toString();
        this.imageView = new ImageView(new Image(filePath));
        super.setGraphic(this.imageView);
    }

    public void setImageSize(double width, double height) {
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
    }
}
