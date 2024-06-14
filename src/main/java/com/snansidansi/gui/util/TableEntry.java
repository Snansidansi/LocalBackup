package com.snansidansi.gui.util;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

public class TableEntry {
    private String srcPath;
    private String destPath;
    private HBox checkBoxBox;
    private CheckBox checkBox = new CheckBox();
    private int index;

    public TableEntry(String srcPath, String destPath, int index, boolean checked) {
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.index = index;

        this.checkBoxBox = new HBox(checkBox);
        this.checkBoxBox.setAlignment(Pos.CENTER);

        this.checkBox.setSelected(checked);
    }

    public String getSrcPath() {
        return srcPath;
    }

    public String getDestPath() {
        return destPath;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public int getIndex() {
        return index;
    }

    public HBox getCheckBoxBox() {
        return checkBoxBox;
    }
}
