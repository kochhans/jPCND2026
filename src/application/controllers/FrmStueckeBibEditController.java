
package application.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.ValuesGlobals;
import application.controllers.base.BaseEditController;
import application.models.BibellisteModel; //anp
import application.models.LiederStueckeBibelModel; //anp
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

public class FrmStueckeBibEditController extends BaseEditController<BibellisteModel> // anp
{
	// --- TableViews
	@FXML
	private TableView<BibellisteModel> tblVwListeAlle; // für jedes Form diese Zeile anpassen (anp.)
	@FXML
	private TableView<LiederStueckeBibelModel> tblVwListeZugewiesen; // anp

	@FXML
	private TableColumn<BibellisteModel, String> tblvwColVerfuegbarSpalte1, tblvwColVerfuegbarSpalte2, tblvwColVerfuegbarSpalte3, tblvwColVerfuegbarSpalte4; // anp
	@FXML
	private TableColumn<LiederStueckeBibelModel, String> tblvwColZugewiesenSpalte1, tblvwColZugewiesenSpalte2, tblvwColZugewiesenSpalte3; // anp

	// --- Steuerelemente definieren
	// Buttons
	@FXML
	private Button btnLoeschen, btnNeu, btnSpeichern, btnWegnehmen, btnZurueck, btnZurueck2, btnZuweisen;
	// Labels
	@FXML
	private Label lblHaupttitel, lblGewaehlt, lblAuswahl;
	// Textfelder
	@FXML
	private TextField txtEingabezeile, txtEingabezeile2, txtEingabezeile3, txtEingabeVerse;

	// --- Listen für TableView anlegen
	// Tableviewliste für alle Datensätze (rechts)
	ObservableList<BibellisteModel> oblist_biballe = FXCollections.observableArrayList(); // anp
	// Tableviewliste zugewiesene Datensätze (links)
	ObservableList<LiederStueckeBibelModel> oblist_bibzugew = FXCollections.observableArrayList();

	// #####################################################################################################################
	// Stage aufbauen

	private Stage stage;
	private DatabaseControllerPcndBibel db;
	public void setDb(DatabaseControllerPcndBibel db)
	{
	    this.db = db;
	}
	//-------------------------------

	public void initialize() throws Exception
	{
		// leer lassen!!!!
		setUIDataListe();
	}

	// INIT
	// init(Stage stage)-- wird vom Startcontroller aufgerufen
	public void init(Stage stage) throws Exception
	{
		this.stage = stage;
		//this.db = new DatabaseControllerPcndBibel();

		// Wurde ein Stück ausgewählt?
		if (ValuesGlobals.Uebergabewert1.equals("") || ValuesGlobals.Uebergabewert1.equals(null))
		{ // nein
			lblAuswahl.setText("kein Stück gewählt - Bibel-Bücherliste editieren..."); // anp
			lblGewaehlt.setAlignment(Pos.CENTER_RIGHT);
			lblGewaehlt.setText("Bibelstellen editieren..."); // anp
			tblVwListeZugewiesen.setDisable(true);
			btnWegnehmen.setDisable(true);
			btnZuweisen.setDisable(true);
			btnZurueck2.setDisable(true);
			txtEingabeVerse.setDisable(true);
			Platform.runLater(() -> {
				btnZurueck.requestFocus();
			});
		}
		else
		{ // ja
			tblVwListeZugewiesen.setDisable(false);
			btnWegnehmen.setDisable(false);
			btnZuweisen.setDisable(false);
			btnZurueck2.setDisable(false);
			lblGewaehlt.setText(ValuesGlobals.Uebergabewert1);
			// TableViews aktualisiseren
		}
		// setUIDataListe(); // (1)
		anzeigenTabelle(null, 0); // (2)

	}

	// ----------- Table Viewlisten - Spalten
	// ------------------------------------------------------

	private void setUIDataListe() // (1) definieren
	{
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).
		tblvwColVerfuegbarSpalte1.setCellValueFactory(new PropertyValueFactory<BibellisteModel, String>("buch")); // anp
		tblvwColVerfuegbarSpalte2.setCellValueFactory(new PropertyValueFactory<BibellisteModel, String>("kuerzel")); // anp
		tblvwColVerfuegbarSpalte3.setCellValueFactory(new PropertyValueFactory<BibellisteModel, String>("birang")); // anp
		tblvwColVerfuegbarSpalte4.setCellValueFactory(new PropertyValueFactory<BibellisteModel, String>("bidb")); // anp

