package com.snansidansi.gui.util;

import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Random;

public class utility {
    private utility() {
    }

    static public void copyTextToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    static public Color getRandomJavaFXColor() {
        Random rand = new Random();
        return Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
}
