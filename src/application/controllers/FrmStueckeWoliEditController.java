
package application.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.ValuesGlobals;
import application.controllers.base.BaseEditController;
import application.models.GesangbuchModel;
import application.models.LiederStueckeWoliModel;
import application.models.WochenliedlisteModel;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FrmStueckeWoliEditController extends BaseEditController<GesangbuchModel> // anp
{
	// --- TableViews
	@FXML
	private TableView<WochenliedlisteModel> tblVwListeAlle; // für jedes Form diese Zeile anpassen (anp.)
	@FXML
	private TableView<LiederStueckeWoliModel> tblVwListeZugewiesen; // anp

	@FXML
	private TableColumn<WochenliedlisteModel, String> tblvwColVerfuegbarSpalte1, tblvwColVerfuegbarSpalte2, tblvwColVerfuegbarSpalte3, tblvwColVerfuegbarSpalte4; // anp
	@FXML
	private TableColumn<LiederStueckeWoliModel, String> tblvwColZugewiesenSpalte1, tblvwColZugewiesenSpalte2, tblvwColZugewiesenSpalte3; // anp

	// --- Steuerelemente definieren
	// Buttons
	@FXML
	private Button btnLoeschen, btnNeu, btnSpeichern, btnWegnehmen, btnZurueck, btnZurueck2, btnZuweisen;
	// Labels
	@FXML
	private Label lblHaupttitel, lblGewaehlt, lblAuswahl;
	// Textfelder
	@FXML
	private TextField txtEingabezeile, txtEingabezeile1, txtEingabezeile2, txtEingabezeile3, txtEingabeNummer;

	// --- Listen für TableView anlegen
	// Tableviewliste für alle Datensätze (rechts)
	ObservableList<WochenliedlisteModel> oblist_wolialle = FXCollections.observableArrayList(); // anp
	// Tableviewliste zugewiesene Datensätze (links)
	ObservableList<LiederStueckeWoliModel> oblist_wolizugew = FXCollections.observableArrayList();

	// #####################################################################################################################
	// Stage aufbauen

	private Stage stage;
	private DatabaseControllerPcndWochenlied db;
	public void setDb(DatabaseControllerPcndWochenlied db)
	{
	    this.db = db;
	}
	//-------------------------------

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	// INIT
	@FXML
	public void initialize() throws Exception
	{

		setUIDataListe();
	}

	// INIT
	// init(Stage stage)-- wird vom Startcontroller aufgerufen
	public void init(Stage stage) throws Exception
	{
		this.stage = stage;
		//db = new DatabaseControllerPcndWochenlied();

		if (ValuesGlobals.Uebergabewert1 == null || ValuesGlobals.Uebergabewert1.isBlank())
		{
			lblAuswahl.setText("kein Stück gewählt - Feiertage/Festtage für Wochenlieder editieren...");
			lblGewaehlt.setText("Feiertage/Festtage editieren...");
			tblVwListeZugewiesen.setDisable(true);
			btnWegnehmen.setDisable(true);
			btnZuweisen.setDisable(true);
			btnZurueck2.setDisable(true);
			Platform.runLater(() -> btnZurueck.requestFocus());
		}
		else
		{
			tblVwListeZugewiesen.setDisable(false);
			btnWegnehmen.setDisable(false);
			btnZuweisen.setDisable(false);
			btnZurueck2.setDisable(false);

			lblGewaehlt.setText(ValuesGlobals.Uebergabewert1);
		}

		anzeigenTabelle("", 0);
	}

	// ----------- Table Viewlisten - Spalten
	// ------------------------------------------------------

	private void setUIDataListe() // (1) definieren
	{
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).
		tblvwColVerfuegbarSpalte1.setCellValueFactory(new PropertyValueFactory<WochenliedlisteModel, String>("wolibez")); // anp
		tblvwColVerfuegbarSpalte2.setCellValueFactory(new PropertyValueFactory<WochenliedlisteModel, String>("wolirang")); // anp
		tblvwColVerfuegbarSpalte3.setCellValueFactory(new PropertyValueFactory<WochenliedlisteModel, String>("wolidb")); // anp
		tblvwColVerfuegbarSpalte4.setCellValueFactory(new PropertyValueFactory<WochenliedlisteModel, String>("woliid")); // anp
		tblvwColZugewiesenSpalte1.setCellValueFactory(new PropertyValueFactory<LiederStueckeWoliModel, String>("woli")); // anpassen an die Modellklasse
		tblvwColZugewiesenSpalte2.setCellValueFactory(new PropertyValueFactory<LiederStueckeWoliModel, String>("id")); // anpassen an die Modellklasse

	}

	public void anzeigenTabelle(String sucheBez, Integer zielIndex) throws Exception
	{
		System.out.println("Anzeigen Tabellen - Wert: " + ValuesGlobals.Uebergabewert1);
		// ---- rechte Tabelle (alle)
		oblist_wolialle.clear();
		List<WochenliedlisteModel> listwl = db.getWochenliedlisteListeAll();
		oblist_wolialle.addAll(listwl);
		tblVwListeAlle.getSortOrder().clear();
		tblVwListeAlle.setItems(oblist_wolialle);

		// ---- linke Tabelle (zugewiesen)
		oblist_wolizugew.clear();
		if (ValuesGlobals.Uebergabewert1 != null && !ValuesGlobals.Uebergabewert1.isBlank())
		{
			List<LiederStueckeWoliModel> listzugew = db.getStueckinfosWoli(ValuesGlobals.Uebergabewert1);
			oblist_wolizugew.addAll(listzugew);
		}
		tblVwListeZugewiesen.getSortOrder().clear();
		tblVwListeZugewiesen.setItems(oblist_wolizugew);

		Platform.runLater(() -> {
			if (oblist_wolialle.isEmpty())
				return;
			// 🔹 FALL 1: nach Objekt (Speichern)
			if (sucheBez != null && !sucheBez.isEmpty())
			{
				for (WochenliedlisteModel item : oblist_wolialle)
				{
					if (sucheBez.equals(item.getWolibez()))
					{
						int idx = oblist_wolialle.indexOf(item);
						selectIndex(idx);
						return;
					}
				}
			}
			// 🔹 FALL 2: nach Index (Löschen)
			if (zielIndex != null)
			{
				int idx = Math.max(0, Math.min(zielIndex, oblist_wolialle.size() - 1));
				selectIndex(idx);
			}
		});
		Platform.runLater(() -> {
			tblVwListeAlle.getSelectionModel().clearSelection();
			tblVwListeZugewiesen.getSelectionModel().clearSelection();
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
		txtEingabezeile.setText("");
		txtEingabezeile1.setText("");
		txtEingabezeile2.setText("");
		txtEingabezeile.requestFocus();
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
		int indexVorher = 0;
		WochenliedlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show( "Datensatz löschen...", "Bitte einen Feiertag/Festtag in der Gesamtliste markieren!");
		}
		else
		{

			if (Msgbox.yesnowarn( "Datenatz löschen...", "Den Feiertag/Festtag " + selected + " endgültig entfernen? "
					+ "\nACHTUNG: Es werden auch alle Zuordnungen zu LIEDERN/STÜCKEN entfernt!!!") == false)
			{
				return;
			}
			selectedtext = selected.getWolibez();
			indexVorher = tblVwListeAlle.getSelectionModel().getSelectedIndex();
			// DatabaseControllerPcndWochenlied wolicon = new
			// DatabaseControllerPcndWochenlied();
			db.setWoliLoeschen(selectedtext);
		}
		anzeigenTabelle(null, indexVorher - 1);
		txtEingabezeile.requestFocus();
		btnNeu.requestFocus();
	}

	@FXML
	void btnNeu_OnClick(ActionEvent event)
	{// Beim KLick auf NEU wird der Inhalt des Eingabefeldes für das Buch gelöscht
		// und focusiert
		tblVwListeAlle.getSelectionModel().clearSelection();
		tblVwListeZugewiesen.getSelectionModel().clearSelection();
		fctEingabefelderLeeren();
	}

	@FXML
	void btnSpeichern_OnClick(ActionEvent event) throws Exception
	{
		//DatabaseControllerPcndWochenlied wolicon = new DatabaseControllerPcndWochenlied(); // neuen DB Controller anlegen
		// Prüfen, ob in der Gesamtliste eine Zeile markiert ist und zwischenspeichern
		WochenliedlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		// Alle EIngabfelder für die Gesamttabelle einlesen
		// String selectedtext = "";
		String wlbez = txtEingabezeile.getText();
		String wlbezalt = "";
		String wlrang = txtEingabezeile1.getText();
		String wldb = "";
		// Fehlende Eingabe prüfen
		if (txtEingabezeile.getText().equals(""))
		{
			Msgbox.show("Datensatz sichern...", "Speichern nicht möglich...\nBitte eine BEZEICHNUNG für den Feiertag/Festtag eingeben!");
			txtEingabezeile.requestFocus();
			return;
		}
		if (txtEingabezeile1.getText().equals(""))
		{
			Msgbox.show("Datensatz sichern...", "Speichern nicht möglich...\nBitte einen RANG für für den Feiertag/Festtag eingeben!");
			txtEingabezeile1.requestFocus();
			return;
		}
		// Eingaben vollständig - nun prüfwen, ob neu oder edit
		if (selected != null)
		{ // Datensatz editieren
			wldb = selected.getWolidb();
			wlbezalt = selected.getWolibez(); // aus der Selektierung die Bezeichnung holen (leer, wenn nichts angeklickt)
			// wlid = selected.getWoliid();
			if (wldb.equals(ValuesGlobals.zentralEingabe))
			{
				if (Msgbox.yesno("!Zentraler Datensatz!", "Soll der zentral erfasste Datensatz geändert werden?") == true)
				{
					wldb = ValuesGlobals.zentralEingabeedit;
				}
				else
				{// wenn der zentrale DS nicht geändert werden soll- Ende
					return;
				}
			}
			else if (wldb.equals(ValuesGlobals.privatEingabe))
			{// privat erfasster DS ändern
				wldb = ValuesGlobals.privatEingabeedit;
			}
			else
			{

			}
			wlbezalt = selected.getWolibez();
			db.setWoliHinzu(wlbez, wlrang, false, wlbezalt, wldb); // Speichernvorgang

		}

		else
		{ // neuer Datensatz
			wlbezalt = "";
			wldb = ValuesGlobals.privatEingabe;
			db.setWoliHinzu(wlbez, wlrang, true, wlbezalt, wldb);
		}

		fctEingabefelderLeeren();

		anzeigenTabelle(wlbez, 0); // aktualisieren
		txtEingabezeile.requestFocus();

	}

	@FXML
	void btnZuweisen_OnClick(ActionEvent event) throws Exception
	{
		String markiertZeile = "";
		String stck = "";
		stck = lblGewaehlt.getText();
		WochenliedlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			markiertZeile = selected.getWolibez();
		}
		else
		{
			markiertZeile = "";
			Msgbox.show( "Wochenliedzuordnung festlegen...", "Bitte einen Feiertag/Festtag aus der Gesamtliste aktivieren!");

			return;
		}

		try
		{
			// DatabaseControllerPcndWochenlied stckwolicon = new
			// DatabaseControllerPcndWochenlied();
			// String selectedtext = "";
			db.setLiedStckWoliZuweisen(markiertZeile, stck); // setThemaHinzu(thbez, false, selectedtext);
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 19 || e.getMessage().contains("UNIQUE"))
			{
				Msgbox.warn("Wochenliedzuordnung festlegen - Achtung Dopplung!", "Dieser Feiertag/Festtag wurde bereits dem LIED/STÜCK zugeordnet.");
			}
			else
			{
				e.printStackTrace();
			}
		}
		tblVwListeAlle.getSelectionModel().clearSelection();
		fctEingabefelderLeeren();
		anzeigenTabelle(markiertZeile, null); // aktualisieren
	}

	@FXML
	void btnWegnehmen_OnClick(ActionEvent event) throws Exception
	{
		String zugew;
		String stueck;
		LiederStueckeWoliModel selected = tblVwListeZugewiesen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show("Verweis auf Feiertag/Festtag entfernen...", "Bitte eine Zeile auswählen!");
			return;
		}

		zugew = tblVwListeZugewiesen.getSelectionModel().getSelectedItem().getWoli();
		stueck = tblVwListeZugewiesen.getSelectionModel().getSelectedItem().getLiedstueck();
