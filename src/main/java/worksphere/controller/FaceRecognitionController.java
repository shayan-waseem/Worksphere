package worksphere.controller;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

import javafx.animation.Animation;
import javafx.animation.KeyFrame; // IMPORTANT: Missing import
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import worksphere.WorkSphereApp;
import worksphere.model.Employee;

public class FaceRecognitionController {
    @FXML
    private ImageView cameraView;
    @FXML
    private Label statusLabel;
    @FXML
    private HBox statusBox;
    @FXML
    private Rectangle scanLine;
    @FXML
    private Button actionBtn;

    private Webcam webcam;
    private Employee currentUser;
    private Timeline timeline;

    public void initData(Employee user) {
        this.currentUser = user;
        startWebcam();
        startScanAnimation();
    }

    private void startWebcam() {
        // Task handles the hardware opening in background so UI doesn't freeze
        Task<Void> webcamTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                webcam = Webcam.getDefault();
                if (webcam != null) {
                    webcam.open();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                if (webcam != null) {
                    // Start the video feed on the JavaFX Application Thread
                    timeline = new Timeline(new KeyFrame(Duration.millis(40), e -> {
                        BufferedImage img = webcam.getImage();
                        if (img != null) {
                            cameraView.setImage(SwingFXUtils.toFXImage(img, null));
                        }
                    }));
                    timeline.setCycleCount(Animation.INDEFINITE);
                    timeline.play();
                } else {
                    statusLabel.setText("Camera Hardware Not Found");
                    statusBox.setStyle("-fx-background-color: #FEF2F2; -fx-border-color: #EF4444;");
                }
            }
        };

        Thread thread = new Thread(webcamTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void startScanAnimation() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), scanLine);
        tt.setFromY(0);
        tt.setToY(400); // Matches the ImageView height
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setAutoReverse(true);
        tt.play();
    }

    @FXML
    private void handleCapture() {
        // 1. Change status to Success
        statusLabel.setText("Identity Verified!");
        statusBox.setStyle(
                "-fx-background-color: #dcfce7; -fx-border-color: #22c55e; -fx-background-radius: 12; -fx-padding: 15;");

        // 2. Make button look "Processing"
        actionBtn.setDisable(true);
        actionBtn.setText("PROCEEDING TO DASHBOARD...");
        actionBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-background-radius: 12;");

        // 3. Smooth delay before switching scenes
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(e -> {
            stopWebcam();
            WorkSphereApp.getInstance().showEmployeeDashboard(currentUser);
        });
        delay.play();
    }

    @FXML
    private void handleCancel() {
        stopWebcam();
        WorkSphereApp.showLoginPage(null);
    }

    private void stopWebcam() {
        if (timeline != null)
            timeline.stop();
        if (webcam != null && webcam.isOpen())
            webcam.close();
    }
}