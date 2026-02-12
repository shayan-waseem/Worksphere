package worksphere.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmployeeAttendanceController {

    @FXML private VBox attendanceContainer;
    @FXML private VBox taskContainer;

    private List<AttendanceRowData> attendanceList = new ArrayList<>();
    private List<TaskRowData> taskList = new ArrayList<>();

    @FXML
    public void initialize() {
        loadSampleData();
        refreshAttendanceUI();
        refreshTaskUI();
    }

    private void refreshAttendanceUI() {
        attendanceContainer.getChildren().clear();
        for (AttendanceRowData a : attendanceList) {
            // Updated: Dark text color (#1e293b) for visibility on white rows
            Label lblDate = new Label(a.date); lblDate.setStyle("-fx-text-fill: #1e293b; -fx-font-weight: bold;");
            Label lblIn = new Label(a.in); lblIn.setStyle("-fx-text-fill: #1e293b;");
            Label lblOut = new Label(a.out); lblOut.setStyle("-fx-text-fill: #1e293b;");
            Label lblStatus = new Label(a.status); lblStatus.setStyle("-fx-text-fill: #1e293b;");

            HBox row = new HBox(45, lblDate, lblIn, lblOut, lblStatus);
            row.setPadding(new Insets(15));
            row.setAlignment(Pos.CENTER_LEFT);
            // Added: Border and Shadow to make white rows visible on background
            row.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                         "-fx-border-color: #e2e8f0; -fx-border-width: 0.5; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");
            
            attendanceContainer.getChildren().add(row);
        }
    }

    private void refreshTaskUI() {
        taskContainer.getChildren().clear();
        for (TaskRowData t : taskList) {
            taskContainer.getChildren().add(createTaskRow(t));
        }
    }

    private HBox createTaskRow(TaskRowData t) {
        Circle avatar = new Circle(15, Color.web("#0f172a")); 
        Label name = new Label("Admin");
        name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label title = new Label(t.title);
        title.setStyle("-fx-text-fill: #334155;");
        title.setMaxWidth(300);

        Button statusBtn = new Button(t.status);
        statusBtn.setStyle("-fx-background-color: " + getStatusColor(t.status) + "; -fx-text-fill: white; -fx-background-radius: 8;");
        statusBtn.setOnAction(e -> showTaskPopup(t));

        HBox row = new HBox(20, avatar, name, title, statusBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");
        HBox.setHgrow(title, Priority.ALWAYS);
        
        return row;
    }

    private void showTaskPopup(TaskRowData t) {
        Stage pop = new Stage();
        pop.initModality(Modality.APPLICATION_MODAL);
        pop.setTitle("Task Update");

        // 1. ADDING BACKGROUND IMAGE TO POPUP
        StackPane root = new StackPane();
        root.setStyle("-fx-background-image: url('/worksphere/view/bg.jpg'); -fx-background-size: cover;");

        // Overlay to keep text readable on the background
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85);"); // Light glass effect

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        Label header = new Label(t.title);
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        
        Label desc = new Label(t.description);
        desc.setStyle("-fx-text-fill: #334155;");
        desc.setWrapText(true);

        Button completeBtn = new Button("Mark as Completed");
        completeBtn.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
        completeBtn.setOnAction(e -> {
            t.status = "Completed";
            refreshTaskUI();
            pop.close();
        });

        content.getChildren().addAll(header, desc, completeBtn);
        root.getChildren().addAll(overlay, content);

        pop.setScene(new Scene(root, 400, 300));
        pop.show();
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "Completed": return "#22c55e";
            case "Read": return "#3b82f6";
            default: return "#facc15";
        }
    }

    private void loadSampleData() {
        attendanceList.add(new AttendanceRowData("Jan 19, 2026", "09:00 AM", "Pending", "Checked In"));
        attendanceList.add(new AttendanceRowData("Jan 18, 2026", "08:55 AM", "05:10 PM", "Present"));
        taskList.add(new TaskRowData("Project Documentation", "Complete the final project report for the teacher.", "Pending"));
        taskList.add(new TaskRowData("Code Refactor", "Clean up the controller logic.", "Read"));
    }

    private static class AttendanceRowData {
        String date, in, out, status;
        AttendanceRowData(String d, String i, String o, String s) {
            this.date = d; this.in = i; this.out = o; this.status = s;
        }
    }

    private static class TaskRowData {
        String title, description, status;
        TaskRowData(String t, String d, String s) {
            this.title = t; this.description = d; this.status = s;
        }
    }
}