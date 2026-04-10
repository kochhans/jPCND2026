
package application.controllers;

import java.util.ArrayList;
import java.util.List;

import application.ValuesGlobals;
import application.controllers.base.BaseEditController;
import application.models.AutorenlisteModel;
import application.uicomponents.Msgbox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


public class FrmAutorenEditController extends BaseEditController<AutorenlisteModel> // anp
{

	
	// --- TableViews
	@FXML
	private TableView<AutorenlisteModel> tblVwListeAlle;
	@FXML
	private TableColumn<AutorenlisteModel, String> tblvwCol1, tblvwCol2, tblvwCol3, tblvwCol4, tblvwCol5, tblvwCol6; // anp

	// --- Steuerelemente definieren
	@FXML
	private Button btnLoeschen, btnNeu, btnSpeichern, btnZurueck;
	@FXML
	private Label lblHaupttitel, lblGewaehlt, lblAuswahl, lblTabellenueberschrift;
	@FXML
	private TextField txtEingabezeile1, txtEingabezeile2, txtEingabezeile3, txtEingabezeile4, txtEingabezeile6, txtEingabezeile7;
	@FXML
	private TextArea txtEingabezeile5;
	
	// --- Stage
	private Stage stage;
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	// ----------------------------------------------------
	// Datenbankcontroller -- mit DB Setter (02/2026)
	// in jedem Fenster, nur Controllername anpassen !!!
	// Beium Aufruf: 
	// FrmAutorenEditController controller = loader.getController();
	// controller.setDb(dbAutoren);
	// 
	//--Musterbeispiel -------------
	private DatabaseControllerPcndAutoren db;
	public void setDb(DatabaseControllerPcndAutoren db)
	{
	    this.db = db;
	}

	//-------------------------------

	//------------------------------------------------------

	// INIT
	@FXML
	public void initialize() 
	{
//		setUIDataListe(); // (1)
//		anzeigenTabelle("", null); // (2)
//		Platform.runLater(() -> {
//			btnZurueck.requestFocus();
//		});
	}
	
	// init(Stage stage) wird vom Startcontroller aufgerufen
	public void init(Stage stage) throws Exception
	{
		this.stage = stage;
		setUIDataListe();
		anzeigenTabelle("", null);
		Platform.runLater(() -> {
			btnZurueck.requestFocus();
		});

	}	

	// ----------- Table Viewlisten - Spalten
	// ------------------------------------------------------

