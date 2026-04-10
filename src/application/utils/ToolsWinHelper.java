package application.utils;

import application.db.DBManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ToolsWinHelper
{
	public static Integer parseIntegerWithAlert(TextField field, String feldName)
	{
		try
		{
			String text = field.getText();
			if (text == null || text.trim().isEmpty())
			{
				return 0; // Leere Eingabe gilt als 0
			}
			return Integer.parseInt(text.trim());
		}
		catch (NumberFormatException e)
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Ungültige Eingabe");
			alert.setHeaderText("Eingabefehler bei \"" + feldName + "\"");
			alert.setContentText("Bitte gib eine ganze Zahl ein.");
			alert.showAndWait();
			// Optional: Fokus zurück auf das Feld
			field.requestFocus();
			return null; // oder ggf. throw, falls zwingend notwendig
		}
	}


	public static void closeApplication()
	{
		try
		{
//			if (Msgbox.yesno("Programm beenden ...", "Soll das Programm wirklich beendet werden?") == false)
//			{
//				return;
//			}

			DBManager.close(); // falls DB vorhanden
			System.out.println("✅ (99) Anwendung wurde geschlossen.");
			Platform.exit(); // JavaFX sauber beenden
			System.exit(0);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ============================================
	// Beliebige ComboBox Wert setzen
	public static <T> void autoSelectComboBoxValue(ComboBox<T> comboBox, String value, Func<T, String> f)
	{
		for (T t : comboBox.getItems())
		{
			if (f.compare(t, value))
			{
				comboBox.setValue(t);
			}
		}
	}

	public interface Func<T, V>
	{
		boolean compare(T t, V v);
	}
	// ============================================

}