//		if(Msgbox.yesno("Verweis auf Feiertag/Festtag entfernen...", "Den Verweis " + zugew + "\nendgültig vom STÜCK \n" + stueck + "\nentfernen?")==false)
//		{
//			return;
//		}
		try
		{

			// DefaultDatabaseController themenstckwegcon = (DefaultDatabaseController)
			// DatabaseControllerFactory.getDatabaseController("adb");
			// DatabaseControllerPcndWochenlied stckwoliwegcon = new
			// DatabaseControllerPcndWochenlied();
			// String selectedtext = "";
			db.setLiedStckWoliWegnehmen(zugew, stueck);
		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}
		finally
		{
			tblVwListeZugewiesen.getSelectionModel().clearSelection();
			anzeigenTabelle(null, null); // aktualisieren
		}
	}

	@FXML
	void handletblvwthemenliste_onmouse_clicked()
	{// Beim Klicken auf die rechte Tabelle soll das markierte Buch in der
		// Eingabezeile darunter erscheinen

		WochenliedlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			txtEingabezeile.setText(selected.getWolibez());
			txtEingabezeile1.setText(selected.getWolirang());
			txtEingabezeile2.setText(selected.getWoliid().toString());
			// txtEingabezeile3.setText(selected.getBem());
			txtEingabezeile.requestFocus(); // Cursor
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
