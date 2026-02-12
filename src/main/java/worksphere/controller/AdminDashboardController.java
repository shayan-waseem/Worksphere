package worksphere.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import worksphere.WorkSphereApp;
import worksphere.SalaryManagementPage;
import worksphere.model.AttendanceRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboardController {

    @FXML private BorderPane mainRoot;
    @FXML private StackPane contentArea;
    @FXML private VBox dashboardOverview;
    @FXML private Rectangle contentClip;
    @FXML private ImageView bgView;
    @FXML private Label lblClock, lblGreeting;
    @FXML private VBox card1, card2, card3, vboxActivityFeed;
    @FXML private PieChart attendancePieChart;
    @FXML private Button btnTheme;

    private Stage stage;
    private ObservableList<AttendanceRecord> attendanceRecords;
    private boolean isDarkMode = true;

    @FXML
    private void initialize() {
        setupClipping();
        startClock();
        setupHoverAnimations();

        // Bind background size
        bgView.fitWidthProperty().bind(contentArea.widthProperty());
        bgView.fitHeightProperty().bind(contentArea.heightProperty());
        bgView.setPreserveRatio(false);
        bgView.setMouseTransparent(true);
    }

    public void initData(Stage stage, ObservableList<AttendanceRecord> records) {
        this.stage = stage;
        this.attendanceRecords = records;
        
        // Initialize dynamic components after data is available
        setupDashboardFeatures();
    }

    private void setupDashboardFeatures() {
        initializePieChart();
        setupActivityListener();
    }

    private void initializePieChart() {
        if (attendanceRecords == null || attendanceRecords.isEmpty()) {
            // Fallback demo data if list is empty
            attendancePieChart.getData().setAll(
                new PieChart.Data("Present", 1),
                new PieChart.Data("Late", 1),
                new PieChart.Data("Absent", 1)
            );
        } else {
            // Calculate real stats from the Master Data
            long presentCount = attendanceRecords.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase("Present")).count();
            long lateCount = attendanceRecords.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase("Late")).count();
            long absentCount = attendanceRecords.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase("Absent")).count();

            attendancePieChart.getData().setAll(
                new PieChart.Data("Present (" + presentCount + ")", presentCount),
                new PieChart.Data("Late (" + lateCount + ")", lateCount),
                new PieChart.Data("Absent (" + absentCount + ")", absentCount)
            );
        }

        // Assign specific colors to chart slices
        String[] colors = {"#10b981", "#f59e0b", "#ef4444"}; // Green, Orange, Red
        int i = 0;
        for (PieChart.Data data : attendancePieChart.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
            }
            i++;
        }
        
        attendancePieChart.setLegendVisible(true);
        attendancePieChart.setLabelsVisible(true);
    }

    private void setupActivityListener() {
        if (attendanceRecords == null) return;

        // Clear existing placeholder rows
        vboxActivityFeed.getChildren().clear();

        // 1. Show existing data as initial logs (Limited to 8 most recent)
        int limit = Math.min(attendanceRecords.size(), 8);
        for (int i = 0; i < limit; i++) {
            AttendanceRecord record = attendanceRecords.get(i);
            addActivityLog(record.getEmployeeName() + " recorded as " + record.getStatus());
        }

        // 2. Add listener for future changes
        attendanceRecords.addListener((ListChangeListener<AttendanceRecord>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (AttendanceRecord record : change.getAddedSubList()) {
                        addActivityLog("New Entry: " + record.getEmployeeName() + " (" + record.getStatus() + ")");
                        initializePieChart(); // Refresh chart when data changes
                    }
                }
            }
        });
        
        addActivityLog("System Secure: Dashboard ready.");
    }

    private void addActivityLog(String message) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 15, 10, 15));
        row.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; " +
                     "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 0.5;");

        Circle indicator = new Circle(4, Color.web("#38bdf8"));
        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px; -fx-font-weight: 500;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label timeLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        row.getChildren().addAll(indicator, msgLabel, spacer, timeLabel);
        // Add to top of the list
        vboxActivityFeed.getChildren().add(0, row);
    }

    // ... (rest of your theme and page loading logic remains the same)
    
    @FXML
    private void toggleTheme(ActionEvent event) {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            mainRoot.setStyle("-fx-background-color: #0f172a;");
            btnTheme.setText("☀️");
            contentArea.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 30 0 0 30;");
        } else {
            mainRoot.setStyle("-fx-background-color: #f1f5f9;");
            btnTheme.setText("🌙");
            contentArea.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 30 0 0 30;");
        }
    }

    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            lblClock.setText(now.format(DateTimeFormatter.ofPattern("EEEE, MMM dd • hh:mm:ss a")));
            
            int hour = now.getHour();
            if (hour < 12) lblGreeting.setText("Good Morning, Admin 👋");
            else if (hour < 17) lblGreeting.setText("Good Afternoon, Admin ☀️");
            else lblGreeting.setText("Good Evening, Admin 🌙");
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void setupHoverAnimations() {
        VBox[] cards = {card1, card2, card3};
        for (VBox card : cards) {
            if (card == null) continue;
            card.setOnMouseEntered(e -> {
                card.setScaleX(1.03);
                card.setScaleY(1.03);
            });
            card.setOnMouseExited(e -> {
                card.setScaleX(1.0);
                card.setScaleY(1.0);
            });
        }
    }

    private void setupClipping() {
        contentArea.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            if (contentClip != null && newBounds.getWidth() > 0) {
                contentClip.setWidth(newBounds.getWidth());
                contentClip.setHeight(newBounds.getHeight());
            }
        });
    }

    @FXML private void handleDashboard(ActionEvent event) { contentArea.getChildren().setAll(bgView, dashboardOverview); }
    @FXML private void handleSalary(ActionEvent event) { displayPage(SalaryManagementPage.getView(this.attendanceRecords)); }
    @FXML private void handleAttendance(ActionEvent event) { loadPage("AttendancePage.fxml"); }
    @FXML private void handleTeams(ActionEvent event) { loadPage("TeamManagementPage.fxml"); }
    @FXML private void handleLeave(ActionEvent event) { loadPage("LeaveApprovalPage.fxml"); }

    private void displayPage(Parent page) {
        if (page instanceof Region region) {
            region.prefWidthProperty().bind(contentArea.widthProperty());
            region.prefHeightProperty().bind(contentArea.heightProperty());
        }
        contentArea.getChildren().setAll(bgView, page);
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/worksphere/view/" + fxmlFile));
            Parent page = loader.load();
            
            // Check if the loaded page controller needs data
            Object controller = loader.getController();
            if (controller instanceof AttendancePageController attCtrl) {
                attCtrl.initData(stage, attendanceRecords);
            }
            
            displayPage(page);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load " + fxmlFile, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to leave?");
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) WorkSphereApp.showLoginPage(stage);
        });
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}