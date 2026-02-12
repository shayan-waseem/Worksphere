package worksphere.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import worksphere.model.AttendanceRecord;
import java.util.List;
import java.util.stream.Collectors;

public class LeaveApprovalController {

    @FXML private VBox rowsContainer;
    @FXML private ComboBox<String> filterType;
    @FXML private DatePicker filterDate;
    @FXML private TextField searchName;

    private ObservableList<LeaveRequest> masterLeaveList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        filterType.setItems(FXCollections.observableArrayList("Casual", "Sick", "Annual", "Emergency"));
        loadInitialData();
        refreshTable(masterLeaveList);
    }

    private void loadInitialData() {
        masterLeaveList.clear();
        masterLeaveList.addAll(
            new LeaveRequest("Zeeshan Ahmed", "Sick", "2026-01-20", "Recovering from viral fever"),
            new LeaveRequest("Sana Khan", "Casual", "2026-01-22", "Family wedding ceremony"),
            new LeaveRequest("Ayesha Malik", "Emergency", "2026-01-19", "Home maintenance issues"),
            new LeaveRequest("Babar Azam", "Annual", "2026-02-05", "Traveling out of city"),
            new LeaveRequest("Mahira Khan", "Sick", "2026-01-21", "Doctor appointment")
        );
    }

    private void refreshTable(List<LeaveRequest> list) {
        rowsContainer.getChildren().clear();
        for (LeaveRequest req : list) {
            rowsContainer.getChildren().add(createProfessionalRow(req));
        }
    }

    private HBox createProfessionalRow(LeaveRequest req) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #1e293b; -fx-padding: 18; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-width: 1;");
        
        // Employee Info
        Label name = new Label(req.getName());
        name.setPrefWidth(200);
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label type = new Label(req.getType());
        type.setPrefWidth(100);
        type.setStyle("-fx-text-fill: #94a3b8;");

        Label date = new Label(req.getDate());
        date.setPrefWidth(120);
        date.setStyle("-fx-text-fill: #94a3b8;");

        Label reason = new Label(req.getReason());
        reason.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic;");
        HBox.setHgrow(reason, Priority.ALWAYS);

        // Status Badge Logic
        Label statusBadge = new Label(req.getStatus().toUpperCase());
        statusBadge.setPrefWidth(110);
        statusBadge.setAlignment(Pos.CENTER);
        
        if (req.getStatus().equals("Pending")) {
            statusBadge.setStyle("-fx-background-color: rgba(245, 158, 11, 0.15); -fx-text-fill: #f59e0b; -fx-padding: 6 12; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 10px;");
        } else if (req.getStatus().equals("Approved")) {
            statusBadge.setStyle("-fx-background-color: rgba(16, 185, 129, 0.15); -fx-text-fill: #10b981; -fx-padding: 6 12; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 10px;");
        } else {
            statusBadge.setStyle("-fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #ef4444; -fx-padding: 6 12; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 10px;");
        }

        // Action Buttons
        Button actionBtn = new Button("Review");
        actionBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 6 15;");
        actionBtn.setOnAction(e -> handleAction(req));

        row.getChildren().addAll(name, type, date, reason, statusBadge, actionBtn);
        return row;
    }

    private void handleAction(LeaveRequest req) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave Approval");
        alert.setHeaderText("Reviewing " + req.getName() + "'s Request");
        alert.setContentText("Reason: " + req.getReason() + "\n\nChoose an action:");

        ButtonType approve = new ButtonType("Approve");
        ButtonType deny = new ButtonType("Deny");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(approve, deny, cancel);
        alert.showAndWait().ifPresent(response -> {
            if (response == approve) req.setStatus("Approved");
            else if (response == deny) req.setStatus("Denied");
            refreshTable(masterLeaveList);
        });
    }

    @FXML
    private void handleSearch() {
        String nameQuery = searchName.getText().toLowerCase();
        String typeFilter = filterType.getValue();

        List<LeaveRequest> filtered = masterLeaveList.stream()
            .filter(l -> l.getName().toLowerCase().contains(nameQuery))
            .filter(l -> typeFilter == null || l.getType().equals(typeFilter))
            .collect(Collectors.toList());

        refreshTable(filtered);
    }

    @FXML
    private void resetFilters() {
        searchName.clear();
        filterType.setValue(null);
        refreshTable(masterLeaveList);
    }

    public void initData(Stage stage, ObservableList<AttendanceRecord> attendanceRecords) {
        // Compatibility method
    }

    public static class LeaveRequest {
        private String name, type, date, reason, status;
        public LeaveRequest(String n, String t, String d, String r) {
            this.name = n; this.type = t; this.date = d; this.reason = r; this.status = "Pending";
        }
        public String getName() { return name; }
        public String getType() { return type; }
        public String getDate() { return date; }
        public String getReason() { return reason; }
        public String getStatus() { return status; }
        public void setStatus(String s) { this.status = s; }
    }
}