	private void setUIDataListe() // (1) definieren
	{
		tblVwListeAlle.setFixedCellSize(24); // Scrollfehler vermeiden
		lblTabellenueberschrift.setText("Gesamtliste");
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).
		tblvwCol1.setCellValueFactory(new PropertyValueFactory<AutorenlisteModel, String>("aautor")); // anp
		tblvwCol2.setCellValueFactory(new PropertyValueFactory<AutorenlisteModel, String>("agjahr")); // anp
		tblvwCol3.setCellValueFactory(new PropertyValueFactory<AutorenlisteModel, String>("atjahr")); // anp
		tblvwCol4.setCellValueFactory(new PropertyValueFactory<AutorenlisteModel, String>("asonst")); // anp
		tblvwCol5.setCellValueFactory(new PropertyValueFactory<AutorenlisteModel, String>("adb")); // anpassen an die Modellklasse
		tblvwCol6.setCellValueFactory(new PropertyValueFactory<AutorenlisteModel, String>("id")); // anpassen an die Modellklasse
	}

	public void anzeigenTabelle(String sucheBez, Integer zielIndex) throws Exception // (2) anzeigen nach Speichern
	{
		//DatabaseControllerPcndAutoren tabellekomlett = new DatabaseControllerPcndAutoren();
		ObservableList<AutorenlisteModel> oblist_autoren = FXCollections.observableArrayList(db.getAutorenAlle());

		tblVwListeAlle.getSortOrder().clear(); // Sortierung in SQL ohne Steuerelemts Sortierung !! vor SetItems
		tblVwListeAlle.setItems(oblist_autoren);


		Platform.runLater(() -> {
			if (oblist_autoren.isEmpty())
				return;
			// 🔹 FALL 1: nach Objekt (Speichern)
			if (sucheBez != null && !sucheBez.isEmpty())
			{
				for (AutorenlisteModel item : oblist_autoren)
				{
					if (sucheBez.equals(item.getAautor()))
					{
						int idx = oblist_autoren.indexOf(item);
						selectIndex(idx);
						return;
					}
				}
			}
			// 🔹 FALL 2: nach Index (Löschen)
			if (zielIndex != null)
			{
				int idx = Math.max(0, Math.min(zielIndex, oblist_autoren.size() - 1));
				selectIndex(idx);
			}
		});
	}

	// --- Listen für TableView anlegen
	// Tableviewliste für alle Datensätze
	ObservableList<AutorenlisteModel> oblist_autorenalle = FXCollections.observableArrayList(); // anp

	
	private void selectIndex(int idx)
	{
		tblVwListeAlle.scrollTo(idx);
		tblVwListeAlle.getSelectionModel().select(idx);
		tblVwListeAlle.getFocusModel().focus(idx);
	}

	void fctEingabefelderLeeren()
	{
		txtEingabezeile1.setText("");
		txtEingabezeile2.setText("");
		txtEingabezeile3.setText("");
		txtEingabezeile4.setText("");
		txtEingabezeile5.setText("");
		txtEingabezeile6.setText("");
		txtEingabezeile7.setText("");
		txtEingabezeile1.requestFocus();
	}

	// ####################################################
	// Buttons mit Aktion

	@FXML
	void btnZurueck_OnClick(ActionEvent event) throws Exception
	{
		this.stage.close();
	}

	@FXML
	void btnLoeschen_OnClick(ActionEvent event) throws Exception
	{
		String selectedtext = "";
		AutorenlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show("Datensatz löschen...", "Bitte einen AUTOR/AUTORIN in der Gesamtliste markieren!");
		}
		else
		{
			selectedtext = selected.getAautor();
			int indexVorher = tblVwListeAlle.getSelectionModel().getSelectedIndex();

//			if (Msgbox.yesnowarn("Datenatz löschen...", "AUTOR/AUTORIN " + selectedtext + "\nendgültig entfernen? "
//					+ "\nACHTUNG: Es werden auch alle Zuordnungen zu LIEDERN/STÜCKEN entfernt!!!") == false)
//			{
//				return;
//			}

			//DatabaseControllerPcndAutoren autorcon = new DatabaseControllerPcndAutoren();
			db.setAutorLoeschen(selectedtext);
			
			
			// autorcon.dbStop();
			anzeigenTabelle(null, indexVorher - 1);
			btnNeu_OnClick(event);
		}

	}

	@FXML
	void btnNeu_OnClick(ActionEvent event)
	{// Beim KLick auf NEU wird der Inhalt des Eingabefeldes für das Buch gelöscht
		// und focusiert
		Platform.runLater(() -> {
			tblVwListeAlle.getSelectionModel().clearSelection();
			fctEingabefelderLeeren();
			txtEingabezeile1.requestFocus();
		});

	}

	@FXML
	void btnSpeichern_OnClick(ActionEvent event) throws Exception
	{
		//DatabaseControllerPcndAutoren autorcon = new DatabaseControllerPcndAutoren(); // neuen DB Controller anlegen
		// Alle EIngabfelder für die Gesamttabelle einlesen
		
		AutorenlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		String anname = txtEingabezeile1.getText();
		String avname = txtEingabezeile2.getText();
		String agjahr = txtEingabezeile3.getText().trim(); // Startjahr
		String atjahr = "";
		if (txtEingabezeile4.getText() != null)
		{
			atjahr = txtEingabezeile4.getText().trim(); // Endjahr (darf leer sein)
		}
		String asonst = txtEingabezeile5.getText();
		String adb = txtEingabezeile6.getText();
		// String aid = txtEingabezeile7.getText();
		String aautor = "";
		String aautoralt = "";

		// Fehlende Eingabe prüfen
		if (txtEingabezeile1.getText().equals(""))
		{
			Msgbox.show("Datensatz sichern...", "Speichern nicht möglich...\nDas Namensfeld ist ein Pflichtfeld!");
			txtEingabezeile1.requestFocus();
			return;
		}
		if (selected != null)
		{
			adb = selected.getAdb();

			if (adb.equals(ValuesGlobals.zentralEingabe))
			{
				if (Msgbox.yesno("!Zentraler Datensatz!", "Soll der zentral erfasste Datensatz geändert werden?\n"
						+ "Damit werden alle Zuordnungen zu Literatur und Stücken automatisch aktualisiert.") == true)
				{
					adb = ValuesGlobals.zentralEingabeedit;
				}
				else
				{
					return;
				}
			}
			else if (adb.equals(ValuesGlobals.privatEingabe))
			{
				adb = ValuesGlobals.privatEingabeedit;
			}
			else
			{
				adb = ValuesGlobals.privatEingabeedit;
			}
			aautoralt = selected.getAautor();
			aautor = anname + ", " + avname;
			db.setAutorHinzu(false, anname, avname, agjahr, atjahr, asonst, adb, aautor, aautoralt); // Speichernvorgang
		}
		else
		{// neuer Datensatz
			aautor = anname + ", " + avname;
			adb = ValuesGlobals.privatEingabe;
			db.setAutorHinzu(true, anname, avname, agjahr, atjahr, asonst, adb, aautor, aautoralt); // Speichernvorgang
		}

		anzeigenTabelle(aautor, null); // aktualisieren
		// autorcon.dbStop();
		btnNeu_OnClick(event);

	}

	@FXML
	void handletblVwListeAlle_OnMouseClicked()
	{// Beim Klicken auf die rechte Tabelle soll der Datensatz in der
		// Eingabezeile darunter erscheinen

		AutorenlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			txtEingabezeile1.setText(selected.getAnname());
			txtEingabezeile2.setText(selected.getAvname());
			txtEingabezeile3.setText(selected.getAgjahr());
			txtEingabezeile4.setText(selected.getAtjahr());
			txtEingabezeile5.setText(selected.getAsonst());
			txtEingabezeile6.setText(selected.getAdb());
			txtEingabezeile7.setText(selected.getId());
		}
	}

	@Override
	protected List<AutorenlisteModel> loadDataFromDatabase() throws Exception
	{
		return new ArrayList<>();
	}

	@Override
	protected Object getDb()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
