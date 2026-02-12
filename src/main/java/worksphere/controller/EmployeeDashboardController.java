package worksphere.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import worksphere.WorkSphereApp;
import worksphere.Utils.DatabaseHelper;
import worksphere.model.Employee;
import worksphere.model.Task;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class EmployeeDashboardController {

    @FXML private ImageView bgView;
    @FXML private VBox mainContainer;
    @FXML private Label timeLabel, userNameLabel, salaryLabel, avatarLabel;
    @FXML private TextField searchField;

    private Stage stage;
    private Employee employee;
    private boolean isSalaryHidden = true;
    private List<Task> allTasks = new ArrayList<>(); // For Searching

    public void initData(Stage stage, Employee employee) {
        this.stage = stage;
        this.employee = employee;

        if (employee != null) {
            userNameLabel.setText(employee.getName());
            avatarLabel.setText(employee.getName().substring(0, 2).toUpperCase());
        }

        if (bgView != null && this.stage != null) {
            bgView.fitWidthProperty().bind(this.stage.widthProperty());
            bgView.fitHeightProperty().bind(this.stage.heightProperty());
        }

        startClock();
        javafx.application.Platform.runLater(this::showDashboard);
    }

    private void startClock() {
        javafx.animation.Timeline clock = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.ZERO, e -> {
                    timeLabel.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a")));
                }), new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1)));
        clock.setCycleCount(javafx.animation.Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void handleViewSalary() {
        if (isSalaryHidden) {
            // Assume getSalary() exists in your Employee Model
            salaryLabel.setText("PKR" + String.format("%.2f", employee.getSalary()));
            isSalaryHidden = false;
        } else {
            salaryLabel.setText("••••••");
            isSalaryHidden = true;
        }
    }

    @FXML
    private void showDashboard() {
        loadPage("/worksphere/view/EmployeeOverview.fxml");
        javafx.application.Platform.runLater(this::loadDashboardData);
    }

    private void loadDashboardData() {
        if (employee == null) return;
        
        allTasks.clear();
        PriorityQueue<Task> taskQueue = new PriorityQueue<>();

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sqlTasks = "SELECT id, team_name, description, status, priority FROM tasks WHERE emp_id = ?";
            PreparedStatement psTask = conn.prepareStatement(sqlTasks);
            psTask.setString(1, employee.getId());
            ResultSet rsT = psTask.executeQuery();

            while (rsT.next()) {
                Task t = new Task(rsT.getInt("id"), rsT.getString("team_name"), 
                                 rsT.getString("description"), rsT.getString("status"), 
                                 rsT.getString("priority"));
                taskQueue.add(t);
                allTasks.add(t);
            }
            renderTaskQueue(taskQueue);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void renderTaskQueue(PriorityQueue<Task> queue) {
        VBox taskListUI = (VBox) stage.getScene().lookup("#taskContainer");
        if (taskListUI == null) return;
        taskListUI.getChildren().clear();

        if (queue.isEmpty()) {
            taskListUI.getChildren().add(new Label("No tasks assigned yet."));
            return;
        }

        while (!queue.isEmpty()) {
            taskListUI.getChildren().add(createTaskRow(queue.poll()));
        }
    }

    private HBox createTaskRow(Task t) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("task-card"); // Add CSS class for hover effects
        row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 15; -fx-background-radius: 12;");

        CheckBox cb = new CheckBox();
        cb.setSelected(t.getStatus().equalsIgnoreCase("Completed"));
        cb.setOnAction(e -> updateTaskStatus(t, cb.isSelected()));

        VBox textContent = new VBox(2);
        Label title = new Label(t.getDescription());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label sub = new Label("Team: " + t.getTeamName());
        sub.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11;");
        textContent.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label priorityTag = new Label(t.getPriority().toUpperCase());
        String color = t.getPriority().equalsIgnoreCase("High") ? "#f87171" : "#fbbf24";
        priorityTag.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 10; -fx-padding: 3 8; " +
                "-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 5; -fx-border-color: " + color + "; -fx-border-radius: 5;");

        row.getChildren().addAll(cb, textContent, spacer, priorityTag);
        return row;
    }

    private void updateTaskStatus(Task t, boolean completed) {
        String newStatus = completed ? "Completed" : "Pending";
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "UPDATE tasks SET status = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadPage(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            if (mainContainer != null) mainContainer.getChildren().setAll(node);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void showLeave() { loadPage("/worksphere/view/EmployeeLeavePage.fxml"); }
    @FXML private void showAttendance() { loadPage("/worksphere/view/EmployeeAttendanceTaskPage.fxml"); }
    @FXML private void handleLogout() { WorkSphereApp.showLoginPage(stage); }
}