package worksphere;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.collections.ObservableList;
import worksphere.model.AttendanceRecord;

public class AdminDashboard {
    private Stage stage;
    private BorderPane root; 
    private ObservableList<AttendanceRecord> attendanceRecords;

    public void show(Stage stage, ObservableList<AttendanceRecord> attendanceRecords) {
        this.stage = stage;
        this.attendanceRecords = attendanceRecords;

        root = new BorderPane();
        root.setStyle("-fx-background-color: black;");

        // 1. Create and Set Sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);
        
        // 2. Load Initial Content
        root.setCenter(createMainContent());

        // 3. FORCE Sidebar to the front layer (Z-index priority)
        sidebar.setViewOrder(-1.0); 

        Scene scene = new Scene(root, 1280, 720);
        scene.setFill(Color.BLACK);

        stage.setTitle("WorkSphere - Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: rgba(30, 41, 59, 0.95);");

        Label title = new Label("Employees");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button dashboardBtn = navButton("📊", "Dashboard");
        dashboardBtn.setOnAction(e -> root.setCenter(createMainContent()));

        Button teamsBtn = navButton("👥", "Teams");
        teamsBtn.setOnAction(e -> root.setCenter(TeamManagementPage.getView(attendanceRecords)));

        Button salaryBtn = navButton("💰", "Salary");
        salaryBtn.setOnAction(e -> root.setCenter(SalaryManagementPage.getView(attendanceRecords)));

        Button attendanceBtn = navButton("📅", "Attendance");
        attendanceBtn.setOnAction(e -> root.setCenter(AttendancePage.getView(attendanceRecords)));

        Button leaveBtn = navButton("📄", "Leave Approval");
        leaveBtn.setOnAction(e -> root.setCenter(LeaveApprovalPage.getView(attendanceRecords)));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = navButton("🚪", "Logout");
        styleLogoutButton(logoutBtn);
        logoutBtn.setOnAction(e -> handleLogout());

        sidebar.getChildren().addAll(title, dashboardBtn, teamsBtn, salaryBtn, attendanceBtn, leaveBtn, spacer, logoutBtn);
        return sidebar;
    }

    private StackPane createMainContent() {
        StackPane contentStack = new StackPane();
        contentStack.setPickOnBounds(false); // Clicks pass through empty areas

        try {
            ImageView bgView = new ImageView(new Image(getClass().getResource("/worksphere/view/bg.jpg").toExternalForm()));
            bgView.setPreserveRatio(false);
            bgView.fitWidthProperty().bind(contentStack.widthProperty());
            bgView.fitHeightProperty().bind(contentStack.heightProperty());
            bgView.setMouseTransparent(true); 
            contentStack.getChildren().add(bgView);
        } catch (Exception e) {
            System.err.println("Background image not found.");
        }

        contentStack.getChildren().add(createAdminDashboardContent());
        return contentStack;
    }

    private VBox createAdminDashboardContent() {
        VBox page = new VBox(35);
        page.setPadding(new Insets(30));
        page.setStyle("-fx-background-color: transparent;");

        Label title = new Label("Dashboard Overview");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox cardsRow = new HBox(25);
        cardsRow.getChildren().addAll(
            createAdminCard("Total Employees", "1089", "#00d4aa"),
            createAdminCard("Overview", "163", "#5b7cff"),
            createAdminCard("Pending", "149", "#ff6b6b")
        );

        HBox chartsRow = new HBox(25);
        chartsRow.getChildren().addAll(
            createAdminChartBox("Pending Today"),
            createAdminChartBox("Monthly Salary Impact")
        );

        page.getChildren().addAll(title, cardsRow, chartsRow);
        return page;
    }

    private VBox createAdminCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefSize(310, 150);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 16;");
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        card.getChildren().addAll(t, v);
        return card;
    }

    private VBox createAdminChartBox(String title) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setPrefWidth(500);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 18;");
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Region placeholder = new Region();
        placeholder.setPrefHeight(200);
        placeholder.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 12;");
        box.getChildren().addAll(t, placeholder);
        return box;
    }

    private Button navButton(String icon, String text) {
        HBox hbox = new HBox(12, new Label(icon), new Label(text));
        hbox.setAlignment(Pos.CENTER_LEFT);
        Button btn = new Button();
        btn.setGraphic(hbox);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-padding: 10 15;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #334155; -fx-text-fill: white;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1;"));
        return btn;
    }

    private void styleLogoutButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #991b1b; -fx-padding: 10 15;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #7f1d1d; -fx-text-fill: white;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #991b1b;"));
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText("Are you sure you want to logout?");
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                WorkSphereApp.showLoginPage(stage);
            }
        });
    }
}