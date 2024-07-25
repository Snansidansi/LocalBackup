package com.snansidansi.gui.util;

import com.snansidansi.app.instances.SettingsManagerInstance;
import com.snansidansi.settings.BackupSetting;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;


public class TableEntry {
    private final HBox srcHbox = new HBox();
    private final String destPath;
    private final HBox checkBoxHBox;
    private final CheckBox checkBox = new CheckBox();
    private final int index;
    private final HBox tagHBox = new HBox();
    private final Label tagLabel = new Label();
    private final ImageView tagImageView = new ImageView();
    private String tagName = null;
    private final String srcPath;

    private static final Image fileImage = new Image(
            TableEntry.class.getResource("/icons/document.png").toString());
    private static Image fileImageDarkMode = null;

    private static final Image dirImage = new Image(
            TableEntry.class.getResource("/icons/folder.png").toString());
    private static Image dirImageDarkMode = null;

    private static final Image tagImage = new Image(
            TableEntry.class.getResource("/icons/tag.png").toString());
    private static final Map<String, Image> coloredTagImagesMap = new HashMap<>();

    public TableEntry(String srcPath, String destPath, int index, boolean checked, boolean srcIsDir) {
        this.destPath = destPath;
        this.index = index;
        this.srcPath = srcPath;

        // Setup delete check box
        this.checkBoxHBox = new HBox(this.checkBox);
        this.checkBoxHBox.setAlignment(Pos.CENTER);
        this.checkBox.setSelected(checked);

        // Setup src
        final int IMAGE_SIZE = 20;

        ImageView srcImageView;
        if (SettingsManagerInstance.settingsManager.getSetting(BackupSetting.COLOR_SCHEME).equals("dark mode")) {
            if (fileImageDarkMode == null) {
                fileImageDarkMode = Utility.changeColorOfTransparentBackgroundImage(fileImage, Color.WHITE);
                dirImageDarkMode = Utility.changeColorOfTransparentBackgroundImage(dirImage, Color.WHITE);
            }
            srcImageView = srcIsDir ? new ImageView(dirImageDarkMode) : new ImageView(fileImageDarkMode);
        }
        else {
            srcImageView = srcIsDir ? new ImageView(dirImage) : new ImageView(fileImage);
        }
        srcImageView.setFitWidth(IMAGE_SIZE);
        srcImageView.setFitHeight(IMAGE_SIZE);

        this.srcHbox.setPadding(new Insets(0, 0, 0, 3));
        this.srcHbox.setSpacing(3);
        this.srcHbox.setAlignment(Pos.CENTER_LEFT);
        this.srcHbox.getChildren().addAll(srcImageView, new Label(this.srcPath));

        // Setup tag
        this.tagImageView.setFitWidth(IMAGE_SIZE);
        this.tagImageView.setFitHeight(IMAGE_SIZE);

        this.tagHBox.setSpacing(3);
        this.tagHBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(tagHBox, Priority.ALWAYS);
    }

    public void setTag(String tagName, String tagColor) {
        this.tagHBox.getChildren().clear();
        this.tagName = tagName;

        if (Boolean.parseBoolean(SettingsManagerInstance.settingsManager.getSetting(BackupSetting.SHOW_TAG_IMAGE))) {
            this.tagHBox.getChildren().add(this.tagImageView);
            if (!coloredTagImagesMap.containsKey(tagColor)) {
                Image coloredTagImage = Utility.changeColorOfTransparentBackgroundImage(tagImage, Color.web(tagColor));
                coloredTagImagesMap.put(tagColor, coloredTagImage);
            }
            this.tagImageView.setImage(coloredTagImagesMap.get(tagColor));
        }
        else {
            this.tagHBox.getChildren().remove(this.tagImageView);
        }

        if (Boolean.parseBoolean(SettingsManagerInstance.settingsManager.getSetting(BackupSetting.SHOW_TAG_NAME))) {
            this.tagHBox.getChildren().add(this.tagLabel);
            this.tagLabel.setText(tagName);
            this.tagLabel.setStyle("-fx-text-fill: " + tagColor + ";-fx-font-weight: bold");
        }
        else {
            this.tagHBox.getChildren().remove(this.tagLabel);
        }
    }

    public void clearTag() {
        this.tagName = null;
        this.tagHBox.getChildren().clear();
    }

    public HBox getTagHBox() {
        return this.tagHBox;
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

    public String getTagName() {
        return this.tagName;
    }

    public String getSrcPath() {
        return this.srcPath;
    }
}
