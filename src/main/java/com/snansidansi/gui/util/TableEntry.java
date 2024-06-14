package com.snansidansi.gui.util;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

public class TableEntry {
    private String srcPath;
    private String destPath;
    private HBox checkBoxHBox;
    private CheckBox checkBox = new CheckBox();
    private int index;

    public TableEntry(String srcPath, String destPath, int index, boolean checked) {
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.index = index;

        this.checkBoxHBox = new HBox(this.checkBox);
        this.checkBoxHBox.setAlignment(Pos.CENTER);

        this.checkBox.setSelected(checked);
    }

    public String getSrcPath() {
        return this.srcPath;
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
