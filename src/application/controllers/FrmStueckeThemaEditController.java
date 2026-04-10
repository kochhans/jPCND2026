package application.controllers;

import java.sql.SQLException;
import java.util.List;

import application.ValuesGlobals;
import application.controllers.base.BaseEditController;
import application.models.ThemenlisteModel;
import application.models.ThemenlisteStueckModel;
import application.uicomponents.Msgbox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FrmStueckeThemaEditController extends BaseEditController<ThemenlisteModel>
{

	// ===============================
	// FXML UI-Komponenten
	// ===============================
	@FXML
	private TableView<ThemenlisteModel> tblVwThemenAlle;
	@FXML
	private TableView<ThemenlisteStueckModel> tblVwThemenZugewiesen;
	@FXML
	private TableColumn<ThemenlisteModel, String> tblvwColThema1, tblvwColThema11, tblvwColThema12;
	@FXML
	private TableColumn<ThemenlisteStueckModel, String> tblvwColThema2;

	@FXML
	private Button btnLoeschen, btnNeu, btnSpeichern, btnWegnehmen, btnZurueck, btnZurueck2, btnZuweisen;
	@FXML
	private Label lblStueckGewaehlt, lblStueckauswahl;
	@FXML
	private TextField txtThema;

	// ===============================
	// Stage & DB-Controller
	// ===============================
	private Stage stage;
	// private DatabaseControllerPcndThemen dbStckThemen;
	private DatabaseControllerPcndThemen db;

	// ===============================
	// ObservableLists für TableViews
	// ===============================
	private ObservableList<ThemenlisteModel> oblist_th1 = FXCollections.observableArrayList();
	private ObservableList<ThemenlisteStueckModel> oblist_th2 = FXCollections.observableArrayList();

	// ===============================
	// Setter für DB-Controller und Stage
	// ===============================
//	public void setDbStckThemen(DatabaseControllerPcndStueckThemen dbStckThemen)
//	{
//		this.dbStckThemen = dbStckThemen;
//	}

	public void setDbThemen(DatabaseControllerPcndThemen dbThemen)
	{
		this.db = dbThemen;
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;

		// Wurde ein Stück ausgewählt?
		if (ValuesGlobals.Uebergabewert1.equals("") || ValuesGlobals.Uebergabewert1.equals(null))
		{ // nein

			lblStueckauswahl.setText("kein Stück gewählt - Bibel-Bücherliste editieren..."); // anp
			lblStueckGewaehlt.setAlignment(Pos.CENTER_RIGHT);
			lblStueckGewaehlt.setText("Bibelstellen editieren..."); // anp
			tblVwThemenZugewiesen.setDisable(true);
			btnWegnehmen.setDisable(true);
			btnZuweisen.setDisable(true);
			btnZurueck2.setDisable(true);
			Platform.runLater(() -> {
				txtThema.requestFocus();
			});
		}
		else
		{ // ja
			tblVwThemenZugewiesen.setDisable(false);
			btnWegnehmen.setDisable(false);
			btnZuweisen.setDisable(false);
			btnZurueck2.setDisable(false);
			lblStueckGewaehlt.setText(ValuesGlobals.Uebergabewert1);
			Platform.runLater(() -> {
				btnZuweisen.requestFocus();
			});
		}

	}

	// ===============================
	// initialize() nur für UI
	// ===============================
	@FXML
	public void setUIDataThemenliste()
	{
		tblvwColThema1.setCellValueFactory(new PropertyValueFactory<>("bez"));
		tblvwColThema11.setCellValueFactory(new PropertyValueFactory<>("db"));
		tblvwColThema12.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblvwColThema2.setCellValueFactory(new PropertyValueFactory<>("bez"));
	}

	public void initController() throws Exception
	{

	}

	public void init(Stage stModalwindow) throws Exception
	{
		if (db == null || db == null)
		{
			throw new IllegalStateException("DB-Controller noch nicht gesetzt!");
		}
		setUIDataThemenliste();
		loadData("",0); // lädt alle Tabellen
		// TODO Auto-generated method stub

	}

	// ===============================
	// Daten aus DB laden (Tabelle aktualisieren)
	// ===============================
	public void loadData(String sucheBez, Integer zielIndex) throws Exception
	{
		if (db == null || db == null)
		{
			throw new IllegalStateException("DB-Controller noch nicht gesetzt!");
		}

		// Themenliste laden
		oblist_th1.clear();
		oblist_th1.addAll(loadDataFromDatabase());
		tblVwThemenAlle.getSortOrder().clear();
		tblVwThemenAlle.setItems(oblist_th1);

		// Zugewiesene Themen für das aktuelle Stück laden
		oblist_th2.clear();
		if (ValuesGlobals.Uebergabewert1 != null && !ValuesGlobals.Uebergabewert1.isEmpty())
		{
			oblist_th2.addAll(db.getThemenFuerStueck(ValuesGlobals.Uebergabewert1));
			tblVwThemenZugewiesen.getSortOrder().clear();
			tblVwThemenZugewiesen.setItems(oblist_th2);
		}

		Platform.runLater(() -> {
			if (oblist_th1.isEmpty())
				return;
			// 🔹 FALL 1: nach Objekt (Speichern)
			if (sucheBez != null && !sucheBez.isEmpty())
			{
				for (ThemenlisteModel item : oblist_th1)
				{
					if (sucheBez.equals(item.getBez()))
					{
						int idx = oblist_th1.indexOf(item);
						selectIndex(idx);
						return;
					}
				}
			}
			// 🔹 FALL 2: nach Index (Löschen)
			if (zielIndex != null)
			{
				int idx = Math.max(0, Math.min(zielIndex, oblist_th1.size() - 1));
				selectIndex(idx);
			}
		});
		
		
		
		Platform.runLater(() -> {
			tblVwThemenAlle.getSelectionModel().clearSelection();
			tblVwThemenZugewiesen.getSelectionModel().clearSelection();
		});
	}

	// ===============================
	// Abstrakte Methode implementieren
	// ===============================
	@Override
	protected List<ThemenlisteModel> loadDataFromDatabase() throws Exception
	{
		if (db == null)
		{
			throw new IllegalStateException("DB-Controller noch nicht gesetzt!");
		}
		return db.getAlleThemen();
	}

	// ===============================
	// Hilfsmethode für Indexauswahl
	// ===============================
	private void selectIndex(int idx)
	{
		tblVwThemenAlle.scrollTo(idx);
		tblVwThemenAlle.getSelectionModel().select(idx);
		tblVwThemenAlle.getFocusModel().focus(idx);
	}

	// ===============================
	// Button Aktionen
	// ===============================
	@SuppressWarnings("unused")
	@FXML
	void btnSpeichern_OnClick(ActionEvent event) throws Exception
	{
		if (db == null)
			throw new IllegalStateException("DB-Themen Controller nicht gesetzt!");

		// Alle EIngabfelder für die Gesamttabelle einlesen
		String selectedtext = "";
		String selctedquelle="";
		String thbez = txtThema.getText();

		ThemenlisteModel selected = tblVwThemenAlle.getSelectionModel().getSelectedItem();
		if(selected !=null) {
			selctedquelle = selected.getDb();			
		}

		// Fehlende Eingabe prüfen
		if (thbez == null || thbez.isEmpty())
		{
			Msgbox.show( "Datensatz sichern...", "Bitte eine Bezeichnung für das neue THEMA eingeben!");
			txtThema.requestFocus();
			return;
		}

		if (selctedquelle.equals(ValuesGlobals.zentralEingabe))
		{

			if (Msgbox.yesno("!Zentraler Datensatz!", "Soll der zentral erfasste Datensatz geändert werden?") == true)
			{
				selctedquelle = ValuesGlobals.zentralEingabeedit;

			}
			else
			{

				return;
			}
		}
		else if (selctedquelle.equals(ValuesGlobals.privatEingabe))
		{
			selctedquelle = ValuesGlobals.privatEingabeedit;
		}
		else
		{
			selctedquelle = ValuesGlobals.privatEingabe;
		}

		if (selected != null)
		{
			db.setThemaHinzu(false, thbez, selected.getBez(), selctedquelle, selected.getId());
		}
		else
		{
			selectedtext="";
			
			db.setThemaHinzu(true, thbez, "", selctedquelle, 0);
		}

		
		
		txtThema.clear();
		loadData(thbez,0); // Tabelle aktualisieren
		txtThema.requestFocus();
	}

	@FXML
	void btnZurueck_OnClick(ActionEvent event)
	{
		this.stage.close();
//		if (stage != null)
//			stage.close();
	}

	@FXML
	void btnNeu_OnClick(ActionEvent event)
	{
		tblVwThemenAlle.getSelectionModel().clearSelection();
		tblVwThemenZugewiesen.getSelectionModel().clearSelection();
		txtThema.clear();
		txtThema.requestFocus();
	}

	@FXML
	void btnLoeschen_OnClick(ActionEvent event) throws Exception
	{
		String selectedthema ="";
		int indexVorher = 0;		
		ThemenlisteModel selected = tblVwThemenAlle.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show("Datensatz löschen...", "Bitte ein THEMA markieren!");
			return;
		}
		else
		{
			selectedthema = selected.getBez();

			if (Msgbox.yesnowarn("Datenatz löschen...", "Das THEMA " + selectedthema + "\nendgültig entfernen? "
					+ "\nACHTUNG: Es werden ALLE Zuordnungen zu LIEDERN/STÜCKEN entfernt!!!\n\n"
					+ "Überlegen Sie diesen Schritt gut - er kann nicht rückgängig gemacht werden.") == false)
			{
				return;
			}
			//DatabaseControllerPcndGesbuch gesbuchcon = new DatabaseControllerPcndGesbuch();
			db.deleteThema(selectedthema);
		}
		//anzeigenTabelle(null, indexVorher - 1);
		indexVorher = tblVwThemenAlle.getSelectionModel().getSelectedIndex();
		db.deleteThema(selected.getBez());
		loadData(null, indexVorher -1);
		txtThema.requestFocus();
		btnNeu.requestFocus();
	}

	@FXML
	void btnZuweisen_OnClick(ActionEvent event) throws SQLException
	{
		ThemenlisteModel selected = tblVwThemenAlle.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show( "Thema zuweisen...", "Bitte ein THEMA aus der Gesamt-Themenliste aktivieren!");
			return;
		}

		db.addThemaZuStueck(selected.getBez(), lblStueckGewaehlt.getText());
		try
		{
			loadData("",0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	void btnWegnehmen_OnClick(ActionEvent event) throws SQLException
	{
		ThemenlisteStueckModel selected = tblVwThemenZugewiesen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show( "Thema entfernen...", "Bitte ein Thema auswählen!");
			return;
		}

		db.removeThemaVonStueck(selected.getBez(), lblStueckGewaehlt.getText());
		try
		{
			loadData(null,0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	void handletblvwthemenliste_onmouse_clicked()
	{
		ThemenlisteModel selected = tblVwThemenAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
			txtThema.setText(selected.getBez());
	}

	@Override
	protected Object getDb()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
