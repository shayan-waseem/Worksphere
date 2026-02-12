package worksphere.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AttendanceRecord {
    // Objects for logic
    private Employee employee; 
    private LocalDateTime rawTimeIn;
    private LocalDateTime rawTimeOut;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

    // Properties for TableView binding
    private final StringProperty employeeName;
    private final StringProperty employeeID;
    private final StringProperty timeIn;
    private final StringProperty timeOut;
    private final StringProperty status;

    /**
     * Constructor for manual/fake data entry (matching UI requirements)
     */
    public AttendanceRecord(String name, String id, String in, String out, String status) {
        this.employeeName = new SimpleStringProperty(name);
        this.employeeID = new SimpleStringProperty(id);
        this.timeIn = new SimpleStringProperty(in);
        this.timeOut = new SimpleStringProperty(out);
        this.status = new SimpleStringProperty(status);
    }

    /**
     * Constructor for dynamic/real-time tracking
     */
    public AttendanceRecord(Employee employee) {
        this.employee = employee;
        this.employeeName = new SimpleStringProperty(employee.getName());
        this.employeeID = new SimpleStringProperty(employee.getId()); 
        this.timeIn = new SimpleStringProperty("---");
        this.timeOut = new SimpleStringProperty("---");
        this.status = new SimpleStringProperty("Absent");
    }

    // --- GETTERS TO FIX COMPILATION ERRORS ---
    
    public String getEmployeeName() { 
        return employeeName.get(); 
    }

    public String getEmployeeID() { 
        return employeeID.get(); 
    }

    public String getStatus() { 
        return status.get(); 
    }

    public String getTimeIn() { 
        return timeIn.get(); 
    }
    
    public String getTimeOut() { 
        return timeOut.get(); 
    }

    // --- LOGIC METHODS ---

    public Employee getEmployee() {
        return this.employee; 
    }

    public void setTimeIn(LocalDateTime dateTime) {
        this.rawTimeIn = dateTime;
        this.timeIn.set(dateTime.format(formatter));
        this.status.set("Present");
    }

    public void setTimeOut(LocalDateTime dateTime) {
        this.rawTimeOut = dateTime;
        this.timeOut.set(dateTime.format(formatter));
    }

    // --- PROPERTY GETTERS (For TableView Binding) ---

    public StringProperty getEmployeeNameProperty() { return employeeName; }
    public StringProperty getEmployeeIDProperty() { return employeeID; }
    public StringProperty getTimeInProperty() { return timeIn; }
    public StringProperty getTimeOutProperty() { return timeOut; }
    public StringProperty getStatusProperty() { return status; }
}