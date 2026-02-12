package worksphere;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import worksphere.model.Employee;
import worksphere.controller.EmployeeDashboardController;
import java.io.IOException;

public class EmployeeDashboard {

    public static void show(Stage stage, Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(EmployeeDashboard.class.getResource("/worksphere/view/EmployeeDashboard.fxml"));
            Parent root = loader.load();

            EmployeeDashboardController controller = loader.getController();
            controller.initData(stage, employee);

            Scene scene = new Scene(root);
            // Link external CSS for effects
            scene.getStylesheets().add(EmployeeDashboard.class.getResource("/worksphere/view/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Employed - Employee Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}