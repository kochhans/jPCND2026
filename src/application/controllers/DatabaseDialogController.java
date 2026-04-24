package application.controllers;

import java.io.File;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DatabaseDialogController {

    private Stage stage;
    private String selectedPath;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getSelectedPath() {
        return selectedPath;
    }

    @FXML
    private void onSelectFile() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(stage);

        if (file != null) {
            selectedPath = file.getAbsolutePath();
        }
    }

    @FXML
    private void onOk() {
        stage.close();
    }

    @FXML
    private void onCancel() {
        selectedPath = null;
        stage.close();
    }
}