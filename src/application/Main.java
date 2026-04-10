package application;

import javafx.application.Application;

import javafx.stage.Stage;



public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
       // primaryStage.setTitle("Meine Anwendung");

        // 🔹 Icons setzen
        primaryStage.getIcons().setAll(AppIcons.getIcons());

        StartupManager startup = new StartupManager(primaryStage);
        startup.start();
    }


}
