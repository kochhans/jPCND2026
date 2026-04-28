package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import java.util.Set;

import java.util.prefs.Preferences;

import application.database.CSVImporterAktionen;
import application.database.CSVImporterCVW;
import application.database.CSVImporterCVWResult;
import application.database.CSVImporterPersonen;
import application.database.CSVImporterPositionen;
import application.database.SQLiteImporter;
import application.database.SQLiteImporterPersonen;
import application.database.SQLiteImporterPositionen;
import application.models.AktionenListeModel;
import application.models.AktionenListePersonenModel;
import application.models.AktionenListePositionenModel;
import application.models.CvwPersonenComboChorModel;
import application.models.CvwPersonenComboGruppeModel;
import application.models.CvwPersonenModel;
import application.uicomponents.Msgbox;
import application.utils.TableData;

// .....................................................
public class FrmAktionenStammdatenController
{
	@FXML
	private BorderPane rootPane;

// -----------------------------------------------------
// Steuerelemente
// -----------------------------------------------------
// ----------------------------
// (0) Menues
// ----------------------------
	// keine---
// ----------------------------
// (1) Kopfbereich
// ----------------------------

	// BUTTONS---------------------
	@FXML
	private Button btnZurueck;

// ----------------------------------------
// (2) Register Tabs - Importieren
// ----------------------------------------
	@FXML
	private TabPane tabPaneStammdaten;
	// TAB-Pane
	@FXML
	private Tab tabStammdatenPersonen, tabImportieren;

	// IMPORT---------------------

	@FXML
	private TextField txtPersFilterName,
			txtPersStammChor, txtPersStammGruppe, txtPersStammStimme, txtPersStammInstrument,
			txtPersStammName, txtPersStammVorname, txtPersStammTelefon, txtPersStammMail, txtPersStammId;

	@FXML
	private ProgressBar pbImport;
	@FXML
	private Label lblImportStatus, lblAnzahlFilter;

	@FXML
	private ComboBox<CvwPersonenComboChorModel> cbxPersFilterChor;

	@FXML
	private ComboBox<CvwPersonenComboGruppeModel> cbxPersFilterGruppe;

	// --- Listen für TableView anlegen
	// Tableview StammdatenPersonen
	@FXML
	private TableView<CvwPersonenModel> tblvwCvwPersonenImport;
	@FXML
	private TableColumn<CvwPersonenModel, Integer> colPekeyid;
	@FXML
	private TableColumn<CvwPersonenModel, Integer> colPeid;
	@FXML
	private TableColumn<CvwPersonenModel, String> colPename;
	@FXML
	private TableColumn<CvwPersonenModel, String> colPevname;
	@FXML
	private TableColumn<CvwPersonenModel, String> colPeinstrument;
	@FXML
	private TableColumn<CvwPersonenModel, String> colPechor;
	@FXML
	private TableColumn<CvwPersonenModel, String> colPestimme;
	@FXML
	private TableColumn<CvwPersonenModel, String> colPegruppe;
	@FXML
	private TableColumn<CvwPersonenModel, String> colPetelefon;
	@FXML
	private TableColumn<CvwPersonenModel, String> colPemail;

	@FXML
	private Button btnFilterAn, btnFilterAus;
	@FXML
	private TabPane tabPanePosFilter;

	@FXML
	private CheckBox chkMehrfachauswahl;

// ####################################################################################################
	// Tblvw-Binding

	private TableData<CvwPersonenModel> oblist_personencvwimportData = new TableData<>();

	// Liste für Filter Personen Combos
	private ObservableList<CvwPersonenComboChorModel> oblist_filterpersonenchor = FXCollections.observableArrayList();
	private ObservableList<CvwPersonenComboGruppeModel> oblist_filterpersonengruppe = FXCollections.observableArrayList();

// ###########################################################################################
// ######### init - Methoden
// ###########################################################################################

	private DatabaseControllerAktionen db;

//******************************************************************************************************
	public void setDbControllerAktionen(DatabaseControllerAktionen db)
	{
		this.db = db;
	}

