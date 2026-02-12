package worksphere.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.collections.ObservableList;
import worksphere.WorkSphereApp;
import worksphere.model.AttendanceRecord;
import worksphere.model.Employee;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class LoginController {

    // FXML UI Elements
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // Data members
    private Map<String, Employee> employeeMap;
    private Set<String> validUserIds;
    private ObservableList<AttendanceRecord> attendanceRecords;

    public LoginController() {
    }

    public void setInitData(Map<String, Employee> employeeMap, 
                            ObservableList<AttendanceRecord> attendanceRecords, 
                            Set<String> validUserIds) {
        this.employeeMap = employeeMap;
        this.attendanceRecords = attendanceRecords;
        this.validUserIds = validUserIds;
    }

    @FXML
    private void handleLogin() {
        String enteredEmail = emailField.getText().trim();
        String enteredId = usernameField.getText().trim();
        String enteredPassword = passwordField.getText();

        // 1. Check for empty fields
        if (enteredEmail.isEmpty() || enteredId.isEmpty() || enteredPassword.isEmpty()) {
            showAlert("Error", "Please fill in all fields (Email, Username, and Password).", Alert.AlertType.WARNING);
            return;
        }

        // 2. Email format validation
        if (!enteredEmail.contains("@") || !enteredEmail.contains(".")) {
            showAlert("Invalid Email", "Please enter a valid email address.", Alert.AlertType.ERROR);
            return;
        }

        // 3. Authenticate User
        Employee user = login(enteredId, enteredPassword);

        if (user != null) {
            // --- UPDATED ROLE-BASED NAVIGATION ---
            if (user.isAdmin()) {
                // Admins bypass the camera and go straight to the dashboard
                System.out.println("Admin login detected. Bypassing camera.");
                WorkSphereApp.getInstance().showAdminDashboard();
            } else {
                // Employees must go through the camera verification
                System.out.println("Employee login detected. Opening camera popup.");
                WorkSphereApp.getInstance().showCameraAttendancePopup(user);
            }
        } else {
            showAlert("Login Failed", "Invalid credentials. Please check your username and password.", Alert.AlertType.ERROR);
        }
    }

    public Employee login(String enteredId, String enteredPassword) {
        String key = enteredId.toLowerCase();

        if (employeeMap == null || !validUserIds.contains(key)) {
            return null;
        }

        Employee user = employeeMap.get(key);

        if (user == null || !user.getPassword().equals(enteredPassword)) {
            return null;
        }

        // Automatic Check-In for employees
        if (!user.isAdmin()) {
            attendanceRecords.stream()
                    .filter(r -> r.getEmployee().getId().equalsIgnoreCase(user.getId()))
                    .findFirst()
                    .ifPresent(r -> {
                        if (r.getTimeIn() == null) {
                            r.setTimeIn(LocalDateTime.now());
                        }
                    });
        }
        return user;
    }

    public void logout(Employee employee) {
        AttendanceRecord record = attendanceRecords.stream()
                .filter(r -> r.getEmployee().getId().equals(employee.getId()))
                .findFirst().orElse(null);
        if (record != null) {
            record.setTimeOut(LocalDateTime.now());
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}