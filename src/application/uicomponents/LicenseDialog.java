package application.uicomponents;

import application.utils.license.License;
import application.utils.license.LicenseCheckResult;
import application.utils.license.LicenseManager;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LicenseDialog extends Stage {

    private final License license;

    public LicenseDialog(License license) {
        this.license = license;

        setTitle("Lizenz eingeben / aktualisieren");
        initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField keyField = new TextField(license.getLicenseKey());
        TextField emailField = new TextField(license.getEmail());
        TextField nameField = new TextField(license.getCustomerName());
        DatePicker validUntilPicker = new DatePicker(
                license.getValidUntil() != null ? license.getValidUntil() : LocalDate.now().plusYears(1)
        );

        grid.add(new Label("Lizenzschlüssel:"), 0, 0);
        grid.add(keyField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Kunde:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Gültig bis:"), 0, 3);
        grid.add(validUntilPicker, 1, 3);

        Button btnOk = new Button("Speichern & Online prüfen");
        btnOk.setOnAction(e -> onSave(keyField.getText(), emailField.getText(),
                nameField.getText(), validUntilPicker.getValue()));

        grid.add(btnOk, 1, 4);

        Scene scene = new Scene(grid);
        setScene(scene);
        showAndWait();
    }

    private void onSave(String key, String email, String customerName, LocalDate validUntil) {

        if (key.isBlank() || email.isBlank()) {
            new Alert(Alert.AlertType.WARNING,
                    "Lizenzschlüssel und Email dürfen nicht leer sein.")
                    .showAndWait();
            return;
        }

        LicenseCheckResult result =
                LicenseManager.getInstance().activate(key, email);

        if (result != LicenseCheckResult.VALID) {
            new Alert(Alert.AlertType.ERROR,
                    "Lizenz konnte nicht aktiviert werden.\nStatus: " + result)
                    .showAndWait();
            return;
        }

        new Alert(Alert.AlertType.INFORMATION,
                "Lizenz erfolgreich aktiviert!")
                .showAndWait();

        close();
    }

	public License getLicense()
	{
		return license;
	}

}
