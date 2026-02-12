package worksphere.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;
import worksphere.model.AttendanceRecord;
import java.time.LocalDate;

public class AttendancePageController {

    @FXML private TableView<AttendanceRecord> attendanceTable;
    @FXML private TableColumn<AttendanceRecord, String> colName, colID, colTimeIn, colTimeOut, colStatus;
    @FXML private DatePicker datePicker;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterBox;
    @FXML private Label lblPresentCount, lblAbsentCount;

    private Stage stage;
    private ObservableList<AttendanceRecord> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Setup Table Columns correctly
        colName.setCellValueFactory(data -> data.getValue().getEmployeeNameProperty());
        colID.setCellValueFactory(data -> data.getValue().getEmployeeIDProperty());
        colTimeIn.setCellValueFactory(data -> data.getValue().getTimeInProperty());
        colTimeOut.setCellValueFactory(data -> data.getValue().getTimeOutProperty());
        colStatus.setCellValueFactory(data -> data.getValue().getStatusProperty());

        setupStatusColumn();

        // 2. Load data
        if (masterData.isEmpty()) {
            loadInitialData();
        }

        // 3. Set up the Search/Filter logic
        FilteredList<AttendanceRecord> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateFilter(filteredData);
        });

        if (filterBox.getItems().isEmpty()) {
            filterBox.setItems(FXCollections.observableArrayList("All Status", "Present", "Absent", "Late"));
            filterBox.setValue("All Status");
        }
        
        filterBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateFilter(filteredData);
        });

        // 4. Attach the data and update UI
        attendanceTable.setItems(filteredData);
        datePicker.setValue(LocalDate.now());
        updateSummaryCounts();
    }

    public void initData(Stage stage, ObservableList<AttendanceRecord> records) {
        this.stage = stage;
        if (records != null && !records.isEmpty()) {
            this.masterData.setAll(records);
            updateSummaryCounts();
        }
    }

    private void updateFilter(FilteredList<AttendanceRecord> filteredData) {
        filteredData.setPredicate(record -> {
            String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
            String statusFilter = filterBox.getValue();

            boolean matchesSearch = record.getEmployeeName().toLowerCase().contains(search) || 
                                   record.getEmployeeID().toLowerCase().contains(search);

            boolean matchesStatus = statusFilter == null || statusFilter.equals("All Status") || 
                                   record.getStatus().equalsIgnoreCase(statusFilter);

            return matchesSearch && matchesStatus;
        });
        updateSummaryCounts(); // Numbers update instantly during search
    }

    /**
     * Professional Logic: Calculates based on current table visibility.
     */
    private void updateSummaryCounts() {
        ObservableList<AttendanceRecord> visibleItems = attendanceTable.getItems();
        
        long present = visibleItems.stream()
                .filter(r -> !r.getStatus().equalsIgnoreCase("Absent")).count();
        long absent = visibleItems.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("Absent")).count();

        if (lblPresentCount != null) lblPresentCount.setText(String.valueOf(present));
        if (lblAbsentCount != null) lblAbsentCount.setText(String.valueOf(absent));
    }

    private void setupStatusColumn() {
        colStatus.setCellFactory(column -> new TableCell<>() {
            private final Button badge = new Button();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    badge.setText(item.toUpperCase());
                    badge.setPrefWidth(100);
                    String style = "-fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold; ";
                    
                    if (item.equalsIgnoreCase("Present")) {
                        badge.setStyle(style + "-fx-background-color: #dcfce7; -fx-text-fill: #166534;");
                    } else if (item.equalsIgnoreCase("Late")) {
                        badge.setStyle(style + "-fx-background-color: #fef9c3; -fx-text-fill: #854d0e;");
                    } else {
                        badge.setStyle(style + "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    /**
     * Professional Functionality: Saves table data to CSV file.
     */
    @FXML 
    private void handleExport() {
        StringBuilder csv = new StringBuilder();
        csv.append("Employee Name,ID,Time In,Time Out,Status\n");

        for (AttendanceRecord record : attendanceTable.getItems()) {
            csv.append(record.getEmployeeName()).append(",")
               .append(record.getEmployeeID()).append(",")
               .append(record.getTimeIn()).append(",")
               .append(record.getTimeOut()).append(",")
               .append(record.getStatus()).append("\n");
        }

        try {
            java.nio.file.Files.writeString(java.nio.file.Paths.get("WorkSphere_Attendance.csv"), csv.toString());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Report Exported");
            alert.setContentText("Attendance has been saved to WorkSphere_Attendance.csv");
            alert.showAndWait();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleMarkAttendance() { 
        System.out.println("Opening attendance entry dialog..."); 
    }

    private void loadInitialData() {
        masterData.addAll(
            new AttendanceRecord("Zeeshan Ahmed", "EMP-101", "08:50 AM", "05:00 PM", "Present"),
            new AttendanceRecord("Sana Khan", "EMP-102", "09:10 AM", "05:05 PM", "Late"),
            new AttendanceRecord("Bilal Siddiqui", "EMP-103", "08:55 AM", "05:00 PM", "Present"),
            new AttendanceRecord("Ayesha Malik", "EMP-104", "---", "---", "Absent"),
            new AttendanceRecord("Usman Qureshi", "EMP-105", "09:20 AM", "05:15 PM", "Late"),
            new AttendanceRecord("Hamza Ali", "EMP-106", "08:45 AM", "04:55 PM", "Present"),
            new AttendanceRecord("Maryam Jameel", "EMP-107", "09:05 AM", "05:00 PM", "Late"),
            new AttendanceRecord("Faisal Raza", "EMP-108", "---", "---", "Absent"),
            new AttendanceRecord("Zainab Bibi", "EMP-109", "08:58 AM", "05:02 PM", "Present"),
            new AttendanceRecord("Omer Farooq", "EMP-110", "09:00 AM", "05:00 PM", "Present"),
            new AttendanceRecord("Hina Shah", "EMP-111", "09:45 AM", "05:30 PM", "Late"),
            new AttendanceRecord("Saad Mansoor", "EMP-112", "---", "---", "Absent"),
            new AttendanceRecord("Khurram Shahzad", "EMP-113", "08:30 AM", "04:30 PM", "Present"),
            new AttendanceRecord("Amna Sheikh", "EMP-114", "09:02 AM", "05:00 PM", "Late"),
            new AttendanceRecord("Tariq Mahmood", "EMP-115", "08:50 AM", "05:10 PM", "Present"),
            new AttendanceRecord("Irfan Lodhi", "EMP-116", "---", "---", "Absent"),
            new AttendanceRecord("Nida Hassan", "EMP-117", "09:15 AM", "05:15 PM", "Late"),
            new AttendanceRecord("Mubeen Ashraf", "EMP-118", "08:55 AM", "05:00 PM", "Present"),
            new AttendanceRecord("Fatima Batool", "EMP-119", "08:40 AM", "05:00 PM", "Present"),
            new AttendanceRecord("Rizwan Haider", "EMP-120", "---", "---", "Absent"),
            new AttendanceRecord("Arsalan Baig", "EMP-121", "08:55 AM", "05:00 PM", "Present"),
            new AttendanceRecord("Mahira Khan", "EMP-122", "09:30 AM", "05:30 PM", "Late"),
            new AttendanceRecord("Babar Azam", "EMP-123", "08:00 AM", "04:00 PM", "Present"),
            new AttendanceRecord("Shaheen Afridi", "EMP-124", "---", "---", "Absent"),
            new AttendanceRecord("Sajal Aly", "EMP-125", "09:05 AM", "05:00 PM", "Late")
        );
    }
}