package worksphere.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import worksphere.model.AttendanceRecord;
import java.time.LocalDate;

public class AttendanceController {

    private TableView<AttendanceRecord> tableView;
    private DatePicker calendar;
    private ObservableList<AttendanceRecord> attendanceRecords;

    public AttendanceController(TableView<AttendanceRecord> tableView, DatePicker calendar,
            ObservableList<AttendanceRecord> records) {
        this.tableView = tableView;
        this.calendar = calendar;
        this.attendanceRecords = records;
        initialize();
    }

    private void initialize() {
        tableView.setItems(attendanceRecords);
        calendar.setValue(LocalDate.now());
    }
}