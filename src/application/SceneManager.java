package application;

import java.io.IOException;
import java.util.prefs.Preferences;

import application.controllers.DatabaseControllerAktionen;
import application.controllers.FrmAktionenController;
import application.controllers.FrmStartController;
import application.utils.ToolsWinHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SceneManager {

    public enum SceneType {
        START,
        AKTIONEN,
        DETAILS
    }

    private static final Preferences prefs = Preferences.userNodeForPackage(SceneManager.class);
    private static final String KEY_LAST_SCENE = "lastScene";

    private static SceneType currentScene = SceneType.START;

    private static Parent startRoot;
    private static FXMLLoader startLoader;

    private static Parent aktionenRoot;
    private static FXMLLoader aktionenLoader;
    
    private static DatabaseControllerAktionen dbController;

    // =========================
    // PUBLIC API
    // =========================

    public static void init(Stage stage, DatabaseControllerAktionen controller) throws Exception {
        dbController = controller;

        SceneType last = loadLastScene();

        switch (last) {
            case AKTIONEN:
                showAktionen(stage);
                break;
            case START:
            default:
                showStart(stage);
                break;
        }
    }

    public static void showStart(Stage stage) throws IOException {
        if (startRoot == null) {
            startLoader = new FXMLLoader(
                SceneManager.class.getResource("/application/views/FrmStart.fxml")
            );
            startRoot = startLoader.load();
            System.out.println("START geladen");
        }

        stage.getScene().setRoot(startRoot);

        FrmStartController controller = startLoader.getController();
        controller.onShow();

        setCurrentScene(SceneType.START);
    }

    public static void showAktionen(Stage stage) throws Exception {
        if (aktionenRoot == null) {
            aktionenLoader = new FXMLLoader(
                SceneManager.class.getResource("/application/views/FrmAktionen.fxml")
            );
            aktionenRoot = aktionenLoader.load();
        }

        FrmAktionenController controller = aktionenLoader.getController();
        controller.setDbControllerAktionen(dbController);

        stage.getScene().setRoot(aktionenRoot);
        controller.onShow();

        setCurrentScene(SceneType.AKTIONEN);
    }

    // =========================
    // STATE HANDLING
    // =========================

    private static void setCurrentScene(SceneType scene) {
        currentScene = scene;
        saveLastScene(scene);
    }

    public static SceneType getCurrentScene() {
        return currentScene;
    }

    private static void saveLastScene(SceneType scene) {
        prefs.put(KEY_LAST_SCENE, scene.name());
    }

    private static SceneType loadLastScene() {
        try {
            return SceneType.valueOf(
                prefs.get(KEY_LAST_SCENE, SceneType.START.name())
            );
        } catch (Exception e) {
            return SceneType.START;
        }
    }

    public static void exitApp() {
        try {
            // 🔥 UI-State speichern
            FilterState.get().save();

            // 🔥 letzte Scene merken
            saveLastScene(currentScene);

            // 🔥 Anwendung wirklich beenden
            ToolsWinHelper.closeApplication();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


//package application;
//
//import java.io.IOException;
//
//import application.controllers.DatabaseControllerAktionen;
//import application.controllers.FrmAktionenController;
//
//import application.controllers.FrmStartController;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.stage.Stage;
//
//
//public class SceneManager {
//	public enum SceneType {
//	    START,
//	    AKTIONEN,
//	    DETAILS
//	}
//	
//	
////	private static Stack<SceneType> history = new Stack<>();
////	private static SceneType currentScene;
//
//    private static Parent startRoot;
//    private static FXMLLoader startLoader;
//
//    private static Parent scene2Root;
//    private static FXMLLoader scene2Loader;
//    
////    private static Parent scene3Root;
////    private static FXMLLoader scene3Loader;
//    
//    
//
//    public static void showStart(Stage stage) throws IOException {
//    	
//        if (startRoot == null) {
//            startLoader = new FXMLLoader(
//                SceneManager.class.getResource("/application/views/FrmStart.fxml")
//            );
//            startRoot = startLoader.load();
//            System.out.println("showStart-FrmStart (geladen)");
//        }
//        stage.getScene().setRoot(startRoot);
//        FrmStartController controller = startLoader.getController();
//        controller.onShow();
//    }
//
//    public static void showScene2(Stage stage, DatabaseControllerAktionen dbController) throws Exception {
//
//        if (scene2Root == null) {
//            scene2Loader = new FXMLLoader(
//                SceneManager.class.getResource("/application/views/FrmAktionen.fxml")
//            );
//            scene2Root = scene2Loader.load();
//            System.out.println("showScene2 (geladen)");
//        }
//
//        FrmAktionenController controller = scene2Loader.getController();
//        controller.setDbControllerAktionen(dbController); // 🔥 JEDES MAL
//
//        stage.getScene().setRoot(scene2Root);
//        controller.onShow();
//    }
//
//
//
//
//}
