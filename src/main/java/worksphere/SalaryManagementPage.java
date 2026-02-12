package worksphere;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import worksphere.controller.SalaryManagementController;
import worksphere.model.AttendanceRecord;

public class SalaryManagementPage {

    /**
     * FIXES COMPILATION ERROR: 
     * This is the 'show' method LeaveApprovalController is looking for.
     */
    public static void show(Stage stage, ObservableList<AttendanceRecord> attendanceRecords) {
        try {
            FXMLLoader loader = new FXMLLoader(SalaryManagementPage.class.getResource("/worksphere/view/SalaryManagementPage.fxml"));
            Parent root = loader.load();

            SalaryManagementController controller = loader.getController();
            // Critical: Passes the data to the controller so the table isn't empty
            controller.initData(stage, attendanceRecords);

            Scene scene = new Scene(root, 1200, 700);
            // Apply styles if you have them
            if (SalaryManagementPage.class.getResource("/worksphere/view/style.css") != null) {
                scene.getStylesheets().add(SalaryManagementPage.class.getResource("/worksphere/view/style.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("WorkSphere - Salary Management");
            stage.show();
        } catch (Exception e) {
            System.err.println("Error showing SalaryManagementPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Used by AdminDashboardController to load the page as a sub-view
     */
    public static Parent getView(ObservableList<AttendanceRecord> attendanceRecords) {
        try {
            FXMLLoader loader = new FXMLLoader(SalaryManagementPage.class.getResource("/worksphere/view/SalaryManagementPage.fxml"));
            Parent root = loader.load();
            SalaryManagementController controller = loader.getController();
            controller.initData(null, attendanceRecords);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}