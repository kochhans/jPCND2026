package application.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;

import com.lowagie.text.Element;

import application.ConfigManager;
import application.SceneManager;
import application.ValuesGlobals;
//import application.database.AccdbChorimportService;
//import application.database.AccdbImportRepository;
import application.database.CSVImporterAktionen;
import application.database.CSVImporterPersonen;
import application.database.CSVImporterPositionen;
import application.database.SQLiteImporter;
import application.database.SQLiteImporterPersonen;
import application.database.SQLiteImporterPositionen;
import application.dbupdate.DatabaseUpdaten;
import application.models.AktionenListeModel;
import application.models.AktionenListePersonenModel;
import application.models.AktionenListePositionenModel;
import application.models.CvwPersonenComboChorModel;
import application.models.CvwPersonenComboGruppeModel;
import application.models.CvwPersonenModel;
import application.uicomponents.Msgbox;
import application.utils.TableData;
import application.utils.TableUtils;
import application.utils.ToolsUpdateChecker;
import application.utils.pdf.PdfExportOptions;
import application.utils.pdf.PdfMasterDetailExporterUtil;
import application.utils.pdf.PdfPathUtil;

// .....................................................
public class FrmAktionenController
{
	@FXML
	private AnchorPane rootScene2;
// -----------------------------------------------------
// Steuerelemente
// -----------------------------------------------------
	// ----------------------------
	// (0) Menues
	// ----------------------------
	@FXML
	private MenuItem men01AktionenBeenden, men02StdatPersonen;

	@FXML
	private MenuItem men40, men40Programm, men40Datenbank;
	// ----------------------------
	// (1) Kopfbereich
	// ----------------------------

	// LABELS---------------------
	@FXML
	private Label lblHaupttitel, lblTitelHinweise, lblAktionAktuell, lblUpdateinfo, lblFilterAnzahl;

	// COMBOBOXEN -----------------
	@FXML
	private ComboBox<String> cbxFilterAktion, cbxFilterOrt, cbxFilterGruppe;

	// TEXTFELDER -----------------
	@FXML
	private DatePicker dpFilterDatumVon, dpFilterDatumBis;

	@FXML
	private TextField txtFilterKurzbeschr;

	// RADIOBUTTONS in Togglegroups ---
	@FXML
	private RadioButton radFilterProben, radFilterAuff, radFilterAlles;
	// BUTTONS---------------------
	@FXML
	private Button btnFilterAn, btnFilterAus, btnAktionDrucken1, btnZuNotenarchiv, btnStammdaten;// btnUpdaten,

	// IMPORT---------------------
	@FXML
	private ProgressBar pbImport;
	@FXML
	private Label lblImportStatus;

	// ----------------------------
	// (2) Listenanzeige links
	// ----------------------------
	// BUTTONS---------------------

	// TABLEVIEW mit Spalten -----
	// Choraktionen
	@FXML
	private TableView<AktionenListeModel> tblvwChoraktionen;
	@FXML
	private TableColumn<AktionenListeModel, LocalDate> tblvwAktionenCol0; // Datum
	@FXML
	private TableColumn<AktionenListeModel, String> tblvwAktionenCol1; // Typ
	@FXML
	private TableColumn<AktionenListeModel, String> tblvwAktionenCol2; // Ort
	@FXML
	private TableColumn<AktionenListeModel, String> tblvwAktionenCol3; // Gruppe
	@FXML
	private TableColumn<AktionenListeModel, LocalTime> tblvwAktionenCol4; // Beginn
	@FXML
	private TableColumn<AktionenListeModel, LocalTime> tblvwAktionenCol5; // Treffpunkt
	@FXML
	private TableColumn<AktionenListeModel, Integer> tblvwAktionenCol6; // Anwesend
	@FXML
	private TableColumn<AktionenListeModel, String> tblvwAktionenCol7; // Beschreibung

	// ----------------------------
	// (3) Register rechts
	// ----------------------------
	@FXML
	private TabPane tabPaneAktionen;
	// TAB-Pane
	@FXML
	private Tab tabDetails, tabDrucken, tabMitwirkende;
	// ----------------------------
	// (3.1) Tab Details
	// ----------------------------
	// BUTTONS---------------------
	@FXML
	private Button btnAktionNeu, btnAktionSpeichern, btnAktionLoeschen, btnAktionDuplizieren, btnPosHoch, btnPosRunter, btnPosEntf, btnPosListeBearbeiten;

	// LABELS---------------------
	@FXML
	private Label lblAktion, lblOrt, lblGruppe, lblDatum, lblTreffpunkt, lblBeginn, lblAnzahl, lblDrucken1;

	@FXML
	private ProgressBar pbDrucken1;

	// TEXTFELDER -----------------
	@FXML
	private DatePicker dpEingabeDatum;
	@FXML
	private Spinner<LocalTime> spEingabeTreffpunkt, spEingabeBeginn;
	@FXML
	private TextField txtEingabeAnzahl, txtEingabeKurzbeschr;
	@FXML
	private TextArea txtEingabeZusatzinfos;

	// RADIOBUTTONS in Togglegroups ---
	@FXML
	private ToggleGroup ogrProbeAuffuehrung;
	@FXML
	private RadioButton radDetailsProbe, radDetailsAuff;

	// CHECKBOXEN einzeln ---------
	@FXML
	private CheckBox chkEingabeGema, chkDuplikatInclTitel;

	// COMBOBOXEN -----------------
	@FXML
	private ComboBox<String> cbxEingabeAktion, cbxEingabeOrt, cbxEingabeGruppe, cbxEingabeVerantwortlich;

	// Tab Details - Choraktionen-Positionen
	// Tableview
	@FXML
	private TableView<AktionenListePositionenModel> tblvwAktionPositionen;
	@FXML
	private TableColumn<AktionenListePositionenModel, Integer> tblvwPosCol0Pos;
	@FXML
	private TableColumn<AktionenListePositionenModel, String> tblvwPosCol1Art, tblvwPosCol2Stcktitel,
			tblvwPosCol3Notenausgabe, tblvwPosCol4Nr, tblvwPosCol4S, tblvwPosCol5Stckart,
			tblvwPosCol6Komp, tblvwPosCol7Besetzung, tblvwPosCol8Anmerkungen;

	// ----------------------------
	// (3.2) Tab Teilnehmer
	// ----------------------------
	@FXML
	private Label lblMitwirkendeDatensatzaktion, lblAktionTeilnehmerliste, lblPersonenGesamtliste;
	@FXML
	private TextField txtMitwEditName, txtMitwEditVorname, txtMitwEditStimme, txtMitwEditInstrument,
			txtMitwFilterName, txtMitwFilterStimme, txtMitwFilterInstrument;

	@FXML
	private Button btnMitwEditSpeichern, btnMitwEditLoeschen, btnMitwEditNeu,
			btnMitwEditLoeschen1, btnMitwEditSpeichern1,
			btnMitwFilterEin, btnMitwFilterAus;

	@FXML
	private CheckBox chkAutomatischPos;

	@FXML
	private ComboBox<CvwPersonenComboChorModel> cbxMitwFilterChor;
	@FXML
	private ComboBox<CvwPersonenComboGruppeModel> cbxMitwFilterGruppe;

	// Tableview
	@FXML
	private TableView<AktionenListePersonenModel> tblvwPersonenZugewiesen;
	@FXML
	private TableColumn<AktionenListePersonenModel, String> tblcolMitwZugewiesenName;
	@FXML
	private TableColumn<AktionenListePersonenModel, String> tblcolMitwZugewiesenVorname;
	@FXML
	private TableColumn<AktionenListePersonenModel, String> tblcolMitwZugewiesenInstrument;
	@FXML
	private TableColumn<AktionenListePersonenModel, String> tblcolMitwZugewiesenStimme;

	@FXML
	private TableView<CvwPersonenModel> tblvwPersonen;
	@FXML
	private TableColumn<CvwPersonenModel, String> tblcolPersonenName;
	@FXML
	private TableColumn<CvwPersonenModel, String> tblcolPersonenVorname;
	@FXML
	private TableColumn<CvwPersonenModel, String> tblcolPersonenChor;
	@FXML
	private TableColumn<CvwPersonenModel, String> tblcolPersonenGruppe;
	@FXML
	private TableColumn<CvwPersonenModel, String> tblcolPersonenStimme;
	@FXML
	private TableColumn<CvwPersonenModel, String> tblcolPersonenInstrument;

	// ----------------------------
	// (3.3) Tab Drucken
	// ----------------------------
	// BUTTONS---------------------
	@FXML
	private Button btnAktionDrucken1Liste, btnListeDruckenVerz;
	@FXML
	private Label lblDruckreportsAnzahl;

	// CHECKBOXEN einzeln ---------
	@FXML
	private CheckBox chkDruckListeZwischentexte, chkDruckListeTitelgrafiken, chkDruckListeZusatzangabe;
	// ----------------------------
	// (3.4) Stammdaten Personen
	// ----------------------------
	// BUTTONS---------------------
//	@FXML
//	private Button btnStammPersFilterAn, btnStammPersFilterAus, btnImportPersonen,
//			btnStammPersSpeichern, btnStammPersLoeschen, btnStammPersNeu;
//	@FXML
//	private TextField txtPersFilterName;
//			txtPersStammChor, txtPersStammGruppe, txtPersStammStimme, txtPersStammInstrument,
//			txtPersStammName, txtPersStammVorname, txtPersStammTelefon, txtPersStammMail, txtPersStammId;

//	@FXML
//	private ComboBox<CvwPersonenComboChorModel> cbxPersFilterChor;
//
//	@FXML
//	private ComboBox<CvwPersonenComboGruppeModel> cbxPersFilterGruppe;

	// Tableview Choraktionen aus gespeicherten Aktionen
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

	// Liste für Tableview
	// ObservableList<CvwPersonenModel> oblist_cvwpersonenImport =
	// FXCollections.observableArrayList();

	// ----------------------------------------------------------------------------------------
	// Variablen init
	// ----------------------------------------------------------------------------------------

	// neu (8.3.2026) Tblvw-Binding
	private TableData<AktionenListeModel> oblist_aktionenData = new TableData<>();
	private TableData<AktionenListePositionenModel> oblist_aktionenpositionenData = new TableData<>();
	private TableData<AktionenListePersonenModel> oblist_aktionenpersonenData = new TableData<>();
	private TableData<CvwPersonenModel> oblist_personenData = new TableData<>();
	private TableData<CvwPersonenModel> oblist_personencvwimportData = new TableData<>();

	// Liste für Filter Personen Combos
	private ObservableList<CvwPersonenComboChorModel> oblist_filterpersonenchor = FXCollections.observableArrayList();
	private ObservableList<CvwPersonenComboGruppeModel> oblist_filterpersonengruppe = FXCollections.observableArrayList();

// ###########################################################################################
// ######### init - Methoden
// ###########################################################################################
	private DatabaseControllerAktionen db;
	private boolean isUpdatingUI = false;

