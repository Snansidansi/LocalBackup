package com.snansidansi.gui.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
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

    public static WritableImage changeColorOfTransparentBackgroundImage(Image image, Color outputColor) {
        int imageWidth = (int) image.getWidth();
        int imageHeight = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                Color pixelColor = pixelReader.getColor(x, y);
                if (!pixelColor.equals(Color.TRANSPARENT)) {
                    double pixelOpacity = pixelColor.getOpacity();
                    pixelColor = new Color(outputColor.getRed(), outputColor.getGreen(), outputColor.getBlue(), pixelOpacity);
                }
                pixelWriter.setColor(x, y, pixelColor);
            }
        }

        return writableImage;
    }
}
