package application;

import java.io.IOException;

import application.controllers.DatabaseControllerAktionen;
import application.controllers.FrmAktionenController;

import application.controllers.FrmStartController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;


public class SceneManager {
	public enum SceneType {
	    START,
	    AKTIONEN,
	    DETAILS
	}
	
	
//	private static Stack<SceneType> history = new Stack<>();
//	private static SceneType currentScene;

    private static Parent startRoot;
    private static FXMLLoader startLoader;

    private static Parent scene2Root;
    private static FXMLLoader scene2Loader;
    
//    private static Parent scene3Root;
//    private static FXMLLoader scene3Loader;
    
    

    public static void showStart(Stage stage) throws IOException {
    	
        if (startRoot == null) {
            startLoader = new FXMLLoader(
                SceneManager.class.getResource("/application/views/FrmStart.fxml")
            );
            startRoot = startLoader.load();
            System.out.println("showStart-FrmStart (geladen)");
        }
        stage.getScene().setRoot(startRoot);
        FrmStartController controller = startLoader.getController();
        controller.onShow();
    }

    public static void showScene2(Stage stage, DatabaseControllerAktionen dbController) throws Exception {

        if (scene2Root == null) {
            scene2Loader = new FXMLLoader(
                SceneManager.class.getResource("/application/views/FrmAktionen.fxml")
            );
            scene2Root = scene2Loader.load();
            System.out.println("showScene2 (geladen)");
        }

        FrmAktionenController controller = scene2Loader.getController();
        controller.setDbControllerAktionen(dbController); // 🔥 JEDES MAL

        stage.getScene().setRoot(scene2Root);
        controller.onShow();
    }




}
