
package application.controllers;

import java.util.ArrayList;
import java.util.List;

import application.ValuesGlobals;
import application.controllers.base.BaseEditController;
import application.models.GesangbuchModel;
import application.models.VerlaglisteModel;
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

public class FrmVerlagEditController extends BaseEditController<GesangbuchModel> // anp
{
	// --- TableViews
	@FXML
	private TableView<VerlaglisteModel> tblVwListeAlle; // für jedes Form diese Zeile anpassen (anp.)

	@FXML
	private TableColumn<VerlaglisteModel, String> tblvwCol1, tblvwCol2, tblvwCol3, tblvwCol4, tblvwCol5; // anp

	@FXML
	private TableColumn<VerlaglisteModel, String> tblvwCol6; // anp

	// --- Steuerelemente definieren
	// Buttons
	@FXML
	private Button btnLoeschen, btnNeu, btnSpeichern, btnWegnehmen, btnZurueck, btnZurueck2, btnZuweisen;
	// Labels
	@FXML
	private Label lblHaupttitel, lblGewaehlt, lblAuswahl;
	// Textfelder
	@FXML
	private TextField txtEingabezeile, txtEingabezeile1, txtEingabezeile2,
			txtEingabezeile4, txtEingabezeile5, txtEingabezeile6;
	@FXML
	private TextArea txtEingabezeile3;

	// --- Listen für TableView anlegen
	// Tableviewliste für alle Datensätze (rechts)
	ObservableList<VerlaglisteModel> oblist_verlagalle = FXCollections.observableArrayList(); // anp

	private DatabaseControllerPcndVerlage db;
	public void setDb(DatabaseControllerPcndVerlage db) {
	    this.db = db;
	    try {
	        anzeigenTabelle("", null); // jetzt db != null
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	//-------------------------------	
	
	// #####################################################################################################################
	// Stage aufbauen

	private Stage stage;
	

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	// INIT
	@FXML
	public void initialize() throws Exception
	{

		// Wurde ein Stück ausgewählt?
		if (ValuesGlobals.Uebergabewert1.equals("") || ValuesGlobals.Uebergabewert1.equals(null))
		{ // nein
			Platform.runLater(() -> {
				btnZurueck.requestFocus();
			});
		}
		else
		{ // ja
			lblGewaehlt.setText(ValuesGlobals.Uebergabewert1);
			// TableViews aktualisiseren
		}
		setUIDataListe(); // (1)
	}


	// ----------- Table Viewlisten - Spalten
	// ------------------------------------------------------

	private void setUIDataListe() // (1) definieren
	{
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).

		tblvwCol1.setCellValueFactory(new PropertyValueFactory<VerlaglisteModel, String>("vverlag")); // anp
		tblvwCol2.setCellValueFactory(new PropertyValueFactory<VerlaglisteModel, String>("vort")); // anp
		tblvwCol3.setCellValueFactory(new PropertyValueFactory<VerlaglisteModel, String>("vbem")); // anp
		tblvwCol4.setCellValueFactory(new PropertyValueFactory<VerlaglisteModel, String>("vdb")); // anp

		tblvwCol5.setCellValueFactory(new PropertyValueFactory<VerlaglisteModel, String>("verfasst")); // anp
		tblvwCol6.setCellValueFactory(new PropertyValueFactory<VerlaglisteModel, String>("vid")); // anp
	}

	public void anzeigenTabelle(String sucheBez, Integer zielIndex) throws Exception // (2) anzeigen nach Speichern
	{
		System.out.println("Anzeigen Tabellen nach Speichern - Erhaltener Wert: " + ValuesGlobals.Uebergabewert1);
		// rechte Tableview
		ObservableList<VerlaglisteModel> oblist_verlage = FXCollections.observableArrayList();
		List<VerlaglisteModel> listverl = db.getVerlaglisteListeAlle();
		oblist_verlage.addAll(listverl);
		tblVwListeAlle.getSortOrder().clear(); // Sortierung in SQL ohne Steuerelemts Sortierung !! vor SetItems
		tblVwListeAlle.setItems(oblist_verlage);

		Platform.runLater(() -> {

			if (oblist_verlage.isEmpty())
				return;

			// 🔹 FALL 1: nach Objekt (Speichern)
			if (sucheBez != null && !sucheBez.isEmpty())
			{
				for (VerlaglisteModel item : oblist_verlage)
				{
					if (sucheBez.equals(item.getVverlag()))
					{
						int idx = oblist_verlage.indexOf(item);
						selectIndex(idx);
						return;
					}
				}
			}

			// 🔹 FALL 2: nach Index (Löschen)
			if (zielIndex != null)
			{
				int idx = Math.max(0, Math.min(zielIndex, oblist_verlage.size() - 1));
				selectIndex(idx);
			}
		});

	}

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
		VerlaglisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show( "Datensatz löschen...", "Bitte einen Verlag in der Gesamtliste markieren!");
			return;
		}
		int indexVorher = tblVwListeAlle.getSelectionModel().getSelectedIndex();

