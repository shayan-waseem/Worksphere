package worksphere;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import worksphere.controller.EmployeeDashboardController;
import worksphere.controller.FaceRecognitionController;
import worksphere.controller.LoginController;
import worksphere.model.AttendanceRecord;
import worksphere.model.Employee;

public class WorkSphereApp extends Application {
    private static WorkSphereApp instance;
    private static Stage primaryStage;

    private Map<String, Employee> employeeMap;
    private ObservableList<AttendanceRecord> attendanceRecords;
    private Set<String> validUserIds;

    @Override
    public void start(Stage stage) {
        instance = this;
        primaryStage = stage;
        initializeData();
        showLoginPage(primaryStage);
    }

    private void initializeData() {
        employeeMap = new HashMap<>();
        attendanceRecords = FXCollections.observableArrayList();
        validUserIds = new HashSet<>();

        // UPDATED: Added salary parameter (last argument) to match new Employee constructor
        Employee admin = new Employee("admin", "Admin User", "admin123", true, 7500.0);
        Employee emp1 = new Employee("employee", "John Doe", "emp123", false, 5000.0);

        Employee[] users = { admin, emp1 };
        for (Employee e : users) {
            employeeMap.put(e.getId().toLowerCase(), e);
            validUserIds.add(e.getId().toLowerCase());
        }
        attendanceRecords.add(new AttendanceRecord(emp1));
    }

    public static WorkSphereApp getInstance() {
        return instance;
    }

    public static void showLoginPage(Stage stage) {
        try {
            Stage targetStage = (stage != null) ? stage : primaryStage;
            if (targetStage == null) return;

            FXMLLoader loader = new FXMLLoader(WorkSphereApp.class.getResource("/worksphere/view/LoginPage.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            if (instance != null) {
                controller.setInitData(instance.employeeMap, instance.attendanceRecords, instance.validUserIds);
            }

            Scene scene = new Scene(root, 1280, 720);
            targetStage.setScene(scene);
            targetStage.setTitle("WorkSphere - Login");
            targetStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAdminDashboard() {
        loadScene("/worksphere/view/AdminDashboard.fxml", "WorkSphere - Admin Dashboard");
    }

    public void showEmployeeDashboard(Employee user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/worksphere/view/EmployeeDashboard.fxml"));
            Parent root = loader.load();

            EmployeeDashboardController controller = loader.getController();
            controller.initData(primaryStage, user);

            if (primaryStage.getScene() != null) {
                primaryStage.getScene().setRoot(root);
            } else {
                primaryStage.setScene(new Scene(root, 1280, 720));
            }
            primaryStage.setTitle("WorkSphere - Employee Dashboard");
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCameraAttendancePopup(Employee user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/worksphere/view/FaceRecognition.fxml"));
            Parent root = loader.load();

            FaceRecognitionController controller = loader.getController();
            controller.initData(user);

            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root, 1280, 720);
                primaryStage.setScene(scene);
            } else {
                primaryStage.getScene().setRoot(root);
            }

            primaryStage.setTitle("WorkSphere - Identity Verification");
            primaryStage.show();
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Error loading FaceRecognition.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
            primaryStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}