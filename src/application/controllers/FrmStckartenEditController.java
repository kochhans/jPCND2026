package application.controllers;

import java.util.List;

import application.ValuesGlobals;
import application.controllers.base.BaseEditController;
import application.models.StueckartlisteModel;
import application.uicomponents.Msgbox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;

public class FrmStckartenEditController extends BaseEditController<StueckartlisteModel>
{
	// --- TableViews
	@FXML
	private TableView<StueckartlisteModel> tblVwListeAlle;
	@FXML
	private TableColumn<StueckartlisteModel, String> tblvwCol1, tblvwCol5, tblvwCol6;

	// --- Steuerelemente
	@FXML
	private Button btnLoeschen, btnNeu, btnSpeichern, btnZurueck;
	@FXML
	private Label lblTabellenueberschrift;
	@FXML
	private TextField txtEingabezeile1, txtEingabezeile2, txtEingabezeile3;
	@FXML
	private TextArea txtEingabezeile5;
	
	// --- Stage
	private Stage stage;
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	// --- Datenbank
	private DatabaseControllerPcndStueckarten db;
	public void setDb(DatabaseControllerPcndStueckarten db)
	{
	    this.db = db;
	}
// ------ Inits ------------------
	@FXML
	public void initialize()
	{
		// Hier nichts DB-relevantes machen
        // NUR UI-Grundsetup
        // KEINE DB!
	}
	
	// init(Stage stage) wird vom Startcontroller aufgerufen
	public void init(Stage stage) throws Exception
	{
		this.stage = stage;
		setUIDataListe();
		anzeigenTabelle("", null);
	}

	// TableView-Spalten setzen
	private void setUIDataListe()
	{
		lblTabellenueberschrift.setText("Gesamtliste");
		tblvwCol1.setCellValueFactory(new PropertyValueFactory<>("bez"));
		tblvwCol5.setCellValueFactory(new PropertyValueFactory<>("db"));
		tblvwCol6.setCellValueFactory(new PropertyValueFactory<>("id"));
	}

	// Tabelle anzeigen
	public void anzeigenTabelle(String sucheBez, Integer zielIndex) throws Exception
	{
		if (db == null)
			return; // Schutz, falls init() nicht aufgerufen
		List<StueckartlisteModel> list = db.getStueckartListeAll();
		ObservableList<StueckartlisteModel> oblist = FXCollections.observableArrayList(list);
		tblVwListeAlle.getSortOrder().clear();
		tblVwListeAlle.setItems(oblist);
		Platform.runLater(() -> {
			if (oblist.isEmpty())
				return;
			// Suche nach Bezeichnung
			if (sucheBez != null && !sucheBez.isEmpty())
			{
				for (StueckartlisteModel item : oblist)
				{
					if (sucheBez.equals(item.getBez()))
					{
						selectIndex(oblist.indexOf(item));
						return;
					}
				}
			}

			// Suche nach Index
			if (zielIndex != null)
			{
				int idx = Math.max(0, Math.min(zielIndex, oblist.size() - 1));
				selectIndex(idx);
			}
		});
	}
	
// ########### Ende Startmodule  #########################################################################
// #######################################################################################################

	private void selectIndex(int idx)
	{
		tblVwListeAlle.scrollTo(idx);
		tblVwListeAlle.getSelectionModel().select(idx);
		tblVwListeAlle.getFocusModel().focus(idx);
	}

	private void fctEingabefelderLeeren()
	{
		txtEingabezeile1.clear();
		txtEingabezeile2.clear();
		txtEingabezeile3.clear();
		txtEingabezeile1.requestFocus();
	}

	// ####################################################
	// Buttons

	@FXML
	void btnZurueck_OnClick(ActionEvent event)
	{
		stage.close();
	}

	@FXML
	void btnLoeschen_OnClick(ActionEvent event) throws Exception
	{
		StueckartlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show("Datensatz löschen...", "Bitte eine Stück-Art markieren!");
			return;
		}

		int indexVorher = tblVwListeAlle.getSelectionModel().getSelectedIndex();

		if (!Msgbox.yesnowarn("Datensatz löschen...", "STUECKART " + selected.getBez() + "\nendgültig entfernen?"))
		{
			return;
		}
		db.setStckartLoeschen(selected.getBez());
		anzeigenTabelle(null, indexVorher - 1);
		btnNeu_OnClick(event);
	}

	@FXML
	void btnNeu_OnClick(ActionEvent event)
	{
		Platform.runLater(() -> {
			tblVwListeAlle.getSelectionModel().clearSelection();
			fctEingabefelderLeeren();
		});
	}

	@FXML
	void btnSpeichern_OnClick(ActionEvent event) throws Exception
	{
		String stckartbez = txtEingabezeile1.getText();
		if (stckartbez.isEmpty())
		{
			Msgbox.show("Datensatz sichern...", "Bezeichnung ist Pflichtfeld!");
			txtEingabezeile1.requestFocus();
			return;
		}

		StueckartlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		String stckartdb = ValuesGlobals.privatEingabe;
		String stckartalt = "";

		if (selected != null)
		{// DS editieren
			stckartdb = selected.getDb();
			stckartalt = selected.getBez();

			if (stckartdb.equals(ValuesGlobals.zentralEingabe))
			{
				if (Msgbox.yesno("!Zentraler Datensatz!", "Soll der zentral erfasste Datensatz geändert werden?") == true)
				{
					stckartdb = ValuesGlobals.zentralEingabeedit;
				}

				else
				{// wenn der zentrale DS nicht geändert werden soll- Ende
					return;
				}
			}
			else if (stckartdb.equals(ValuesGlobals.privatEingabe))
			{// privat erfasster DS ändern
				stckartdb = ValuesGlobals.privatEingabeedit;
			}
			else
			{

			}
			db.setStckartbezSpeichern(stckartbez, false, stckartalt, stckartdb);
		}
		else
		{// neuer Datensatz
			stckartalt = "";
			stckartdb = ValuesGlobals.privatEingabe;
			db.setStckartbezSpeichern(stckartbez, true, stckartalt, stckartdb);
		}

		anzeigenTabelle(stckartbez, null);
		btnNeu_OnClick(event);
	}

	@FXML
	void handletblVwListeAlle_OnMouseClicked()
	{
		StueckartlisteModel selected = tblVwListeAlle.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			txtEingabezeile1.setText(selected.getBez());
			txtEingabezeile2.setText(selected.getDb());
			txtEingabezeile3.setText(Integer.toString(selected.getId()));
		}
	}

	@Override
	protected List<StueckartlisteModel> loadDataFromDatabase() throws Exception
	{
		return db.getStueckartListeAll();
	}

	@Override
	protected Object getDb()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
