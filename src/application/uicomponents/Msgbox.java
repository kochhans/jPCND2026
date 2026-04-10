package application.uicomponents;

import java.util.HashMap;
import java.util.Map;

import application.ValuesGlobals;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Universelle Messagebox (VBA-Style) Anpassbare Titel, Icons, Text & Buttonauswahl.
 */
public class Msgbox extends Stage {
	

    private MsgboxBtnType myPressedButton = MsgboxBtnType.NONE;
    

    // ============================================================
    // Öffentliche Kurzaufrufe (VBA-Style)
    // ============================================================

    public static void show(String title, String message) {
        showInternal(null, MsgboxWinType.FRMINFORMATION, title, message, "/icons/javafx/information.png");
    }

    public static void warn(String title, String message) {
        showInternal(null, MsgboxWinType.FRMINFORMATION, title, message, "/icons/javafx/warnung.png");
    }

    public static void error(String title, String message) {
        showInternal(null, MsgboxWinType.FRMINFORMATION, title, message, "/icons/javafx/error.png");
    }

    public static boolean yesno(String title, String message) {
        Msgbox box = showInternal(null, MsgboxWinType.FRMYESNO, title, message, "/icons/javafx/question.png");
        return box != null && box.getBtn() == MsgboxBtnType.YES;
    }

    public static boolean weiterabbruch(String title, String message) {
        Msgbox box = showInternal(null, MsgboxWinType.WEITERABBRUCH, title, message, "/icons/javafx/question.png");
        return box != null && box.getBtn() == MsgboxBtnType.FORWARD;
    }

    public static boolean yesnowarn(String title, String message) {
        Msgbox box = showInternal(null, MsgboxWinType.FRMYESNO, title, message, "/icons/javafx/warnung.png");
        return box != null && box.getBtn() == MsgboxBtnType.YES;
    }

    public static MsgboxBtnType askEx(String title, String message) {
        Msgbox box = showInternal(null, MsgboxWinType.FRMYESNOCANCEL, title, message, "/icons/javafx/question.png");
        return (box != null) ? box.getBtn() : MsgboxBtnType.NONE;
    }

    // ============================================================
    // Konstruktor & Initialisierung
    // ============================================================

    protected Msgbox(Stage owner, MsgboxWinType type, String title, String message, String iconPath) throws Exception {
        init(owner, type, title, message, iconPath);
    }

    private void init(Stage owner, MsgboxWinType type, String title, String message, String iconPath) throws Exception {
        if (owner != null)
            this.initOwner(owner);

        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(title != null ? title : ValuesGlobals.progTitel);
        this.setAlwaysOnTop(true);
        this.setResizable(false);

        // ---------- Buttons ----------
        Map<MsgboxBtnType, Button> buttonMap = new HashMap<>();
        buttonMap.put(MsgboxBtnType.OK, new Button("OK"));
        buttonMap.put(MsgboxBtnType.CANCEL, new Button("Abbrechen"));
        buttonMap.put(MsgboxBtnType.YES, new Button("Ja"));
        buttonMap.put(MsgboxBtnType.NO, new Button("Nein"));
        buttonMap.put(MsgboxBtnType.APPLY, new Button("Anwenden"));
        buttonMap.put(MsgboxBtnType.BACK, new Button("Zurück"));
        buttonMap.put(MsgboxBtnType.FORWARD, new Button("Weiter"));

        buttonMap.forEach((buttonType, button) -> {
            button.getStyleClass().add("msgbox-button");
            button.setOnAction(e -> {
                myPressedButton = buttonType;
                Msgbox.this.close();
            });
        });

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        HBox buttonLine = new HBox(10);
        buttonLine.setAlignment(Pos.CENTER);
        buttonLine.setPadding(new Insets(10));

        switch (type) {
            case FRMYESNO -> buttonLine.getChildren().addAll(spacerLeft, buttonMap.get(MsgboxBtnType.YES),
                    buttonMap.get(MsgboxBtnType.NO), spacerRight);
            case FRMYESNOCANCEL -> buttonLine.getChildren().addAll(spacerLeft, buttonMap.get(MsgboxBtnType.YES),
                    buttonMap.get(MsgboxBtnType.NO), buttonMap.get(MsgboxBtnType.CANCEL), spacerRight);
            case WEITERABBRUCH -> buttonLine.getChildren().addAll(spacerLeft, buttonMap.get(MsgboxBtnType.FORWARD),
                    buttonMap.get(MsgboxBtnType.CANCEL), spacerRight);
            default -> buttonLine.getChildren().addAll(spacerLeft, buttonMap.get(MsgboxBtnType.OK), spacerRight);
        }

        // ---------- Textbereich ----------
        Text txtMessage = new Text(message);
        txtMessage.getStyleClass().add("msgbox-text");

        TextFlow textFlow = new TextFlow(txtMessage);
        textFlow.setMaxWidth(400);
        textFlow.setPadding(new Insets(10));

        // ---------- Icon ----------
        ImageView iconView = null;
        if (iconPath != null) {
            try {
                iconView = new ImageView(new Image(iconPath));
                iconView.setFitHeight(48);
                iconView.setFitWidth(48);
            } catch (Exception ignored) {}
        }

        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.TOP_LEFT);
        messageBox.getChildren().addAll(iconView != null ? iconView : new Region(), textFlow);