	public void setDbControllerAktionen(DatabaseControllerAktionen db)
	{
		this.db = db;
	}
	// ----

	@FXML
	public void initialize()
	{
		System.out.println("FXML initialize (nur 1x)");
		// Hier nichts DB-relevantes machen
		// NUR UI-Grundsetup
		// KEINE DB!

		oblist_aktionenData.bind(tblvwChoraktionen);
		oblist_aktionenpositionenData.bind(tblvwAktionPositionen);
		oblist_aktionenpersonenData.bind(tblvwPersonenZugewiesen);
		oblist_personenData.bind(tblvwPersonen);
		// oblist_personencvwimportData.bind(tblvwCvwPersonenImport);

		tblvwAktionPositionen.setFixedCellSize(26);
		tblvwChoraktionen.getSelectionModel()
				.selectedItemProperty()
				.addListener((obs, oldVal, newVal) -> {

					if (newVal == null || newVal == oldVal)
						return;

					handleAktionSelected(newVal);
				});
//		tblvwChoraktionen.getSelectionModel()
//				.selectedItemProperty()
//				.addListener((obs, oldVal, newVal) -> {
//					handleAktionSelected(newVal);
//				});
	}
	//// TODO

	@FXML
	public void onShow() throws Exception
	{// Hier deine Scene-spezifische Initialisierung
		System.out.println("Scene2 wird angezeigt");
		if (db == null)
		{
			throw new IllegalStateException("DB-Controller wurde nicht gesetzt!");
		}
		// initTabPanes();
		initData();
		// ===== Update-Check nur 1x pro Sitzung =====
		if (ValuesGlobals.updatecheck == true)
		{ // beim Starten werden Updates geprüft
			checkUpdatesAktionen();
			ValuesGlobals.updatecheck = false;
		}
		else
		{ // ab 2. Mal übernehmen von Globalwerten
			men40Datenbank.setVisible(false);
			men40Programm.setVisible(false);
			lblUpdateinfo.setVisible(false);
			if (ValuesGlobals.updateprogramm.equals("") && ValuesGlobals.updatedatenbank.equals(""))
			{
				// kein Update -Menü unsichtbar und lbl unsichtbar
				men40.setVisible(false);

			}
			else
			{
				// Update nötig, welches?

				if (!ValuesGlobals.updateprogramm.isEmpty())
				{ // kein Programmupdate
					men40Programm.setVisible(true);
				}
				if (!ValuesGlobals.updatedatenbank.isEmpty())
				{
					lblUpdateinfo.setText(ValuesGlobals.updateprogramm + "\n" + ValuesGlobals.updatedatenbank);
					lblUpdateinfo.setVisible(true);
					men40Datenbank.setVisible(true);
				}
				men40.setVisible(true);
			}
		}

	}

	private void initData() throws Exception
	{
		initFilterfelder();
		initTableviewsAktionen();
		initSonderfelderAktionen();
		initCombosAktionen();
		initTableviewsAktionenPositionen();
		initTblvwMitwirkende();
		initTblvwPersonen();
		anzeigenTabelleAktionen();
		anzeigenTblvwPersonen();
		Platform.runLater(dpEingabeDatum::requestFocus);
		// ------ Tableviews konfigurieren
		// alle Tableviews Spalten unverschiebbar machen
		tblvwAktionPositionen.getColumns().forEach(col -> col.setReorderable(false));
		tblvwChoraktionen.getColumns().forEach(col -> col.setReorderable(false));
		tblvwPersonenZugewiesen.getColumns().forEach(col -> col.setReorderable(false));
		// Tableviews für Doppelklick bereit machen
		tblvwPersonen.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2)
			{
				System.out.println("Double Click");
				try
				{
					handleTblvwPersonen_onmouseDoubleClicked();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				handleTblvwPersonen_onmouseClicked();

			}
		});
		tblvwPersonenZugewiesen.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2)
			{
				System.out.println("Double Click");
				try
				{
					handleTblvwPersonenZugewiesen_onmouseDoubleClicked();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else
			{
				handleTblvwPersonenZugewiesen_onmouseClicked();

			}
		});

		// initTableCvwPersonenImport();
		initCombosTabPersonen();
		// restoreCombosTabPersonen();
		restoreFilterAktionen();
		anzeigenCombosTabPersonen();

		// --- Filter ausführen (Tabelle anzeigen zum Start) ---------------
		Platform.runLater(() -> {
			try
			{
				btnFilterAn_OnClick(null);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}

	private void checkUpdatesAktionen()
	{
		System.out.println("(3) checkUpdatesStart()");
		// btnUpdaten.setVisible(false);
		men40.setVisible(false);
		try
		{
			String infotextdb = "";
			String infotextprog = "";
			String versionNeu;

			versionNeu = ToolsUpdateChecker.checkForUpdatesUniversell("prog");
			if (!versionNeu.isEmpty())
			{
				if (versionNeu == "xxx")
				{
					lblUpdateinfo.setText("KEINE INTERNETVERBINDUNG VORHANDEN\nVersionscheck nicht möglich");
					lblUpdateinfo.setVisible(true);
					return; // sofort abbrechen, wenn keine Internetverbindung da ist
				}
				else
				{// Programm ist neu
					infotextprog = "Neues Programm verfügbar:  " + versionNeu + "  ";
					men40Programm.setVisible(true);
					ValuesGlobals.updateprogramm = infotextprog;
					lblUpdateinfo.setText(infotextprog);
				}
			}
			else
			{// Programm ist nicht neu
				men40Programm.setVisible(false);
				lblUpdateinfo.setVisible(false);
				ValuesGlobals.updateprogramm = "";
			}

			versionNeu = ToolsUpdateChecker.checkForUpdatesUniversell("db");
			if (!versionNeu.isEmpty())
			{ // neue Datenbank
				infotextdb += "Neue Datenbank verfügbar:  " + versionNeu + "  ";
				men40Datenbank.setVisible(true);
				ValuesGlobals.updatedatenbank = infotextdb;

			}
			else
			{ // keine neue Datenbank
				men40Datenbank.setVisible(false);
				ValuesGlobals.updatedatenbank = "";
			}

			if (infotextdb.isEmpty() && infotextprog.isEmpty())
			{
				// btnUpdaten.setVisible(false);
				men40.setVisible(false);
				lblUpdateinfo.setVisible(false);
				ValuesGlobals.updatecheck = false;
				ValuesGlobals.updateprogramm = "";
				ValuesGlobals.updatedatenbank = "";
			}
			else
			{
				men40.setVisible(true);
				lblUpdateinfo.setVisible(true);
				lblUpdateinfo.setText(infotextprog + "\n" + infotextdb);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ValuesGlobals.updatecheck = false;
		}
	}
	// ############################### Updaten
	// #############################################

	// ############################### Updaten
	// #############################################
	public void fctUpdaten() throws IOException
	{
		DatabaseUpdaten.fctDatabaseupdate(btnAktionNeu.getScene().getWindow());

	}

	@FXML
	public void men40Programm_OnAction()
	{
		String url="https://www.pcnd.eu/jpcnd/index.php?aw=20-01-prg.php"; //Update Programm
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Desktop wird nicht unterstützt");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	@FXML
	public void men40Datenbank_OnAction() throws IOException
	{
		fctUpdaten();
	}


	// Tabwechsel
//	@FXML
//	public void initTabPanes()
//	{
//		tabPaneAktionen.getSelectionModel().selectedItemProperty().addListener(
//				(obs, oldTab, newTab) -> {
//					if ("Stammdaten Personen".equals(newTab.getText()))
//					{
//						leerenEingabenMitwirkende();
//
//						// anzeigenTableCvwPersonenImport(0);
//
//						try
//						{
//
//						}
//						catch (Exception e)
//						{
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					if ("Mitwirkende".equals(newTab.getText()))
//					{
//						// cbxMitwFilterChor.getEditor().setText(cbxPersFilterChor.getEditor().getText());
//						// cbxMitwFilterGruppe.getEditor().setText(cbxPersFilterGruppe.getEditor().getText());
//						// txtMitwFilterName.setText(txtPersFilterName.getText());
//						// leerenStammdatenPers();
//						anzeigenTblvwMitwirkende();
//						anzeigenTblvwPersonen();
//
//					}
//				});
//	}

	public void tabDrucken_OnSelectionChanged()
	{

		File pdfDir = PdfPathUtil.getPdfDirectory();
		if (!pdfDir.exists())
		{
			Msgbox.warn("Ordner mit den PDF-Druckdateien öffnen ...",
					"Der PDF-Ordner existiert nicht:\n" + pdfDir.getAbsolutePath());
			return;
		}

		// 👉 Anzahl der PDFs ermitteln
		int anzahl = countPdfFiles(pdfDir);

		// 👉 Label setzen

		lblDruckreportsAnzahl.setText(anzahl + " gespeicherte REPORTS im Ordner");

	}

	// ==================================================================================
	// Inits und Anzeigen für Tableviews
	// ==================================================================================

	// Aktionen -------
	private void initTableviewsAktionen() // (1) definieren
	{
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).
		// TABLEVIEWES mit Spalten -----

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		// ---------------- Datum ----------------
		tblvwAktionenCol0.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCadatum()));
		tblvwAktionenCol0.setCellFactory(col -> new TableCell<AktionenListeModel, LocalDate>()
		{
			@Override
			protected void updateItem(LocalDate item, boolean empty)
			{
				super.updateItem(item, empty);
				setText(empty || item == null ? "" : item.format(dateFormatter));
			}
		});

		// ---------------- Aktion ----------------
		tblvwAktionenCol1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaakttyp() != null ? cellData.getValue().getCaakttyp() : ""));

		// ---------------- Ort ----------------
		tblvwAktionenCol2.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaaktionsort() != null ? cellData.getValue().getCaaktionsort() : ""));

		// ---------------- Gruppe ----------------
		tblvwAktionenCol3.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCagruppe() != null ? cellData.getValue().getCagruppe() : ""));

		// ---------------- Beginn ----------------
		tblvwAktionenCol4.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCabeginn()));
		tblvwAktionenCol4.setCellFactory(col -> new TableCell<AktionenListeModel, LocalTime>()
		{
			@Override
			protected void updateItem(LocalTime item, boolean empty)
			{
				super.updateItem(item, empty);
				setText(empty || item == null ? "" : item.format(timeFormatter));
			}
		});

		// ---------------- Treffpunkt ----------------
		tblvwAktionenCol5.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCatreffpunkt()));
		tblvwAktionenCol5.setCellFactory(col -> new TableCell<AktionenListeModel, LocalTime>()
		{
			@Override
			protected void updateItem(LocalTime item, boolean empty)
			{
				super.updateItem(item, empty);
				setText(empty || item == null ? "" : item.format(timeFormatter));
			}
		});

		// ---------------- Anwesend ----------------
		tblvwAktionenCol6.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCaanwesend()));

		// ---------------- Art Probe/Auff ----------------
		// tblvwAktionenCol7.setCellValueFactory(cellData -> new
		// ReadOnlyObjectWrapper<>(cellData.getValue().getCaauftrittstermin()));

		// --------------- Beschreibung -------------
		tblvwAktionenCol7.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCabeschreibung() != null ? cellData.getValue().getCabeschreibung() : ""));

		// ---------------- Lade Daten aus DB ----------------
		try
		{
			List<AktionenListeModel> daten = db.getAktionenListeAll();
			oblist_aktionenData.master.setAll(daten);

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	public void anzeigenTabelleAktionen() throws Exception // (2) anzeigen nach Speichern
	{
		oblist_aktionenData.master.setAll(db.getAktionenListeAll());

	}

	// Aktionen Positionen ----------------

	private void initTableviewsAktionenPositionen() throws SQLException // (1) definieren
	{
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).
		// TABLEVIEWES mit Spalten -----
		// tblvwPosCol1Art, tblvwPosCol2Stcktitel,
		// tblvwPosCol3Notenausgabe, tblvwPosCol4Nr, tblvwPosCol5Stckart,
		// tblvwPosCol6Komp, tblvwPosCol7Besetzung, tblvwPosCol8Anmerkungen;
		// ---------------- PosNr ----------------
		tblvwPosCol0Pos.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCapoPos()));
		tblvwPosCol0Pos.setStyle("-fx-alignment: CENTER;");
		// ---------------- Bemerkungen zur Zeile ----------------
		tblvwPosCol1Art.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoSonstiges() != null ? cellData.getValue().getCapoSonstiges() : ""));
		// ---------------- Stcktitel ----------------
		tblvwPosCol2Stcktitel.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoStcktitel() != null ? cellData.getValue().getCapoStcktitel() : ""));

		// ---------------- Notenausgabe ----------------
		tblvwPosCol3Notenausgabe.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoEdition() != null ? cellData.getValue().getCapoEdition() : ""));

		// ---------------- Nummer Seite ----------------
		tblvwPosCol4Nr.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoNr() != null ? cellData.getValue().getCapoNr() : ""));
		tblvwPosCol4Nr.setStyle("-fx-alignment: CENTER;");

		tblvwPosCol4S.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoSeite() != null ? cellData.getValue().getCapoSeite() : ""));
		tblvwPosCol4S.setStyle("-fx-alignment: CENTER;");
		// ---------------- Stueckart ----------------
		tblvwPosCol5Stckart.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoArt() != null ? cellData.getValue().getCapoArt() : ""));

		// ---------------- Komponist ----------------
		tblvwPosCol6Komp.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoKomponist() != null ? cellData.getValue().getCapoKomponist() : ""));
		// ---------------- Besetzung ----------------
		tblvwPosCol7Besetzung.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoBesetzung() != null ? cellData.getValue().getCapoBesetzung() : ""));

		// ---------------- Anmerkungen ----------------
		tblvwPosCol8Anmerkungen.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoBem() != null ? cellData.getValue().getCapoBem() : ""));

		oblist_aktionenpositionenData.master.setAll(db.getAktionenPositionenListeAll(0));
	}

	public void anzeigenTabelleAktionenPositionen() throws Exception
	{
//		AktionenListeModel selected = tblvwChoraktionen.getSelectionModel().getSelectedItem();
//
//		if (selected == null)
//		{
//			oblist_aktionenpositionenData.master.clear();
//			return;
//		}
//
//		int aktuelleAktion = selected.getCaid();
//		tblvwAktionPositionen.getSortOrder().clear();
//		oblist_aktionenpositionenData.master.setAll(db.getAktionenPositionenListeAll(aktuelleAktion));

		AktionenListeModel selected = tblvwChoraktionen.getSelectionModel().getSelectedItem();

		if (selected == null)
		{
			return; // ❗ kein clear mehr
		}

		int aktuelleAktion = selected.getCaid();

		oblist_aktionenpositionenData.master
				.setAll(db.getAktionenPositionenListeAll(aktuelleAktion));
	}

