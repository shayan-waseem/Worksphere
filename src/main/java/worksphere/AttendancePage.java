package worksphere;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import worksphere.model.AttendanceRecord;
import worksphere.controller.AttendancePageController;
import java.io.IOException;

public class AttendancePage {

    /**
     * Loads the Attendance view as a Parent (used for centering in AdminDashboard)
     */
    public static Parent getView(ObservableList<AttendanceRecord> attendanceRecords) {
        try {
            FXMLLoader loader = new FXMLLoader(AttendancePage.class.getResource("/worksphere/view/AttendancePage.fxml"));
            Parent root = loader.load();
            
            AttendancePageController controller = loader.getController();
            
            // Fix: Ensure we have a valid list to pass to the controller
            ObservableList<AttendanceRecord> data = (attendanceRecords != null) ? 
                    attendanceRecords : 
                    javafx.collections.FXCollections.observableArrayList();
            
            controller.initData(null, data);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return new VBox(new Label("Error loading Attendance Page."));
        }
    }

    /**
     * Shows the Attendance Page in a new standalone Stage
     */
    public static void show(Stage stage, ObservableList<AttendanceRecord> attendanceRecords) {
        try {
            FXMLLoader loader = new FXMLLoader(AttendancePage.class.getResource("/worksphere/view/AttendancePage.fxml"));
            Parent root = loader.load();

            AttendancePageController controller = loader.getController();
            
            // Fix: Ensure we have a valid list to pass to the controller
            ObservableList<AttendanceRecord> data = (attendanceRecords != null) ? 
                    attendanceRecords : 
                    javafx.collections.FXCollections.observableArrayList();
            
            controller.initData(stage, data);

            Scene scene = new Scene(root, 1200, 700);
            
            try {
                scene.getStylesheets().add(AttendancePage.class.getResource("/worksphere/view/style.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS not found, using FXML inline styles.");
            }

            stage.setScene(scene);
            stage.setTitle("WorkSphere - Attendance Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}