        VBox content = new VBox(10, messageBox, buttonLine);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Scene scene = new Scene(content);
        scene.getStylesheets().add(getClass().getResource("/application/views/msgbox.css").toExternalForm());
        this.setScene(scene);
        this.getIcons().setAll(application.AppIcons.getIcons());
        this.sizeToScene();
        this.centerOnScreen();
    }

    // ============================================================
    // Rückgabewert
    // ============================================================
    public MsgboxBtnType getBtn() {
        return myPressedButton;
    }

    // ============================================================
    // Interner Show-Helper
    // ============================================================
    protected static Msgbox showInternal(Stage owner, MsgboxWinType type, String title, String message,
            String iconPath) {
        try {
            if (owner == null) {
                owner = inferOwnerFromStack(); // 🔹 automatisch ermitteln
            }
            Msgbox box = new Msgbox(owner, type, title, message, iconPath);
            box.showAndWait();
            return box;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ============================================================
    // Msgbox mit klickbaren Links
    // ============================================================
    public static void showUrl(String title, String message) {
        showInternalUrl(null, MsgboxWinType.FRMINFORMATION, title, message, "/icons/javafx/information.png");
    }

    protected static void showInternalUrl(Stage owner, MsgboxWinType type, String title, String message, String iconPath) {
        try {
            if (owner == null) {
                owner = inferOwnerFromStack(); // 🔹 automatisch ermitteln
            }
            Msgbox box = new Msgbox(owner, type, title, message, iconPath);

            // ---------- Textbereich mit klickbaren Links ----------
            TextFlow textFlow = new TextFlow();
            textFlow.setMaxWidth(400);
            textFlow.setPadding(new Insets(10));

            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(https?://\\S+)").matcher(message);

            int lastIndex = 0;
            while (matcher.find()) {
                if (matcher.start() > lastIndex) {
                    Text normalText = new Text(message.substring(lastIndex, matcher.start()));
                    normalText.getStyleClass().add("msgbox-text");
                    textFlow.getChildren().add(normalText);
                }

                String url = matcher.group(1);
                Hyperlink link = new Hyperlink(url);
                link.getStyleClass().add("hyperlink");
                link.setOnAction(e -> openUrl(url));
                textFlow.getChildren().add(link);

                lastIndex = matcher.end();
            }

            if (lastIndex < message.length()) {
                Text remainingText = new Text(message.substring(lastIndex));
                remainingText.getStyleClass().add("msgbox-text");
                textFlow.getChildren().add(remainingText);
            }

            // ---------- Icon ----------
            ImageView iconView = null;
            if (iconPath != null) {
                try {
                    iconView = new ImageView(new Image(iconPath));
                    iconView.setFitHeight(48);
                    iconView.setFitWidth(48);
                } catch (Exception ignored) {}
            }

            HBox messageBox = new HBox(10);
            messageBox.setAlignment(Pos.TOP_LEFT);
            messageBox.getChildren().addAll(iconView != null ? iconView : new Region(), textFlow);

            // ---------- Buttons ----------
            Map<MsgboxBtnType, Button> buttonMap = new HashMap<>();
            buttonMap.put(MsgboxBtnType.OK, new Button("OK"));
            buttonMap.get(MsgboxBtnType.OK).getStyleClass().add("msgbox-button");
            buttonMap.get(MsgboxBtnType.OK).setOnAction(e -> box.close());

            HBox buttonLine = new HBox(10, buttonMap.get(MsgboxBtnType.OK));
            buttonLine.setAlignment(Pos.CENTER);
            buttonLine.setPadding(new Insets(10));

            VBox content = new VBox(10, messageBox, buttonLine);
            content.setPadding(new Insets(20));
            content.setAlignment(Pos.CENTER);

            Scene scene = new Scene(content);
            scene.getStylesheets().add(Msgbox.class.getResource("/application/views/msgbox.css").toExternalForm());
            box.setScene(scene);
            
            box.sizeToScene();
            box.centerOnScreen();

            box.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // Hilfsmethoden
    // ============================================================
    private static Stage inferOwnerFromStack() {
        for (Window window : Window.getWindows()) {
            if (window.isShowing() && window.isFocused()) {
                return (Stage) window;
            }
        }
        return null;
    }

    private static void openUrl(String url) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(new java.net.URI(url));
                    return;
                }
            }
            // Fallback für Linux
            new ProcessBuilder("xdg-open", url).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}