		tblvwColZugewiesenSpalte1.setCellValueFactory(new PropertyValueFactory<>("lsbibez"));
		tblvwColZugewiesenSpalte2.setCellValueFactory(new PropertyValueFactory<>("lsbiId"));

	}

	public void anzeigenTabelle(String sucheBez, Integer zielIndex) throws Exception // (2) anzeigen nach Speichern
	{
		System.out.println("Anzeigen Tabellen nach Speichern - Erhaltener Wert: " + ValuesGlobals.Uebergabewert1);
		// rechte Tableview (alle)
		oblist_biballe.clear();
		List<BibellisteModel> listbib = db.getBibelListeAll(); // getBibelListeAll();
		oblist_biballe.addAll(listbib);
		tblVwListeAlle.getSortOrder().clear(); // Sortierung in SQL ohne Steuerelemts Sortierung !! vor SetItems
		tblVwListeAlle.setItems(oblist_biballe);
		// linke Tableview (zugewiesen)
		oblist_bibzugew.clear();
		if (ValuesGlobals.Uebergabewert1 != null && !ValuesGlobals.Uebergabewert1.isBlank())
		{
			List<LiederStueckeBibelModel> listzugewiesen = db.getBibellisteZugew(ValuesGlobals.Uebergabewert1);
			oblist_bibzugew.addAll(listzugewiesen);
		}
		tblVwListeZugewiesen.getSortOrder().clear();
		tblVwListeZugewiesen.setItems(oblist_bibzugew);
		
		Platform.runLater(() -> {
			if (oblist_biballe.isEmpty())
				return;
			// 🔹 FALL 1: nach Objekt (Speichern)
			if (sucheBez != null && !sucheBez.isEmpty())
			{
				for (BibellisteModel item : oblist_biballe)
				{
					if (sucheBez.equals(item.getBuch()))
					{
						int idx = oblist_biballe.indexOf(item);
						selectIndex(idx);
						return;
					}
				}
			}
			// 🔹 FALL 2: nach Index (Löschen)
			if (zielIndex != null)
			{
				int idx = Math.max(0, Math.min(zielIndex, oblist_biballe.size() - 1));
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
		txtEingabeVerse.setText("");
		txtEingabezeile.setText("");
		txtEingabezeile2.setText("");
		// txtEingabezeile3.setText("");
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
		BibellisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show( "Datensatz löschen...", "Bitte ein BIBELBUCH in der Gesamtliste markieren, das gelöscht werden soll!");
		}
		else
		{

			if (Msgbox.yesnowarn( "Datenatz löschen...", "Das BIBELBUCH " + selectedtext + "\nendgültig entfernen? "
					+ "\nACHTUNG: Es werden auch alle Zuordnungen zu LIEDERN/STÜCKEN entfernt!!!") == false)
			{
				return;
			}
			selectedtext = selected.getBuch();
			// DatabaseControllerPcndBibel bibelcon = new DatabaseControllerPcndBibel();
			indexVorher = tblVwListeAlle.getSelectionModel().getSelectedIndex();
			db.setBibelbuchLoeschen(selectedtext);
		}
		anzeigenTabelle(null, indexVorher - 1); // aktualisieren
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
		//DatabaseControllerPcndBibel bibelcon = new DatabaseControllerPcndBibel(); // neuen DB Controller anlegen
		// Alle EIngabfelder für die Gesamttabelle einlesen
		String bibuchbez = txtEingabezeile.getText();
		String bibuchkurz = txtEingabezeile2.getText();
		String bibuchrang = txtEingabezeile3.getText();
		String bidb = "";
		String selectedtext = "";

		// Fehlende Eingabe prüfen
		if (txtEingabezeile.getText().equals(""))
		{
			Msgbox.show("Datensatz sichern...", "Speichern nicht möglich...\nBitte eine BEZEICHNUNG für das neue BIBELBUCH eingeben!");
			txtEingabezeile.requestFocus();
			return;
		}
		if (txtEingabezeile2.getText().equals(""))
		{
			Msgbox.show( "Datensatz sichern...", "Speichern nicht möglich...\nBitte ein KÜRZEL für das neue BIBELBUCH eingeben!");
			txtEingabezeile2.requestFocus();
			return;
		}
		// Prüfen, ob in der Gesamtliste eine Zeile markiert ist und zwischenspeichern
		BibellisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();

		if (selected != null)
		{
			bidb = selected.getBidb();
		}

		if (txtEingabezeile3.getText().isEmpty())
		{// falls fehlerhafte Eingabe vorliegt, berichtigen
			txtEingabezeile3.setText("0000");
		}
		else
		{
			try
			{
				int value = Integer.parseInt(bibuchrang);
				txtEingabezeile3.setText(String.format("%04d", value));
			}
			catch (NumberFormatException e)
			{
				txtEingabezeile3.setText("0000"); // Fallback bei ungültigem Inhalt
			}
		}
		bibuchrang = txtEingabezeile3.getText(); // Variable mit dem endgültigen Wert füllen

		if (bidb.equals(ValuesGlobals.zentralEingabe))
		{

			if (Msgbox.yesno( "!Zentraler Datensatz!", "Soll der zentral erfasste Datensatz geändert werden?") == true)
			{
				bidb = ValuesGlobals.zentralEingabeedit;
			}
			else
			{
				return;
			}
		}
		else if (bidb.equals(ValuesGlobals.privatEingabe))
		{
			bidb = ValuesGlobals.privatEingabeedit;
		}
		else 
		{
			bidb = ValuesGlobals.privatEingabeedit;
		}

		if (selected != null)
		{ // Datensatz editieren
			selectedtext = selected.getBuch();
			db.setBuchHinzu(bibuchbez, bibuchkurz, bibuchrang, false, selectedtext, bidb); // Speichernvorgang
		}
		else
		{ // neuer Datensatz
			selectedtext = "";
			bidb = ValuesGlobals.privatEingabe;
			db.setBuchHinzu(bibuchbez, bibuchkurz, bibuchrang, true, selectedtext, bidb); // Speichernvorgang
		}

		fctEingabefelderLeeren();
		anzeigenTabelle(bibuchbez, 0); // aktualisieren
		txtEingabezeile.requestFocus();

	}

	@FXML
	void btnZuweisen_OnClick(ActionEvent event) throws Exception
	{
		String markiertZeile = "";
		BibellisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();

		if (selected != null)
		{
			markiertZeile = selected.getBuch();
		}
		else
		{
			markiertZeile = "";
			Msgbox.show("Bibelstelle zuweisen...", "Bitte ein BIBELBUCH aus der Gesamtliste aktivieren!");
			return;
		}

		String bibuch = markiertZeile;
		String bibverse = txtEingabeVerse.getText();
		String stck = lblGewaehlt.getText();

		try
		{
			// DefaultDatabaseController themenstckcon = (DefaultDatabaseController)
			// DatabaseControllerFactory.getDatabaseController("adb");
			//DatabaseControllerPcndBibel bibelstckcon = new DatabaseControllerPcndBibel();
			// String selectedtext = "";
			db.setLiedStckBibelNeu(bibuch, bibverse, stck); // setThemaHinzu(thbez, false, selectedtext);
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 19 || e.getMessage().contains("UNIQUE"))
			{
				Msgbox.warn("Thema zuordnen...", "Diese BIBELSTELLE wurde bereits dem LIED/STÜCK zugeordnet.");
			}
			else
			{
				e.printStackTrace();
			}
		}
		tblVwListeAlle.getSelectionModel().clearSelection();
		fctEingabefelderLeeren();
		anzeigenTabelle(bibuch, 0); // aktualisieren

	}

	@FXML
	void btnWegnehmen_OnClick(ActionEvent event) throws Exception
	{
		String zugew = "";
		LiederStueckeBibelModel selected = tblVwListeZugewiesen.getSelectionModel().getSelectedItem();
		zugew = selected.getLsbiId();
		String stueck = lblGewaehlt.getText();
		if (zugew.equals(""))
		{
			Msgbox.show( "Bibelstelle entfernen...", "Bitte ein bisher zugewiesenes BIBELSTELLE aus der Liste markieren!");
			return;
		}

		try
		{
			//DatabaseControllerPcndBibel bibelstckwegcon = new DatabaseControllerPcndBibel();
			db.setLiedStckBibelWegnehmen(zugew, stueck); // setThemaHinzu(thbez, false, selectedtext);
		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}
		finally
		{
			anzeigenTabelle(null, 0); // aktualisieren
		}
	}

	@FXML
	void handletblvwthemenliste_onmouse_clicked()
	{// Beim Klicken auf die rechte Tabelle soll das markierte Buch in der
		// Eingabezeile darunter erscheinen

		BibellisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			txtEingabezeile.setText(selected.getBuch());
			txtEingabezeile2.setText(selected.getKuerzel());
			txtEingabezeile3.setText(selected.getBirang());
			txtEingabeVerse.requestFocus(); // Cursor auf die Eingabe der Verse
		}

	}

	@Override
	protected List<BibellisteModel> loadDataFromDatabase() throws Exception
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
