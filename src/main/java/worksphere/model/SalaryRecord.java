package worksphere.model;

/**
 * Model representing an Employee's Salary details linked to Attendance.
 */
public class SalaryRecord {
    private String employeeName;
    private int attendanceDays;
    private double basicSalary;
    private double deductions;
    private String colorCode;

    // Optimized Constructor
    public SalaryRecord(String name, int attendance, double basic, double deductions, String color) {
        this.employeeName = name;
        this.attendanceDays = attendance;
        this.basicSalary = basic;
        this.deductions = deductions;
        this.colorCode = color;
    }

    // --- Getters ---
    public String getEmployeeName() { return employeeName; }
    public int getAttendanceDays() { return attendanceDays; }
    public double getBasicSalary() { return basicSalary; }
    public double getDeductions() { return deductions; }
    public String getColorCode() { return colorCode; }
    
    /**
     * Calculates the Net Pay dynamically.
     */
    public double getTotalSalary() { 
        return basicSalary - deductions; 
    }

    // --- Setters (Essential for dynamic updates) ---
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setAttendanceDays(int attendanceDays) { this.attendanceDays = attendanceDays; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setDeductions(double deductions) { this.deductions = deductions; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
}