package worksphere.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import worksphere.WorkSphereApp;
import worksphere.EmployeeDashboard;
import worksphere.EmployeeAttendanceTaskPage;

import java.util.ArrayList;
import java.util.List;

public class EmployeeLeaveController {

    @FXML
    private VBox leaveRowsContainer;
    @FXML
    private ComboBox<String> leaveTypeCombo;
    @FXML
    private DatePicker leaveDatePicker;
    @FXML
    private TextArea reasonArea;

    // private Stage stage;
    private static List<LeaveRequest> myLeaves = new ArrayList<>();

    // Sample data initialization
    static {
        myLeaves.add(new LeaveRequest("Casual", "2026-01-01", "Feeling unwell", "Approved"));
        myLeaves.add(new LeaveRequest("Sick", "2026-01-05", "Cold & Flu", "Denied"));
        myLeaves.add(new LeaveRequest("Annual", "2026-01-10", "Family trip", "Pending"));
    }

    @FXML
    public void initialize() {
        leaveTypeCombo.getItems().addAll("Casual", "Sick", "Annual");
        refreshLeaveTable();
    }

    /*
     * public void setStage(Stage stage) {
     * this.stage = stage;
     * }
     */

    private void refreshLeaveTable() {
        leaveRowsContainer.getChildren().clear();
        for (LeaveRequest l : myLeaves) {
            leaveRowsContainer.getChildren().add(createRow(l));
        }
    }

    private HBox createRow(LeaveRequest l) {
        HBox row = new HBox(20);
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 12;");

        Label type = new Label(l.type);
        type.setPrefWidth(150);
        type.setStyle("-fx-text-fill: white;");

        Label date = new Label(l.date);
        date.setPrefWidth(150);
        date.setStyle("-fx-text-fill: white;");

        Button statusBtn = new Button(l.status);
        statusBtn.setPrefWidth(110);
        statusBtn.setStyle(getStatusStyle(l.status));
        statusBtn.setOnAction(e -> showViewPopup(l));

        row.getChildren().addAll(type, date, statusBtn);
        return row;
    }

    private String getStatusStyle(String status) {
        switch (status) {
            case "Approved":
                return "-fx-background-color:#22c55e;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:12;";
            case "Denied":
                return "-fx-background-color:#ef4444;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:12;";
            default:
                return "-fx-background-color:#fde68a;-fx-text-fill:black;-fx-font-weight:bold;-fx-background-radius:12;";
        }
    }

    @FXML
    private void handleSendLeave() {
        if (leaveTypeCombo.getValue() != null && leaveDatePicker.getValue() != null
                && !reasonArea.getText().isEmpty()) {
            myLeaves.add(new LeaveRequest(
                    leaveTypeCombo.getValue(),
                    leaveDatePicker.getValue().toString(),
                    reasonArea.getText(),
                    "Pending"));
            refreshLeaveTable();

            // Clear fields
            leaveTypeCombo.setValue(null);
            leaveDatePicker.setValue(null);
            reasonArea.clear();
        }
    }

    private void showViewPopup(LeaveRequest l) {
        Stage pop = new Stage();
        pop.initModality(Modality.APPLICATION_MODAL);

        ImageView bgView = new ImageView(new Image("file:C:/WorkSphere/src/worksphere/view/bg2.png"));
        bgView.setFitWidth(420);
        bgView.setFitHeight(320);

        Label title = new Label("Leave Details");
        title.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#1e3a8a;");

        Label details = new Label(
                "Type: " + l.type + "\nDate: " + l.date + "\n\nReason: " + l.reason + "\n\nStatus: " + l.status);
        details.setWrapText(true);

        VBox content = new VBox(15, title, details);
        content.setPadding(new Insets(20));

        pop.setScene(new Scene(new StackPane(bgView, content), 420, 320));
        pop.show();
    }

    // Navigation
    /*
     * @FXML private void goToDashboard() { EmployeeDashboard.show(stage, null); }
     * 
     * @FXML private void goToAttendance() { EmployeeAttendanceTaskPage.show(stage);
     * }
     * 
     * @FXML private void handleLogout() { WorkSphereApp.showLoginPage(stage); }
     */

    // Model class internal for simplicity
    public static class LeaveRequest {
        String type, date, reason, status;

        LeaveRequest(String t, String d, String r, String s) {
            type = t;
            date = d;
            reason = r;
            status = s;
        }
    }
}