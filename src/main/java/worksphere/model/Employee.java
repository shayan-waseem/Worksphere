package worksphere.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Employee {
    private String id;
    private String name;
    private String password;
    private boolean isAdmin;
    private double salary; // Added for the Dashboard feature

    public Employee(String id, String name, String password, boolean isAdmin, double salary) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
        this.salary = salary;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return isAdmin; }
    
    // New Getter for Salary
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }
}