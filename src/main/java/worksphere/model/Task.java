package worksphere.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Updated Task Model with ID and JavaFX Properties.
 */
public class Task implements Comparable<Task> {

    private final IntegerProperty id; // Added to identify tasks in Database
    private final StringProperty teamName;
    private final StringProperty description;
    private final StringProperty status;
    private final StringProperty priority;

    public Task(int id, String teamName, String description, String status, String priority) {
        this.id = new SimpleIntegerProperty(id);
        this.teamName = new SimpleStringProperty(teamName);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
        this.priority = new SimpleStringProperty(priority);
    }

    // ===== Priority Logic for Data Structures =====
    public int getPriorityRank() {
        String p = getPriority().toLowerCase();
        if (p.contains("high")) return 1;
        if (p.contains("med")) return 2;
        return 3;
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.getPriorityRank(), other.getPriorityRank());
    }

    // ===== Getters =====
    public int getId() { return id.get(); }
    public String getTeamName() { return teamName.get(); }
    public String getDescription() { return description.get(); }
    public String getStatus() { return status.get(); }
    public String getPriority() { return priority.get(); }

    // ===== Properties (For JavaFX Binding) =====
    public IntegerProperty idProperty() { return id; }
    public StringProperty teamNameProperty() { return teamName; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty statusProperty() { return status; }
    public StringProperty priorityProperty() { return priority; }

    // ===== Setters =====
    public void setStatus(String status) { this.status.set(status); }
}