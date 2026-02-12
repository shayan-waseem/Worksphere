package worksphere.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import worksphere.model.AttendanceRecord;
import worksphere.model.SalaryRecord;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class SalaryManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> monthFilter;
    @FXML private VBox salaryRowsContainer;
    
    @FXML private Label totalPayrollLabel;
    @FXML private Label totalDeductionLabel;
    @FXML private Label employeeCountLabel;

    private Stage stage;
    private ObservableList<AttendanceRecord> attendanceRecords;
    private ObservableList<SalaryRecord> salaryRecords = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (monthFilter != null) {
            monthFilter.setItems(FXCollections.observableArrayList("All Months", "January", "February", "March"));
            monthFilter.setValue("All Months");
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterData());
        }
    }

    public void initData(Stage stage, ObservableList<AttendanceRecord> attendanceRecords) {
        this.stage = stage;
        this.attendanceRecords = attendanceRecords;

        if (attendanceRecords != null && !attendanceRecords.isEmpty()) {
            generateSalariesFromAttendance();
        } else {
            loadMockData(); 
        }

        refreshTable(salaryRecords); 
    }

    private void generateSalariesFromAttendance() {
        salaryRecords.clear();
        var groupedData = attendanceRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeName));

        groupedData.forEach((name, records) -> {
            long presentDays = records.stream()
                    .filter(r -> !r.getStatus().equalsIgnoreCase("Absent")).count();
            long absentDays = records.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase("Absent")).count();

            double basic = 60000.0; 
            double deductionPerDay = 2500.0;
            double totalDeduction = absentDays * deductionPerDay;
            String color = (totalDeduction > 0) ? "#ff4d4d" : "#00d4aa";

            salaryRecords.add(new SalaryRecord(name, (int)presentDays, basic, totalDeduction, color));
        });
    }

    private void loadMockData() {
        salaryRecords.clear();
        // Data perfectly synced with your Attendance Master List
        salaryRecords.addAll(
            new SalaryRecord("Zeeshan Ahmed", 22, 75000, 0, "#00d4aa"),
            new SalaryRecord("Sana Khan", 21, 75000, 1500, "#ff6b6b"), 
            new SalaryRecord("Bilal Siddiqui", 22, 85000, 0, "#00d4aa"),
            new SalaryRecord("Ayesha Malik", 0, 60000, 60000, "#ff4d4d"), 
            new SalaryRecord("Usman Qureshi", 20, 65000, 2000, "#ff6b6b"),
            new SalaryRecord("Hamza Ali", 22, 90000, 0, "#00d4aa"),
            new SalaryRecord("Maryam Jameel", 21, 70000, 1500, "#ff6b6b"),
            new SalaryRecord("Faisal Raza", 0, 55000, 55000, "#ff4d4d"), 
            new SalaryRecord("Zainab Bibi", 22, 45000, 0, "#00d4aa"),
            new SalaryRecord("Omer Farooq", 22, 110000, 0, "#00d4aa"),
            new SalaryRecord("Hina Shah", 19, 62000, 4500, "#ff6b6b"),
            new SalaryRecord("Saad Mansoor", 0, 50000, 50000, "#ff4d4d"), 
            new SalaryRecord("Khurram Shahzad", 22, 95000, 0, "#00d4aa"),
            new SalaryRecord("Amna Sheikh", 21, 68000, 1000, "#ff6b6b"),
            new SalaryRecord("Tariq Mahmood", 22, 72000, 0, "#00d4aa"),
            new SalaryRecord("Irfan Lodhi", 0, 55000, 55000, "#ff4d4d"), 
            new SalaryRecord("Nida Hassan", 20, 64000, 2500, "#ff6b6b"),
            new SalaryRecord("Mubeen Ashraf", 22, 70000, 0, "#00d4aa"),
            new SalaryRecord("Fatima Batool", 22, 75000, 0, "#00d4aa"),
            new SalaryRecord("Rizwan Haider", 0, 50000, 50000, "#ff4d4d"), 
            new SalaryRecord("Arsalan Baig", 22, 78000, 0, "#00d4aa"),
            new SalaryRecord("Mahira Khan", 20, 120000, 3000, "#ff6b6b"),
            new SalaryRecord("Babar Azam", 22, 250000, 0, "#00d4aa"), 
            new SalaryRecord("Shaheen Afridi", 0, 180000, 180000, "#ff4d4d"), 
            new SalaryRecord("Sajal Aly", 21, 130000, 2000, "#ff6b6b")
        );
    }

    private void refreshTable(ObservableList<SalaryRecord> list) {
        if (salaryRowsContainer == null) return;
        
        salaryRowsContainer.getChildren().clear();
        double grandTotal = 0;
        double totalDeductions = 0;

        for (SalaryRecord record : list) {
            salaryRowsContainer.getChildren().add(createRow(record));
            grandTotal += record.getTotalSalary();
            totalDeductions += record.getDeductions();
        }

        // Update the cards at the top
        if (totalPayrollLabel != null) 
            totalPayrollLabel.setText("Rs. " + String.format("%,.0f", grandTotal));
        if (totalDeductionLabel != null) 
            totalDeductionLabel.setText("Rs. " + String.format("%,.0f", totalDeductions));
        if (employeeCountLabel != null) 
            employeeCountLabel.setText(String.valueOf(list.size()));
    }

    private HBox createRow(SalaryRecord r) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

        // Hover Effect
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 15; -fx-background-color: #f8fafc; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;"));

        // Name & ID Column
        VBox nameCol = new VBox(2);
        nameCol.setPrefWidth(200);
        Label nameLabel = new Label(r.getEmployeeName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-size: 14px;");
        Label idLabel = new Label("EMP-ID: " + (101 + salaryRecords.indexOf(r))); 
        idLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        nameCol.getChildren().addAll(nameLabel, idLabel);

        // Attendance Column
        Label att = new Label(r.getAttendanceDays() + " Days");
        att.setPrefWidth(120);
        att.setStyle("-fx-text-fill: #475569;");

        // Basic Salary
        Label basic = new Label("Rs. " + String.format("%,.0f", r.getBasicSalary()));
        basic.setPrefWidth(120);

        // Deductions (Red if > 0)
        Label deduct = new Label("-Rs. " + String.format("%,.0f", r.getDeductions()));
        deduct.setPrefWidth(120);
        deduct.setStyle("-fx-text-fill: " + (r.getDeductions() > 0 ? "#ef4444" : "#64748b") + "; -fx-font-weight: bold;");

        // Net Salary (Highlight)
        Label total = new Label("Rs. " + String.format("%,.0f", r.getTotalSalary()));
        total.setPrefWidth(120);
        total.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: 800;");

        // Status Badge
        Label statusLabel = new Label(r.getAttendanceDays() > 0 ? "PROCESSED" : "ON HOLD");
        statusLabel.setStyle("-fx-background-color: " + (r.getAttendanceDays() > 0 ? "#dcfce7" : "#fee2e2") + "; " +
                             "-fx-text-fill: " + (r.getAttendanceDays() > 0 ? "#166534" : "#991b1b") + "; " +
                             "-fx-padding: 4 10; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");
        StackPane statusBadge = new StackPane(statusLabel);
        statusBadge.setPrefWidth(100);

        // Pay Button
        Button btn = new Button("Pay");
        btn.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;");
        btn.setOnAction(e -> showAlert("Transaction Successful", "Salary for " + r.getEmployeeName() + " has been processed."));
        
        row.getChildren().addAll(nameCol, att, basic, deduct, total, statusBadge, btn);
        return row;
    }

    private void filterData() {
        if (searchField == null) return;
        String query = searchField.getText().toLowerCase();
        ObservableList<SalaryRecord> filtered = salaryRecords.stream()
                .filter(r -> r.getEmployeeName().toLowerCase().contains(query))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        refreshTable(filtered);
    }

    @FXML 
    private void handleExport() {
        StringBuilder csv = new StringBuilder();
        csv.append("Employee Name,Attendance Days,Basic Salary,Deductions,Net Pay\n");
        for (SalaryRecord r : salaryRecords) {
            csv.append(r.getEmployeeName()).append(",")
               .append(r.getAttendanceDays()).append(",")
               .append(r.getBasicSalary()).append(",")
               .append(r.getDeductions()).append(",")
               .append(r.getTotalSalary()).append("\n");
        }
        try {
            Files.writeString(Paths.get("WorkSphere_Payroll_Report.csv"), csv.toString());
            showAlert("Export Successful", "Saved as 'WorkSphere_Payroll_Report.csv'");
        } catch (Exception e) {
            showAlert("Error", "Could not save the file.");
        }
    }

    @FXML 
    private void handleGenerateSlip() {
        showAlert("Slip Generation", "Generating and emailing monthly salary slips to all employees...");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}