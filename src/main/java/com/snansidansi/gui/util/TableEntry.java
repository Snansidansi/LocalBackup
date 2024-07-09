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
    private static final ImageView fileImageView = new ImageView(new Image(
            TableEntry.class.getResource("/icons/document.png").toString()));
    private static final ImageView dirImageView = new ImageView(new Image(
            TableEntry.class.getResource("/icons/openedFolder.png").toString()));

    static {
        final int IMAGE_SIZE = 20;
        fileImageView.setFitWidth(IMAGE_SIZE);
        fileImageView.setFitHeight(IMAGE_SIZE);
        dirImageView.setFitWidth(IMAGE_SIZE);
        dirImageView.setFitHeight(IMAGE_SIZE);
    }

    public TableEntry(String srcPath, String destPath, int index, boolean checked) {
        this.destPath = destPath;
        this.index = index;

        this.checkBoxHBox = new HBox(this.checkBox);
        this.checkBoxHBox.setAlignment(Pos.CENTER);
        this.checkBox.setSelected(checked);

        this.srcHbox.setPadding(new Insets(0, 0, 0, 3));
        this.srcHbox.setSpacing(3);
        this.srcHbox.setAlignment(Pos.CENTER_LEFT);
        this.srcHbox.getChildren().addAll(Files.isDirectory(Path.of(srcPath)) ? dirImageView : fileImageView,
                new Label(srcPath));
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
