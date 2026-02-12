package worksphere;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import worksphere.controller.LeaveApprovalController;
import worksphere.model.AttendanceRecord;
import javafx.collections.ObservableList;

public class LeaveApprovalPage {

    /**
     * NEW: Returns the Parent node to be displayed in the Center of the AdminDashboard.
     */
    public static Parent getView(ObservableList<AttendanceRecord> attendanceRecords) {
        try {
            FXMLLoader loader = new FXMLLoader(LeaveApprovalPage.class.getResource("/worksphere/view/LeaveApprovalPage.fxml"));
            Parent root = loader.load();

            // Initialize the controller
            LeaveApprovalController controller = loader.getController();
            // Passing null for stage because we are staying on the Dashboard stage
            controller.initData(null, attendanceRecords);

            return root;
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback UI if loading fails
            VBox errorBox = new VBox(new Label("Error loading Leave Approval Page."));
            errorBox.setStyle("-fx-alignment: center; -fx-padding: 20;");
            return errorBox;
        }
    }

    /**
     * Existing method for standalone window display.
     */
    public static void show(Stage stage, ObservableList<AttendanceRecord> attendanceRecords) {
        try {
            FXMLLoader loader = new FXMLLoader(LeaveApprovalPage.class.getResource("/worksphere/view/LeaveApprovalPage.fxml"));
            Parent root = loader.load();

            LeaveApprovalController controller = loader.getController();
            controller.initData(stage, attendanceRecords);

            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(LeaveApprovalPage.class.getResource("/worksphere/view/style.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("WorkSphere - Admin Leave Approval");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}