	// ----
	@FXML
	public void initialize()
	{ // kein Datenzugriff hier, nur UI
		oblist_personencvwimportData.bind(tblvwCvwPersonenImport);
		lblAnzahlFilter.setText("0 Personen gefiltert");
		tblvwCvwPersonenImport.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	@FXML
	public void onShow(String woher) throws Exception
	{// Hier die Scene-spezifische Initialisierung
		if (db == null)
		{
			throw new IllegalStateException("DB-Controller wurde nicht gesetzt!");
		}
		initTabPanes();
		initData();

		if ("personen".equals(woher))
		{
			tabPaneStammdaten.getSelectionModel().select(tabStammdatenPersonen);
		}
		else if ("import".equals(woher))
		{
			tabPaneStammdaten.getSelectionModel().select(tabImportieren);
		}
	}

	private void initData() throws Exception
	{
		initFilterfelder();
		// ------ Tableviews konfigurieren
		// alle Tableviews Spalten unverschiebbar machen
		initTableCvwPersonenImport();
		anzeigenCombosTabPersonen();
		anzeigenTableCvwPersonenImport(0);
	}

	@FXML
	public void initEingabefelder()
	{

	}

	private void initFilterfelder() throws Exception
	{

		cbxPersFilterChor.setValue(null);
		cbxPersFilterChor.getEditor().setText("");
		cbxPersFilterGruppe.setValue(null);
		cbxPersFilterGruppe.getEditor().setText("");
		txtPersFilterName.setText("");

	}

	public void initTableCvwPersonenImport()
	{
		colPekeyid.setCellValueFactory(new PropertyValueFactory<>("pekeyid"));
		colPeid.setCellValueFactory(new PropertyValueFactory<>("peid"));
		colPename.setCellValueFactory(new PropertyValueFactory<>("pename"));
		colPevname.setCellValueFactory(new PropertyValueFactory<>("pevname"));
		colPeinstrument.setCellValueFactory(new PropertyValueFactory<>("peinstrument"));
		colPechor.setCellValueFactory(new PropertyValueFactory<>("pechor"));
		colPestimme.setCellValueFactory(new PropertyValueFactory<>("pestimme"));
		colPegruppe.setCellValueFactory(new PropertyValueFactory<>("pegruppe"));
		colPetelefon.setCellValueFactory(new PropertyValueFactory<>("petelefon"));
		colPemail.setCellValueFactory(new PropertyValueFactory<>("pemail"));
	}

	public void anzeigenCombosTabPersonen() throws Exception
	{
		// Vereinfachung ohne separate Sort-Zeile
		// ---------------- Chor aktualisieren ----------------
		List<CvwPersonenComboChorModel> listChor = db.getCvwPersonenComboChor()
				.stream()
				.sorted(Comparator.comparing(
						CvwPersonenComboChorModel::getPechor,
						String.CASE_INSENSITIVE_ORDER))
				.toList();

		oblist_filterpersonenchor.setAll(listChor);

		// ---------------- Gruppe aktualisieren ----------------
		List<CvwPersonenComboGruppeModel> listGruppe = db.getCvwPersonenComboGruppe()
				.stream()
				.sorted(Comparator.comparing(
						CvwPersonenComboGruppeModel::getPegruppe,
						String.CASE_INSENSITIVE_ORDER))
				.toList();

		oblist_filterpersonengruppe.setAll(listGruppe);

		cbxPersFilterChor.setItems(oblist_filterpersonenchor);
		cbxPersFilterGruppe.setItems(oblist_filterpersonengruppe);

	}

//######################################################################################
	// =================================
	// Steuerelemente im Kopfbereich
	// =================================

	@FXML
	private void btnZurueck_OnClick(ActionEvent event) throws Exception
	{
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // aktuelle Stage rausfinden
		stage.close(); // stage schließen

	}

	public void registerHandling()
	{

	}

	// ==================================================================================
	// Steuerelemente im RegTab Stammdaten Personen
	// ==================================================================================
	@FXML
	public void btnStammPersFilterAn_onClick()
	{
		anzeigenTableCvwPersonenImport(0);
	}

	@FXML
	public void btnStammPersFilterAus_onClick()
	{
		txtPersFilterName.setText("");
		cbxPersFilterChor.getEditor().setText("");
		cbxPersFilterGruppe.getEditor().setText("");
		anzeigenTableCvwPersonenImport(0);
	}

	@FXML
	public void btnStammPersSpeichern_onClick() throws Exception
	{
		int pekeyid = 0;
		if (txtPersStammName.getText().isBlank() ||
				txtPersStammVorname.getText().isBlank() ||
				txtPersStammChor.getText().isBlank() ||
				txtPersStammGruppe.getText().isBlank())
		{ // Validation
			Msgbox.show("Person speichern ...", "Bitte geben Sie in den Pflichtfeldern Daten ein!");
			txtPersStammName.requestFocus();
			return;
		}
		String pechor = txtPersStammChor.getText();
		String pegruppe = txtPersStammGruppe.getText();
		String peinstrument = txtPersStammInstrument.getText();
		String pemail = txtPersStammMail.getText();
		String pename = txtPersStammName.getText();
		String pestimme = txtPersStammStimme.getText();
		String petelefon = txtPersStammTelefon.getText();
		String pevorname = txtPersStammVorname.getText();

		if (txtPersStammId.getText().isBlank())
		{// das ist ein neuer DS neue ID gleich abholoen
			pekeyid = db.saveCvwPerson(0, pechor, pegruppe, peinstrument, pemail, pename, pestimme, petelefon, pevorname, pekeyid);

		}
		else
		{ // alter DS
			pekeyid = Integer.parseInt(txtPersStammId.getText());
			db.saveCvwPerson(0, pechor, pegruppe, peinstrument, pemail, pename, pestimme, petelefon, pevorname, pekeyid);
		}

		anzeigenTableCvwPersonenImport(pekeyid);
		leerenStammdatenPers();
		// anzeigenCombosTabPersonen();
	}

	@FXML
	public void btnStammPersLoeschen_onClick() throws Exception
	{

		ObservableList<CvwPersonenModel> selectedList = FXCollections.observableArrayList(
				tblvwCvwPersonenImport.getSelectionModel().getSelectedItems());
		int markierterIndex = tblvwCvwPersonenImport.getSelectionModel().getSelectedIndex();

		if (selectedList.isEmpty())
		{
			Msgbox.show("Person löschen ...", "Bitte markieren Sie mindestens eine Zeile!");
			return;
		}
		if (selectedList.size() > 1)
		{
			if (!Msgbox.yesno("SS", "Wirklich " + selectedList.size() + " Einträge löschen?"))
			{
				return;
			}
		}

		// 👉 mehrere oder eine – egal
		for (CvwPersonenModel person : selectedList)
		{
			db.deleteCvwPerson(person.getPekeyid());
		}

		anzeigenTableCvwPersonenImport(0);
		leerenStammdatenPers();

		// Selektion setzen
		ObservableList<CvwPersonenModel> items = tblvwCvwPersonenImport.getItems();
		if (!items.isEmpty())
		{
			if (markierterIndex >= items.size())
			{
				markierterIndex = items.size() - 1;
			}
			tblvwCvwPersonenImport.getSelectionModel().select(markierterIndex);

		}

	}

	@FXML
	public void btnStammPersNeu_onClick()
	{
		leerenStammdatenPers();

	}

	private void leerenStammdatenPers()
	{
		txtPersStammChor.setText("");
		txtPersStammGruppe.setText("");
		txtPersStammInstrument.setText("");
		txtPersStammMail.setText("");
		txtPersStammName.setText("");
		txtPersStammStimme.setText("");
		txtPersStammTelefon.setText("");
		txtPersStammVorname.setText("");
		txtPersStammId.setText("");
		tblvwCvwPersonenImport.getSelectionModel().clearSelection();

	}

	// Tabwechsel
	// Tabwechsel
	@FXML
	public void initTabPanes()
	{
		tabPaneStammdaten.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldTab, newTab) -> {
					if ("Stammdaten Personen".equals(newTab.getText()))
					{
//						cbxPersFilterChor.getEditor().setText(cbxMitwFilterChor.getEditor().getText());
//						cbxPersFilterGruppe.getEditor().setText(cbxMitwFilterGruppe.getEditor().getText());
//						txtPersFilterName.setText(txtMitwFilterName.getText());
//						leerenEingabenMitwirkende();

						anzeigenTableCvwPersonenImport(0);

						try
						{

						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if ("Importieren".equals(newTab.getText()))
					{

					}
				});
	}

// #################* INIT- HILFSFUNKTIONEN *###########################

	// ===============================================================================================
	// FILTERBEREICH-ComboBoxen init -- erstellt alle Filter-Comboboxenlisten
	// ===============================================================================================

	// ===============================================================================================
	// Tableviews init
	// ===============================================================================================

	void clearFilterfelder()
	{

	}

	@FXML
	void handletblvwCvwPersonenImport_onmouseClicked()
	{
		// 👉 Nur im SINGLE-Modus reagieren!
		if (tblvwCvwPersonenImport.getSelectionModel().getSelectionMode() != SelectionMode.SINGLE)
		{
			return; // im Mehrfachmodus nichts tun
		}

		CvwPersonenModel selected = tblvwCvwPersonenImport.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;

		txtPersStammChor.setText(selected.getPechor());
		txtPersStammGruppe.setText(selected.getPegruppe());
		txtPersStammInstrument.setText(selected.getPeinstrument());
		txtPersStammMail.setText(selected.getPemail());
		txtPersStammName.setText(selected.getPename());
		txtPersStammStimme.setText(selected.getPestimme());
		txtPersStammTelefon.setText(selected.getPetelefon());
		txtPersStammVorname.setText(selected.getPevname());
		txtPersStammId.setText(Integer.toString(selected.getPekeyid()));
	}

	@FXML
	void chkMehrfachauswahl_OnAction()
	{
		if (chkMehrfachauswahl.isSelected())
		{
			handleMehrfachmodusEin();

		}
		else
		{
			handleMehrfachmodusAus();
		}
	}

	@FXML
	void handleMehrfachmodusEin()
	{
		tblvwCvwPersonenImport.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	@FXML
	void handleMehrfachmodusAus()
	{
		tblvwCvwPersonenImport.getSelectionModel().clearSelection();
		tblvwCvwPersonenImport.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
// -------------------- Tabs aktivieren --------------------
	// ===============================================================================================
	// Tableview in Tab0 LITERATUR - Daten in Textfelder übergeben
	// ===============================================================================================

	@FXML
	public void handleTabChanged() throws Exception
	{
		Tab selectedTab = tabPanePosFilter.getSelectionModel().getSelectedItem();

		if (!selectedTab.isSelected())
			return;

		int selectedTabindex = tabPanePosFilter.getSelectionModel().getSelectedIndex();
		System.out.println(selectedTabindex);
		if (selectedTabindex == 0)
		{
			// oblist_lit.clear();

		}
		else if (selectedTabindex == 1)
		{
			// oblist_lit.clear();

		}
	}

	// ============================================================
	// Button-Handling rechte Seite Tab 1 - gespeicherte Aktionen
	// ===========================================================

	public void anzeigenTableCvwPersonenImport(int pekeyid)
	{
		// ---------------- Lade Daten aus DB ----------------
		try
		{
			String filcvwpersonname = "";
			String filcvwpersonvname = "";
			String filcvwpersoninstrument = "";
			String filcvwpersonchor = "";
			String filcvwpersongruppe = "";
			String filcvwpersonstimme = "";
			int filteranzahl = 0;
			filcvwpersonname = txtPersFilterName.getText();
			// filcvwpersoninstrument = txtPersFilterInstrument.getText();
			filcvwpersonchor = cbxPersFilterChor.getEditor().getText();
			filcvwpersongruppe = cbxPersFilterGruppe.getEditor().getText();
			// filcvwpersonstimme = txtPersFilterStimme.getText();

			List<CvwPersonenModel> daten = db.getPersonenListeAll(
					filcvwpersonname, filcvwpersonvname, filcvwpersoninstrument, filcvwpersonchor, filcvwpersongruppe, filcvwpersonstimme);
			// oblist_personenData.master.setAll(daten);
			oblist_personencvwimportData.master.setAll(daten);
			filteranzahl = oblist_personencvwimportData.master.size();
			lblAnzahlFilter.setText(String.valueOf(filteranzahl) + " Personen gefiltert.");
			// ObservableList<CvwPersonenModel> items = tblvwCvwPersonenImport.getItems();
			// alt!!!!!
			// items.setAll(daten);

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@FXML // --------- Personenliste aus CVW-importieren bzw. abgleichen
	private void handleBtnImportPersonen_onClick(ActionEvent event) throws Exception
	{

		if (!Msgbox.yesno("Import Personenliste ...",
				"Wollen Sie eine Personenliste aus dem Programm Chorverwaltung einlesen?"))
		{
			return;
		}

		// CSV auswählen (wie bisher)
		FileChooser chooser = new FileChooser();
		chooser.setTitle("CVW Personenliste - Datei auswählen ...");
		chooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter(
						"Chor-Personenliste (cvw_personen.csv)",
						"cvw_personen.csv"));

		File file = chooser.showOpenDialog(btnZurueck.getScene().getWindow());
		if (file == null)
			return;

		List<CvwPersonenModel> alleCVWPersonen;
		try
		{
			alleCVWPersonen = CSVImporterCVW.readCSV(file.getAbsolutePath());
		}
		catch (Exception e)
		{
			Msgbox.error("Fehler beim CSV-Import", e.getMessage());
			return;
		}

		Scene scene = btnZurueck.getScene();
		scene.setCursor(Cursor.WAIT);

		Task<CSVImporterCVWResult> task = new Task<>()
		{
			@Override
			protected CSVImporterCVWResult call() throws Exception
			{
				return SQLiteImporter.insertCvwPersonen(alleCVWPersonen);
			}
		};

		task.setOnSucceeded(e -> {
			CSVImporterCVWResult result = task.getValue();

			Msgbox.show("Personen-Import abgeschlossen",
				    "Neu eingefügt: " + result.inserted + "\n" +
				    "Aktualisiert: " + result.updated + "\n" +
				    "Unverändert: " + result.unchanged + "\n" +
				    
				    "Gesamt: " +
				    (result.inserted + result.updated + result.unchanged));
			try
			{
				anzeigenTableCvwPersonenImport(0);
				// anzeigenCombosTabPersonen();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}

			scene.setCursor(Cursor.DEFAULT);
		});

		task.setOnFailed(e -> {
			scene.setCursor(Cursor.DEFAULT);
			Msgbox.error("Fehler beim Import",
					task.getException().getMessage());
		});

		new Thread(task).start();

	}

	// =============================================================================================================================
	// ##### IMPORT Aktionen aus PCND CA -- 3xCSV-Dateien: ChorAktionen.csv,
	// ChorAktionenPositionen.csv, ChorAktionPersonen.csv
	// =============================================================================================================================
	@FXML
	public void btnImportCsv_OnClick() throws SQLException
	{

		// 1) CSV-Datei Choraktionen auswählen
		if (!Msgbox.weiterabbruch("IMPORT-Assistent",
				"Bitte wählen Sie nun die aus dem Programm \nPCND-Choraktivitäten exportierte CSV-Datei\n"
						+ "\nChorAktionen.csv aus!"))
		{
			return;
		}

		FileChooser chooserAktionen = new FileChooser();
		chooserAktionen.setTitle("(1) CSV-Datei Choraktionen auswählen");
		chooserAktionen.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("ChorAktionen", "ChorAktionen.csv"));

		Preferences prefs = Preferences.userNodeForPackage(getClass());
		String lastDir = prefs.get("lastImportDir", null);

		if (lastDir != null)
		{
			File dir = new File(lastDir);
			if (dir.exists())
				chooserAktionen.setInitialDirectory(dir);
		}

		File fileAktionen = chooserAktionen.showOpenDialog(btnZurueck.getScene().getWindow());
		if (!fileAktionen.getName().equalsIgnoreCase("ChorAktionen.csv"))
		{
			Msgbox.error("Fehler", "Bitte wählen Sie die Datei ChorAktionen.csv aus!");
			return;
		}
		prefs.put("lastImportDir", fileAktionen.getParent());

		// ----------------------------------------------------
		// 2 + 3) automatisch aus gleichem Verzeichnis laden
		// ----------------------------------------------------

		File importDir = fileAktionen.getParentFile();

		File filePositionen = new File(importDir, "ChorAktionenPositionen.csv");
		File filePersonen = new File(importDir, "ChorAktionPersonen.csv");
		// Existenz prüfen
		if (!filePositionen.exists())
		{
			Msgbox.error("Fehler", "Datei ChorAktionenPositionen.csv wurde nicht gefunden!");
			return;
		}

		if (!filePersonen.exists())
		{
			Msgbox.error("Fehler", "Datei ChorAktionPersonen.csv wurde nicht gefunden!");
			return;
		}

		// 4) CSV einlesen
		List<AktionenListeModel> alleAktionen;
		List<AktionenListePositionenModel> allePositionen;
		List<AktionenListePersonenModel> allePersonen;

		try
		{
			alleAktionen = CSVImporterAktionen.readCSV(fileAktionen.getAbsolutePath());
			allePositionen = CSVImporterPositionen.readCSV(filePositionen.getAbsolutePath());
			allePersonen = CSVImporterPersonen.readCSV(filePersonen.getAbsolutePath());
		}
		catch (Exception e)
		{
			Msgbox.error("Fehler beim CSV-Import", e.getMessage());
			return;
		}

		if (alleAktionen.isEmpty())
		{
			Msgbox.show("Import", "Die CSV-Datei der Choraktionen enthält keine Daten.");
			return;
		}

		// 6️⃣ Duplikate Choraktionen prüfen
		this.db = new DatabaseControllerAktionen();
		Set<Integer> existingAktionIds;
		try
		{
			existingAktionIds = db.getAlleAktionIds();
		}
		catch (SQLException e)
		{
			Msgbox.error("Datenbankfehler", e.getMessage());
			return;
		}

		List<AktionenListeModel> neuAktionen = new ArrayList<>();
		List<AktionenListeModel> doppeltAktionen = new ArrayList<>();

		for (AktionenListeModel a : alleAktionen)
		{
			if (existingAktionIds.contains(a.getCaid()))
			{
				doppeltAktionen.add(a);
			}
			else
			{
				neuAktionen.add(a);
			}
		}

		if (!doppeltAktionen.isEmpty())
		{
			boolean proceed = Msgbox.yesno(
					"Duplikate gefunden",
					doppeltAktionen.size() + " Choraktionen existieren bereits.\n" +
							"Nur neue (" + neuAktionen.size() + ") importieren?");
			if (!proceed)
				return;
		}

		// 7️⃣ Duplikate Positionen und Personen prüfen (nur neue Aktionen)
		Set<Integer> aktionNeuIds = new HashSet<>();
		for (AktionenListeModel a : neuAktionen)
			aktionNeuIds.add(a.getCaid());
		// -- Positionen
		List<AktionenListePositionenModel> neuPositionen = new ArrayList<>();
		for (AktionenListePositionenModel p : allePositionen)
		{
			if (aktionNeuIds.contains(p.getCapoCaId()))
			{
				neuPositionen.add(p);
			}
		}
		// -- Personen
		List<AktionenListePersonenModel> neuPersonen = new ArrayList<>();
		for (AktionenListePersonenModel pe : allePersonen)
		{
			if (aktionNeuIds.contains(pe.getCapecaid()))
			{
				neuPersonen.add(pe);
			}
		}
		// 8) Importieren ...
		Scene scene = btnZurueck.getScene();
		final List<AktionenListeModel> fAlleAktionen = alleAktionen;
		final List<AktionenListeModel> fNeuAktionen = neuAktionen;
		final List<AktionenListeModel> fDoppeltAktionen = doppeltAktionen;
		final List<AktionenListePositionenModel> fNeuPositionen = neuPositionen;
		final List<AktionenListePersonenModel> fNeuPersonen = neuPersonen;

		scene.setCursor(Cursor.WAIT);

//			Task<Void> importTask = new Task<>()
//			{
//				@Override
//				protected Void call() throws Exception
//				{
//					updateMessage("Importiere Choraktionen …");
//					updateProgress(0, 3);
//					SQLiteImporter.insertAktionen(fNeuAktionen);
		//
//					updateMessage("Importiere Positionen …");
//					updateProgress(1, 3);
//					SQLiteImporterPositionen.insertPositionen(fNeuPositionen);
		//
//					updateMessage("Importiere Personen …");
//					updateProgress(2, 3);
//					SQLiteImporterPersonen.insertPersonen(fNeuPersonen);
		//
//					updateProgress(3, 3);
//					updateMessage("Fertig");
//					return null;
//				}
//			};

		Task<Void> importTask = new Task<>()
		{
			@Override
			protected Void call() throws Exception
			{
				// DB sicher initialisieren
				if (db == null)
					db = new DatabaseControllerAktionen();

				updateMessage("Importiere Choraktionen …");
				updateProgress(0, 5);
				SQLiteImporter.insertAktionen(fNeuAktionen);

				updateMessage("Importiere Positionen …");
				updateProgress(1, 5);
				SQLiteImporterPositionen.insertPositionen(fNeuPositionen);

				updateMessage("Importiere Personen …");
				updateProgress(2, 5);
				SQLiteImporterPersonen.insertPersonen(fNeuPersonen);

				// 🔥 NEU: DB-Update hier rein!
				updateMessage("Aktualisiere Notenausgaben …");
				updateProgress(3, 5);
				db.saveUpdateNotenausgaben();

				updateMessage("Aktualisiere Titelgrafiken …");
				updateProgress(3, 5);
				db.saveUpdateTitelgrafiken();

				updateMessage("Fertig");
				updateProgress(4, 5);

				return null;
			}
		};

		// --direkt nach Erzeugen des Tasks
		pbImport.progressProperty().bind(importTask.progressProperty());
		lblImportStatus.textProperty().bind(importTask.messageProperty());

		pbImport.setVisible(true);
		lblImportStatus.setVisible(true);
		// ----------

		importTask.setOnRunning(e -> {

		});

//			importTask.setOnSucceeded(e -> {
//				scene.setCursor(Cursor.DEFAULT);
//				pbImport.progressProperty().unbind();
//				lblImportStatus.textProperty().unbind();
//				pbImport.setVisible(false);
//				lblImportStatus.setVisible(false);
		//
//				Platform.runLater(() -> Msgbox.show(
//						"Import abgeschlossen",
//						"CSV Choraktionen gelesen: " + fAlleAktionen.size() + "\n" +
//								"Neu importierte Aktionen: " + fNeuAktionen.size() + "\n" +
//								"Duplikate übersprungen: " + fDoppeltAktionen.size() + "\n" +
//								"Positionen für Aktionen importiert: " + fNeuPositionen.size() + "\n" +
//								"Teilnehmer für Aktionen importiert: " + fNeuPersonen.size()));
//				try
//				{
//					refreshComboBoxes();
//				}
//				catch (SQLException e1)
//				{
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
		//
//				try
//				{
//					//Notenausgaben von Kürzel auf Lang kopieren
//					this.db.saveUpdateNotenausgaben();
//					anzeigenTabelleAktionen();
//					
//				}
//				catch (Exception ex)
//				{
//					ex.printStackTrace();
//				}
//			});

		importTask.setOnSucceeded(e -> {
			scene.setCursor(Cursor.DEFAULT);

			pbImport.progressProperty().unbind();
			lblImportStatus.textProperty().unbind();
			pbImport.setVisible(false);
			lblImportStatus.setVisible(false);

			Msgbox.show(
					"Import abgeschlossen",
					"CSV Choraktionen gelesen: " + fAlleAktionen.size() + "\n" +
							"Neu importierte Aktionen: " + fNeuAktionen.size() + "\n" +
							"Duplikate übersprungen: " + fDoppeltAktionen.size() + "\n" +
							"Positionen importiert: " + fNeuPositionen.size() + "\n" +
							"Teilnehmer importiert: " + fNeuPersonen.size());

		});

		importTask.setOnFailed(e -> {
			scene.setCursor(Cursor.DEFAULT);

			pbImport.progressProperty().unbind();
			lblImportStatus.textProperty().unbind();
			pbImport.setVisible(false);
			lblImportStatus.setVisible(false);

			Msgbox.error(
					"Import fehlgeschlagen",
					importTask.getException().getMessage());
		});

		new Thread(importTask, "CSV-Import-Thread").start();

	}

	@FXML
	public void menImportCapo_OnClick()
	{

	}

	// ============================================================================
	// Hilfsmethoden für normale Comboboxen
	// ============================================================================
//	private void initComboBox(
//			ComboBox<String> combo,
//			List<AktionenListeModel> baseList,
//			Function<AktionenListeModel, String> extractor, boolean autoopen)
//	{
//
//		List<String> items = baseList.stream()
//				.map(extractor)
//				.filter(Objects::nonNull)
//				.filter(s -> !s.isBlank())
//				.distinct()
//				.sorted(String::compareToIgnoreCase)
//				.toList();
//
//		combo.setItems(FXCollections.observableArrayList(items));
//		combo.setEditable(true);
//
//		// makeSearchable(combo);
//		if (autoopen == true)
//		{
//			installAutoOpenOnFocus(combo);
//		}
//	}

	// Combo klappt runter, wenn man mit der Tab-Taste draufgeht
//	private void installAutoOpenOnFocus(ComboBox<?> combo)
//	{
//		combo.focusedProperty().addListener((obs, oldF, newF) -> {
//			if (newF)
//			{
//				Platform.runLater(combo::show);
//			}
//		});
//	}

}
