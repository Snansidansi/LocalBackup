package com.snansidansi.app.singletons;

import com.snansidansi.app.instances.BackupServiceInstance;
import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class RunBackupThreadSingleton {
    private static runBackupThread thread = null;
    private static Animation animation = null;
    private static Node animatedNode = null;
    private static Label finishedLabel = null;

    private RunBackupThreadSingleton() {
    }

    private static class runBackupThread extends Thread {
        public void run() {
            if (animation != null && animatedNode != null) {
                animatedNode.setVisible(true);
                animation.playFromStart();

                BackupServiceInstance.backupService.runBackup();

                animation.stop();
                animatedNode.setVisible(false);
            } else {
                BackupServiceInstance.backupService.runBackup();
            }

            if (finishedLabel != null) {
                finishedLabel.setVisible(true);
            }
        }
    }

    public static void start() {
        thread = new runBackupThread();
        thread.start();
    }

    public static boolean isAlive() {
        if (thread == null) {
            return false;
        }
        return thread.isAlive();
    }

    public static void setAnimation(Animation newAnimation, Node nodeFromAnimation) {
        animation = newAnimation;
        animatedNode = nodeFromAnimation;
    }

    public static void setFinishedLabel(Label label) {
        finishedLabel = label;
    }
}
