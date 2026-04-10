package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppNavigator {

    public static void reloadMainView(Stage stage) {
        try {
        	System.out.println("reloadMainView");
            FXMLLoader loader = new FXMLLoader(
                AppNavigator.class.getResource("/application/views/FrmStart.fxml")
            );

            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
