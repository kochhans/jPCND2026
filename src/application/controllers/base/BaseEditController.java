//1. Basisklasse für alle "EditController"
//
//Erstelle eine abstrakte Klasse, z. B.:
//Damit kapselst du gemeinsame Methoden wie Daten neu laden, Stage speichern, Warnungen anzeigen.
//Dein FrmStueckeThemaEditController kann davon erben

package application.controllers.base;


import java.util.List;

import application.uicomponents.Msgbox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public abstract class BaseEditController<T>
{

	protected Stage stage;
	protected ObservableList<T> dataList = FXCollections.observableArrayList();
    protected abstract Object getDb();


	
	public void setStage(Stage stage)
	{
		this.stage = stage;
		System.out.println("BaseEditController init: " + this.getClass().getSimpleName());
	}

	@FXML
	protected TableView<T> tableView;

	/** Muss in Unterklassen überschrieben werden */
	protected abstract List<T> loadDataFromDatabase() throws Exception;

	/** Tabelle neu laden */
	protected void reloadTable() throws Exception
	{
		dataList.setAll(loadDataFromDatabase());
		tableView.setItems(dataList);
	}

	/**
	 * Info-Dialog anzeigen
	 * 
	 * @throws Exception
	 */
	protected void showInfo(String message) throws Exception
	{
		Msgbox.show( "Info...", message);
	}

	/**
	 * Warnung anzeigen
	 * 
	 * @throws Exception
	 */
	protected void showWarning(String message) throws Exception
	{
		Msgbox.warn("Achtung!", message);

	}

	/** Fenster schließen */
	@FXML
	protected void onClose()
	{
		if (stage != null)
		{
			stage.close();
		}
	}
}
