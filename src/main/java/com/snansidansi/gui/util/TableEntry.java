package com.snansidansi.gui.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.nio.file.Files;
import java.nio.file.Path;


public class TableEntry {
    private final HBox srcHbox = new HBox();
    private final String destPath;
    private final HBox checkBoxHBox;
    private final CheckBox checkBox = new CheckBox();
    private final int index;
    private static final Image fileImage = new Image(
            TableEntry.class.getResource("/icons/document.png").toString());
    private static final Image dirImage= new Image(
            TableEntry.class.getResource("/icons/openedFolder.png").toString());

    public TableEntry(String srcPath, String destPath, int index, boolean checked) {
        this.destPath = destPath;
        this.index = index;

        this.checkBoxHBox = new HBox(this.checkBox);
        this.checkBoxHBox.setAlignment(Pos.CENTER);
        this.checkBox.setSelected(checked);

        final int IMAGE_SIZE = 20;
        ImageView srcImageView = Files.isDirectory(Path.of(srcPath)) ? new ImageView(dirImage) : new ImageView(fileImage);
        srcImageView.setFitWidth(IMAGE_SIZE);
        srcImageView.setFitHeight(IMAGE_SIZE);

        this.srcHbox.setPadding(new Insets(0, 0, 0, 3));
        this.srcHbox.setSpacing(3);
        this.srcHbox.setAlignment(Pos.CENTER_LEFT);
        this.srcHbox.getChildren().addAll(srcImageView, new Label(srcPath));
    }

    public HBox getSrcHBox() {
        return this.srcHbox;
    }

    public String getDestPath() {
        return this.destPath;
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    public int getIndex() {
        return this.index;
    }

    public HBox getCheckBoxHBox() {
        return this.checkBoxHBox;
    }
}
