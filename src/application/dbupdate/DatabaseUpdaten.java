package application.dbupdate;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DatabaseUpdaten
{
	public static void fctDatabaseupdate( Window myowner) throws IOException
	{
		FXMLLoader fxmlLoader = new FXMLLoader(
			    DatabaseUpdaten.class.getResource("/application/dbupdate/DatabaseMergeView.fxml")
			);
		//FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/dbupdate/DatabaseMergeView.fxml"));
		Parent root = fxmlLoader.load();

		Stage stModalwindow = new Stage();
		Scene scene = new Scene(root);

		DatabaseMergeController controller = fxmlLoader.getController();
		controller.setStage(stModalwindow);

		stModalwindow.setTitle("Update der Datenbank...");
		stModalwindow.setResizable(false);

		// 🔹 Owner setzen → KEIN extra Taskleisten-Icon mehr
		//stModalwindow.initOwner(btnBeenden.getScene().getWindow());
		
		stModalwindow.initOwner(myowner);

		// 🔹 Modalität
		stModalwindow.initModality(Modality.APPLICATION_MODAL);

		// 🔹 Icons aus zentraler Klasse (empfohlen)
		stModalwindow.getIcons().setAll(application.AppIcons.getIcons());

		stModalwindow.setScene(scene);

		stModalwindow.setOnCloseRequest(e -> {
			e.consume();
			stModalwindow.close();
		});

		stModalwindow.showAndWait();
	}

}
