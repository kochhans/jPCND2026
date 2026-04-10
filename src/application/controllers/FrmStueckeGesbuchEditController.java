
package application.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.ValuesGlobals;
import application.controllers.base.BaseEditController;
import application.models.GesangbuchModel;
import application.models.LiederStueckeGesbModel;
import application.uicomponents.Msgbox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FrmStueckeGesbuchEditController extends BaseEditController<GesangbuchModel> // anp
{
	// --- TableViews
	@FXML
	private TableView<GesangbuchModel> tblVwListeAlle; // für jedes Form diese Zeile anpassen (anp.)
	@FXML
	private TableView<LiederStueckeGesbModel> tblVwListeZugewiesen; // anp

	@FXML
	private TableColumn<GesangbuchModel, String> tblvwColVerfuegbarSpalte1, tblvwColVerfuegbarSpalte2, tblvwColVerfuegbarSpalte3, tblvwColVerfuegbarSpalte4; // anp
	@FXML
	private TableColumn<LiederStueckeGesbModel, String> tblvwColZugewiesenSpalte1, tblvwColZugewiesenSpalte2, tblvwColZugewiesenSpalte3; // anp

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
	ObservableList<GesangbuchModel> oblist_gesbballe = FXCollections.observableArrayList(); // anp
	// Tableviewliste zugewiesene Datensätze (links)
	ObservableList<LiederStueckeGesbModel> oblist_gesbzugew = FXCollections.observableArrayList();

	// #####################################################################################################################
	// Stage aufbauen

	private Stage stage;
	private DatabaseControllerPcndGesbuch db;
	public void setDb(DatabaseControllerPcndGesbuch db)
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
		//this.db = new DatabaseControllerPcndGesbuch();
		// Wurde ein Stück ausgewählt?
		if (ValuesGlobals.Uebergabewert1.equals("") || ValuesGlobals.Uebergabewert1.equals(null))
		{ // nein
			lblAuswahl.setText("kein Stück gewählt - Gesangbuchliste editieren..."); // anp
			lblGewaehlt.setAlignment(Pos.CENTER_RIGHT);
			lblGewaehlt.setText("Gesangbücher editieren..."); // anp
			tblVwListeZugewiesen.setDisable(true);
			btnWegnehmen.setDisable(true);
			btnZuweisen.setDisable(true);
			btnZurueck2.setDisable(true);
			txtEingabeNummer.setDisable(true);
			Platform.runLater(() -> {
				btnZurueck.requestFocus();
			});
		}
		else
		{ // ja
			lblGewaehlt.setText(ValuesGlobals.Uebergabewert1);
			// TableViews aktualisiseren
		}
		anzeigenTabelle("", 0);

	}

	// ----------- Table Viewlisten - Spalten
	// ------------------------------------------------------

	private void setUIDataListe() // (1) definieren
	{
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).
		tblvwColVerfuegbarSpalte1.setCellValueFactory(new PropertyValueFactory<GesangbuchModel, String>("bez")); // anp
		tblvwColVerfuegbarSpalte2.setCellValueFactory(new PropertyValueFactory<GesangbuchModel, String>("kurz")); // anp
		tblvwColVerfuegbarSpalte4.setCellValueFactory(new PropertyValueFactory<GesangbuchModel, String>("db")); // anp
		tblvwColVerfuegbarSpalte3.setCellValueFactory(new PropertyValueFactory<GesangbuchModel, String>("bem")); // anp
		tblvwColZugewiesenSpalte1.setCellValueFactory(new PropertyValueFactory<LiederStueckeGesbModel, String>("gesb")); // anpassen an die Modellklasse
		tblvwColZugewiesenSpalte2.setCellValueFactory(new PropertyValueFactory<LiederStueckeGesbModel, String>("nummer")); // anpassen an die Modellklasse
		tblvwColZugewiesenSpalte3.setCellValueFactory(new PropertyValueFactory<LiederStueckeGesbModel, String>("liedstueck")); // anpassen an die Modellklasse

	}

	public void anzeigenTabelle(String sucheBez, Integer zielIndex) throws Exception // (2) anzeigen nach Speichern
	{
		oblist_gesbballe.clear();
		oblist_gesbzugew.clear();
		System.out.println("000Anzeigen Tabellen nach Speichern - Erhaltener Wert: " + ValuesGlobals.Uebergabewert1);
		// rechte Tableview

		List<GesangbuchModel> listgb = db.getGbListeAll();//db.getGesAll();
		oblist_gesbballe.addAll(listgb);
		tblVwListeAlle.getSortOrder().clear(); // Sortierung in SQL ohne Steuerelemts Sortierung !! vor SetItems
		tblVwListeAlle.setItems(oblist_gesbballe);

		// linke Tableview (zugewiesen)
		List<LiederStueckeGesbModel> listzugewiesen = db.getLsGbuchListe(ValuesGlobals.Uebergabewert1);
		oblist_gesbzugew.addAll(listzugewiesen);
		tblVwListeZugewiesen.getSortOrder().clear(); // Sortierung in SQL ohne Steuerelemts Sortierung !! vor SetItems
		tblVwListeZugewiesen.setItems(oblist_gesbzugew);
		Platform.runLater(() -> {
			if (oblist_gesbballe.isEmpty())
				return;
			// 🔹 FALL 1: nach Objekt (Speichern)
			if (sucheBez != null && !sucheBez.isEmpty())
			{
				for (GesangbuchModel item : oblist_gesbballe)
				{
					if (sucheBez.equals(item.getBez()))
					{
						int idx = oblist_gesbballe.indexOf(item);
						selectIndex(idx);
						return;
					}
				}
			}
			// 🔹 FALL 2: nach Index (Löschen)
			if (zielIndex != null)
			{
				int idx = Math.max(0, Math.min(zielIndex, oblist_gesbballe.size() - 1));
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
		txtEingabeNummer.setText("");
		txtEingabezeile.setText("");
		txtEingabezeile1.setText("");
		txtEingabezeile2.setText("");
		txtEingabezeile3.setText("");
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
		GesangbuchModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show( "Datensatz löschen...", "Bitte ein GESANGBUCH in der Gesamtliste markieren, das gelöscht werden soll!");
		}
		else
		{

			if (Msgbox.yesnowarn("Datenatz löschen...", "Das GESANGBUCH " + selectedtext + "\nendgültig entfernen? "
					+ "\nACHTUNG: Es werden auch alle Zuordnungen zu LIEDERN/STÜCKEN entfernt!!!") == false)
			{
				return;
			}
			selectedtext = selected.getBez();
			//DatabaseControllerPcndGesbuch gesbuchcon = new DatabaseControllerPcndGesbuch();
			db.setGesbuchLoeschen(selectedtext);
		}
		anzeigenTabelle(null, indexVorher - 1);
		txtEingabezeile.requestFocus();
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
		// Alle EIngabfelder für die Gesamttabelle einlesen
		String gbbez = txtEingabezeile.getText();
		String gbkurz = txtEingabezeile1.getText();
		String gbdb = txtEingabezeile2.getText();
		String gbbem = txtEingabezeile3.getText();
		String selectedtext = "";
		// Prüfen, ob in der Gesamtliste eine Zeile markiert ist und zwischenspeichern
		GesangbuchModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();

		// Fehlende Eingabe prüfen
		if (txtEingabezeile.getText().equals(""))
		{
			Msgbox.show( "Datensatz sichern...", "Speichern nicht möglich...\nBitte eine BEZEICHNUNG für das neue GESANGBUCH eingeben!");
			txtEingabezeile.requestFocus();
			return;
		}
		if (txtEingabezeile1.getText().equals(""))
		{
			Msgbox.show("Datensatz sichern...", "Speichern nicht möglich...\nBitte ein KÜRZEL für das neue GESANGBUCH eingeben!");
			txtEingabezeile1.requestFocus();
			return;
		}

		if (gbdb.equals(ValuesGlobals.zentralEingabe))
		{

			if (Msgbox.yesno("!Zentraler Datensatz!", "Soll der zentral erfasste Datensatz geändert werden?") == true)
			{
				gbdb = ValuesGlobals.zentralEingabeedit;

			}
			else
			{

				return;
			}
		}
		else if (gbdb.equals(ValuesGlobals.privatEingabe))
		{
			gbdb = ValuesGlobals.privatEingabeedit;
		}

		if (selected != null)
		{ // Datensatz bearbeitet - Nachfrage, ob geändert werden soll
//			if (Msgbox.yesno("Datensatz bearbeiten...", "Möchten Sie wirklich die Bezeichnung des bestehenden Gesangbuchs\n " + selected +
//					"\n in der kompletten Datenbank ändern?") == false)
//			{
//				txtEingabezeile.setText("");
//				txtEingabezeile.requestFocus();
//				tblVwListeAlle.getSelectionModel().clearSelection();
//				return;
//			}

			selectedtext = selected.getBez();
			db.setGbHinzu(gbbez, gbkurz, gbdb, gbbem, false, selectedtext); // Speichernvorgang
		}
		else
		{ // neuer Datensatz
			selectedtext = "";
			gbdb = ValuesGlobals.privatEingabe;
			db.setGbHinzu(gbbez, gbkurz, gbdb, gbbem, true, selectedtext); // Speichernvorgang
		}
		fctEingabefelderLeeren();
		anzeigenTabelle(gbbez, 0); // aktualisieren
		txtEingabezeile.requestFocus();
	}

	@FXML
	void btnZuweisen_OnClick(ActionEvent event) throws Exception
	{
		String markiertZeile = "";
		int gesnrsort = 0;
		String gesbuch = "";
		String gesnr = "";
		String stck = "";
		GesangbuchModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			markiertZeile = selected.getBez();
		}
		else
		{
			markiertZeile = "";
			Msgbox.show("Gesangbuchverweis festlegen...", "Bitte ein GESANGBUCH aus der Gesamtliste aktivieren!");

			return;
		}

		if (txtEingabeNummer.getText().isEmpty())
		{// falls fehlerhafte Eingabe vorliegt, berichtigen
			Msgbox.show("Gesangbuchverweis festlegen...", "Bitte die NUMMER des Stücks aus dem Gesangbuch eingeben!");
			txtEingabeNummer.requestFocus();
			return;
		}

		gesbuch = markiertZeile;
		gesnr = txtEingabeNummer.getText();
		stck = lblGewaehlt.getText();

		try
		{
			gesnr = txtEingabeNummer.getText(); // Variable mit dem endgültigen Wert füllen
			gesnrsort = Integer.parseInt(txtEingabeNummer.getText());
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			gesnrsort = 999;
		}

		System.out.println(gesnrsort);

		try
		{
			// DatabaseControllerPcndGesbuch gesbuchstckcon = new
			// DatabaseControllerPcndGesbuch();
			// String selectedtext = "";
			db.setLiedStckGesbuchNeu(gesbuch, gesnr, stck, gesnrsort); // setThemaHinzu(thbez, false, selectedtext);
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 19 || e.getMessage().contains("UNIQUE"))
			{
				Msgbox.warn("Gesangbuchverweis zuordnen...", "Dieses GESANGBUCH und NUMMER wurde bereits dem LIED/STÜCK zugeordnet.");
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
		int zugew = 0;
		// String stueck="";
		LiederStueckeGesbModel selected = tblVwListeZugewiesen.getSelectionModel().getSelectedItem();

		if (selected == null)
		{
			Msgbox.show("Gesangbuchverweis entfernen...", "Bitte eine Zeile auswählen!");
			return;
		}

		zugew = tblVwListeZugewiesen.getSelectionModel().getSelectedItem().getId();

		try
		{
			db.setLiedStckGesbuchWegnehmen(zugew);
		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}
		finally
		{
			anzeigenTabelle(null, null); // aktualisieren
		}
	}

	@FXML
	void handletblvwthemenliste_onmouse_clicked()
	{// Beim Klicken auf die rechte Tabelle soll das markierte Buch in der
		// Eingabezeile darunter erscheinen

		GesangbuchModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			txtEingabezeile.setText(selected.getBez());
			txtEingabezeile1.setText(selected.getKurz());
			txtEingabezeile3.setText(selected.getDb());
			txtEingabezeile2.setText(selected.getBem());
			txtEingabeNummer.requestFocus(); // Cursor auf die Eingabe der Verse
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
