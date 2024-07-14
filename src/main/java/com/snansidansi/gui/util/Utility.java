package com.snansidansi.gui.util;

import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Random;

public class Utility {
    private Utility() {
    }

    public static void copyTextToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static Color getRandomJavaFXColor() {
        Random rand = new Random();
        return Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public static String convertJavaFXColorToHexColor(Color color) {
        return String.format("#%02x%02x%02x",
                Math.round(color.getRed() * 255),
                Math.round(color.getGreen() * 255),
                Math.round(color.getBlue() * 255));
    }
}
