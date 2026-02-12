package worksphere.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import worksphere.model.Task;
import worksphere.model.AttendanceRecord;

import java.util.List;

public class TeamManagementController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> colTeam;
    @FXML private TableColumn<Task, String> colTask;
    @FXML private TableColumn<Task, String> colStatus;
    @FXML private TableColumn<Task, String> colPriority;
    @FXML private FlowPane teamFlowPane;

    private static final ObservableList<Task> masterTaskList = FXCollections.observableArrayList();

    public static class TeamMember {
        private String name, id, timeIn, timeOut, status;
        public TeamMember(String n, String i, String ti, String to, String s) {
            this.name = n; this.id = i; this.timeIn = ti; this.timeOut = to; this.status = s;
        }
        public String getName() { return name; }
        public String getId() { return id; }
        public String getTimeIn() { return timeIn; }
        public String getTimeOut() { return timeOut; }
        public String getStatus() { return status; }
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        if (masterTaskList.isEmpty()) {
            loadInitialData();
        }
        taskTable.setItems(masterTaskList);
        renderTeamCards();
    }

    private void setupTableColumns() {
        colTeam.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        colTask.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        colStatus.setCellFactory(column -> new TableCell<>() {
            private final Label badge = new Label();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    badge.setText(item.toUpperCase());
                    badge.setPadding(new Insets(5, 12, 5, 12));
                    badge.setStyle("-fx-background-radius: 15; -fx-font-size: 10; -fx-font-weight: bold;");
                    
                    if (item.equalsIgnoreCase("Completed")) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #dcfce7; -fx-text-fill: #166534;");
                    } else if (item.equalsIgnoreCase("In Progress")) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #dbeafe; -fx-text-fill: #1e40af;");
                    } else {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #f1f5f9; -fx-text-fill: #475569;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void renderTeamCards() {
        teamFlowPane.getChildren().clear();
        String[] teams = {"Junior", "Senior", "UI/UX", "Backend", "Fullstack"};
        String[] colors = {"#60a5fa", "#fb923c", "#a78bfa", "#34d399", "#f87171"};

        for (int i = 0; i < teams.length; i++) {
            VBox card = createTeamCard(teams[i], colors[i]);
            teamFlowPane.getChildren().add(card);
        }
    }

    private VBox createTeamCard(String name, String accentColor) {
        VBox card = new VBox(0);
        card.setPrefSize(200, 260);
        card.setAlignment(Pos.TOP_CENTER);
        
        String baseStyle = "-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
        card.setStyle(baseStyle);

        Region topBar = new Region();
        topBar.setPrefHeight(80);
        topBar.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 15 15 0 0;");

        VBox content = new VBox(12);
        content.setPadding(new Insets(-35, 10, 20, 10));
        content.setAlignment(Pos.CENTER);

        // --- UPDATED: CIRCLE WITH LOGO ---
        StackPane iconStack = new StackPane();
        Circle avatarCircle = new Circle(40, Color.WHITE);
        avatarCircle.setStroke(Color.web("#f1f5f9"));
        avatarCircle.setStrokeWidth(3);

        ImageView logoView = new ImageView();
        try {
            // Converts "UI/UX" to "uiux.png", "Junior" to "junior.png"
            String fileName = name.toLowerCase().replace("/", "") + ".png";
            Image logo = new Image(getClass().getResourceAsStream("/worksphere/images/" + fileName));
            logoView.setImage(logo);
            logoView.setFitWidth(45);
            logoView.setFitHeight(45);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            // Fallback if image is missing
            System.err.println("Logo not found for: " + name);
        }

        iconStack.getChildren().addAll(avatarCircle, logoView);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label idLabel = new Label("ID: " + name.toUpperCase() + "-UNIT");
        idLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11;");

        Button actionBtn = new Button("View Members");
        actionBtn.setPrefWidth(130);
        actionBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;");
        
        actionBtn.setOnAction(e -> showTeamRosterPopup(name));

        content.getChildren().addAll(iconStack, nameLabel, idLabel, actionBtn);
        card.getChildren().addAll(topBar, content);

        card.setOnMouseEntered(e -> card.setStyle(baseStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(baseStyle + "-fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
        
        return card;
    }

    private void showTeamRosterPopup(String teamName) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Team Roster: " + teamName);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8fafc;");

        Label title = new Label(teamName + " Team Members");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label hint = new Label("Double-click an employee to assign a task.");
        hint.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11; -fx-font-style: italic;");

        TableView<TeamMember> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<TeamMember, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<TeamMember, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<TeamMember, String> inCol = new TableColumn<>("Time In");
        inCol.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
        TableColumn<TeamMember, String> outCol = new TableColumn<>("Time Out");
        outCol.setCellValueFactory(new PropertyValueFactory<>("timeOut"));
        TableColumn<TeamMember, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(nameCol, idCol, inCol, outCol, statusCol);
        table.setItems(getMembersByTeam(teamName));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> {
            TableRow<TeamMember> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    TeamMember member = row.getItem();
                    handleAssignTaskToMember(member, teamName);
                }
            });
            return row;
        });

        root.getChildren().addAll(title, hint, table);
        Scene scene = new Scene(root, 700, 500);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void handleAssignTaskToMember(TeamMember member, String teamName) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Assign Task");
        dialog.setHeaderText("Assigning task to: " + member.getName());

        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        VBox layout = new VBox(10);
        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter task description...");
        ComboBox<String> priorityInput = new ComboBox<>(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
        priorityInput.setValue("Medium");
        priorityInput.setMaxWidth(Double.MAX_VALUE);

        layout.getChildren().addAll(new Label("Task Description:"), taskInput, new Label("Priority:"), priorityInput);
        dialog.getDialogPane().setContent(layout);

        dialog.setResultConverter(dialogButton -> (dialogButton == assignButtonType) ? taskInput.getText() : null);

        dialog.showAndWait().ifPresent(description -> {
            if (!description.trim().isEmpty()) {
                Task newTask = new Task(0, teamName, "[" + member.getName() + "] " + description, "Pending", priorityInput.getValue());
                masterTaskList.add(0, newTask);
                taskTable.refresh();
            }
        });
    }

    private ObservableList<TeamMember> getMembersByTeam(String teamName) {
        ObservableList<TeamMember> members = FXCollections.observableArrayList();
        List<TeamMember> fullList = List.of(
            new TeamMember("Zeeshan Ahmed", "EMP-101", "08:50 AM", "05:00 PM", "Present"),
            new TeamMember("Sana Khan", "EMP-102", "09:10 AM", "05:05 PM", "Late"),
            new TeamMember("Bilal Siddiqui", "EMP-103", "08:55 AM", "05:00 PM", "Present"),
            new TeamMember("Ayesha Malik", "EMP-104", "---", "---", "Absent"),
            new TeamMember("Usman Qureshi", "EMP-105", "09:20 AM", "05:15 PM", "Late"),
            new TeamMember("Hamza Ali", "EMP-106", "08:45 AM", "04:55 PM", "Present"),
            new TeamMember("Maryam Jameel", "EMP-107", "09:05 AM", "05:00 PM", "Late"),
            new TeamMember("Faisal Raza", "EMP-108", "---", "---", "Absent"),
            new TeamMember("Zainab Bibi", "EMP-109", "08:58 AM", "05:02 PM", "Present"),
            new TeamMember("Omer Farooq", "EMP-110", "09:00 AM", "05:00 PM", "Present"),
            new TeamMember("Hina Shah", "EMP-111", "09:45 AM", "05:30 PM", "Late"),
            new TeamMember("Saad Mansoor", "EMP-112", "---", "---", "Absent"),
            new TeamMember("Khurram Shahzad", "EMP-113", "08:30 AM", "04:30 PM", "Present"),
            new TeamMember("Amna Sheikh", "EMP-114", "09:02 AM", "05:00 PM", "Late"),
            new TeamMember("Tariq Mahmood", "EMP-115", "08:50 AM", "05:10 PM", "Present"),
            new TeamMember("Irfan Lodhi", "EMP-116", "---", "---", "Absent"),
            new TeamMember("Nida Hassan", "EMP-117", "09:15 AM", "05:15 PM", "Late"),
            new TeamMember("Mubeen Ashraf", "EMP-118", "08:55 AM", "05:00 PM", "Present"),
            new TeamMember("Fatima Batool", "EMP-119", "08:40 AM", "05:00 PM", "Present"),
            new TeamMember("Rizwan Haider", "EMP-120", "---", "---", "Absent"),
            new TeamMember("Arsalan Baig", "EMP-121", "08:55 AM", "05:00 PM", "Present"),
            new TeamMember("Mahira Khan", "EMP-122", "09:30 AM", "05:30 PM", "Late"),
            new TeamMember("Babar Azam", "EMP-123", "08:00 AM", "04:00 PM", "Present"),
            new TeamMember("Shaheen Afridi", "EMP-124", "---", "---", "Absent"),
            new TeamMember("Sajal Aly", "EMP-125", "09:05 AM", "05:00 PM", "Late")
        );

        int startIndex = 0;
        if (teamName.equals("Senior")) startIndex = 5;
        else if (teamName.equals("UI/UX")) startIndex = 10;
        else if (teamName.equals("Backend")) startIndex = 15;
        else if (teamName.equals("Fullstack")) startIndex = 20;

        for (int i = startIndex; i < startIndex + 5 && i < fullList.size(); i++) {
            members.add(fullList.get(i));
        }
        return members;
    }

    private void loadInitialData() {
        masterTaskList.addAll(
            new Task(0, "Backend", "Database Migration", "In Progress", "Critical"),
            new Task(0, "UI/UX", "Login Screen Redesign", "Completed", "High"),
            new Task(0, "Junior", "Documentation Update", "Pending", "Low")
        );
    }

    @FXML
    private void handleAddTask() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Task Assignment");
        dialog.setHeaderText("Enter task description for this team:");
        dialog.showAndWait().ifPresent(desc -> {
            if (!desc.trim().isEmpty()) {
                masterTaskList.add(new Task(0, "Admin", desc, "Pending", "Medium"));
                taskTable.refresh();
            }
        });
    }

    public void initData(ObservableList<AttendanceRecord> attendanceRecords) {}
}