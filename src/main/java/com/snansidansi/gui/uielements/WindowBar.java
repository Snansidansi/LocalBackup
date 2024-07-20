package com.snansidansi.gui.uielements;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class WindowBar extends BorderPane {
    private static final Image appIcon = new Image(
            WindowBar.class.getResource("/icons/appIcon.png").toString());
    private static final Image minimizeWindowImage = new Image(
            WindowBar.class.getResource("/icons/minimizeWindow.png").toString());
    private static final Image toggleFullScreenImage = new Image(
            WindowBar.class.getResource("/icons/toggleFullScreen.png").toString());
    private static final Image closeImage = new Image(
            WindowBar.class.getResource("/icons/close.png").toString());

    private static final int IMAGE_SIZE = 18;
    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 25;
    private static final int EDGE_DETECTION_THRESHOLD = 10;

    private final HBox buttonHBox = new HBox();
    private final HBox iconAndNameHBox = new HBox();
    private final Pane stageMainContainer;
    private Button toggleFullScreenButton;
    private Stage currentStage;
    private Scene currentScene;

    private double mouseDragStartX;
    private double mouseDragStartY;
    private boolean widthResize = false;
    private boolean heightResize = false;
    private boolean resizable = true;

    public WindowBar(Pane stageMainContainer) {
        super();
        super.setLeft(this.iconAndNameHBox);
        super.setRight(this.buttonHBox);

        this.stageMainContainer = stageMainContainer;
        Platform.runLater(() -> {
            this.currentStage = (Stage) this.getScene().getWindow();
            this.currentScene = this.currentStage.getScene();
            setupStageResizing();
            setupStageDragging();
        });

        setupIconAndNameHBox();
        setupMinimizeButton();
        setupToggleFullScreenButton();
        setupCloseButton();
    }

    private void setupStageResizing() {
        currentScene.setOnMouseMoved(this::setupMouseMovedEvent);
        currentScene.setOnMouseDragged(this::setupMouseResizeDragEvent);
    }

    private void setupMouseResizeDragEvent(MouseEvent mouseEvent) {
        if (this.widthResize && this.heightResize) {
            this.currentStage.setWidth(mouseEvent.getSceneX());
            this.currentStage.setHeight(mouseEvent.getSceneY());
        }
        else if (this.widthResize) {
            this.currentStage.setWidth(mouseEvent.getSceneX());
        }
        else if (this.heightResize) {
            this.currentStage.setHeight(mouseEvent.getSceneY());
        }
    }

    private void setupMouseMovedEvent(MouseEvent mouseEvent) {
        if (!this.resizable) {
            return;
        }

        if (onRightEdge(mouseEvent)) {
            if (onBottomEdge(mouseEvent)) {
                this.heightResize = true;
                currentScene.setCursor(Cursor.SE_RESIZE);
            }
            else {
                this.heightResize = false;
                currentScene.setCursor(Cursor.H_RESIZE);
            }
            this.widthResize = true;
            return;
        }

        if (onBottomEdge(mouseEvent)) {
            this.heightResize = true;
            this.widthResize = false;
            currentScene.setCursor(Cursor.V_RESIZE);
            return;
        }

        this.heightResize = false;
        this.widthResize = false;
        currentScene.setCursor(Cursor.DEFAULT);
    }

    private boolean onRightEdge(MouseEvent mouseEvent) {
        return mouseEvent.getSceneX() > this.stageMainContainer.getWidth() - EDGE_DETECTION_THRESHOLD;
    }

    private boolean onBottomEdge(MouseEvent mouseEvent) {
        return mouseEvent.getSceneY() > this.stageMainContainer.getHeight() - EDGE_DETECTION_THRESHOLD;
    }

    private void setupStageDragging() {
        this.currentStage.getScene().setOnMousePressed(mouseEvent -> {
            this.mouseDragStartX = mouseEvent.getSceneX();
            this.mouseDragStartY = mouseEvent.getSceneY();
        });

        this.setOnMouseDragged(mouseEvent -> {
            this.currentStage.setX(mouseEvent.getScreenX() - this.mouseDragStartX);
            this.currentStage.setY(mouseEvent.getScreenY() - this.mouseDragStartY);
        });
    }

    private void setupToggleFullScreenButton() {
        ImageView toggleFullScreenImageView = new ImageView(toggleFullScreenImage);
        toggleFullScreenImageView.setFitWidth(IMAGE_SIZE);
        toggleFullScreenImageView.setFitHeight(IMAGE_SIZE);

        Button toggleFullScreenButton = new Button();
        toggleFullScreenButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        toggleFullScreenButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        toggleFullScreenButton.setGraphic(toggleFullScreenImageView);

        toggleFullScreenButton.setOnMouseClicked(mouseEvent -> {

            if (this.currentStage.isMaximized()) {
                this.currentStage.setMaximized(false);
                return;
            }
            this.currentStage.setMaximized(true);
        });

        this.toggleFullScreenButton = toggleFullScreenButton;
        this.buttonHBox.getChildren().add(toggleFullScreenButton);
    }

    private void setupMinimizeButton() {
        ImageView minimizeImageView = new ImageView(minimizeWindowImage);
        minimizeImageView.setFitWidth(IMAGE_SIZE);
        minimizeImageView.setFitHeight(IMAGE_SIZE);

        Button minimizeButton = new Button();
        minimizeButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        minimizeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        minimizeButton.setGraphic(minimizeImageView);

        minimizeButton.setOnMouseClicked(mouseEvent -> {
            this.currentStage.setIconified(true);
        });

        this.buttonHBox.getChildren().add(minimizeButton);
    }

    private void setupCloseButton() {
        ImageView closeImageView = new ImageView(closeImage);
        closeImageView.setFitWidth(IMAGE_SIZE);
        closeImageView.setFitHeight(IMAGE_SIZE);

        Button closeButton = new Button();
        closeButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        closeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        closeButton.setGraphic(closeImageView);

        closeButton.setOnMouseClicked(mouseEvent -> {
            this.currentStage.close();
        });

        this.buttonHBox.getChildren().add(closeButton);
    }

    private void setupIconAndNameHBox() {
        ImageView appIconView = new ImageView(appIcon);
        appIconView.setFitWidth(BUTTON_HEIGHT);
        appIconView.setFitHeight(BUTTON_HEIGHT);

        Label programNameLabel = new Label("Local backup");

        this.iconAndNameHBox.setAlignment(Pos.CENTER_LEFT);
        this.iconAndNameHBox.setSpacing(5);
        this.iconAndNameHBox.setPadding(new Insets(0, 0, 0, 5));
        this.iconAndNameHBox.getChildren().addAll(appIconView, programNameLabel);
    }

    public void setStageResizable(boolean resizable) {
        this.resizable = resizable;
        if (resizable) {
            this.buttonHBox.getChildren().add(1, this.toggleFullScreenButton);
            return;
        }
        this.buttonHBox.getChildren().remove(this.toggleFullScreenButton);
    }
}