			if (Msgbox.yesnowarn("Datenatz löschen...", "Den Verlag " + selected + " endgültig entfernen? \n"
					+ "Es werden auch alle Zuordnungen zu EDITIONEN entfernt!!!") == false)
			{
				return;
			}
			selectedtext = selected.getVverlag();
			
			db.setVerlagLoeschen(selectedtext);

			// 🔽 eine Zeile oberhalb
			anzeigenTabelle(null, indexVorher - 1);
			btnNeu_OnClick(event);
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
		// Prüfen, ob in der Gesamtliste eine Zeile markiert ist und zwischenspeichern
		VerlaglisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		// Alle EIngabfelder für die Gesamttabelle einlesen
		// String selectedtext = "";
		String vverlag = txtEingabezeile1.getText();
		String vort = txtEingabezeile2.getText();
		String vbem = txtEingabezeile3.getText();
		String verfasst = txtEingabezeile4.getText();
		String vdb = txtEingabezeile5.getText();
		String vid = txtEingabezeile6.getText();
		String vverlagalt = "";

		// Fehlende Eingabe prüfen
		if (txtEingabezeile1.getText().equals(""))
		{
			Msgbox.show("Datensatz sichern...", "Speichern nicht möglich...\nBitte eine Verlagsbezeichnung eingeben");
			txtEingabezeile.requestFocus();
			return;
		}

		// Eingaben vollständig - nun prüfwen, ob neu oder edit
		if (selected != null)
		{ // Datensatz editieren
			vdb = selected.getVdb();

			// wlid = selected.getWoliid();
			if (vdb.equals(ValuesGlobals.zentralEingabe))
			{
				if (Msgbox.yesno("!Zentraler Datensatz!", "Soll der zentral erfasste Datensatz geändert werden?\n"
						+ "Damit werden alle Zuordnungen zu den Noteneditionen ebenfalls angepasst!") == true)
				{
					vdb = ValuesGlobals.zentralEingabeedit;
				}
				else
				{// wenn der zentrale DS nicht geändert werden soll- Ende
					return;
				}
			}
			else if (vdb.equals(ValuesGlobals.privatEingabe))
			{// privat erfasster DS ändern
				vdb = ValuesGlobals.privatEingabeedit;
			}
			else
			{
				vdb = ValuesGlobals.privatEingabeedit;
			}

			vverlagalt = selected.getVverlag(); // aus der Selektierung die Bezeichnung holen (leer, wenn nichts angeklickt)
			db.setVerlagSpeichern(false, vverlag, vort, vbem, verfasst, vdb, vverlagalt, vid); // Speichernvorgang
			// setVerlagSpeichern(Boolean neu, String vverlag, String vort, String vbem,
			// String vdb, String vverlagalt, int vid
		}

		else
		{ // neuer Datensatz
			vdb = ValuesGlobals.privatEingabe;
			vverlagalt = "";
			db.setVerlagSpeichern(true, vverlag, vort, vbem, verfasst, vdb, vverlagalt, vid);
		}

		anzeigenTabelle(vverlag, null); // aktualisieren

		btnNeu_OnClick(event);

	}

	@FXML
	void handletblVwListeAlle_OnMouseClicked()
	{// Beim Klicken auf die rechte Tabelle soll das markierte Buch in der
		// Eingabezeile darunter erscheinen

		VerlaglisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			txtEingabezeile1.setText(selected.getVverlag());
			txtEingabezeile2.setText(selected.getVort());
			txtEingabezeile3.setText(selected.getVbem());
			txtEingabezeile4.setText(selected.getVerfasst());
			txtEingabezeile5.setText(selected.getVdb());
			txtEingabezeile6.setText(selected.getVid().toString());
			txtEingabezeile1.requestFocus(); // Cursor
		}

	}

	@Override
	protected List<GesangbuchModel> loadDataFromDatabase() throws Exception
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
