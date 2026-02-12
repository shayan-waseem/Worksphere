package worksphere;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import worksphere.controller.TeamManagementController;
import worksphere.model.AttendanceRecord;
import javafx.collections.ObservableList;

public class TeamManagementPage {

    public static Parent getView(ObservableList<AttendanceRecord> attendanceRecords) {
        try {
            FXMLLoader loader = new FXMLLoader(TeamManagementPage.class.getResource("/worksphere/view/TeamManagement.fxml"));
            Parent root = loader.load();

            TeamManagementController controller = loader.getController();
            controller.initData(attendanceRecords);

            return root;
        } catch (Exception e) {
            e.printStackTrace();
            VBox errorBox = new VBox(new Label("Unable to load Team Management."));
            errorBox.setAlignment(javafx.geometry.Pos.CENTER);
            return errorBox;
        }
    }
}