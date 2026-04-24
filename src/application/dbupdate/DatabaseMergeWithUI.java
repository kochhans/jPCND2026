package application.dbupdate;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import application.StartupManager;
import application.ValuesGlobals;
import application.uicomponents.Msgbox;

public class DatabaseMergeWithUI extends Stage {

    private TextArea logArea;
    private ProgressBar progressBar;
    private ListView<String> tableListView;
    private Button mergeButton;
    private String sourceDbPath;
    private DatabaseMergeService service;

    public DatabaseMergeWithUI(Stage owner) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Datenbank Merge");

        createUI();
    }

    private void createUI() {
        Button selectSourceBtn = new Button("Quelldatenbank auswählen");
        selectSourceBtn.setOnAction(e -> chooseSourceDatabase());

        tableListView = new ListView<>();
        tableListView.setPrefHeight(200);
        enableDragAndDrop(tableListView);

        mergeButton = new Button("Merge starten");
        mergeButton.setDisable(true);
        mergeButton.setOnAction(e -> startMergeTask());

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true); // langer Text wird umgebrochen

        VBox root = new VBox(10, selectSourceBtn,
                new Label("Tabellenreihenfolge (Drag & Drop):"),
                tableListView, mergeButton, progressBar, logArea);
        root.setPadding(new Insets(10));

        setScene(new Scene(root, 600, 500));
    }

    private void chooseSourceDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Quelldatenbank auswählen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SQLite Dateien", "*.db*", "*.sqlite")
        );
        File file = fileChooser.showOpenDialog(this);
        if (file != null) {
            sourceDbPath = file.getAbsolutePath();
            loadTablesFromSource();
        }
    }

    private void loadTablesFromSource() {
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:" + sourceDbPath)) {
            List<String> tables = new ArrayList<>();
            try (java.sql.ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) tables.add(rs.getString("TABLE_NAME"));
            }
            // Automatisch nach FK sortieren
            tables = sortTablesByDependencies(conn, tables);
            ObservableList<String> obsList = FXCollections.observableArrayList(tables);
            tableListView.setItems(obsList);
            mergeButton.setDisable(false);
            logArea.appendText("Tabellen geladen: " + tables + "\n");
        } catch (Exception e) {
            logArea.appendText("Fehler beim Laden der Tabellen: " + e.getMessage() + "\n");
        }
    }

    private void startMergeTask() {
        if (sourceDbPath == null) {
            logArea.appendText("❌ Bitte zuerst eine Quelldatenbank auswählen.\n");
            return;
        }
        if (ValuesGlobals.dbPfad == null) {
            logArea.appendText("❌ Ziel-Datenbankpfad nicht gesetzt.\n");
            return;
        }

        service = new DatabaseMergeService(sourceDbPath, ValuesGlobals.dbPfad);

//        Task<Void> task = new Task<>() {
//            @Override
//            protected Void call() throws Exception {
//                service.mergeTables(
//                    tableListView.getItems(),
//                    (current, total) -> updateProgress(current, total),
//                    msg -> updateMessage(msg)
//                );
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection sourceConn = DriverManager.getConnection("jdbc:sqlite:" + sourceDbPath);
                     Connection targetConn = DriverManager.getConnection("jdbc:sqlite:" + ValuesGlobals.dbPfad)) {

                    List<String> tables = tableListView.getItems();

                    service.mergeAllTables(
                        tables,
                        sourceConn,
                        targetConn,
                        (current, total) -> updateProgress(current, total),
                        msg -> Platform.runLater(() -> logArea.appendText(msg + "\n"))
                    );
                }

                // Nach dem Merge: Neustart einleiten
                updateMessage("✔ Datenbank-Merge abgeschlossen. Anwendung wird neu gestartet...");

                // Kurzer Delay, damit User die Nachricht sieht
                Thread.sleep(1000);

                // Neustart aufrufen
                Msgbox.show("Update abgeschlossen", "Die Anwendung wird nun neu gestartet.");
                //StartupManager.restart();

                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        task.messageProperty().addListener((obs, old, msg) -> {
            logArea.appendText(msg + "\n");
            logArea.setScrollTop(Double.MAX_VALUE); // automatisch nach unten scrollen
        });

        new Thread(task, "db-merge-task").start();
    }

    private void enableDragAndDrop(ListView<String> listView) {
        listView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(cell.getItem());
                    db.setContent(content);
                    event.consume();
                }
            });

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    int draggedIdx = listView.getItems().indexOf(db.getString());
                    int thisIdx = cell.getIndex();
                    Collections.swap(listView.getItems(), draggedIdx, thisIdx);
                    event.setDropCompleted(true);
                    listView.getSelectionModel().select(thisIdx);
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            });

            return cell;
        });
    }

    private List<String> sortTablesByDependencies(java.sql.Connection conn, List<String> tables) throws Exception {
        List<String> sorted = new ArrayList<>();
        Set<String> handled = new HashSet<>();
        while (sorted.size() < tables.size()) {
            for (String table : tables) {
                if (handled.contains(table)) continue;
                List<String> deps = getForeignKeys(conn, table);
                if (handled.containsAll(deps)) {
                    sorted.add(table);
                    handled.add(table);
                }
            }
        }
        return sorted;
    }

    private List<String> getForeignKeys(java.sql.Connection conn, String table) throws Exception {
        List<String> fkTables = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list(" + table + ")")) {
            while (rs.next()) fkTables.add(rs.getString("table"));
        }
        return fkTables;
    }
}