//	private void reloadAktionenPositionen(int caid) {
//	    oblist_aktionenpositionenData.setAll(
//	        db.getAktionenPositionenListeAll(caid)
//	    );
//	}

	// Aktionen Mitwirkende ----------
	public void initTblvwMitwirkende()
	{
		tblcolMitwZugewiesenName.setCellValueFactory(new PropertyValueFactory<AktionenListePersonenModel, String>("capename"));
		tblcolMitwZugewiesenVorname.setCellValueFactory(new PropertyValueFactory<AktionenListePersonenModel, String>("capevname"));
		tblcolMitwZugewiesenInstrument.setCellValueFactory(new PropertyValueFactory<AktionenListePersonenModel, String>("capeinstrument"));
		tblcolMitwZugewiesenStimme.setCellValueFactory(new PropertyValueFactory<AktionenListePersonenModel, String>("capestimme"));
		// tblvwPersonenZugewiesen.setItems(oblist_mitwzugew); alt!!!
	}

	public void anzeigenTblvwMitwirkende()
	{
		int aktionid = 0;
		AktionenListeModel selected = tblvwChoraktionen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			return;
		}
		aktionid = selected.getCaid();

		// ---------------- Lade Daten aus DB ----------------
		try
		{
			List<AktionenListePersonenModel> daten = db.getAktionenPersonenListeAll(aktionid);
			// tblvwPersonenZugewiesen.getItems().setAll(daten);
			oblist_aktionenpersonenData.master.setAll(daten);
			// ersten Mitwirkenden markieren
			if (!daten.isEmpty())
			{
				// tblvwPersonenZugewiesen.getSelectionModel().clearSelection();
				// tblvwPersonenZugewiesen.getSelectionModel().select(0);
				// tblvwPersonenZugewiesen.scrollTo(0);

			}
			lblAktionTeilnehmerliste.setText("Teilnehmerliste [ " + String.valueOf(oblist_aktionenpersonenData.master.size()) + " ]");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void leerenEingabenMitwirkende()
	{
		txtMitwEditName.setText("");
		txtMitwEditVorname.setText("");
		txtMitwEditStimme.setText("");
		txtMitwEditInstrument.setText("");
		// oblist_aktionenpositionenData.master.clear();

	}

	// Personen --------------------------
	public void initTblvwPersonen()
	{
		tblcolPersonenName.setCellValueFactory(new PropertyValueFactory<CvwPersonenModel, String>("pename"));
		tblcolPersonenVorname.setCellValueFactory(new PropertyValueFactory<CvwPersonenModel, String>("pevname"));
		tblcolPersonenChor.setCellValueFactory(new PropertyValueFactory<CvwPersonenModel, String>("pechor"));
		tblcolPersonenGruppe.setCellValueFactory(new PropertyValueFactory<CvwPersonenModel, String>("pegruppe"));
		tblcolPersonenStimme.setCellValueFactory(new PropertyValueFactory<CvwPersonenModel, String>("pestimme"));
		tblcolPersonenInstrument.setCellValueFactory(new PropertyValueFactory<CvwPersonenModel, String>("peinstrument"));
		// tblvwPersonen.setItems(oblist_cvwpersonen); alt!!!
	}

	public void anzeigenTblvwPersonen()
	{
		// ---------------- Lade Daten aus DB ----------------
		try
		{
			String filpersonname = "";
			String filpersonvname = "";
			String filpersoninstrument = "";
			String filpersonchor = "";
			String filpersongruppe = "";
			String filpersonstimme = "";
			filpersonname = txtMitwFilterName.getText();
			filpersoninstrument = txtMitwFilterInstrument.getText();
			filpersonchor = cbxMitwFilterChor.getEditor().getText();
			filpersongruppe = cbxMitwFilterGruppe.getEditor().getText();
			filpersonstimme = txtMitwFilterStimme.getText();

			List<CvwPersonenModel> daten = db.getPersonenListeAll(
					filpersonname, filpersonvname, filpersoninstrument, filpersonchor, filpersongruppe, filpersonstimme);
			// tblvwPersonen.getItems().setAll(daten); alt!!!

			oblist_personenData.master.setAll(daten);
			lblPersonenGesamtliste.setText("Personen-Gesamtliste [ " + String.valueOf(oblist_personenData.master.size()) + " ]");

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
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
			filcvwpersonname = txtMitwFilterName.getText();
			// filcvwpersoninstrument = txtPersFilterInstrument.getText();
			filcvwpersonchor = cbxMitwFilterChor.getEditor().getText();
			filcvwpersongruppe = cbxMitwFilterGruppe.getEditor().getText();
			// filcvwpersonstimme = txtPersFilterStimme.getText();

			List<CvwPersonenModel> daten = db.getPersonenListeAll(
					filcvwpersonname, filcvwpersonvname, filcvwpersoninstrument, filcvwpersonchor, filcvwpersongruppe, filcvwpersonstimme);
			oblist_personenData.master.setAll(daten);
			oblist_personencvwimportData.master.setAll(daten);
			// ObservableList<CvwPersonenModel> items = tblvwCvwPersonenImport.getItems();
			// alt!!!!!
			// items.setAll(daten);

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	// =======================================
	// Handles für Tableviews
	// =======================================

	@FXML
	void handleTblvwPersonenZugewiesen_onmouseClicked()
	{
		AktionenListePersonenModel selected = tblvwPersonenZugewiesen.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		// -------------------- Textfelder --------------------
		txtMitwEditName.setText(selected.getCapename() != null ? selected.getCapename() : "");
		txtMitwEditVorname.setText(selected.getCapevname() != null ? selected.getCapevname() : "");
		txtMitwEditStimme.setText(selected.getCapestimme() != null ? selected.getCapestimme() : "");
		txtMitwEditInstrument.setText(selected.getCapeinstrument() != null ? selected.getCapeinstrument() : "");
		lblMitwirkendeDatensatzaktion.setText("Teilnehmende bearbeiten oder entfernen ...");
		tblvwPersonen.getSelectionModel().clearSelection();
	}

	@FXML
	void handleTblvwPersonenZugewiesen_onmouseDoubleClicked() throws SQLException
	{
		AktionenListePersonenModel selected = tblvwPersonenZugewiesen.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;

		handleBtnMitwEditLoeschen_onClick();

	}

	@FXML
	void handleTblvwPersonen_onmouseClicked()
	{
		CvwPersonenModel selected = tblvwPersonen.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		// -------------------- Textfelder --------------------
		txtMitwEditName.setText(selected.getPename() != null ? selected.getPename() : "");
		txtMitwEditVorname.setText(selected.getPevname() != null ? selected.getPevname() : "");
		txtMitwEditStimme.setText(selected.getPestimme() != null ? selected.getPestimme() : "");
		txtMitwEditInstrument.setText(selected.getPeinstrument() != null ? selected.getPeinstrument() : "");
		lblMitwirkendeDatensatzaktion.setText("Teilnehmende hinzufügen ...");
		tblvwPersonenZugewiesen.getSelectionModel().clearSelection();
	}

	@FXML
	void handleTblvwPersonen_onmouseDoubleClicked() throws SQLException
	{
		CvwPersonenModel selected = tblvwPersonen.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		txtMitwEditName.setText(selected.getPename() != null ? selected.getPename() : "");
		txtMitwEditVorname.setText(selected.getPevname() != null ? selected.getPevname() : "");
		txtMitwEditStimme.setText(selected.getPestimme() != null ? selected.getPestimme() : "");
		txtMitwEditInstrument.setText(selected.getPeinstrument() != null ? selected.getPeinstrument() : "");
		lblMitwirkendeDatensatzaktion.setText("Teilnehmende hinzufügen ...");
		tblvwPersonenZugewiesen.getSelectionModel().clearSelection();
		handleBtnMitwEditSpeichern1_onClick();

	}

// ============================
//	  Button Handling
// ============================

	@FXML
	private void btnZuNotenarchiv_OnClick(ActionEvent event) throws IOException
	{
		speichereAktionenfilter();

		System.out.println("Zurückbutton in Scene 2 betätigt");

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		SceneManager.showStart(stage); // 👈 echte Wiederherstellung
	}

	private void speichereAktionenfilter()
	{
		ConfigManager.saveFilterAktionDatumvon(dpFilterDatumVon.getEditor().getText());
		ConfigManager.saveFilterAktionDatumbis(dpFilterDatumBis.getEditor().getText());
		ConfigManager.saveFilterAktion(cbxFilterAktion.getEditor().getText());
		ConfigManager.saveFilterAktionOrt(cbxFilterOrt.getEditor().getText());
		ConfigManager.saveFilterAktionGruppe(cbxFilterGruppe.getEditor().getText());
		ConfigManager.saveFilterAktionBeschreibung(txtFilterKurzbeschr.getText());

		ConfigManager.saveFilterGruppe(cbxMitwFilterGruppe.getEditor().getText());
		ConfigManager.saveFilterChor(cbxMitwFilterChor.getEditor().getText());

	}

	@FXML
	private void btnStammdaten_OnAction(ActionEvent event) throws Exception
	{
		openStammdaten("personen");

	}

	@FXML
	private void openStammdaten(String woher)
	{
		oblist_personenData.master.clear();

		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmAktionenStammdaten.fxml"));
			Parent root = fxmlLoader.load();
			FrmAktionenStammdatenController controller = fxmlLoader.getController();
			controller.setDbControllerAktionen(this.db);
			// ✅ Owner aus dem Button holen
			Stage owner = (Stage) rootScene2.getScene().getWindow();
			// Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Choraktionen - Liste");
			dialogStage.getIcons().add(new Image("/icons/javafx/werkzeug.png"));
			dialogStage.initOwner(owner); // Owner setzen (sehr wichtig!)
			dialogStage.initModality(Modality.APPLICATION_MODAL); // blockiert Hauptfenster
			// ####################
			// 80% vom Bildschirm
//			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//			dialogStage.setWidth(screenBounds.getWidth() * 0.8);
//			dialogStage.setHeight(screenBounds.getHeight() * 0.8);
//			dialogStage.setX((screenBounds.getWidth() - dialogStage.getWidth()) / 2);
//			dialogStage.setY((screenBounds.getHeight() - dialogStage.getHeight()) / 2);
			dialogStage.setMinWidth(950);
			dialogStage.setMinHeight(600);
			dialogStage.setResizable(false);
			// --------------------
			controller.onShow(woher); // init in zugeh. EditController!
			dialogStage.setScene(new Scene(root));
			dialogStage.showAndWait(); // wartet bis Fenster geschlossen wird
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// refresh der notwendigen Combos und Tabellen
		// updateComboNotenmappeItems(); // Filtercombo aktualisieren
	}

	@FXML
	private void btnAktionNeu_OnClick(ActionEvent event) throws Exception
	{
		leerenAktionsfelder();
		leerenTabelleAktionenPositionen();
		tblvwChoraktionen.getSelectionModel().clearSelection();
		ohneAktionElementeDisablen();
		// tabPaneAktionen.getSelectionModel().select(tabDetails);
		Platform.runLater(radDetailsProbe::requestFocus);	

	}

	private void ohneAktionElementeDisablen()
	{
		tabDrucken.setDisable(true);
		tabMitwirkende.setDisable(true);
		btnPosEntf.setDisable(true);
		btnPosHoch.setDisable(true);
		btnPosListeBearbeiten.setDisable(true);
		btnPosRunter.setDisable(true);
		btnAktionDuplizieren.setDisable(true);
		btnAktionDrucken1.setDisable(true);
		chkDuplikatInclTitel.setDisable(true);
		// lblAktionAktuell.setDisable(true);
	}

	@FXML
	private void btnAktionSpeichern_OnClick(ActionEvent event) throws Exception
	{
		aktionSpeichern(event, false);
	}

	@FXML
	private void btnAktionDuplizieren_OnClick(ActionEvent event) throws Exception
	{
		AktionenListeModel selected = tblvwChoraktionen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.warn("Duplizieren einer Aktion ...", "Bitte vor dem Duplizieren eine Aktion auswählen!");
			return;
		}
		// aktion id Quelle zwischenspeichern
		int alteCaid = selected.getCaid();
		// neue Aktion speichern → neue ID!
		int neueCaid = aktionSpeichern(event, true);
		// aktionSpeichern(event, true);
		if (chkDuplikatInclTitel.isSelected() == true)
		{ // nun sollen alle Datensätze der Quelle inclusive 1:n verknüpfte Daten kopiert
			// werden
			// 1: tblChoraktionen.ca_id (primary key)
			// n: tblChoraktionenPositionen.capo_ca_id (foreign key)
			// Ablauf:
			// höchste id herauslesen (neue ca_id)
			// alle Positionen der Quelle kopieren (alte ca_id == alte capo_ca_id) mit neuer
			// höchster ca_id im Feld capo_ca_id versehen
			db.copyPositionen(alteCaid, neueCaid);
		}
		btnAktionNeu_OnClick(null);

	}

	private void handleAktionSelected(AktionenListeModel selected)
	{
		if (selected == null || isUpdatingUI)
			return;

		isUpdatingUI = true;
		try
		{
			// AktionenListeModel selected =
			// tblvwChoraktionen.getSelectionModel().getSelectedItem();

			// System.out.println("Probe oder Auftritt " + selected.getCaauftrittstermin());
			// -------------------- Checkboxen --------------------//
			radDetailsProbe.setSelected(selected.isProbe());
			radDetailsAuff.setSelected(selected.isAuftritt());

			// chkEingabeGema.setSelected(selected.isGema());

			chkEingabeGema.setSelected(selected.getCagema() != 0);

			// -------------------- Textfelder --------------------
			txtEingabeAnzahl.setText(Integer.toString(selected.getCaanwesend()));
			txtEingabeKurzbeschr.setText(selected.getCabeschreibung() != null ? selected.getCabeschreibung() : "");
			txtEingabeZusatzinfos.setText(selected.getCabemerkung() != null ? selected.getCabemerkung() : "");

			// -------------------- ComboBoxes --------------------
			cbxEingabeAktion.getSelectionModel().select(selected.getCaakttyp());
			cbxEingabeGruppe.getSelectionModel().select(selected.getCagruppe());
			cbxEingabeVerantwortlich.getSelectionModel().select(selected.getCaverantwortlich());
			cbxEingabeOrt.getSelectionModel().select(selected.getCaaktionsort());

			// -------------------- Spinner für Beginn --------------------
			LocalTime beginn = selected.getCabeginn();
			if (beginn != null)
			{
				spEingabeBeginn.getValueFactory().setValue(beginn);
			}
			else
			{
				spEingabeBeginn.getValueFactory().setValue(LocalTime.of(20, 0));
			}

			// -------------------- Spinner für Treffpunkt --------------------
			LocalTime treffpunkt = selected.getCatreffpunkt();
			if (treffpunkt != null)
			{
				spEingabeTreffpunkt.getValueFactory().setValue(treffpunkt);
			}
			else
			{
				spEingabeTreffpunkt.getValueFactory().setValue(LocalTime.of(20, 0));
			}

			LocalDate datum = selected.getCadatum();
			dpEingabeDatum.setValue(datum);

			try
			{
				anzeigenTabelleAktionenPositionen();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// -------------------- Tabs aktivieren --------------------

			tabDrucken.setDisable(false);
			// tabImportieren.setDisable(false);
			// tabStammdatenPersonen.setDisable(false);
			tabMitwirkende.setDisable(false);
			btnPosEntf.setDisable(false);
			btnPosHoch.setDisable(false);
			btnPosListeBearbeiten.setDisable(false);
			btnPosRunter.setDisable(false);
			btnAktionDuplizieren.setDisable(false);
			btnAktionDrucken1.setDisable(false);
			chkDuplikatInclTitel.setDisable(false);
			if ((chkAutomatischPos.isSelected()) || (tabPaneAktionen.getSelectionModel().isSelected(2)) )
			{
				tabPaneAktionen.getSelectionModel().select(tabDetails);
			}

			// lblAktionAktuell.setDisable(false);

			// leerenEingabenMitwirkende();
			// anzeigenTabelleAktionenPositionen();
			anzeigenTblvwMitwirkende();
			// anzeigenTblvwPersonen();

		}
		finally
		{
			isUpdatingUI = false;
		}

	}

	private int aktionSpeichern(ActionEvent event, boolean duplikat) throws Exception
	{

		AktionenListeModel tblvwaktionenselected = tblvwChoraktionen.getSelectionModel().getSelectedItem();
		// Datum und Zeiten direkt als LocalDate / LocalTime
		LocalDate datum = dpEingabeDatum.getValue();
		LocalTime beginn = spEingabeBeginn.getValue();
		LocalTime treffpunkt = spEingabeTreffpunkt.getValue();

		String caakttyp = cbxEingabeAktion.getValue(); // Bezeichnung für die Aktion
		String caort = cbxEingabeOrt.getValue();
		String caverantwortlich = cbxEingabeVerantwortlich.getValue();
		String cagruppe = cbxEingabeGruppe.getValue();
		String cakurzbeschr = txtEingabeKurzbeschr.getText();
		String cabemerkung = txtEingabeZusatzinfos.getText();
		String caveranstalter = ""; // Falls später ergänzt
		int caprobeauftritt = 0; // Probe=0, Auftritt=1

		int caanwesend = 0;
		if (txtEingabeAnzahl.getText() != null && !txtEingabeAnzahl.getText().isBlank())
		{
			caanwesend = Integer.parseInt(txtEingabeAnzahl.getText());
		}

		boolean cagema = chkEingabeGema.isSelected();

		if (radDetailsProbe.isSelected() == true)
		{
			caprobeauftritt = 0; // Auftritt
		}
		if (radDetailsAuff.isSelected() == true)
		{
			caprobeauftritt = 1; // Probe
		}

		// Validierungen
		if (cagruppe == null || cagruppe.isBlank())
		{
			Msgbox.warn("Speichern abgebrochen", "Bitte wählen Sie eine Gruppe aus oder geben Sie eine neue Gruppe ein.");
			Platform.runLater(cbxEingabeGruppe::requestFocus);
			return 0;
		}
		if (caverantwortlich == null || caverantwortlich.isBlank())
		{
			Msgbox.warn("Speichern abgebrochen", "Bitte wählen Sie eine(n) Verantwortliche(n) oder geben Sie hier neu ein.");
			Platform.runLater(cbxEingabeVerantwortlich::requestFocus);
			return 0;
		}
		if (caort == null || caort.isBlank())
		{
			Msgbox.warn("Speichern abgebrochen", "Bitte wählen Sie einen Ort aus oder geben Sie einen neuen ein.");
			Platform.runLater(cbxEingabeOrt::requestFocus);
			return 0;
		}
		if (caakttyp == null || caakttyp.isBlank())
		{
			Msgbox.warn("Speichern abgebrochen", "Bitte wählen Sie eine Bezeichnung für die Planung ein oder geben Sie eine neue ein.");
			Platform.runLater(cbxEingabeAktion::requestFocus);
			return 0;
		}

		boolean neu;

		if (tblvwaktionenselected == null)
		{
			neu = true;
		}
		else
		{
			neu = false;
		}

		int caid;
		if (neu)
		{
			caid = 0;
		}
		else
		{
			caid = tblvwaktionenselected.getCaid();
		}
		if (duplikat == true)
		{
			neu = true;
			caid = 0;
			// Titel anpassen
			if (caakttyp != null && !caakttyp.endsWith("(Kopie)"))
			{
				caakttyp = caakttyp + " (Kopie)";
			}

			// Datum auf heute setzen
			// datum = LocalDate.now().plusDays(0);
		}

		// Save direkt mit LocalDate und LocalTime
		int neueCaid = db.saveAktion(neu, caakttyp, datum, cakurzbeschr, treffpunkt, beginn,
				caanwesend, cabemerkung, caverantwortlich, cagruppe,
				caort, caveranstalter, caprobeauftritt, cagema, caid);

		filternAktionen();

		refreshComboBoxes();
		if (neu == true)
		{
			// btnAktionNeu_OnClick(event);
		}
		// Cursor wieder setzen
		TableUtils.selectRowById(tblvwChoraktionen, neueCaid, AktionenListeModel::getCaid);
		
		if(neu==true) {
			leerenTabelleAktionenPositionen();
			leerenAktionsfelder();
		}
		else {
			
		}
		//tblvwChoraktionen.getSelectionModel().clearSelection();
		//ohneAktionElementeDisablen();
		// tabPaneAktionen.getSelectionModel().select(tabDetails);
		Platform.runLater(radDetailsProbe::requestFocus);		
		return neueCaid;
	}

	@FXML
	void btnAktionLoeschen_OnClick(ActionEvent event) throws Exception
	{
		String selectedtext = "";
		int markierterIndex = tblvwChoraktionen.getSelectionModel().getSelectedIndex();
		// int indexVorher = 0;
		int loeschenId = 0;
		AktionenListeModel selected = tblvwChoraktionen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show("Aktion mit allen Listenpositionen löschen ...", "Bitte eine Aktion in der Liste auswählen!");
			return;
		}
		selectedtext = selected.getCaakttyp() + " (" + selected.getCadatum() + ")";
		loeschenId = selected.getCaid();

		if (Msgbox.yesnowarn("Aktion mit allen Listenpositionen löschen ...", "Soll die Aktion " + selectedtext + "\nendgültig entfernt werden?\n "
				+ "\nACHTUNG: Es werden alle Listenpositionen entfernt") == false)
		{
			return;
		}
		db.deleteAktion(loeschenId);
		anzeigenTabelleAktionen();
		refreshComboBoxes();
		btnAktionNeu_OnClick(event);

		// Selektion setzen
		ObservableList<AktionenListeModel> items = tblvwChoraktionen.getItems();
		if (!items.isEmpty())
		{
			if (markierterIndex >= items.size())
			{
				markierterIndex = items.size() - 1;
			}
			tblvwChoraktionen.getSelectionModel().select(markierterIndex);
			// tblvwChoraktionen.scrollTo(markierterIndex);
		}
		btnAktionNeu_OnClick(null);		

	}

	@FXML
	private void btnFilterAn_OnClick(ActionEvent event) throws Exception
	{
		// leerenTabelleAktionen();
		Platform.runLater(btnFilterAus::requestFocus);
		tblvwChoraktionen.getSelectionModel().clearSelection();
		leerenTabelleAktionenPositionen();
		leerenTabelleAktionenMitwirkende();
		leerenEingabenMitwirkende();
		filternAktionen();

	}

	@FXML
	private void btnFilterAus_OnClick(ActionEvent event) throws Exception
	{
		Platform.runLater(btnFilterAn::requestFocus);
		initFilterfelder();
		filternAktionen();
		ohneAktionElementeDisablen();

	}

	@FXML
	private void btnAktionDrucken1_OnClick(ActionEvent event) throws Exception
	{
		//int anzahlPdfs = 0;
		fctAblaufplanDrucken(2); // Nur Musikstücke mit Zusatzangaben
		
		// 👉 Anzahl der PDFs ermitteln

	}

	@FXML
	private void btnAktionDrucken1Liste_OnCLick(ActionEvent event) throws Exception
	{// int druckwahl:
		// 0 = kompakte Liste nur Notenausgabenzeilen
		// 1 = Zwischentexten
		// 2= Zusatzangaben
		// 4= Grafiken
		int listenart = 0;
		if (chkDruckListeZwischentexte.isSelected())
		{
			listenart |= 1;
		}

		if (chkDruckListeZusatzangabe.isSelected())
		{
			listenart |= 2;
		}
		if (chkDruckListeTitelgrafiken.isSelected())
		{
			listenart |= 4;
		}

		fctAblaufplanDrucken(listenart);
	}

	private void fctAblaufplanDrucken(int druckwahl) throws Exception
	{ // int druckwahl:
		// 0 = kompakte Liste
		// 1 = zusätzlich Zwischentexte
		// 2= nur Zusatzangaben
		// 3 = Zusatzangaben und Zwischentexte
		// 4= zusätzlich Grafiken
		System.out.println(druckwahl);
		AktionenListeModel aktion = tblvwChoraktionen.getSelectionModel().getSelectedItem();
		if (aktion == null)
		{
			Msgbox.show("Ablaufplan kompakt drucken ...", "Bitte wählen Sie eine Aktion aus, die gedruckt werden soll.");
			return;
		}
		// 1) Optionen erstellen
		PdfExportOptions<AktionenListePositionenModel> options0 = PdfExportOptions.defaultsAblaufplan();

		if (druckwahl == 0 || druckwahl == 1 || druckwahl == 2 || druckwahl == 3)
		{ // kompakt ohne Grafiken
			// 2) Spalten ausblenden
			// options0.excludedColumns.add(tblvwPosCol0Pos);
			// options0.excludedColumns.add(tblvwPosCol1Art);
			// options0.excludedColumns.add(tblvwPosCol2Stcktitel);
			// options0.excludedColumns.add(tblvwPosCol3Notenausgabe);
			// options0.excludedColumns.add(tblvwPosCol4Nr);
			// options0.excludedColumns.add(tblvwPosCol4S);
			options0.excludedColumns.add(tblvwPosCol5Stckart);
			// options0.excludedColumns.add(tblvwPosCol6Komp);
			options0.excludedColumns.add(tblvwPosCol7Besetzung);
			options0.excludedColumns.add(tblvwPosCol8Anmerkungen);
			// Ausrichtungen ---
			options0.columnAlignment.put(tblvwPosCol0Pos, Element.ALIGN_CENTER);
			options0.columnAlignment.put(tblvwPosCol4S, Element.ALIGN_CENTER);
			options0.columnAlignment.put(tblvwPosCol4Nr, Element.ALIGN_CENTER);

			options0.mitTabellenkopf = true;
			options0.mitDetailtabelle = true;
			if (druckwahl == 0 || druckwahl == 2) // kompakt bzw. mit Zusatzangaben, aber ohne Zwischentexte
			{
				if (druckwahl == 0)
				{

					options0.title = "Ablaufplan ohne Zwischentexte";
				}
				else
				{
					options0.title = "Ablaufplan mit Zusatzangaben";
					options0.detailRemarkExtractor = r -> r.getCapoBem();
				}
				options0.excludedColumns.add(tblvwPosCol0Pos); // wenn ohne Zwischentext, dann auch ohne Nummerierung
				options0.rowFilter = r -> {
					// nur Zeilen mit Zwischentext = 0 , d.h. Zwischentexte weglassen
					return r.getCapoZwischentext() != 1;
				};
			}
			else if (druckwahl == 1)
			{
				options0.title = "Ablaufplan mit Zwischentexten";
			}

			else if (druckwahl == 3)
			{
				options0.title = "Ablaufplan mit Zwischentexten und Zusatzangaben";
				options0.detailRemarkExtractor = r -> r.getCapoBem();
			}
		}
		// ------ Ende ohne Grafiken ---------------------

		else if (druckwahl == 4 || druckwahl == 5 || druckwahl == 6 || druckwahl == 7)
		{ // ab hier mit Grafiken und rechts daneben zusammengesetzte Daten
			// 2) Spalten ausblenden
			// options0.excludedColumns.add(tblvwPosCol1Art);
			// options0.excludedColumns.add(tblvwPosCol2Stcktitel);
			options0.excludedColumns.add(tblvwPosCol3Notenausgabe);
			options0.excludedColumns.add(tblvwPosCol4Nr);
			options0.excludedColumns.add(tblvwPosCol4S);
			options0.excludedColumns.add(tblvwPosCol7Besetzung);
			options0.excludedColumns.add(tblvwPosCol8Anmerkungen);
			options0.mitTabellenkopf = false;
			options0.mitDetailtabelle = true;
			// ====================
			// Bild + Text nebeneinander
			// ====================
			// ====================
			// Bild + Text nebeneinander – Design-Version
			// ====================
			if (druckwahl == 4 || druckwahl == 6) // zwischentexte weglassen, trotzdem mit PosNummer (wegen Grafik)
			{
				options0.excludedColumns.add(tblvwPosCol0Pos);
				options0.rowFilter = r -> {
					return r.getCapoZwischentext() != 1;
				};
			}
			// Bild rechts platzieren
			options0.detailImageExtractor = r -> {
				String dateiname = r.getCapoTitelbild(); // z.B. "bild1.png"
				if (dateiname == null || dateiname.isBlank())
					return null;
				return application.ValuesGlobals.progPfadGrafik + "/" + dateiname;
			};
			// jetzt kommt der Text neben das Bild
			options0.detailImageTextExtractor = r -> {
				StringBuilder inhalt = new StringBuilder();

				// ===== Edition GROSSBUCHSTABEN =====
				if (r.getCapoEdition() != null && !r.getCapoEdition().isEmpty())
				{
					inhalt.append(r.getCapoEdition().toUpperCase());
				}

				// ===== Nr. + Seite =====
				boolean hasNr = r.getCapoNr() != null && !r.getCapoNr().isEmpty();
				boolean hasSeite = r.getCapoSeite() != null && !r.getCapoSeite().isEmpty();
				if (hasNr || hasSeite)
				{
					inhalt.append("\n"); // neue Zeile unter Edition
					if (hasNr)
					{
						inhalt.append("  Nr. ").append(r.getCapoNr());
					}
					if (hasSeite)
					{
						if (hasNr)
						{
							inhalt.append("     |   "); // Trennung zwischen Nr. und Seite
						}
						inhalt.append("  S. ").append(r.getCapoSeite());
					}
				}

				// ===== Bemerkungen zur Position =====

				if (r.getCapoBem() != null && !r.getCapoBem().isEmpty())
				{
					inhalt.append(" \n --- ").append(r.getCapoBem());
				}

				return inhalt.toString();
			};

		}
		// ***********************************************************************************************************
		// 3) Detailzeilen-Lambdas setzen -- wird nicht gebraucht RESERVE!!!
		// options0.detailLine1Extractor = r -> " " + r.getCapoTonart();
		// options0.detailLine2Extractor = r -> "Komp: " + r.getCapoKomponist() + " N:"
		// + r.getCapoArt();
		// options0.detailLine3Extractor = r -> "Komp: " + r.getCapoKomponist() + " N:"
		// + r.getCapoArt();
		// ***********************************************************************************************************
		// 4) PDF exportieren
		PdfMasterDetailExporterUtil.exportMasterDetail(
				aktion,
				tblvwAktionPositionen,
				options0,
				pbDrucken1,
				lblDrucken1);

	}

	@FXML
	public void btnListeDruckenVerz_OnClick()
	{
		System.out.println("PDF-Verzeichnis öffnen");
		try
		{
			File pdfDir = PdfPathUtil.getPdfDirectory();
			if (!pdfDir.exists())
			{
				Msgbox.warn("PDF-Ordner öffnen",
						"Der PDF-Ordner existiert nicht:\n" + pdfDir.getAbsolutePath());
				return;
			}

			boolean opened = false;
			String os = System.getProperty("os.name").toLowerCase();

			// Linux: direkt xdg-open verwenden
			if (os.contains("linux"))
			{
				try
				{
					new ProcessBuilder("xdg-open", pdfDir.getAbsolutePath()).start();
					opened = true;
				}
				catch (Exception e)
				{
					System.out.println("xdg-open fehlgeschlagen: " + e.getMessage());
				}
			}
			else if (os.contains("mac"))
			{
				try
				{
					new ProcessBuilder("open", pdfDir.getAbsolutePath()).start();
					opened = true;
				}
				catch (Exception e)
				{
					System.out.println("open fehlgeschlagen: " + e.getMessage());
				}
			}
			// Windows
			else
			{
				try
				{
					java.awt.Desktop.getDesktop().open(pdfDir);
					opened = true;
				}
				catch (Exception e)
				{
					System.out.println("Desktop.open fehlgeschlagen: " + e.getMessage());
				}
			}

			// Fallback: Hyperlink
			if (!opened)
			{
				Hyperlink link = new Hyperlink(pdfDir.getAbsolutePath());
				link.setOnAction(event -> {
					try
					{
						if (os.contains("linux"))
						{
							new ProcessBuilder("xdg-open", pdfDir.getAbsolutePath()).start();
						}
						else if (os.contains("mac"))
						{
							new ProcessBuilder("open", pdfDir.getAbsolutePath()).start();
						}
						else
						{
							java.awt.Desktop.getDesktop().open(pdfDir);
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						Msgbox.warn("Öffnen fehlgeschlagen",
								"Kann den Ordner nicht öffnen:\n" + ex.getMessage());
					}
				});

				VBox content = new VBox(link);
				content.setSpacing(10);
				Msgbox.warn("PDF-Ordner öffnen...", "Der PDF-Ordner konnte nicht automatisch geöffnet werden.");
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("PDF-Ordner öffnen");
				alert.setHeaderText("Der PDF-Ordner konnte nicht automatisch geöffnet werden.");
				alert.getDialogPane().setContent(content);
				alert.showAndWait();
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Msgbox.warn("PDF-Ordner öffnen",
					"Der PDF-Ordner konnte nicht geöffnet werden:\n" + ex.getMessage());
		}
	}

	private int countPdfFiles(File dir) // Reportdateien zählen
	{
		File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
		return files != null ? files.length : 0;
	}

	@FXML
	private void btnInfo_OnClick(ActionEvent event) throws IOException
	{
		Msgbox.show("Aktionen planen ...", "In diesem Bereich werden Chor-Aktionen geplant und dokumentiert.");
	}

	@FXML
	private void handleBtnMitwEditSpeichern_onClick() throws SQLException
	{
		int personId = 0;
		int aktionId = 0;
		String capename = txtMitwEditName.getText();
		String capevorname = txtMitwEditVorname.getText();
		String capestimme = txtMitwEditStimme.getText();
		String capeinstrument = txtMitwEditInstrument.getText();
		AktionenListeModel selected0 = tblvwChoraktionen.getSelectionModel().getSelectedItem();
		if (selected0 == null)
		{
			Msgbox.show("Mitwirkende(n) der Aktion hinzufügen ...", "Es ist keine Aktion markiert - bitte klicken Sie eine Zeile in der Aktionen-Liste an");
			return;
		}
		aktionId = selected0.getCaid();

		if (capename == null || capename.isBlank() || capevorname == null || capevorname.isBlank())
		{
			Msgbox.show("Mitwirkende(n) der Aktion hinzufügen ...", "Bitte füllen Sie mindestens die Felder Name und Vorname aus!");
			return;
		}

		CvwPersonenModel selected1 = tblvwPersonen.getSelectionModel().getSelectedItem();
		if (selected1 != null)
		{
			// Mitwirkenden aus Personenliste NEU zur Aktion
			personId = 0;
		}
		AktionenListePersonenModel selected2 = tblvwPersonenZugewiesen.getSelectionModel().getSelectedItem();
		if (selected2 != null)
		{
			personId = selected2.getCapeid();
			// Mitwirkenden aus schon zugewiesenen Personen bearbeiten
		}

		db.saveAktionenPerson(aktionId, personId, capename, capevorname, capestimme, capeinstrument);
		anzeigenTblvwMitwirkende();
		handleBtnMitwEditNeu_onClick();

	}

	@FXML
	private void handleBtnMitwEditNeu_onClick()
	{
		// oblist_cvwpersonen = null;
		// oblist_mitwzugew = null;
		tblvwPersonenZugewiesen.getSelectionModel().clearSelection();
		tblvwPersonen.getSelectionModel().clearSelection();
		leerenEingabenMitwirkende();
		// leerenTabelleAktionenMitwirkende();
		lblMitwirkendeDatensatzaktion.setText("Mitwirkende(n) neu erfassen oder auswählen ...");

	}

	@FXML
	private void handleBtnMitwFilterEin_onCick()
	{

		anzeigenTblvwPersonen();

	}

	@FXML
	private void handleBtnMitwFilterAus_onCick()
	{
		cbxMitwFilterChor.getEditor().setText("");
		cbxMitwFilterGruppe.getEditor().setText("");
		txtMitwFilterInstrument.setText("");
		txtMitwFilterName.setText("");
		txtMitwFilterStimme.setText("");
		anzeigenTblvwPersonen();
	}

	@FXML
	private void handleBtnMitwEditLoeschen_onClick() throws SQLException
	{
		int capeid = 0;

		AktionenListePersonenModel selected = tblvwPersonenZugewiesen.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			capeid = selected.getCapeid();
			// Mitwirkenden aus schon zugewiesenen Personen bearbeiten
		}
		else
		{
			Msgbox.show("Teilnehmer aus der Aktivität entfernen ...", "Bitte markieren Sie eine Zeile in der Tabelle der Teilnehmer");
			return;
		}
		db.deleteAktionenPerson(capeid);
		anzeigenTblvwMitwirkende();
		leerenEingabenMitwirkende();
		handleBtnMitwEditNeu_onClick();
	}

	@FXML
	private void handleBtnMitwEditSpeichern1_onClick() throws SQLException
	{

		handleBtnMitwEditSpeichern_onClick();

	}

	@FXML
	private void handleBtnMitwEditLoeschen1_onClick() throws SQLException
	{
		handleBtnMitwEditLoeschen_onClick();
	}

// =============================================================================================================================
// ##### IMPORT Aktionen aus PCND CA -- 3xCSV-Dateien: ChorAktionen.csv,
// ChorAktionenPositionen.csv, ChorAktionPersonen.csv
// =============================================================================================================================
	@FXML
	public void btnImportCsv_OnClick() throws SQLException
	{
		leerenTabelleAktionenPositionen();

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

		File fileAktionen = chooserAktionen.showOpenDialog(tblvwChoraktionen.getScene().getWindow());
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
		Scene scene = tblvwChoraktionen.getScene();
		final List<AktionenListeModel> fAlleAktionen = alleAktionen;
		final List<AktionenListeModel> fNeuAktionen = neuAktionen;
		final List<AktionenListeModel> fDoppeltAktionen = doppeltAktionen;
		final List<AktionenListePositionenModel> fNeuPositionen = neuPositionen;
		final List<AktionenListePersonenModel> fNeuPersonen = neuPersonen;

		scene.setCursor(Cursor.WAIT);

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

			try
			{
				refreshComboBoxes();
				try
				{
					anzeigenTabelleAktionen();
				}
				catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
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

// ===========================================
//		filtern
// ===========================================	

	public void filternAktionen() throws Exception
	{

		// Filterwerte direkt aus DatePicker
		LocalDate filterDatumVon = dpFilterDatumVon.getValue();

		// String filterDatumVon = dpFilterDatumVon.getEditor().getText();
		LocalDate filterDatumBis = dpFilterDatumBis.getValue();
		// String filterDatumBis = dpFilterDatumBis.getEditor().getText();

		String filterAktion = cbxFilterAktion.getEditor().getText();
		String filterAktionOrt = cbxFilterOrt.getEditor().getText();
		String filterAktionGruppe = cbxFilterGruppe.getEditor().getText();
		String filterAktionenBeschreibung = "";
		filterAktionenBeschreibung = txtFilterKurzbeschr.getText();

		// Art-Filter
		String filterArt = "alles";
		if (radFilterProben.isSelected())
			filterArt = "prob";
		else if (radFilterAuff.isSelected())
			filterArt = "auff";
		else if (radFilterAlles.isSelected())
			filterArt = "alles";
		else
		{
			filterArt = "alles";
		}
		List<AktionenListeModel> listaktionen = db.getAktionenListeFilter(
				filterDatumVon, filterDatumBis, filterAktion, filterAktionOrt,
				filterAktionGruppe, filterArt, filterAktionenBeschreibung);
		// ObservableList auffüllen
		oblist_aktionenData.master.setAll(listaktionen);

		lblFilterAnzahl.setText(" " + String.valueOf(oblist_aktionenData.master.size()) + " gefiltert");
		// leerenTabelleAktionenPositionen();
	}

	public void leerenAktionsfelder()
	{

		initSonderfelderAktionen();
		radDetailsProbe.setSelected(true);
		chkEingabeGema.setSelected(false);

		cbxEingabeAktion.setValue(null); // Wert zurücksetzen
		cbxEingabeAktion.getEditor().clear(); // Textfeld leeren
		cbxEingabeGruppe.setValue(null); // Wert zurücksetzen
		cbxEingabeGruppe.getEditor().clear(); // Textfeld leeren
		cbxEingabeOrt.setValue(null); // Wert zurücksetzen
		cbxEingabeOrt.getEditor().clear(); // Textfeld leeren
		cbxEingabeVerantwortlich.setValue(null); // Wert zurücksetzen
		cbxEingabeVerantwortlich.getEditor().clear(); // Textfeld leeren

		txtEingabeAnzahl.setText(null);
		txtEingabeKurzbeschr.setText(null);
		txtEingabeZusatzinfos.setText(null);

	}

	public void leerenTabelleAktionen()
	{
		oblist_aktionenData.master.clear();
		leerenAktionsfelder();

	}

	public void leerenTabelleAktionenPositionen()
	{
		oblist_aktionenpositionenData.master.clear();
		leerenAktionsfelder();

	}

	public void leerenTabelleAktionenMitwirkende()
	{
		oblist_aktionenpersonenData.master.clear();

	}

	// ------- schieben der Positionsnummern ---------
	@FXML
	private void btnPosRunter_OnClick(ActionEvent event) throws Exception
	{
		schiebenPositon(1);

	}

	@FXML
	private void btnPosHoch_OnClick(ActionEvent event) throws Exception
	{
		schiebenPositon(2);

	}

	public void schiebenPositon(int richtung) throws Exception
	{
		System.out.println("Richtung" + richtung);
		int posnrAktuell = 0;
		int idAktuell = 0;
		// int posnrDarueber = 0;
		int idDarueber = 0;
		// int posnrDarunter = 0;
		int idDarunter = 0;
		int anzahlPos = 0;

		int markierterIndex = tblvwAktionPositionen.getSelectionModel().getSelectedIndex(); // index der matkiertenZeile
		anzahlPos = tblvwAktionPositionen.getItems().size();
		AktionenListePositionenModel selected = tblvwAktionPositionen.getSelectionModel().getSelectedItem();

		if (selected == null)
		{
			Msgbox.show("Verschieben von Zeilen in der Positionsliste ...", "Bitte markieren Sie die Zeile, die verschoben werden soll!");
			return;
		}
		idAktuell = selected.getCapoId();

		if (markierterIndex == 0 && richtung == 2)
		{// oben und hoch gedrückt?
			return;
		}
		if ((markierterIndex == (anzahlPos - 1)) && richtung == 1)
		{// unten und runter gedrückt?
			return;
		}

		if (richtung == 2)
		{ // rauf
			AktionenListePositionenModel darueber = tblvwAktionPositionen.getItems().get(markierterIndex - 1);
			posnrAktuell = selected.getCapoPos();
			// posnrDarueber = darueber.getCapoPos();
			idDarueber = darueber.getCapoId();
			db.setVerschiebenPosNr(idAktuell, posnrAktuell + -1);
			db.setVerschiebenPosNr(idDarueber, posnrAktuell);

		}
		else if (richtung == 1)
		{// runter
			AktionenListePositionenModel darunter = tblvwAktionPositionen.getItems().get(markierterIndex + 1);
			// noch nicht ganz unten
			posnrAktuell = selected.getCapoPos();
			// posnrDarunter = darunter.getCapoPos();
			idDarunter = darunter.getCapoId();
			db.setVerschiebenPosNr(idAktuell, posnrAktuell + 1);
			db.setVerschiebenPosNr(idDarunter, posnrAktuell);

		}

		anzeigenTabelleAktionenPositionen();
		// selektierte Zeile wieder markieren
		int neuerIndex = markierterIndex;
		if (richtung == 1 && markierterIndex < anzahlPos - 1)
		{
			neuerIndex = markierterIndex + 1;
		}
		else if (richtung == 2 && markierterIndex > 0)
		{
			neuerIndex = markierterIndex - 1;
		}
		tblvwAktionPositionen.getSelectionModel().select(neuerIndex);
		tblvwAktionPositionen.scrollTo(neuerIndex);

	}

	public void btnPosEntf_OnClick() throws Exception
	{
		AktionenListePositionenModel selectedPos = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		if (selectedPos == null)
		{
			Msgbox.show("Zeile aus der Positionsliste löschen...", "Bitte eine Position / Programmpunkt zum Löschen markieren!");
			return;
		}

		// int posnrAktuell = selectedPos.getCapoPos();
		// int capo_caid = selectedPos.getCapoCaId();
		int markierterIndex = tblvwAktionPositionen.getSelectionModel().getSelectedIndex();

		db.deleteAndRenumber(
				selectedPos.getCapoId(),
				selectedPos.getCapoPos(),
				selectedPos.getCapoCaId());

		// 3️⃣ Tabelle neu laden
		anzeigenTabelleAktionenPositionen();

		// 4️⃣ Selektion setzen
		ObservableList<AktionenListePositionenModel> items = tblvwAktionPositionen.getItems();
		if (!items.isEmpty())
		{
			if (markierterIndex >= items.size())
			{
				markierterIndex = items.size() - 1;
			}
			tblvwAktionPositionen.getSelectionModel().select(markierterIndex);
			tblvwAktionPositionen.scrollTo(markierterIndex);
		}

	}

	@FXML
	private void btnPosListeBearbeiten_OnClick(ActionEvent event) throws Exception
	{
		AktionenListeModel selected = tblvwChoraktionen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			Msgbox.show("Positionsliste der Aktivität bearbeiten ...", "Bitte wählen Sie eine Aktivität aus, die bearbeiotet werden soll.");
			return;
		}
		int caid = selected.getCaid();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmAktionenPositionen.fxml"));
		Parent root = fxmlLoader.load();
		FrmAktionenPositionenController controller = fxmlLoader.getController();
		controller.setDbControllerAktionen(this.db);
		// ✅ Owner aus dem Button holen
		Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Stage dialogStage = new Stage();
		dialogStage.setTitle("Chorktivitäten - Liste");
		dialogStage.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
		dialogStage.initOwner(owner); // Owner setzen (sehr wichtig!)
		dialogStage.initModality(Modality.APPLICATION_MODAL); // blockiert Hauptfenster
		// ####################
		// 80% vom Bildschirm
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		dialogStage.setWidth(screenBounds.getWidth() * 0.8);
		dialogStage.setHeight(screenBounds.getHeight() * 0.8);
		dialogStage.setX((screenBounds.getWidth() - dialogStage.getWidth()) / 2);
		dialogStage.setY((screenBounds.getHeight() - dialogStage.getHeight()) / 2);
		dialogStage.setMinWidth(1240);
		dialogStage.setMinHeight(768);
		// --------------------
		controller.onShow(caid); // init in zugeh. EditController!
		dialogStage.setScene(new Scene(root));
		dialogStage.showAndWait(); // wartet bis Fenster geschlossen wird
		// refresh der notwendigen Combos und Tabellen
		// 🔄 TableView sauber neu laden
//		tblvwAktionPositionen.setItems(
//		    FXCollections.observableArrayList(
//		        db.getAktionenPositionenListeAll(caid)
//		    )
//		);

		anzeigenTabelleAktionenPositionen();

	}

// =======================================================================
//	  allgemeine Funktionen für Comboboxen und andere Steuerelemente
// =======================================================================

	// ### allgemeine Combobox ###
	// für serachable Combobox:
	// @FXML
	// private SearchableComboBox<AktionenListeModel> cbxEingabeAktion;

	private void initCombosAktionen() throws SQLException
	{
		List<AktionenListeModel> baseList = db.getAktionenListeAll();
		// init(welcheCombobox, welche Listes, welches Modell::getter)
		initComboBox(cbxEingabeAktion, baseList, AktionenListeModel::getCaakttyp, true);
		initComboBox(cbxEingabeOrt, baseList, AktionenListeModel::getCaaktionsort, true);
		initComboBox(cbxEingabeGruppe, baseList, AktionenListeModel::getCagruppe, true);
		initComboBox(cbxEingabeVerantwortlich, baseList, AktionenListeModel::getCaverantwortlich, true);

		// Comboboxen bei Focus
		installFocusStyle(cbxEingabeAktion);
		installFocusStyle(cbxEingabeOrt);
		installFocusStyle(cbxEingabeGruppe);
		installFocusStyle(cbxEingabeVerantwortlich);

		initComboBox(cbxFilterAktion, baseList, AktionenListeModel::getCaakttyp, false);
		initComboBox(cbxFilterOrt, baseList, AktionenListeModel::getCaaktionsort, false);
		initComboBox(cbxFilterGruppe, baseList, AktionenListeModel::getCagruppe, false);

		// cbxMitwFilterChor.valueProperty().bindBidirectional(cbxPersFilterChor.valueProperty());
		// cbxMitwFilterGruppe.valueProperty().bindBidirectional(cbxPersFilterGruppe.valueProperty());
	}

	private void initSonderfelderAktionen()
	{
		// initDatePicker(dpEingabeDatum, LocalDate.now());
		initDatePicker(dpEingabeDatum, null);
		dpEingabeDatum.setValue(null);
		initTimeSpinner(spEingabeBeginn, LocalTime.of(20, 0));
		initTimeSpinner(spEingabeTreffpunkt, LocalTime.of(20, 0));
	}

	private void initFilterfelder() throws Exception
	{
		dpFilterDatumBis.setValue(null);
		dpFilterDatumBis.getEditor().setText("");
		dpFilterDatumVon.setValue(null);
		dpFilterDatumVon.getEditor().setText("");
		cbxFilterAktion.setValue(null);
		cbxFilterAktion.getEditor().setText("");
		cbxFilterGruppe.setValue(null);
		cbxFilterGruppe.getEditor().setText("");
		cbxFilterOrt.setValue(null);
		cbxFilterOrt.getEditor().setText("");
		radFilterAlles.setSelected(true);
		txtFilterKurzbeschr.setText("");

	}

//	private ObservableList<CvwPersonenComboChorModel> chorList;
//	private ObservableList<CvwPersonenComboGruppeModel> gruppeList;
//
//	private void loadComboChorGruppenData() throws SQLException
//	{
//
//		List<CvwPersonenComboChorModel> listChor = db.getCvwPersonenComboChor();
//		listChor.sort(Comparator.comparing(CvwPersonenComboChorModel::getPechor, String::compareToIgnoreCase));
//		chorList = FXCollections.observableArrayList(listChor);
//
//		List<CvwPersonenComboGruppeModel> listGruppe = db.getCvwPersonenComboGruppe();
//		listGruppe.sort(Comparator.comparing(CvwPersonenComboGruppeModel::getPegruppe, String::compareToIgnoreCase));
//		gruppeList = FXCollections.observableArrayList(listGruppe);
//	}

	private void setupChorComboBox(ComboBox<CvwPersonenComboChorModel> comboBox)
	{

		// comboBox.setItems(chorList);
		// comboBox.setEditable(true);

		// comboBox.setConverter(new StringConverter<>()
//		{
//			@Override
//			public String toString(CvwPersonenComboChorModel object)
//			{
//				return object != null ? object.getPechor() : "";
//			}
//
//			@Override
//			public CvwPersonenComboChorModel fromString(String string)
//			{
//				if (string == null || string.isBlank())
//					return null;
//
//				return chorList.stream()
//						.filter(c -> c.getPechor().equalsIgnoreCase(string))
//						.findFirst()
//						.orElseGet(() -> new CvwPersonenComboChorModel(string));
//			}
//		});
	}

	private void setupGruppeComboBox(ComboBox<CvwPersonenComboGruppeModel> comboBox)
	{

		// comboBox.setItems(gruppeList);
		// comboBox.setEditable(true);

		// comboBox.setConverter(new StringConverter<>()
//		{
//			@Override
//			public String toString(CvwPersonenComboGruppeModel object)
//			{
//				return object != null ? object.getPegruppe() : "";
//			}
//
//			@Override
//			public CvwPersonenComboGruppeModel fromString(String string)
//			{
//				if (string == null || string.isBlank())
//					return null;
//
//				return gruppeList.stream()
//						.filter(g -> g.getPegruppe().equalsIgnoreCase(string))
//						.findFirst()
//						.orElseGet(() -> new CvwPersonenComboGruppeModel(string));
//			}
//		});
	}

	public void initCombosTabPersonen() throws SQLException
	{
		setupChorComboBox(cbxMitwFilterChor);
		setupGruppeComboBox(cbxMitwFilterGruppe);

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

		// cbxPersFilterChor.setItems(oblist_filterpersonenchor);
		// cbxPersFilterGruppe.setItems(oblist_filterpersonengruppe);
		cbxMitwFilterChor.setItems(oblist_filterpersonenchor);
		cbxMitwFilterGruppe.setItems(oblist_filterpersonengruppe);
	}

	@FXML
	public void men01AktionenBeenden_OnClick()
	{
		if (!Msgbox.yesno("Programm beenden", "Möchten Sie das Programm wirklich beenden?"))
		{
			return;
		}
		speichereAktionenfilter();
		SceneManager.exitApp();
	}

	@FXML
	public void men01Einstellungen_OnAction() throws Exception
	{
		Msgbox.show("Dateipfade ändern ...", "Bitte wählen Sie die Funktion zum Ändern der Dateipfade im Programmteil NOTENARCHIV aus!");
	}

	@FXML
	public void men02StdatPersonen_OnAction() throws Exception
	{
		openStammdaten("personen");
	}

	@FXML
	public void men02StdatImport_OnAction() throws Exception
	{
		openStammdaten("import");
	}

// --------------------------------------------------------------------------------
// Hilfsmethoden für Steuerelemente -----------------------------------------------
// --------------------------------------------------------------------------------
	private void initDatePicker(DatePicker datePicker, LocalDate startDatum)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		datePicker.setConverter(new StringConverter<LocalDate>()
		{

			@Override
			public String toString(LocalDate date)
			{
				return date != null ? formatter.format(date) : "";
			}

			@Override
			public LocalDate fromString(String string)
			{
				return (string == null || string.isEmpty())
						? null
						: LocalDate.parse(string, formatter);
			}
		});

		// Startwert setzen, Default = heute
		datePicker.setValue(startDatum != null ? startDatum : LocalDate.now());
	}

	private void initTimeSpinner(Spinner<LocalTime> spinner, LocalTime startTime)
	{

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

		SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<>()
		{
			{
				setValue(startTime);
			}

			@Override
			public void decrement(int steps)
			{
				if (getValue() != null)
					setValue(getValue().minusMinutes(15 * steps));
			}

			@Override
			public void increment(int steps)
			{
				if (getValue() != null)
					setValue(getValue().plusMinutes(15 * steps));
			}
		};

		factory.setConverter(new StringConverter<>()
		{
			@Override
			public String toString(LocalTime time)
			{
				return time == null ? "" : formatter.format(time);
			}

			@Override
			public LocalTime fromString(String s)
			{
				try
				{
					return LocalTime.parse(s, formatter);
				}
				catch (Exception e)
				{
					return factory.getValue();
				}
			}
		});

		spinner.setValueFactory(factory);
		spinner.setEditable(true);

		TextField editor = spinner.getEditor();

		// ✅ Filter: erlaubt freies Tippen, formatiert sanft
		UnaryOperator<TextFormatter.Change> filter = change -> {
			String text = change.getControlNewText();

			// max 5 Zeichen (HH:mm)
			if (text.length() > 5)
				return null;

			// nur Ziffern und :
			if (!text.matches("[0-9:]*"))
				return null;

			// nur EIN :
			if (text.chars().filter(c -> c == ':').count() > 1)
				return null;

			return change;
		};

		editor.setTextFormatter(new TextFormatter<>(filter));

		// ✅ Automatisch ':' einfügen
		editor.textProperty().addListener((obs, old, text) -> {
			if (text.length() == 2 && !text.contains(":"))
			{
				editor.setText(text + ":");
				editor.positionCaret(3);
			}
		});

		// ✅ Commit bei Fokusverlust
		spinner.focusedProperty().addListener((obs, old, focused) -> {
			if (focused)
			{
				Platform.runLater(editor::selectAll);
			}
			else
			{
				try
				{
					if (editor.getText().length() == 5)
					{
						spinner.getValueFactory()
								.setValue(LocalTime.parse(editor.getText(), formatter));
					}
				}
				catch (Exception ignored)
				{
					editor.setText(
							factory.getValue() == null ? "" : formatter.format(factory.getValue()));
				}
			}
		});

	}

// ============================================================================
// Hilfsmethoden für normale Comboboxen
// ============================================================================
	private void initComboBox(
			ComboBox<String> combo,
			List<AktionenListeModel> baseList,
			Function<AktionenListeModel, String> extractor, boolean autoopen)
	{

		List<String> items = baseList.stream()
				.map(extractor)
				.filter(Objects::nonNull)
				.filter(s -> !s.isBlank())
				.distinct()
				.sorted(String::compareToIgnoreCase)
				.toList();

		combo.setItems(FXCollections.observableArrayList(items));
		combo.setEditable(true);

		// makeSearchable(combo);
//		if (autoopen == true)
//		{
//			installAutoOpenOnFocus(combo);
//		}
	}

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

	// Hilfsmethode für die Gestaltung der Comboboxen
	private void installFocusStyle(ComboBox<?> comboBox)
	{
		comboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal)
			{
				if (!comboBox.getStyleClass().contains("combo-focused"))
				{
					comboBox.getStyleClass().add("combo-focused");
				}
			}
			else
			{
				comboBox.getStyleClass().remove("combo-focused");
			}
		});
	}

	private void refreshComboBoxes() throws SQLException
	{

		List<AktionenListeModel> baseList = db.getAktionenListeAll();

		refreshComboBox(cbxEingabeAktion, baseList, AktionenListeModel::getCaakttyp);
		refreshComboBox(cbxEingabeOrt, baseList, AktionenListeModel::getCaaktionsort);
		refreshComboBox(cbxEingabeGruppe, baseList, AktionenListeModel::getCagruppe);
		refreshComboBox(cbxEingabeVerantwortlich, baseList, AktionenListeModel::getCaverantwortlich);

		refreshComboBox(cbxFilterAktion, baseList, AktionenListeModel::getCaakttyp);
		refreshComboBox(cbxFilterOrt, baseList, AktionenListeModel::getCaaktionsort);
		refreshComboBox(cbxFilterGruppe, baseList, AktionenListeModel::getCagruppe);
	}

	private void refreshComboBox(
			ComboBox<String> combo,
			List<AktionenListeModel> baseList,
			Function<AktionenListeModel, String> extractor)
	{

		String currentValue = combo.getValue(); // merken

		List<String> items = baseList.stream()
				.map(extractor)
				.filter(Objects::nonNull)
				.filter(s -> !s.isBlank())
				.distinct()
				.sorted(String::compareToIgnoreCase)
				.toList();

		combo.getItems().setAll(items);

		// Auswahl wiederherstellen, falls noch vorhanden
		if (currentValue != null && items.contains(currentValue))
		{
			combo.setValue(currentValue);
		}
		else
		{
			combo.setValue(null);
		}
	}

	// ================================================================================================
	// Konfigurationswerte laden

	public void restoreFilterAktionen() throws SQLException
	{ // Filterwerte wieder holen aus Prop

		dpFilterDatumVon.getEditor().setText(ConfigManager.loadFilterAktionDatumvon());
		dpFilterDatumBis.getEditor().setText(ConfigManager.loadFilterAktionDatumbis());
		Platform.runLater(dpFilterDatumVon::requestFocus);
		Platform.runLater(dpFilterDatumBis::requestFocus);
		Platform.runLater(btnFilterAn::requestFocus);

		cbxFilterAktion.getEditor().setText(ConfigManager.loadFilterAktion());
		cbxFilterOrt.getEditor().setText(ConfigManager.loadFilterAktionOrt());
		cbxFilterGruppe.getEditor().setText(ConfigManager.loadFilterAktionGruppe());
		txtFilterKurzbeschr.setText(ConfigManager.loadFilterAktionBeschreibung());

		cbxMitwFilterChor.getEditor().setText(ConfigManager.loadFilterChor());
		cbxMitwFilterGruppe.getEditor().setText(ConfigManager.loadFilterGruppe());

	}

//	public void restoreCombosTabPersonen() throws SQLException
//	{ // Filterwerte wieder holen aus Prop
//
//		String savedChor = ConfigManager.loadFilterChor();
//		if (!savedChor.isBlank())
//		{
//			cbxMitwFilterChor.getItems().stream()
//					.filter(c -> c.getPechor().equals(savedChor))
//					.findFirst()
//					.ifPresent(cbxMitwFilterChor::setValue);
//		}
//
//		String savedGruppe = ConfigManager.loadFilterGruppe();
//		if (!savedGruppe.isBlank())
//		{
//			cbxMitwFilterGruppe.getItems().stream()
//					.filter(g -> g.getPegruppe().equals(savedGruppe))
//					.findFirst()
//					.ifPresent(cbxMitwFilterGruppe::setValue);
//		}
//
//	}

}
