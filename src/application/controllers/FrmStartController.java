package application.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.utils.pdf.PdfTableExporter;
import application.utils.pdf.PdfExportOptions;
import application.utils.pdf.PdfFilenameUtil;
import application.utils.pdf.PdfPathUtil; // für DB-Pfad
import application.ConfigManager;
import application.FilterState;
import application.SceneManager;
import application.ValuesGlobals;

import application.dbupdate.DatabaseUpdaten;
import application.models.AutorenlisteModel;
import application.models.BibellisteModel;
import application.models.EditionenlisteComboModel;
import application.models.EditionenlisteComboNaModel;
import application.models.EditionenlisteModel;
import application.models.GesangbuchModel;
import application.models.LiederStueckeBibModel;
import application.models.LiederStueckeComboModel;
import application.models.LiederStueckeGesbModel;
import application.models.LiederStueckeModel;
import application.models.LiederStueckeThemenModel;
import application.models.LiederStueckeWoliModel;
import application.models.LiteraturlisteModel;
import application.models.NotenmappeModel;
import application.models.ProgrammversionenModel;
import application.models.StueckartlisteModel;
import application.models.ThemenlisteModel;
import application.models.VerlaglisteModel;
import application.models.WochenliedlisteModel;
import application.uicomponents.Msgbox;
import application.utils.AktuellesDatum;
import application.utils.StringBeschneiden;
import application.utils.TableUtils;
import application.utils.ToolsSelectionRestorer;
import application.utils.ToolsUpdateChecker;
import application.utils.ToolsWinHelper;
import application.utils.license.License;
import application.utils.license.LicenseManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color; entfrent, weil AWT-Farben genommen werden für PDF
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class FrmStartController implements Initializable
{

// -------------------------------------------------------------------------
// Steuerelemente
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Bereich ganzes Fenster
	@FXML
	private AnchorPane rootStart;// rootPane;
// TAB-Pane
	@FXML
	private TabPane tabpaneStartfenster;
//BUTTONS---------------------
	@FXML
	private Button btnFilterAn, btnFilterAus, btnListenLeer, btnListeDrucken,
			btnNotenmappen, btnAktivitaeten, btnBeenden;
	@FXML
	private ProgressBar progressPdf;
	@FXML
	private Label lblStatusPdf, lblUpdateinfo;
	@FXML
	private Button btnListeDruckenVerz;// , btnUpdaten;

//MENUES----------------------
	@FXML
	private MenuItem men01Einstellungen, men01Beenden, men01Importieren;
	@FXML
	private MenuItem men03Programminfo, men03WebAllgemein, men03UpdatesPruefen, men03HilfeVersion, men03Lizenz, men03WebOnlinehilfe;
	@FXML
	private MenuItem men40, men40Programm, men40Datenbank;

//================================================
//----FILTERBEREICH---------
	@FXML
	private CheckBox chkFilterSammeln, chkDsBearbeiten;

	// Filter Combos
	@FXML
	private ComboBox<ThemenlisteModel> cbxFilterThema;
	@FXML
	private ComboBox<WochenliedlisteModel> cbxFilterWochenlied;
	@FXML
	private ComboBox<StueckartlisteModel> cbxFilterStueckart;
	@FXML
	private ComboBox<NotenmappeModel> cbxFilterNotenmappe;// List noch ändern!!!!
	@FXML
	private ComboBox<BibellisteModel> cbxFilterBibel;
	@FXML
	private ComboBox<GesangbuchModel> cbxFilterGesangbuch;

	@FXML
	private TextField txtFilterTitel, txtFilterEdit, txtFilterKomp, txtFilterEditVerlag, txtFilterDicht;
	@FXML
	private Label lblHaupttitel, lblFilterFortschrittsinfo; //
// -------------------------------------------------------------
// einzelne Steuerelemente innerhalb der Tabs
// -------------------------------------------------------------
	// Tab-Bereich Literatur-Details
	@FXML
	private Tab tabregLiteratur;
	@FXML
	private Label lblLitInfozeile, lblLitInfozeileKurz;
	@FXML
	private TextField txtLitIdCheck, txtLitStckLiedId, txtLitDbCheck;
	@FXML
	private ComboBox<EditionenlisteComboNaModel> cbxLitEditNotenausgabe;
	@FXML
	private ComboBox<AutorenlisteModel> cbxLitEditKomp;
	@FXML
	private ComboBox<AutorenlisteModel> cbxLitEditBearb;

	@FXML
	private ComboBox<StueckartlisteModel> cbxLitStueckart;
	@FXML
	private ComboBox<LiederStueckeComboModel> cbxLitEditTitel;
	@FXML
	private TextField txtLitEditSeite, txtLitEditSeitezus, txtLitEditNr, txtLitEditNrzus, txtLitEditTonart, txtLitEditTitelgrafikpfad;
	@FXML
	private TextArea txtaLitEditBesetzung;

	@FXML
	private ImageView imgLitEditTitelgrafik;

	@FXML
	private Button btnLitSpeichern, btnLitNeu, btnLitLoeschen, btnLitZuStueckeRegister, btnLitZuNotenausgabeRegister, btnKomponistEdit, btnStueckartEdit;

	// --------------------------
	// Tab-Bereich Notenausgaben
	@FXML
	private Tab tabregNotenausgaben;
	@FXML
	private Label lblNaEditInfozeile;
	@FXML
	private TextField txtNaEditLang, txtNaEditKurz, txtNaEditJahr, txtNaEditHrsg, txtNaEditErfasst,
			txtNaEditTitelgrafikpfad, txtNaEditBestnr, txtNaEditSchw, txtNaIdCheck,
			txtLitEditSec, txtLitEditMin, txtNaEditDb;
	@FXML
	private TextArea txtaNaEditBeschr;
	@FXML
	private ComboBox<VerlaglisteModel> cbxNaEditVerlag;
	@FXML
	private ComboBox<EditionenlisteComboModel> cbxNaEditArt;
	@FXML
	private ImageView imgNaTitelgrafik;
	@FXML
	private Button btnNaNeu, btnNaSpeichern, btnNaLoeschen, btnVerlagEdit, btnNaGrafikwahl, btnEdionsartEdit;

	// ----------------------------
	// Tab-Bereich Stücke/Lieder
	@FXML
	private Tab tabregStck;
	@FXML
	private Label lblStckInfozeile;
	@FXML
	private TextField txtStueckTitel, txtStueckGeaendert;
	@FXML
	private ComboBox<AutorenlisteModel> cbxStckEditDichter;
	@FXML
	private Button btnStckNeu, btnStckSpeichern, btnStckLoeschen, btnStckThemaEdit, btnStckBibEdit, btnStckWoliEdit, btnStckGesbEdit;
	// Eingabefelder zur Dateneingabe
	@FXML
	private ComboBox<WochenliedlisteModel> cbxWochenlied;
	@FXML
	private ComboBox<ThemenlisteModel> cbxThema;
	//
//========================================================
// ------TABLE VIEWS -------------------------------------
	// TableView Literatur mit Spalten
	@FXML
	private TableView<LiteraturlisteModel> tblvwLiteratur;

	@FXML
	private TableColumn<LiteraturlisteModel, String> tblvwColThema, tblvwColWoli, tblvwColBibel, tblvwColGesbuch, tblvwColNotenmappe,
			tblvwColTitel, tblvwColEdit, tblvwColTArt, tblvwColKomp, tblvwColBearb, tblvwColBes, tblvwColId;
	@FXML
	private TableColumn<LiteraturlisteModel, String> tblvwColDb;
	@FXML
	private TableColumn<LiteraturlisteModel, String> tblvwColStckArt;
	@FXML
	private TableColumn<LiteraturlisteModel, String> tblvwColLitGrafik;
	@FXML
	private TableColumn<LiteraturlisteModel, String> tblvwColErfasst;
	@FXML
	private TableColumn<LiteraturlisteModel, Integer> tblvwColS;
	@FXML
	private TableColumn<LiteraturlisteModel, Integer> tblvwColN;

	// -----------------------------
	// TableView Editonen mit Spalten
	@FXML
	private TableView<EditionenlisteModel> tblvwEditionen;

	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditId;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditNoma;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditLt;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditKt;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditVerlag;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditJahr;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditHrsg;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditArt;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditGrafik;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditEingabe;
	@FXML
	private TableColumn<EditionenlisteModel, String> tblvwColEditAusDb;

	// ------------------------------
	// TableView Stücke mit Spalten
	@FXML
	private TableView<LiederStueckeModel> tblvwStuecke;

	@FXML
	private TableColumn<LiederStueckeModel, String> tblvwColStckTitel;
	@FXML
	private TableColumn<LiederStueckeModel, String> tblvwColStckDicht;
	@FXML
	private TableColumn<LiederStueckeModel, String> tblvwColStckThema;
	@FXML
	private TableColumn<LiederStueckeModel, String> tblvwColStckWoli;
	@FXML
	private TableColumn<LiederStueckeModel, String> tblvwColStckBibel;
	@FXML
	private TableColumn<LiederStueckeModel, String> tblvwColStckGesbuch;
	@FXML
	private TableColumn<LiederStueckeModel, String> tblvwColStckDb;
	@FXML
	private TableColumn<LiederStueckeModel, String> tblvwColStckErfasst;
//------------------------- 2.1.2023
	@FXML
	private TableView<LiederStueckeThemenModel> tblvwStckThemen;
	@FXML
	private TableColumn<LiederStueckeThemenModel, String> tblvwColStckThemenThema;
	@FXML
	private TableView<LiederStueckeWoliModel> tblvwStckWoli;
	@FXML
	private TableColumn<LiederStueckeWoliModel, String> tblvwColStckWoliWoli;
	@FXML
	private TableView<LiederStueckeBibModel> tblvwStckBib;
	@FXML
	private TableColumn<LiederStueckeBibModel, String> tblvwColStckBibBib;
	@FXML
	private TableColumn<LiederStueckeBibModel, String> tblvwColStckBibVers;

	@FXML
	private TableView<LiederStueckeGesbModel> tblvwStckGesb;
	@FXML
	private TableColumn<LiederStueckeGesbModel, String> tblvwColStckGesbGesb;
	@FXML
	private TableColumn<LiederStueckeGesbModel, String> tblvwColStckGesbNr;
//+++++++++++++++++++++++++++++
	int aktivindextableview = 0;
	FileChooser fileAuswahlbox = new FileChooser();
//-------------------------
	// Listen für TableView anlegen
	ObservableList<EditionenlisteModel> oblist_edit = FXCollections.observableArrayList();
	protected int tblvwzeile;
	// Observablelisten für TblViews
	ObservableList<LiteraturlisteModel> oblist_lit = FXCollections.observableArrayList();
	ObservableList<LiederStueckeModel> oblist_st = FXCollections.observableArrayList();
	ObservableList<LiederStueckeThemenModel> oblist_stth = FXCollections.observableArrayList();
	ObservableList<LiederStueckeWoliModel> oblist_stwoli = FXCollections.observableArrayList();
	ObservableList<LiederStueckeBibModel> oblist_stbib = FXCollections.observableArrayList();
	ObservableList<LiederStueckeGesbModel> oblist_stgesb = FXCollections.observableArrayList();
	// +++++++++++++++++++++++++++++++++++++++++++++++
	// KLassenvariablen
	private Stage stage = null;
	private boolean suppressAutoSelection = false;

	// =================================
	// alle benötigten DB-Controller
	// =================================

	private DatabaseControllerPcndStartfenster dbStart() throws SQLException
	{
		return new DatabaseControllerPcndStartfenster();
	}

	private DatabaseControllerPcndAutoren dbAutoren()
	{
		return new DatabaseControllerPcndAutoren();
	}

	private DatabaseControllerPcndNotenmappen dbNotenmappen() throws SQLException
	{
		return new DatabaseControllerPcndNotenmappen();
	}

	private DatabaseControllerPcndStueckarten dbStueckart() throws SQLException
	{
		return new DatabaseControllerPcndStueckarten();
	}

	private DatabaseControllerPcndVerlage dbVerlag() throws SQLException
	{
		return new DatabaseControllerPcndVerlage();
	}

	private DatabaseControllerPcndEditionsart dbEditionsart()
	{
		return new DatabaseControllerPcndEditionsart();
	}

	private DatabaseControllerPcndThemen dbThemen() throws SQLException
	{
		return new DatabaseControllerPcndThemen();
	}

	private DatabaseControllerPcndBibel dbBibel()
	{
		return new DatabaseControllerPcndBibel();
	}

	private DatabaseControllerPcndGesbuch dbGesb()
	{
		return new DatabaseControllerPcndGesbuch();
	}

	private DatabaseControllerPcndWochenlied dbWochenlied() throws SQLException
	{
		return new DatabaseControllerPcndWochenlied();
	}

// =======================================================================
// Startvorgang beginnt hier
// =======================================================================
	// Initialisierung der Combos und der TableViews
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		System.out.println("(1) initialize - FrmStart 01");
		// btnUpdaten.setVisible(false);
		lblUpdateinfo.setVisible(false);
		// men40.setVisible(false);
	}

	public void onShow()
	{
		System.out.println("Scene1 wird angezeigt");
		// Hier deine Scene-spezifische Initialisierung

		initView();

	}

	public void initView()
	{
		System.out.println("(2) initView()");
		try
		{
			// Converter einmalig setzen
			initCombosFilterConverters();
			initComboNotenmappeFilterConverter();
			// Items laden
			updateCombosFilterItems();
			updateComboNotenmappeItems();
			setUIDataLiteraturliste();
			setUIDataEditliste();
			setUIDataStckLiedliste();
			setUIDataStckLiedThemaliste();
			setUIDataStckLiedWoliliste();
			setUIDataStckLiedGesbliste();
			setUIDataStckLiedBibliste();
			// setButtons(); kommt bei loadfilterStart nochmals
			// ==== Alle Combos laden ====
			// Converter + Listener einmalig setzen
			initCombosNaIndivConverters();
			initCombosStckDichterConverters();
			initCombosLitConverters();
			// Items initial laden
			updateCombosNaIndiv();
			updateCombosStckDichter();
			updateCombosLitItems();
			// Felder auf dem ersten Rgeister leeren
			clearLitEingabefelder();
//			checkUpdates();
			// ===== Update-Check nur 1x pro Sitzung =====
			if (ValuesGlobals.updatecheck == true)
			{ // beim Starten werden Updates geprüft
				checkUpdatesStart();
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
						men40Datenbank.setVisible(true);
						lblUpdateinfo.setVisible(true);
					}
					men40.setVisible(true);
				}
			}
			// Alle TableViews in rootPane durchgehen und Spalten fixieren
			rootStart.lookupAll(".table-view").forEach(tv -> ((TableView<?>) tv).getColumns().forEach(col -> col.setReorderable(false)));
			// Filterwerte laden
			loadFilterStart();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void checkUpdatesStart()
	{
		System.out.println("(3) checkUpdatesStart()");
		// btnUpdaten.setVisible(false);
		men40.setVisible(false);
		men40Programm.setVisible(false);
		men40Datenbank.setVisible(false);
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

// -----------------------------------------------------------------
	// Stage setzen
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

// -----------------------------------------------------------------
	// Buttons setzen
	public void setButtons()
	{

		System.out.println("(5) setButtons()");
	}

	// -----------------------------------------------------------------
	// Filtereinträge vom letzten Mal holenn
	public void loadFilterStart() throws Exception
	{
//		// Filterwerte holen -- alt!
//		txtFilterTitel.setText(ConfigManager.loadFilterStartTitel());
//		cbxFilterStueckart.getEditor().setText(ConfigManager.loadFilterStartStckart());
//		txtFilterEdit.setText(ConfigManager.loadFilterStartEdition());
//		txtFilterKomp.setText(ConfigManager.loadFilterStartKomponist());
//		txtFilterDicht.setText(ConfigManager.loadFilterStartDichter());
//		txtFilterEditVerlag.setText(ConfigManager.loadFilterStartVerlag());
//		cbxFilterWochenlied.getEditor().setText(ConfigManager.loadFilterStartWoli());
//		cbxFilterThema.getEditor().setText(ConfigManager.loadFilterStartThema());
//		cbxFilterNotenmappe.getEditor().setText(ConfigManager.loadFilterStartNoma());
//		cbxFilterBibel.getEditor().setText(ConfigManager.loadFilterStartBib());
//		cbxFilterGesangbuch.getEditor().setText(ConfigManager.loadFilterStartGesangbuch());

		// --------- Filterstatus holen
		FilterState f = FilterState.get();

		txtFilterTitel.setText(f.titel);
		cbxFilterStueckart.getEditor().setText(f.stueckart);
		txtFilterEdit.setText(f.edition);
		txtFilterKomp.setText(f.komponist);
		txtFilterDicht.setText(f.dichter);
		txtFilterEditVerlag.setText(f.verlag);
		cbxFilterWochenlied.getEditor().setText(f.wochenlied);
		cbxFilterThema.getEditor().setText(f.thema);
		cbxFilterNotenmappe.getEditor().setText(f.notenmappe);
		cbxFilterBibel.getEditor().setText(f.bibel);
		cbxFilterGesangbuch.getEditor().setText(f.gesangbuch);
		// ----------------------------------

		lblFilterFortschrittsinfo.setText("Bitte Filterkriterien eingeben ...");
		filtern(0);

	}

// Filter speichern beim ändern der Scene	
	private void saveFilterToState()
	{
		FilterState f = FilterState.get();

		f.titel = txtFilterTitel.getText();
		f.stueckart = cbxFilterStueckart.getEditor().getText();
		f.edition = txtFilterEdit.getText();
		f.komponist = txtFilterKomp.getText();
		f.dichter = txtFilterDicht.getText();
		f.verlag = txtFilterEditVerlag.getText();
		f.wochenlied = cbxFilterWochenlied.getEditor().getText();
		f.thema = cbxFilterThema.getEditor().getText();
		f.notenmappe = cbxFilterNotenmappe.getEditor().getText();
		f.bibel = cbxFilterBibel.getEditor().getText();
		f.gesangbuch = cbxFilterGesangbuch.getEditor().getText();
	}
// ===================================================================================

// ----------- Table Viewlisten - Spalten ------------------------------------------------------
	private void setUIDataLiteraturliste()
	{
		tblvwColId.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("id"));
		tblvwColErfasst.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("literfasst"));
		tblvwColErfasst.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLiterfasst().substring(0, 10)));
		tblvwColTitel.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("littitel"));

		tblvwColEdit.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litedit"));
		tblvwColDb.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("dbkennung"));
		tblvwColKomp.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litkomp"));
		tblvwColBearb.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litbearb"));

		tblvwColN.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, Integer>("nummersort"));
		tblvwColN.setCellFactory(new Callback<TableColumn<LiteraturlisteModel, Integer>, TableCell<LiteraturlisteModel, Integer>>()
		{
			@Override
			public TableCell<LiteraturlisteModel, Integer> call(TableColumn<LiteraturlisteModel, Integer> column)
			{
				return new TableCell<LiteraturlisteModel, Integer>()
				{
					@Override
					protected void updateItem(Integer item, boolean empty)
					{
						super.updateItem(item, empty);
						if (empty || item == null)
						{
							setText("");
						}
						else
						{
							setText(item.toString());
						}
					}
				};
			}
		});
		tblvwColS.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, Integer>("seitesort"));
		tblvwColS.setCellFactory(new Callback<TableColumn<LiteraturlisteModel, Integer>, TableCell<LiteraturlisteModel, Integer>>()
		{
			@Override
			public TableCell<LiteraturlisteModel, Integer> call(TableColumn<LiteraturlisteModel, Integer> column)
			{
				return new TableCell<LiteraturlisteModel, Integer>()
				{
					@Override
					protected void updateItem(Integer item, boolean empty)
					{
						super.updateItem(item, empty);
						if (empty || item == null)
						{
							setText("");
						}
						else
						{
							setText(item.toString());
						}
					}
				};
			}
		});

		tblvwColBes.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litbesetzung"));
		tblvwColStckArt.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litstueckart"));
		tblvwColLitGrafik.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litgrafik"));
		tblvwColTArt.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("littonart"));
		tblvwColWoli.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litwoli"));
		tblvwColThema.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litthema"));
		tblvwColNotenmappe.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litnoma"));

		tblvwLiteratur.setItems(oblist_lit);
	}

	private void setUIDataStckLiedliste()
	{
		tblvwColStckDb.setCellValueFactory(new PropertyValueFactory<LiederStueckeModel, String>("dbs"));
		tblvwColStckTitel.setCellValueFactory(new PropertyValueFactory<LiederStueckeModel, String>("stcktitel"));
		tblvwColStckDicht.setCellValueFactory(new PropertyValueFactory<LiederStueckeModel, String>("stckdicht"));
		tblvwColStckBibel.setCellValueFactory(new PropertyValueFactory<LiederStueckeModel, String>("stckbib"));
		tblvwColStckGesbuch.setCellValueFactory(new PropertyValueFactory<LiederStueckeModel, String>("stckgesangbuch"));
		tblvwColStckThema.setCellValueFactory(new PropertyValueFactory<LiederStueckeModel, String>("stckthema"));
		tblvwColStckWoli.setCellValueFactory(new PropertyValueFactory<LiederStueckeModel, String>("stckwoli"));
		tblvwColStckErfasst.setCellValueFactory(new PropertyValueFactory<LiederStueckeModel, String>("stckerfasst"));
		tblvwColStckErfasst.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStckerfasst().substring(0, 10)));

		tblvwStuecke.setItems(oblist_st);
	}

	private void setUIDataStckLiedThemaliste()
	{
		tblvwColStckThemenThema.setCellValueFactory(new PropertyValueFactory<LiederStueckeThemenModel, String>("thema"));
		tblvwColStckThemenThema.setSortable(true);
		tblvwColStckThemenThema.setComparator(String::compareToIgnoreCase);
		tblvwStckThemen.getSortOrder().add(tblvwColStckThemenThema);
		tblvwColStckThemenThema.setSortType(TableColumn.SortType.ASCENDING);

		tblvwStckThemen.sort();
	}

	private void setUIDataStckLiedWoliliste()
	{
		tblvwColStckWoliWoli.setCellValueFactory(new PropertyValueFactory<LiederStueckeWoliModel, String>("woli"));
	}

	private void setUIDataStckLiedBibliste()
	{
		tblvwColStckBibBib.setCellValueFactory(new PropertyValueFactory<LiederStueckeBibModel, String>("bib"));
		tblvwColStckBibVers.setCellValueFactory(new PropertyValueFactory<LiederStueckeBibModel, String>("versangabe"));
	}

	private void setUIDataStckLiedGesbliste()
	{
		tblvwColStckGesbGesb.setCellValueFactory(new PropertyValueFactory<LiederStueckeGesbModel, String>("gesb"));
		tblvwColStckGesbNr.setCellValueFactory(new PropertyValueFactory<LiederStueckeGesbModel, String>("nummer"));
	}
	// ++++++++++++++++++++++++++++++++++++++++++

	private void setUIDataEditliste()
	{

		tblvwColEditId.setCellValueFactory(cd -> cd.getValue().dbkeditProperty()); // falls "Id" = dbkedit
		tblvwColEditLt.setCellValueFactory(cd -> cd.getValue().ltProperty());
		tblvwColEditNoma.setCellValueFactory(cd -> cd.getValue().nomaProperty());
		tblvwColEditAusDb.setCellValueFactory(cd -> cd.getValue().dbkeditProperty());
		tblvwColEditKt.setCellValueFactory(cd -> cd.getValue().ktProperty());
		tblvwColEditVerlag.setCellValueFactory(cd -> cd.getValue().verlagProperty());
		tblvwColEditJahr.setCellValueFactory(cd -> cd.getValue().edjahrProperty());
		tblvwColEditHrsg.setCellValueFactory(cd -> cd.getValue().hrsgProperty());
		tblvwColEditGrafik.setCellValueFactory(cd -> cd.getValue().titelgrafikpfadProperty());
		// tblvwColEditEingabe.setCellValueFactory(cd ->
		// cd.getValue().erfasstProperty());
		// tblvwColEditEingabe.setCellValueFactory(cd ->
		// cd.getValue().eingabezeitpunktProperty());
		tblvwColEditEingabe.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEingabezeitpunkt().substring(0, 10)));
		tblvwColEditArt.setCellValueFactory(cd -> cd.getValue().edartProperty());
	}

//========================================================================================================
// Ende Startvorgang
//========================================================================================================

//=========================================================================================================
	// Filtervorgang
//=========================================================================================================
	private void filtern(int filterLitId) throws Exception
	{
		System.out.println("(4) filtern()");
		Boolean filtersammeln = false;
		String filterlittitel = "";
		String filterlitedit = "";
		String filterlitkomp = "";
		String filterlitstueckart = "";
		String filterlitthema = "";
		String filterlitwoli = "";
		String filterlitnoma = "";

		String filternaeditlang = "";
		String filternaverlag = "";

		String filterstcktitel = "";
		String filterstckdicht = "";
		String filterstckthema = "";
		String filterstckwoli = "";
		String filterstckbib = "";
		String filterstckgesbuch = "";
		setButtons();

		try
		{
			filtersammeln = this.chkFilterSammeln.isSelected();
			filterlittitel = txtFilterTitel.getText();
			filterlitedit = txtFilterEdit.getText();
			filterlitkomp = txtFilterKomp.getText();
			filterlitstueckart = cbxFilterStueckart.getEditor().getText();
			filterstckdicht = txtFilterDicht.getText();
			filternaeditlang = txtFilterEdit.getText();
			filternaverlag = txtFilterEditVerlag.getText();

			// Filtervariablen für Literatur
			filterlitthema = cbxFilterThema.getEditor().getText();
			filterlitwoli = cbxFilterWochenlied.getEditor().getText();
			filterlitnoma = cbxFilterNotenmappe.getEditor().getText();

			// Filtervariablen für Stücke-/Lieder
			filterstckbib = cbxFilterBibel.getEditor().getText();
			filterstckgesbuch = cbxFilterGesangbuch.getEditor().getText();
			filterstckthema = cbxFilterThema.getEditor().getText();
			filterstcktitel = filterlittitel;
			filterstckwoli = filterlitwoli;

			// ======= Tabreg LITERATUR filtern
			if (tabregLiteratur.isSelected() == true)
			{

				if (filtersammeln == false)
				{// Filterergebnis vom vorigen Mal NICHT sammeln
					oblist_lit.clear();
				}
				List<LiteraturlisteModel> listadb = dbStart().getLiteraturListeFilter(
						filterLitId,
						filterlittitel,
						filterlitedit,
						filterlitkomp,
						filterlitstueckart,
						filterlitthema,
						filterlitwoli,
						filterlitnoma,
						filternaverlag,
						filterstckdicht);
				listadb.forEach((item) -> oblist_lit.add(item));

				tblvwLiteratur.setItems(oblist_lit);
				tblvwLiteratur.getSortOrder().add(tblvwColTitel);
				lblFilterFortschrittsinfo.setText("" + oblist_lit.size() + " Einträge gefunden");// (maximal " + ValuesGlobals.filtermax + ")");

				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						tblvwLiteratur.requestFocus();// benötigt Verzögerung
						tblvwLiteratur.getSelectionModel().select(0); // wird für die Ausrichtung der Zellen...
						tblvwLiteratur.getFocusModel().focus(0); // .. des TblView benötigt
						tblvwLiteratur.getSelectionModel().clearSelection();
					}
				});

				// Spalten in der Tableview ein/ausblenden
				if (!(cbxFilterThema.getEditor().getText()).isEmpty())
				{
					tblvwColThema.setVisible(true);
				}
				else
				{
					tblvwColThema.setVisible(false);
				}
				if (!(cbxFilterWochenlied.getEditor().getText()).isEmpty())
				{
					tblvwColWoli.setVisible(true);
				}
				else
				{
					tblvwColWoli.setVisible(false);
				}

				if (!(cbxFilterNotenmappe.getEditor().getText()).isEmpty())
				{
					tblvwColNotenmappe.setVisible(true);
				}
				else
				{
					tblvwColNotenmappe.setVisible(false);
				}

			}
			// ========= Tabreg STUECKE LIEDER filtern

			if (tabregStck.isSelected() == true)
			{
				oblist_stwoli.clear();
				oblist_stbib.clear();
				oblist_stgesb.clear();
				oblist_stth.clear();
				if (filtersammeln == false)
				{// Filterergebnis vom vorigen Mal NICHT sammeln
					oblist_st.clear();

				}
				List<LiederStueckeModel> listadb = dbStart().getStueckeLiederListe(filterstcktitel, filterstckdicht, filterstckthema, filterstckwoli, filterstckbib,
						filterstckgesbuch);
				listadb.forEach((item) -> oblist_st.add(item));
				tblvwStuecke.setItems(oblist_st);
				lblFilterFortschrittsinfo.setText("" + oblist_st.size() + " Einträge gefunden");
				tblvwStuecke.getSortOrder().add(tblvwColStckTitel);// sortieren nach erster Spalte
				if (suppressAutoSelection == false)
				{
					Platform.runLater(() -> {
						tblvwStuecke.requestFocus();
						tblvwStuecke.getSelectionModel().select(0);
						tblvwStuecke.getFocusModel().focus(0);
						tblvwStuecke.getSelectionModel().clearSelection();
					});
				}
				// Spalten in der Tableview ein/ausblenden
				if (!(cbxFilterThema.getEditor().getText()).isEmpty())
				{
					tblvwColStckThema.setVisible(true);
				}
				else
				{
					tblvwColStckThema.setVisible(false);
				}
				if (!(cbxFilterWochenlied.getEditor().getText()).isEmpty())
				{
					tblvwColStckWoli.setVisible(true);
				}
				else
				{
					tblvwColStckWoli.setVisible(false);
				}

				if (!(cbxFilterGesangbuch.getEditor().getText()).isEmpty())
				{
					tblvwColStckGesbuch.setVisible(true);
				}
				else
				{
					tblvwColStckGesbuch.setVisible(false);
				}
				if (!(cbxFilterBibel.getEditor().getText()).isEmpty())
				{
					tblvwColStckBibel.setVisible(true);
				}
				else
				{
					tblvwColStckBibel.setVisible(false);
				}
			}

			// ==== Tabreg EDIITONEN filtern

			if (tabregNotenausgaben.isSelected() == true)
			{
				if (filtersammeln == false)
				{// Filterergebnis vom vorigen Mal NICHT sammeln
					oblist_edit.clear();
				}
				List<EditionenlisteModel> listedit = dbStart().getEditionenListeFilter(filternaeditlang, filternaverlag, filterlitnoma);
				listedit.forEach((item) -> oblist_edit.add(item));
				tblvwEditionen.setItems(oblist_edit);
				lblFilterFortschrittsinfo.setText("" + oblist_edit.size() + " Einträge gefunden");
				System.out.println(tblvwEditionen.getSortOrder());
				tblvwEditionen.getSortOrder().add(tblvwColEditLt);// sortieren nach erster Spalte
				Platform.runLater(new Runnable()
				{

					@Override
					public void run()
					{
						tblvwEditionen.requestFocus();
						tblvwEditionen.getSelectionModel().select(0);
						tblvwEditionen.getFocusModel().focus(0);
						tblvwEditionen.getSelectionModel().clearSelection();
					}
				});
				// Spalten in der Tableview ein/ausblenden
				if (!(cbxFilterNotenmappe.getEditor().getText()).isEmpty())
				{
					tblvwColEditNoma.setVisible(true);
				}
				else
				{
					tblvwColEditNoma.setVisible(false);
				}
			}
		}
		catch (

		SQLException e)
		{
			e.printStackTrace();
		}
		// Finally - Bereich
		finally
		{
			clearNaEingabefelder();
			titelgrafikAnzeigen();
			clearStckEingabefelder();
			clearLitEingabefelder();
		}
	}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Allgemeine Filterfunktionen
// ===========================================================================
	void clearFilterfelder()
	{
		txtFilterTitel.setText("");
		txtFilterEdit.setText("");
		txtFilterKomp.setText("");
		txtFilterEdit.setText("");
		txtFilterEditVerlag.setText("");
		txtFilterDicht.setText("");

		cbxFilterWochenlied.getSelectionModel().clearSelection();
		cbxFilterWochenlied.getEditor().setText("");
		cbxFilterThema.getSelectionModel().clearSelection();
		cbxFilterThema.getEditor().setText("");

		cbxFilterStueckart.getSelectionModel().clearSelection();
		cbxFilterStueckart.getEditor().setText("");
		cbxFilterBibel.getSelectionModel().clearSelection();
		cbxFilterBibel.getEditor().setText("");
		cbxFilterGesangbuch.getSelectionModel().clearSelection();
		cbxFilterGesangbuch.getEditor().setText("");
		cbxFilterNotenmappe.getSelectionModel().clearSelection();
		cbxFilterNotenmappe.getEditor().setText("");
	}

	void clearListen()
	{
		int selectedTabindex = 0;
		selectedTabindex = tabpaneStartfenster.getSelectionModel().getSelectedIndex();
		lblFilterFortschrittsinfo.setText("--");
		switch (selectedTabindex)
		{
		case 0: // Literatur
			oblist_lit.clear();
			break;
		case 1: // Notenausgaben
			oblist_edit.clear();
			break;
		case 2: // Stücke Lieder
			oblist_st.clear();
			oblist_stth.clear();
			oblist_stwoli.clear();
			oblist_stbib.clear();
			oblist_stgesb.clear();
			break;
		}
		titelgrafikAnzeigen();
	}

	void clearNaEingabefelder()
	{
		txtNaEditBestnr.setText("");
		txtNaEditErfasst.setText("");
		txtNaEditHrsg.setText("");
		txtNaEditJahr.setText("");
		txtNaEditKurz.setText("");
		txtNaEditLang.setText("");
		txtNaEditSchw.setText("");
		txtNaEditTitelgrafikpfad.setText("");
		txtNaIdCheck.setText("");
		txtNaEditDb.setText("");
		txtaNaEditBeschr.setText("");
		cbxNaEditArt.setValue(null);
		cbxNaEditVerlag.setValue(null);

	}

	void clearLitEingabefelder()
	{
		txtLitEditNr.setText("");
		txtLitEditNrzus.setText("");
		txtLitEditSeite.setText("");
		txtLitEditSeitezus.setText("");
		txtLitEditTitelgrafikpfad.setText("");
		txtLitEditTonart.setText("");
		txtLitIdCheck.setText("");
		txtaLitEditBesetzung.setText("");
		txtLitEditSec.setText("");
		txtLitEditMin.setText("");
		txtLitStckLiedId.setText("");
		cbxLitStueckart.setValue(null);
		cbxLitEditBearb.setValue(null);
		cbxLitEditKomp.setValue(null);
		cbxLitEditNotenausgabe.setValue(null);
		cbxLitEditTitel.setValue(null);
		txtLitDbCheck.setText("");
		titelgrafikAnzeigen();
	}

	void clearStckEingabefelder()
	{
		txtStueckTitel.setText("");
		txtStueckGeaendert.setText("");
		cbxStckEditDichter.setValue(null);
		tblvwStuecke.getSelectionModel().clearSelection();
		ValuesGlobals.geklickteZeileTblView = -1;
		ValuesGlobals.Uebergabewert1 = "";
	}

//========================================================================================================
// Ende Filtervorgänge
//========================================================================================================

//=======================================================================
// Button-Handling (FXML)
//=======================================================================
	@FXML // Button Filtern...
	public void btnFilterAn_OnClick(ActionEvent event) throws Exception
	{
		filtern(0);
	}

	@FXML // Button Filter aus
	public void btnFilterAus_OnClick(ActionEvent event)
	{
		try
		{
			clearFilterfelder();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	public void NomaEdit_OnClick(ActionEvent event)
	{
		try
		{
			cbxFilterNotenmappe.getSelectionModel().clearSelection();
			cbxFilterNotenmappe.getEditor().setText("");
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmNotenmappenEdit.fxml"));
			Parent root = fxmlLoader.load();
			FrmNotenmappenEditController controller = fxmlLoader.getController();
			Stage stModalwindow = new Stage();
			controller.setDb(dbNotenmappen());
			controller.init(stModalwindow); // init in zugeh. EditController!
			stModalwindow.setOnCloseRequest(e -> { // verbessert 01.2026
			});
			stModalwindow.setTitle("Notenmappen bearbeiten...");
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			stModalwindow.setResizable(false);
			stModalwindow.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
			stModalwindow.setScene(new Scene(root));
			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.setUserData(stModalwindow);
			stModalwindow.showAndWait();
			// refresh der notwendigen Combos und Tabellen
			updateComboNotenmappeItems(); // Filtercombo aktualisieren
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void btnAktivitaeten_OnClick(ActionEvent event) throws Exception
	{

		saveFilterToState(); // ALLE Filter speichern
		// btnUpdaten.setVisible(false);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		SceneManager.showAktionen(stage);
	}

//######## TabBereich Lieder Stücke	
//------------------------------------------------------------------------
//7.1.2023 / 04.05.2025
	public void btnStckSpeichern_OnClick() throws Exception
	{
		String titel = txtStueckTitel.getText();

		if (titel == null || titel.trim().isEmpty())
		{
			Msgbox.warn("Titel nicht angegeben", "Der TITEL des neuen Stücks darf nicht leer sein!");
			txtStueckTitel.requestFocus();
			return;
		}

		if (cbxStckEditDichter.getValue() == null)
		{
			Msgbox.show("Bitte Dichter/in auswählen", "Ein DICHTER / eine DICHTERIN  des Stücks muss ausgewählt oder unter Stücke-/Lieder neu erfasst werden!");
			cbxStckEditDichter.requestFocus();
			return;
		}
		String stcktiteledit = "", stckdichter = "", stckdb = "";
		// wer hat den Datensatz eingegeben?
		var selected = tblvwStuecke.getSelectionModel().getSelectedItem();
		if (selected == null || selected.getDbs() == null)
		{
			stckdb = "";
		}
		else
		{
			stckdb = selected.getDbs();
		}

		stckdichter = cbxStckEditDichter.getValue().toString();
		stcktiteledit = txtStueckTitel.getText().toString();
		if (stckdb.equals(ValuesGlobals.zentralEingabe))
		{

			if (Msgbox.yesno("!Zentraler Datensatz!", "Soll der zentral erfasste Datensatz geändert werden?") == true)
			{
				stckdb = ValuesGlobals.zentralEingabeedit;

			}
			else
			{

				return;
			}
		}
		try
		{
			if (stckdb.equals(""))
			{// neuer DS LiedStueck - neue private Eingabe
				stckdb = ValuesGlobals.privatEingabe; // "neu priv.";

				try
				{
					dbStart().setStckNeu(stckdb, stcktiteledit, stckdichter);
					// Erfolgsmeldung optional
					System.out.println("neuer Datensatz gespeichert" + stcktiteledit);
					int NeuerDsId = dbStart().getStckNeuerDs();
					filtern(NeuerDsId);
				}
				catch (SQLException e)
				{
					if (e.getErrorCode() == 19 || e.getMessage().contains("UNIQUE"))
					{
						System.out.println("UNIQUE");
						Msgbox.warn("Titel schon vorhanden!",
								"Dieser Stück-TITEL ist bereits in der Datenbank vorhanden\n"
										+ "Bitte prüfen Sie z.B. durch Filterung, ob es sich wirklich um denselben Titel handelt bzw. geben Sie ergänzende Angaben zu Ihrem neuen Datensatz als Titelbezeichnung ein!");

					}
					else
					{
						e.printStackTrace();
					}
				}
				finally
				{
				}
			}

			else
			{// bestehenden Datensatz LiedrStueck editieren

				String stcktitel = tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel();
				stcktiteledit = txtStueckTitel.getText();
				// 16.10.2025 STück editieren
				// Prüfen, ob private oder originale zentrale Eingabe editiert wird
				if (stckdb.equals(ValuesGlobals.zentralEingabe))
				{
					stckdb = ValuesGlobals.zentralEingabeedit;
				}
				else if (stckdb.equals(ValuesGlobals.privatEingabe))
				{
					stckdb = "edit priv."; // Kennung für editierte private Eingabe
				}
				else if (stckdb.equals(ValuesGlobals.zentralEingabeedit))
				{
					stckdb = ValuesGlobals.zentralEingabeedit; // zentrale Eingabe wird editiert
				}
				else
				{// kommt nicht vor
					stckdb = "edit priv.";
				}
				dbStart().setStckEdit(stckdb, stcktitel, stcktiteledit, stckdichter);

				System.out.println("alter Datensatz editiert gespeichert");
				filtern(0);
				// Cursor wieder setzen
				TableUtils.selectRowById(tblvwStuecke, (stcktiteledit), LiederStueckeModel::getStcktitel);
				// Zeile auslesen und wieder anzeigen
				// Noch ein "später" RunLater, da selectRowById selbst asynchron ist
				Platform.runLater(() -> {
					try
					{
						handleTblvwLit_OnKeyRel();
					}
					catch (Exception e)
					{

						e.printStackTrace();
					}
				});

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		updateCombosLitItems(); // Items neu laden
	}

	@FXML
	public void btnStckNeu_OnClick()// btnLitNeu_OnClick()//neuer ADB-Datensatz
	{
		try
		{
			clearFilterfelder();
			clearListen();
			clearStckEingabefelder();
			txtStueckTitel.requestFocus();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// TODO: 10.2025 Stück löschen
	@FXML // Button DS Stück löschen
	public void btnStckLoeschen_OnClick(ActionEvent event) throws Exception
	{
		System.out.println("Titelcheck1206: " + txtStueckTitel.getText() + " ---");
		if (txtStueckTitel.getText().equals("") || txtStueckTitel.getText() == null)
		{
			Msgbox.warn("Stück/Titel löschen...", "Bitte markieren Sie einen \nSTÜCKTITEL\n für die Löschaktion!");
			return;
		}

		String err = "";
		String lstitel = tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel();
		if (Msgbox.yesno("Lied/Stück löschen...", "Soll das LIED/STÜCK   " + lstitel + "   endgültig gelöscht werden?") == false)
		{
			return;
		}
		else
		{
			String sqlerr = dbStart().setStckLoeschen(lstitel);
			if (!sqlerr.equals(""))
			{
				// System.out.println(sqlerr); // allgemeiner Fehler
				Msgbox.warn("Löschen nicht möglich!", sqlerr);
			}
		}
		System.out.println(err); // allgemeiner Fehler

		tblvwStckBib.getItems().clear(); // Inhalt leeren
		tblvwStckBib.getSelectionModel().clearSelection(); // Selektion aufheben

		tblvwStckGesb.getItems().clear();
		tblvwStckGesb.getSelectionModel().clearSelection();

		tblvwStckThemen.getItems().clear();
		tblvwStckThemen.getSelectionModel().clearSelection();

		tblvwStckWoli.getItems().clear();
		tblvwStckWoli.getSelectionModel().clearSelection();

		filtern(0);
	}

	@FXML
	public void btnStckThemaEdit_OnClick(ActionEvent event) throws Exception
	{

		if (chkDsBearbeiten.isSelected() == false)
		{
			Msgbox.warn("Schreibschutz...", "Datenänderung nicht möglich.\nAktivieren Sie die Checkbox [Daten bearbeiten]!");
			return;
		}
		int aktuelleZeile = tblvwStuecke.getSelectionModel().getSelectedIndex();
		if (aktuelleZeile >= 0)
		{
			ValuesGlobals.geklickteZeileTblView = aktuelleZeile;
			ValuesGlobals.Uebergabewert1 = tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel();
		}
		else
		{
			ValuesGlobals.geklickteZeileTblView = -1;
		}
		try
		{
			// ---------------------------
			// 1️⃣ FXML laden
			// ---------------------------
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmStueckeThemaEdit.fxml"));
			Parent root = fxmlLoader.load();
			// ---------------------------
			// 2️⃣ Controller ermitteln
			// ---------------------------
			FrmStueckeThemaEditController controller = fxmlLoader.getController();
			// ---------------------------
			// 3️⃣ Stage erstellen
			// ---------------------------
			Stage stModalwindow = new Stage();
			controller.setStage(stModalwindow);

			// ---------------------------
			// 45️⃣ DB-Controller an Controller übergeben
			// ---------------------------
			controller.setDbThemen(dbThemen());

			// ---------------------------
			// 5 Controller initialisieren
			// ---------------------------
			controller.init(stModalwindow); // init in zugeh. EditController!

			// 6 Scene und Modal window
			// Einstellungen fpr das modale Fenster
			stModalwindow.setOnCloseRequest(e -> { // verbessert 01.2026
			});
			stModalwindow.setTitle("Themen für Lieder/Stücke bearbeiten...");
			stModalwindow.setResizable(false);
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			stModalwindow.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
			stModalwindow.setScene(new Scene(root));
			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.setUserData(stModalwindow);
			stModalwindow.showAndWait(); // Warten, bis das Fenster wieder geschlossen wird...
			// ---------------------------
			// 7 Nach dem Schließen evtl. Combos oder andere UI updaten
			// ---------------------------
			// ------------------------------
			// Filter anwenden OHNE Auto-Selektieren
			suppressAutoSelection = true; // wichtig, um die Markierung wieder herzustellen //geklickte Zeile merken
			// Comboboxen und Literaturliste aktualisieren
			updateCombosFilterItems(); // Combos neu laden
			if (!ValuesGlobals.geklickteZeileTblView.equals(-1))
			{
				ToolsSelectionRestorer.restoreSelectionWhenReady(tblvwStuecke, ValuesGlobals.geklickteZeileTblView); // geklickte Zeile wieder holen
				handleTblvwStck_OnMouseRel(); // Zeile auch wirklich anklicken
				suppressAutoSelection = false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	public void btnStckGesbEdit_OnClick(ActionEvent event) throws Exception
	{
		if (chkDsBearbeiten.isSelected() == false)
		{
			Msgbox.warn("Schreibschutz...", "Datenänderung nicht möglich.\nAktivieren Sie die Checkbox [Daten bearbeiten]!");
			return;
		}
		int aktuelleZeile = tblvwStuecke.getSelectionModel().getSelectedIndex();
		if (aktuelleZeile >= 0)
		{
			ValuesGlobals.geklickteZeileTblView = aktuelleZeile;
			ValuesGlobals.Uebergabewert1 = tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel();
		}
		else
		{
			ValuesGlobals.geklickteZeileTblView = -1;
		}
		try
		{ // Fenster kann geöffnet werden...
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmStueckeGesbEdit.fxml"));
			Parent root = fxmlLoader.load();
			FrmStueckeGesbuchEditController controller = fxmlLoader.getController();// ###
			Stage stModalwindow = new Stage();
			controller.setDb(dbGesb());
			controller.init(stModalwindow);

			// Werte an das modale Fenster übergeben
			// Einstellungen für das modale Fenster
			stModalwindow.setOnCloseRequest(e -> {
			});
			stModalwindow.setTitle("Gesangbücher für Lieder/Stücke bearbeiten...");
			// stModalwindow.setHeight(520);
			// stModalwindow.setWidth(815);
			stModalwindow.setResizable(false);
			stModalwindow.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
			stModalwindow.setScene(new Scene(root));
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.setUserData(stModalwindow);
			stModalwindow.showAndWait(); // Warten, bis das Fenster wieder geschlossen wird...
			// ------------------------------
			// Filter anwenden OHNE Auto-Selektieren
			suppressAutoSelection = true; // wichtig, um die Markierung wieder herzustellen //geklickte Zeile merken
			// Comboboxen und Literaturliste aktualisieren
			updateCombosFilterItems(); // Combos neu laden
			if (!ValuesGlobals.geklickteZeileTblView.equals(-1))
			{
				ToolsSelectionRestorer.restoreSelectionWhenReady(tblvwStuecke, ValuesGlobals.geklickteZeileTblView); // geklickte Zeile wieder holen
				handleTblvwStck_OnMouseRel(); // Zeile auch wirklich anklicken
				suppressAutoSelection = false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------
	@FXML
	public void btnStckBibEdit_OnClick(ActionEvent event) throws Exception
	{
		if (chkDsBearbeiten.isSelected() == false)
		{
			Msgbox.warn("Schreibschutz...", "Datenänderung nicht möglich.\nAktivieren Sie die Checkbox [Daten bearbeiten]!");
			return;
		}
		int aktuelleZeile = tblvwStuecke.getSelectionModel().getSelectedIndex();
		if (aktuelleZeile >= 0)
		{
			ValuesGlobals.geklickteZeileTblView = aktuelleZeile;
			ValuesGlobals.Uebergabewert1 = tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel();
		}
		else
		{
			ValuesGlobals.geklickteZeileTblView = -1;
		}
		try
		{ // Fenster kan geöffnet werden...
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmStueckeBibEdit.fxml"));
			Parent root = fxmlLoader.load();
			FrmStueckeBibEditController controller = fxmlLoader.getController();// ###
			Stage stModalwindow = new Stage();
			controller.setDb(dbBibel());
			controller.init(stModalwindow); // init in zugeh. EditController!

			// Werte an das modale Fenster übergeben

			// Einstellungen fpr das modale Fenster
			stModalwindow.setOnCloseRequest(e -> { // verbessert 01.2026
			});
			stModalwindow.setTitle("Bibelstellen für Lieder/Stücke bearbeiten...");
			// stModalwindow.setHeight(520);
			// stModalwindow.setWidth(815);
			stModalwindow.setResizable(false);
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			stModalwindow.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
			stModalwindow.setScene(new Scene(root));
			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.setUserData(stModalwindow);
			stModalwindow.showAndWait(); // Warten, bis das Fenster wieder geschlossen wird...
			// ------------------------------
			// System.out.println("Das modale Fenster wurde geschlossen.");
			// Filter anwenden OHNE Auto-Selektieren
			suppressAutoSelection = true; // wichtig, um die Markierung wieder herzustellen //geklickte Zeile merken
			// Comboboxen und Literaturliste aktualisieren
			updateCombosFilterItems(); // Combos neu laden
			if (!ValuesGlobals.geklickteZeileTblView.equals(-1))
			{
				ToolsSelectionRestorer.restoreSelectionWhenReady(tblvwStuecke, ValuesGlobals.geklickteZeileTblView); // geklickte Zeile wieder holen
				handleTblvwStck_OnMouseRel(); // Zeile auch wirklich anklicken
				suppressAutoSelection = false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------
	@FXML
	public void btnStckWoliEdit_OnClick(ActionEvent event) throws Exception
	{
		if (!chkDsBearbeiten.isSelected())
		{
			Msgbox.warn("Schreibschutz...",
					"Datenänderung nicht möglich.\nAktivieren Sie die Checkbox [Daten bearbeiten]!");
			return;
		}

		try
		{
			ValuesGlobals.Uebergabewert1 = txtStueckTitel.getText();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmStueckeWoliEdit.fxml"));
			Parent root = fxmlLoader.load();
			FrmStueckeWoliEditController controller = fxmlLoader.getController();
			Stage stModalwindow = new Stage();
			controller.setDb(dbWochenlied());
			controller.init(stModalwindow); // init in zugeh. EditController!

			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			// stModalwindow.initOwner(((Node) event.getSource()).getScene().getWindow());
			stModalwindow.setTitle("Tage für Wochenlieder bearbeiten...");
			stModalwindow.setResizable(false);
			stModalwindow.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
			stModalwindow.setScene(new Scene(root));
			stModalwindow.setOnCloseRequest(e -> stModalwindow.close());
			stModalwindow.showAndWait();

			// ===== Nach dem Schließen =====
			suppressAutoSelection = true;
			updateCombosFilterItems(); // Combos neu laden

			if (!ValuesGlobals.geklickteZeileTblView.equals(-1))
			{
				ToolsSelectionRestorer.restoreSelectionWhenReady(
						tblvwStuecke, ValuesGlobals.geklickteZeileTblView);
				handleTblvwStck_OnMouseRel();
				suppressAutoSelection = false;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

//########### RegTab Literatur ###########################################
//------------------------------------------------------------------------------
	@FXML
	public void btnLitNeuAdb_OnClick()// btnLitNeu_OnClick()//neuer ADB-Datensatz
	{
		try
		{
			clearFilterfelder();
			clearListen();
			clearLitEingabefelder();
			cbxLitEditTitel.requestFocus();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void btnLitSpeichern_OnClick() throws Exception
	{
		if (txtLitEditNr.getText().matches("\\d+"))
		{
			System.out.println("Nur Zahlen: ");
		}
		else
		{
			txtLitEditNr.setText(StringBeschneiden.extractNumbers(txtLitEditNr.getText()));
			System.out.println("Ungültig – enthält nicht-numerische Zeichen!");
		}

		if (txtLitEditSeite.getText().matches("\\d+"))
		{
			System.out.println("Nur Zahlen: ");
		}
		else
		{
			txtLitEditSeite.setText(StringBeschneiden.extractNumbers(txtLitEditSeite.getText()));
			System.out.println("Ungültig – enthält nicht-numerische Zeichen!");
		}

		if ((cbxLitEditTitel.getValue() == null))
		{
			Msgbox.show("Pflichteingabe fehlt!", "Der TITEL muss ausgewählt oder unter \\\"Stücke-/Lieder\\\" neu erfasst werden!");
			cbxLitEditTitel.requestFocus();
			// cbxLitEditTitel.setBackground(null);;
			return;
		}

		if ((cbxLitEditNotenausgabe.getValue() == null))
		{
			Msgbox.show("Pflichteingabe fehlt!", "Die NOTENAUSGABE muss ausgewählt oder unter \"Notenausgaben\" neu erfasst werden!");
			cbxLitEditNotenausgabe.requestFocus();
			return;
		}

		if ((cbxLitEditKomp.getValue() == null))
		{
			Msgbox.show("Pflichteingabe fehlt!", "Ein KOMPONIST/KOMPONISTIN muss ausgewählt werden!");
			cbxLitEditKomp.requestFocus();
			return;
		}

		if ((cbxLitEditBearb.getValue() == null))
		{
			Msgbox.show("Pflichteingabe fehlt!", "Ein BEARBEITER/BEARBEITERIN muss ausgewählt werden!");
			cbxLitEditBearb.requestFocus();
			return;
		}

		if ((cbxLitStueckart.getValue() == null))
		{
			Msgbox.show("Pflichteingabe fehlt!", "Eine STÜCKART muss ausgewählt werden!");
			cbxLitStueckart.requestFocus();
			return;
		}
		if (txtLitEditNr.getText().equals(""))
		{
			// SOrtiernummer mit dem Teil der eingegebenen Nummer füllen
			txtLitEditNr.setText(StringBeschneiden.extractNumbers(txtLitEditNrzus.getText()));
		}
		else
		{

		}
		if (txtLitEditSeite.getText().equals(""))
		{
			txtLitEditSeite.setText(StringBeschneiden.extractNumbers(txtLitEditSeitezus.getText()));
		}
		else
		{

		}

		String littiteledit = "", litnotenausgedit = "", litkompedit = "", litbearbedit = "", litstueckartedit = "";
		littiteledit = cbxLitEditTitel.getValue().toString();
		litnotenausgedit = cbxLitEditNotenausgabe.getValue().toString();
		litkompedit = cbxLitEditKomp.getValue().toString();
		litbearbedit = cbxLitEditBearb.getValue().toString();
		litstueckartedit = cbxLitStueckart.getValue().toString();
		Integer litdauermin = 0;
		Integer litdauersec = 0;
		if (txtLitEditMin.getText().equals(null) || txtLitEditMin.getText().equals(""))
		{
			litdauermin = 0;
		}
		else
		{

			litdauermin = ToolsWinHelper.parseIntegerWithAlert(txtLitEditMin, "Dauer Minuten");

		}
		if (txtLitEditSec.getText().equals(null) || txtLitEditSec.getText().equals(""))
		{
			litdauersec = 0;
		}
		else
		{
			litdauersec = ToolsWinHelper.parseIntegerWithAlert(txtLitEditSec, "Dauer Sekunden");
		}
		if (litdauermin == null || litdauersec == null)
		{
			return;
		}

		String litgrafikedit = txtLitEditTitelgrafikpfad.getText();

		// --Nummer----------------------------------------------
		Integer litnummeredit = 0;
		String litnummerzusedit = txtLitEditNrzus.getText();
		try
		{
			if (!(txtLitEditNr.getText().equals("")))
			{
				litnummeredit = Integer.parseInt(txtLitEditNr.getText());

			}
			else
			{
				litnummeredit = 0;
			}
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			txtLitDbCheck.setText("Original ADB");
		}

		if (txtLitEditNrzus.getText().length() > 29)
		{
			Msgbox.show("Eingabe Nummern-Zusatz ...", "Die Eingabe ist zu lang und wird auf 30 Zeichen gekürzt");
			litnummerzusedit = litnummerzusedit.substring(0, 28);
		}
		// -- Seitenzahl ---------------------------------------------------------
		Integer litseiteedit = 0;
		String litseitezusedit = txtLitEditSeitezus.getText();
		if (!(txtLitEditSeite.getText().equals("")))
		{
			litseiteedit = Integer.parseInt(txtLitEditSeite.getText());
		}
		else
		{
			litseiteedit = 0;
		}
		if (txtLitEditSeitezus.getText().length() > 29)
		{
			Msgbox.show("Eingabe Seite-Zusatz ...", "Die Eingabe ist zu lang und wird auf 30 Zeichen gekürzt");
			litseitezusedit = litseitezusedit.substring(0, 28);
		}

		// --
		String littonartedit = txtLitEditTonart.getText();
		String litbesetzungedit = txtaLitEditBesetzung.getText();
		String litidedit = txtLitIdCheck.getText();
		String litdb = "";

		// -------------
		// Prüfen, ob private oder originale zentrale Eingabe editiert wird
		if (txtLitDbCheck.getText().equals(ValuesGlobals.zentralEingabe))
		{
			txtLitDbCheck.setText(ValuesGlobals.zentralEingabeedit);
		}
		else if (txtLitDbCheck.getText().equals(ValuesGlobals.privatEingabe))
		{
			txtLitDbCheck.setText(ValuesGlobals.privatEingabeedit); // Kennung für editierte private Eingabe
		}
		else if (txtLitDbCheck.getText().equals(ValuesGlobals.zentralEingabeedit))
		{
			txtLitDbCheck.setText(ValuesGlobals.zentralEingabeedit); // zentrale Eingabe wird editiert
		}
		else
		{// kommt nicht vor

		}

		try
		{
			String idcheck = txtLitIdCheck.getText();
			if (idcheck.equals(""))
			{// neuer DS
				litdb = ValuesGlobals.privatEingabe; // "neu priv.";
				dbStart().setLiteraturNeu(
						litdb,
						littiteledit,
						litnotenausgedit,
						litkompedit,
						litbearbedit,
						litstueckartedit,
						litdauermin,
						litdauersec,
						litgrafikedit,
						litseiteedit,
						litseitezusedit,
						litnummeredit,
						litnummerzusedit,
						littonartedit,
						litbesetzungedit);

				System.out.println("neuer Datensatz gespeichert" + litdb);

				Integer NeuerDsId = dbStart().getLitIdNeuerDs();
				System.out.println(dbStart().getLitIdNeuerDs());
				filtern(NeuerDsId);

			}
			else
			{// editieren
				String editvers = tblvwLiteratur.getSelectionModel().getSelectedItem().getDbkennung();

				if (editvers.equals(ValuesGlobals.zentralEingabe))
				{
					litdb = ValuesGlobals.zentralEingabeedit;
				}
				else if (editvers.equals(ValuesGlobals.privatEingabe))
				{
					litdb = "edit priv.";

				}
				else if (editvers.equals(ValuesGlobals.zentralEingabeedit))
				{
					litdb = ValuesGlobals.zentralEingabeedit;
				}
				else
				{
					litdb = "edit priv.";
				}

				dbStart().setLiteraturEdit(litdb,
						littiteledit,
						litnotenausgedit,
						litkompedit,
						litbearbedit,
						litstueckartedit,
						litdauermin,
						litdauersec,
						litgrafikedit,
						litseiteedit,
						litseitezusedit,
						litnummeredit,
						litnummerzusedit,
						littonartedit,
						litbesetzungedit,
						litidedit);

				System.out.println("alter Datensatz editiert gespeichert" + litdb);
				filtern(0);
				// Cursor wieder setzen
				TableUtils.selectRowById(tblvwLiteratur, Integer.parseInt(idcheck), LiteraturlisteModel::getId);
				// Zeile auslesen und wieder anzeigen
				// Noch ein "später" RunLater, da selectRowById selbst asynchron ist
				Platform.runLater(() -> {
					try
					{
						handleTblvwLit_OnKeyRel();
					}
					catch (Exception e)
					{

						e.printStackTrace();
					}
				});

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@FXML // Button DS Literatur löschen
	public void btnLitLoeschen_OnClick(ActionEvent event) throws Exception
	{
		System.out.println("ÖidIdCheck: " + txtLitIdCheck.getText() + " ---");
		if (txtLitIdCheck.getText().equals("") || txtLitIdCheck.getText() == null)
		{
			Msgbox.show("Literatureintrag löschen...", "Bitte markieren Sie einen \nLITERATUREINTRAG \nfür die Löschaktion!");
			return;
		}
		String err = "";
		int literaturid = Integer.parseInt(txtLitIdCheck.getText());
		String literatureintrag = tblvwLiteratur.getSelectionModel().getSelectedItem().getLittitel();
		if (Msgbox.yesno("Literatur löschen...", "Soll der Literaturdatensatz  " + literatureintrag + "\nendgültig gelöscht werden?") == false)// msgLoeschen.getBtn().equals(MsgboxBtnType.NO))
		{
			return;
		}
		err = dbStart().fctLiteratureintragLoeschen(literaturid); // setEditionLoeschen(edit);
		System.out.println(err);
		filtern(0);
		TableUtils.selectRowById(tblvwLiteratur, 0);
		// Zeile auslesen und wieder anzeigen
		// Noch ein "später" RunLater, da selectRowById selbst asynchron ist
		Platform.runLater(() -> {
			try
			{
				handleTblvwLit_OnKeyRel();
			}
			catch (Exception e)
			{

				e.printStackTrace();
			}
		});
	}

	@FXML // Button ... von Literatur zu Stücke-Register wechseln
	public void btnLitZuStueckeRegister_OnClick(ActionEvent event) throws Exception
	{
		tabpaneStartfenster.getSelectionModel().select(tabregStck);
	}

	@FXML // Button ... von Literatur zu Stücke-Register wechseln
	public void btnLitZuNotenausgabeRegister_OnClick(ActionEvent event) throws Exception
	{
		tabpaneStartfenster.getSelectionModel().select(tabregNotenausgaben);
	}

	// TODO 2025-11-22
	@FXML
	public void btnKomponistEdit_OnClick(ActionEvent event) throws Exception
	{
		if (chkDsBearbeiten.isSelected() == false)
		{
			Msgbox.warn("Schreibschutz...", "Datenänderung nicht möglich.\nAktivieren Sie die Checkbox [Daten bearbeiten]!");
			return;
		}
		try
		{ // Fenster kan geöffnet werden...

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmAutorenEdit.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stModalwindow = new Stage();
			// Werte an das modale Fenster übergeben
			FrmAutorenEditController controller = fxmlLoader.getController();
			// 👇 DB + Stage übergeben
			controller.setDb(dbAutoren()); // neu 02/2026
			controller.init(stModalwindow);// init() baut DB auf und lädt Tabelle
			stModalwindow.setOnCloseRequest(e -> {
				e.consume();
				try
				{
					stModalwindow.close();

				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

			});
			stModalwindow.setUserData(stModalwindow);
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			stModalwindow.setTitle("Autorinnen und Autoren bearbeiten...");
			stModalwindow.setResizable(false);
			stModalwindow.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
			stModalwindow.setScene(new Scene(root));
			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.showAndWait();
			// beim Zurückkehren aus dem Modalen Fenster refresh

			Platform.runLater(() -> txtFilterTitel.requestFocus());

			updateCombosFilterItems(); // Combos neu laden
			updateCombosLitItems(); // Items neu laden
			updateCombosStckDichter();
			clearStckEingabefelder();
			cbxStckEditDichter.getSelectionModel().clearSelection();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	public void btnStueckartEdit_OnClick(ActionEvent event)
	{
		if (!chkDsBearbeiten.isSelected())
		{
			Msgbox.warn("Schreibschutz...",
					"Datenänderung nicht möglich.\nAktivieren Sie die Checkbox [Daten bearbeiten]!");
			return;
		}

		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmStueckartEdit.fxml"));
			Parent root = fxmlLoader.load();
			FrmStckartenEditController controller = fxmlLoader.getController();

			Stage stModalwindow = new Stage();
			// 🔹 Owner setzen → KEIN extra Taskleisten-Icon mehr
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			controller.setDb(dbStueckart());
			controller.init(stModalwindow); // init() baut DB auf und lädt Tabelle
			stModalwindow.setWidth(740);
			stModalwindow.setHeight(400);
			stModalwindow.setResizable(false);
			stModalwindow.setScene(new Scene(root));
			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.showAndWait();
			// beim Zurückkehren aus dem Modalen Fenster refresh
			updateCombosFilterItems(); // Combos neu laden
			updateCombosLitItems(); // Items neu laden
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
//####### Tabreg NOTENAUSGABEN ################################################
//--------------------------------------------------

	@FXML
	public void btnNaNeu_OnClick()// btnLitNeu_OnClick()//neuer ADB-Datensatz
	{
		try
		{
			// clearFilterfelder();
			// clearListen();
			tblvwEditionen.getSelectionModel().clearSelection();

			clearNaEingabefelder();
			txtNaEditLang.requestFocus();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML // Button DS Notenausgaben speichern
	public void btnNaSpeichern_OnClick(ActionEvent event) throws Exception
	{
		String lt = "", kt = "", hrsg = "", verlag = "", beschr = "", edart = "",
				bestnr = "", grafik = "", editjahr = "", schwierig = "", erfasst = "", db = "", idcheck = "", ltalt = "";
		int idAlt = 0;
		int idNeuEdit = 0;

		// tblvwEditionen.getSortOrder().clear();
		EditionenlisteModel selected = tblvwEditionen.getSelectionModel().getSelectedItem();
		if (selected != null)
		{
			ltalt = selected.getLt();
		}

		if ((txtNaEditLang.getText() == "") || (txtNaEditKurz.getText() == ""))
		{
			Msgbox.show("Eingabe unvollständig...", "Bitte geben Sie eine eindeutige Bezeichnung\n"
					+ "für die neue NOTENAUSGABE/EDITION ein!\n\n" + "Erforderlich sind mindestens\n eine Langbezeichnung und eine Kurzbezeichnung.");
			txtNaEditKurz.requestFocus();
			return;
		}
		if ((cbxNaEditVerlag.getValue() == null))
		{
			Msgbox.show("Eingabe unvollständig...", "Bitte einen VERLAG auswählen!");
			cbxNaEditVerlag.requestFocus();
			return;
		}
		if ((cbxNaEditArt.getValue() == null))
		{
			Msgbox.show("Eingabe unvollständig...", "Bitte eine EDITIONS-ART\nauswählen!");
			cbxNaEditArt.requestFocus();
			return;
		}
		lt = txtNaEditLang.getText();
		kt = txtNaEditKurz.getText();
		hrsg = txtNaEditHrsg.getText();
		beschr = txtaNaEditBeschr.getText();
		bestnr = txtNaEditBestnr.getText();
		grafik = txtNaEditTitelgrafikpfad.getText();
		editjahr = txtNaEditJahr.getText();
		schwierig = txtNaEditSchw.getText();
		idcheck = txtNaIdCheck.getText();
		if (!idcheck.equals(""))
		{
			idAlt = Integer.parseInt(txtNaIdCheck.getText());
		}

		verlag = cbxNaEditVerlag.getValue().toString();
		edart = cbxNaEditArt.getValue().toString();
		erfasst = txtNaEditErfasst.getText();
		db = txtNaEditDb.getText();
		erfasst = new AktuellesDatum().getDateAsString();
		// TODO 22.01.2026
		// Speichervorgang
		if (txtNaIdCheck.getText() == "" || txtNaIdCheck.getText().isBlank())// .getText() == null)
		{// neuer Datensatz
			db = ValuesGlobals.privatEingabe;
			// int neuDs = 1;
			dbStart().setNotenausgabeNeu(lt, kt, verlag, hrsg, bestnr, edart, editjahr, schwierig, grafik, beschr, erfasst, db);
			// Erfolgsmeldung optional
			System.out.println("neuer Datensatz gespeichert" + lt);
			idNeuEdit = dbStart().getStckNeuerDs();
		}
		else
		{// Datensatz ändern

			if (db.equals(ValuesGlobals.zentralEingabe))
			{
				db = ValuesGlobals.zentralEingabeedit;
			}
			else if (db.equals(ValuesGlobals.privatEingabe))
			{
				db = ValuesGlobals.privatEingabeedit;
			}
			else
			{
				db = ValuesGlobals.privatEingabeedit;
			}
			dbStart().setNotenausgabeEditSpeichern(lt, kt, verlag, hrsg, bestnr, edart, editjahr, schwierig, grafik, beschr, erfasst, db, idAlt, ltalt); // ( kt, verlag, hrsg,
																																							// bestnr,
			idNeuEdit = idAlt;
		}
		// neu einlesen und Tabelle scrollen
		updateCombosFilterItems();
		updateCombosLitItems();

		filtern(0);
		// ObservableList<EditionenlisteModel> items = tblvwEditionen.getItems();
		selectEditionById(idNeuEdit);
		btnNaNeu.requestFocus();

		// Cursor wieder setzen
		TableUtils.selectRowById(tblvwEditionen, idNeuEdit, EditionenlisteModel::getId);
		System.out.println("Curror wieder setzen " + idNeuEdit);
		// Zeile auslesen und wieder anzeigen
		// Noch ein "später" RunLater, da selectRowById selbst asynchron ist
		Platform.runLater(() -> {
			try
			{
				handleTblvwNa_OnKeyRel();
			}
			catch (Exception e)
			{

				e.printStackTrace();
			}
		});

	}

	@FXML // Button DS löschen
	public void btnNaLoeschen_OnClick(ActionEvent event) throws Exception
	{
		int id = 0;
		if (txtNaIdCheck.getText().equals("") || txtNaIdCheck.getText().equals(null))
		{
			Msgbox.show("Datensatz löschen..", "Bitte markieren Sie eine \nNOTENAUSGABE\nfür die Löschaktion!");
			return;
		}
		String edit = tblvwEditionen.getSelectionModel().getSelectedItem().getLt();
		if (Msgbox.yesno("Datensatz löschen...", "Soll die NOTENAUSGABE   [" + edit + "]  endgültig gelöscht werden?\n"
				+ "!!! ACHTUNG !!! --- Es werden auch alle zugehörigen Literatureinträge gelöscht!") == false)
		{
			return;
		}
		else
		{
			id = Integer.parseInt(txtNaIdCheck.getText());
			dbStart().deleteNotenausgabe(id);
		}
		updateCombosLitItems(); // Items neu laden
		updateCombosFilterItems();
		filtern(0);
		selectEditionById(id);
		btnNaNeu.requestFocus();

	}

	private void selectEditionById(int id)
	{
		for (EditionenlisteModel m : tblvwEditionen.getItems())
		{
			if (m.getId() == id)
			{
				tblvwEditionen.getSelectionModel().select(m);
				tblvwEditionen.scrollTo(m);

				return;
			}
		}
	}

	@FXML
	void handleKeyPressOk(KeyEvent event)
	{
		switch (event.getCode())
		{
		case ENTER:
			try
			{
				btnFilterAn.requestFocus();
				filtern(0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			break;
		case LEFT:
			//
			break;
		default:
			break;
		}
	}

	@FXML // Button Filter aus und Listen leeren
	public void btnListenLeer_OnClick(ActionEvent event)
	{
		try
		{
			System.out.println("Listen leeren");
			btnFilterAus_OnClick(event); // clear Filterfelder
			clearFilterfelder();
			clearListen();
			clearLitEingabefelder();
			clearNaEingabefelder();
			clearStckEingabefelder();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	public void handleTblvwEditionen_OnSort()
	{

	}

	@FXML // Titelgrafikpfad auswählen...
	public void btnNaGrafikwahl_OnClick() throws Exception
	{
		grafikwahlBox();
	}

	public void grafikwahlBox() throws Exception
	{
		try
		{
			String pathFile;
			fileAuswahlbox.setTitle("Bitte die Grafikdatei auswählen...");
			fileAuswahlbox.setInitialDirectory(new File((ValuesGlobals.progPfadGrafik)));
			fileAuswahlbox.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"),
					new FileChooser.ExtensionFilter("BMP", "*.bmp"), new FileChooser.ExtensionFilter("Alle Dateien", "*.*"));

			File selectedFile = fileAuswahlbox.showOpenDialog(stage);
			if (selectedFile == null)
			{
				return;
			}
			pathFile = selectedFile.toString();
			System.out.println(pathFile.substring(ValuesGlobals.progPfadGrafik.length(), selectedFile.toString().length()));
			pathFile = (pathFile.substring(ValuesGlobals.progPfadGrafik.length(), selectedFile.toString().length()));
			if (txtNaEditTitelgrafikpfad.getLength() > 0)
			{
				if (Msgbox.yesno("Grafikpfad neu setzen", "Soll der bestehende Pfad zur Grafik überschrieben werden?") == true)

				{
					txtNaEditTitelgrafikpfad.setText(pathFile);
				}

			}
			else
			{
				txtNaEditTitelgrafikpfad.setText(pathFile);
			}
			titelgrafikAnzeigen();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Msgbox.show("Titelgrafikpfad ...", "Es ist ein Fehler bei der Auswahl der Titelgrafik aufgetreten\n"
					+ "Bitte prüfen Sie den Installationspfad!\n\n" + e.getMessage() + e);
		}
	}

	@FXML // Verlag editieren auswählen...
	public void btnVerlagEdit_OnClick()
	{

		if (chkDsBearbeiten.isSelected() == false)
		{
			Msgbox.warn("Schreibschutz...", "Datenänderung nicht möglich.\nAktivieren Sie die Checkbox [Daten bearbeiten]!");
			return;
		}

		try
		{ // Fenster kan geöffnet werden...
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmVerlagEdit.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Scene scene = new Scene(root);
			Stage stModalwindow = new Stage();
			// Werte an das modale Fenster übergeben
			FrmVerlagEditController controller = fxmlLoader.getController();// ###
			controller.setDb(dbVerlag());
			controller.setStage(stModalwindow);

			stModalwindow.setOnCloseRequest(e -> {
				e.consume();
				try
				{
					stModalwindow.close();

				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

			});
			stModalwindow.setTitle("Verlage bearbeiten...");
			stModalwindow.setResizable(false);
			stModalwindow.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
			stModalwindow.setScene(scene);
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.setUserData(stModalwindow);
			stModalwindow.showAndWait(); // Warten, bis das Fenster wieder geschlossen wird...
			// ------------------------------
			updateCombosNaIndiv(); // nur Items aktualisieren
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML // Verlag editieren auswählen...
	public void btnEdionsartEdit_OnClick()
	{

		if (chkDsBearbeiten.isSelected() == false)
		{
			Msgbox.warn("Schreibschutz...", "Datenänderung nicht möglich.\nAktivieren Sie die Checkbox [Daten bearbeiten]!");
			return;
		}

		try
		{ // Fenster kan geöffnet werden...
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmEditionsartEdit.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Scene scene = new Scene(root);
			Stage stModalwindow = new Stage();
			// Werte an das modale Fenster übergeben
			FrmEditionsartController controller = fxmlLoader.getController();// ###
			controller.setDb(dbEditionsart());
			controller.setStage(stModalwindow);

			stModalwindow.setOnCloseRequest(e -> {
				e.consume();
				try
				{
					stModalwindow.close();

				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

			});
			stModalwindow.setTitle("Editionsarten bearbeiten...");
			stModalwindow.setResizable(false);
			stModalwindow.initOwner(btnBeenden.getScene().getWindow());
			stModalwindow.getIcons().add(new Image("/icons/javafx/jpcndicon0016.png"));
			stModalwindow.setScene(scene);
			stModalwindow.initModality(Modality.APPLICATION_MODAL);
			stModalwindow.setUserData(stModalwindow);
			stModalwindow.showAndWait(); // Warten, bis das Fenster wieder geschlossen wird...
			// ------------------------------
			updateCombosNaIndiv(); // nur Items aktualisieren
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

//############################### Updaten #############################################
	public void fctUpdaten() throws IOException
	{
		DatabaseUpdaten.fctDatabaseupdate(btnBeenden.getScene().getWindow());

	}

////############################### Drucken #############################################
//	 Drucken aus dem Hauptfenster...
	@FXML
	public void btnListeDrucken_OnClick()
	{

		if (tabregNotenausgaben.isSelected())
		{
			// 🔥 Sortierspalte

			printTable(tblvwEditionen, "Notenausgaben", opt -> {
				opt.excludedColumns.add(tblvwColEditId);
				opt.excludedColumns.add(tblvwColEditAusDb);
				opt.excludedColumns.add(tblvwColEditHrsg);
				opt.defaultGroupByColumn = tblvwColEditVerlag;
				opt.defaultGroupHeaderPrefix = " grp nach Verlag";
			});

		}
		else if (tabregLiteratur.isSelected())
		{

			printTable(tblvwLiteratur, "Literaturliste", opt -> {
				opt.excludedColumns.add(tblvwColErfasst);
				opt.excludedColumns.add(tblvwColDb);
				opt.defaultGroupByColumn = tblvwColEdit;
				opt.defaultGroupHeaderPrefix = " grp Notenausgabe";
			});

		}
		else if (tabregStck.isSelected())
		{

			printTable(tblvwStuecke, "Lieder und Stücke", opt -> {
				opt.excludedColumns.add(tblvwColStckErfasst);
				opt.excludedColumns.add(tblvwColStckDb);
				opt.defaultGroupByColumn = tblvwColStckDicht;
				opt.defaultGroupHeaderPrefix = " grp DichterIn";
			});

		}
		else
		{
			Msgbox.warn("Drucken", "Keine Liste ausgewählt.");
		}
	}

	private <T> void printTable(
			TableView<T> table,
			String titel,
			Consumer<PdfExportOptions<T>> customizer)
	{
		if (table.getItems().isEmpty())
		{
			Msgbox.show(titel + " drucken...",
					"Es sind keine Daten gefiltert worden.\nWählen Sie bitte andere Filterkriterien.");
			return;
		}

		PdfExportOptions<T> opt = PdfExportOptions.defaultsLiteraturlisten();

		// 🔥 Sortierspalte
		TableColumn<T, ?> sortCol = getPrimarySortColumn(table);

		// 🔹 Gruppierung bestimmen
		if (sortCol != null)
		{
			titel += " grp nach " + sortCol.getText();
			opt.groupByColumn = sortCol;
			opt.groupHeaderPrefix = sortCol.getText() + ": ";
		}
		else if (opt.defaultGroupByColumn != null)
		{
			opt.groupByColumn = opt.defaultGroupByColumn;
			opt.groupHeaderPrefix = opt.defaultGroupHeaderPrefix;
		}
		else if (!table.getColumns().isEmpty())
		{
			// Fallback, falls keine sortierte Spalte: erste sichtbare Spalte
			TableColumn<T, ?> firstCol = table.getColumns().get(0);
			opt.groupByColumn = firstCol;
			opt.groupHeaderPrefix = "";// firstCol.getText() + ": ";
			Msgbox.show("Liste als PDF ausdrucken ...", "Das Erstellen eines Ausdrucks ist nur möglich, wenn eine Spaltensortierung aktiv ist!\n\n"
					+ "Bitte sortieren Sie die Liste durch Mausklick auf einen Spaltenkopf \nund starten Sie den Druck erneut.");
			return;
		}

		// individuelle Anpassung
		customizer.accept(opt);

		// Metadaten
		opt.title = titel;
		opt.subtitle = "Datenbank: " + new File(ValuesGlobals.dbPfad).getName();

		// Datei
		String date = LocalDate.now().toString();
		String pdfFileName = PdfFilenameUtil.toSafePdfName(titel, date);
		File pdfFile = PdfPathUtil.resolvePdfPath(pdfFileName);

		runExportTask(table, pdfFile, opt);
	}

	private <T> void runExportTask(
			TableView<T> table,
			File pdfFile,
			PdfExportOptions<T> opt)
	{
		Task<Void> exportTask = new Task<>()
		{
			@Override
			protected Void call() throws Exception
			{
				PdfTableExporter.druckenLiteraturlisten(
						table,
						pdfFile,
						opt,
						progress -> updateProgress(progress, 1.0));
				return null;
			}
		};

		lblStatusPdf.setVisible(true);
		progressPdf.visibleProperty().bind(exportTask.runningProperty());
		progressPdf.progressProperty().bind(exportTask.progressProperty());

		exportTask.setOnSucceeded(e -> {
			cleanupProgress();

			openPdfAsync(pdfFile);
		});

		exportTask.setOnFailed(e -> {
			cleanupProgress();

			Msgbox.warn("PDF-Export fehlgeschlagen",
					exportTask.getException().getMessage());
		});

		new Thread(exportTask, "PDF-Export-Thread").start();
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

//=======================================================================
// Ende Button-Handling (FXML)
//=======================================================================

	@FXML
	public void handleTabChanged() throws Exception
	{
		Boolean filterGesetzt = false;

		// Sicherstellen, dass die GUI fertig geladen ist
		if (txtNaEditBestnr == null)
			return;
		// Feststellen, ob irgend ein Filter eingetragen ist, wenn ja kann gefiltert
		// werden beim Wechsel

		Tab selectedTab = tabpaneStartfenster.getSelectionModel().getSelectedItem();

		// Event nur für den aktuell ausgewählten Tab ausführen
		if (!selectedTab.isSelected())
			return;

		int selectedTabindex = tabpaneStartfenster.getSelectionModel().getSelectedIndex();
		// Zentrale Reset-Logik: Alle Tabellen & Eingabefelder vorbereiten
		tblvwLiteratur.getSelectionModel().clearSelection();
		tblvwEditionen.getSelectionModel().clearSelection();
		tblvwStuecke.getSelectionModel().clearSelection();
		clearLitEingabefelder();
		clearNaEingabefelder();
		clearStckEingabefelder();
		oblist_stth.clear();
		oblist_stwoli.clear();
		oblist_stbib.clear();
		oblist_stgesb.clear();

		// Je nach Tab die Steuerelemente konfigurieren
		switch (selectedTabindex)
		{
		case 0: // Literatur
			// oblist_lit.clear();
			filterGesetzt = fctFilterzustand(0);
			lblHaupttitel.setText("Literatur - Gesamtliste");
			lblFilterFortschrittsinfo.setText(oblist_lit.size() + " Einträge gefunden");// + " Literatureinträge angezeigt (maximal " + ValuesGlobals.filtermax + ")");

			// Textfelder
			txtFilterTitel.setDisable(false);
			txtFilterKomp.setDisable(false);
			txtFilterEditVerlag.setDisable(false);
			txtFilterDicht.setDisable(false);
			txtFilterEdit.setDisable(false);

			// ComboBoxen
			cbxFilterStueckart.setDisable(false);
			cbxFilterWochenlied.setDisable(false);
			cbxFilterThema.setDisable(false);
			cbxFilterNotenmappe.setDisable(false);
			cbxFilterBibel.setDisable(true);
			cbxFilterGesangbuch.setDisable(true);
			btnNotenmappen.setDisable(false);

			// Fokus setzen
			Platform.runLater(() -> txtFilterTitel.requestFocus());

			break;

		case 1: // Notenausgaben
			filterGesetzt = fctFilterzustand(1);
			// oblist_edit.clear();
			lblHaupttitel.setText("Notenausgaben");
			lblFilterFortschrittsinfo.setText(oblist_edit.size() + " Einträge gefunden");// " Editionen (Notenausgaben) gefiltert (maximal " + ValuesGlobals.filtermax +
																							// ")");

			txtFilterTitel.setDisable(true);
			txtFilterKomp.setDisable(true);
			txtFilterEditVerlag.setDisable(false);
			txtFilterDicht.setDisable(true);
			txtFilterEdit.setDisable(false);

			cbxFilterStueckart.setDisable(true);
			cbxFilterWochenlied.setDisable(true);
			cbxFilterThema.setDisable(true);
			cbxFilterNotenmappe.setDisable(false);
			cbxFilterBibel.setDisable(true);
			cbxFilterGesangbuch.setDisable(true);
			btnNotenmappen.setDisable(false);
			// Fokus setzen
			Platform.runLater(() -> txtFilterEdit.requestFocus());
			break;

		case 2: // Stücke/Lieder
			filterGesetzt = fctFilterzustand(2);
			// oblist_st.clear();
			lblHaupttitel.setText("Stücke/Lieder");
			lblFilterFortschrittsinfo.setText(oblist_st.size() + " Einträge gefunden");// + " Stücke und Lieder gefiltert (maximal " + ValuesGlobals.filtermax + ")");

			txtFilterTitel.setDisable(false);
			txtFilterKomp.setDisable(true);
			txtFilterEditVerlag.setDisable(true);
			txtFilterDicht.setDisable(false);
			txtFilterEdit.setDisable(true);

			cbxFilterStueckart.setDisable(true);
			cbxFilterWochenlied.setDisable(false);
			cbxFilterThema.setDisable(false);
			cbxFilterNotenmappe.setDisable(true);
			cbxFilterBibel.setDisable(false);
			cbxFilterGesangbuch.setDisable(false);
			btnNotenmappen.setDisable(true);
			Platform.runLater(() -> txtFilterTitel.requestFocus());
			break;
		}
		if (filterGesetzt == true)
		{// wenn mindestens 1 Filterfeld gefüllt ist, dann beim Tabwechsel neu filtern
			filtern(0);
		}

	}

	private boolean fctFilterzustand(int tab)
	{
		boolean filtern = true;
		if (tab == 0)
		{
			if (cbxFilterNotenmappe.getEditor().getText().equals("")
					&& cbxFilterStueckart.getEditor().getText().equals("")
					&& cbxFilterThema.getEditor().getText().equals("")
					&& cbxFilterWochenlied.getEditor().getText().equals("")
					&& txtFilterDicht.getText().equals("")
					&& txtFilterEdit.getText().equals("")
					&& txtFilterEditVerlag.getText().equals("")
					&& txtFilterKomp.getText().equals("")
					&& txtFilterTitel.getText().equals(""))
			{
				filtern = false;
			}
		}

		else if (tab == 1)
		{
			if (cbxFilterNotenmappe.getEditor().getText().equals("")
					&& txtFilterEdit.getText().equals("")
					&& txtFilterEditVerlag.getText().equals(""))
			{
				filtern = false;

			}

		}
		else if (tab == 2)
		{
			if (cbxFilterBibel.getEditor().getText().equals("")
					&& cbxFilterGesangbuch.getEditor().getText().equals("")
					&& cbxFilterThema.getEditor().getText().equals("")
					&& cbxFilterWochenlied.getEditor().getText().equals("")
					&& txtFilterDicht.getText().equals("")
					&& txtFilterTitel.getText().equals(""))
			{
				filtern = false;

			}

		}
		else
		{
			filtern = true;

		}
		return filtern;

	}

//------- Ende  Focuswechsel der Registertabs---------------------------------------------------------------

//################################# Table View Handles #################################	
// == TableView Literatur Handles =======================
	// Hilfsmethode für sichere Integer-Konvertierung

	// == TableView Literatur Handles =======================
	public void handleTblvwLit_OnMouseRel() throws Exception
	{
		try
		{
			if (!oblist_lit.isEmpty())
			{ // nur, wenn in tblView eine Zeile vorhanden ist
				LiteraturlisteModel selected = tblvwLiteratur.getSelectionModel().getSelectedItem();
				if (selected == null)
					return;
				updateCombosLitItems(); // Items neu laden
				txtLitIdCheck.setText(safeIntToString(selected.getId()));
				txtLitDbCheck.setText(selected.getDbkennung());
				txtLitEditNr.setText(safeIntToString(selected.getNummersort()));
				txtLitEditSeite.setText(safeIntToString(selected.getSeitesort()));
				txtLitEditNrzus.setText(selected.getLitnr());
				txtLitEditSeitezus.setText(selected.getLitseite());
				txtLitEditTitelgrafikpfad.setText(selected.getLitgrafik());
				txtLitEditTonart.setText(selected.getLittonart());
				txtaLitEditBesetzung.setText(selected.getLitbesetzung());
				txtLitEditMin.setText(safeIntToString(selected.getLitmin()));
				txtLitEditSec.setText(safeIntToString(selected.getLitsec()));

				ToolsWinHelper.autoSelectComboBoxValue(cbxLitEditTitel, selected.getLittitel(),
						(cbxControl, val) -> cbxControl.getStcktitel().equals(val));
				ToolsWinHelper.autoSelectComboBoxValue(cbxLitStueckart, selected.getLitstueckart(),
						(cbxControl, val) -> cbxControl.getBez().equals(val));
				ToolsWinHelper.autoSelectComboBoxValue(cbxLitEditKomp, selected.getLitkomp(),
						(cbxControl, val) -> cbxControl.getAautor().equals(val));
				ToolsWinHelper.autoSelectComboBoxValue(cbxLitEditBearb, selected.getLitbearb(),
						(cbxControl2, val) -> cbxControl2.getAautor().equals(val));
				ToolsWinHelper.autoSelectComboBoxValue(cbxLitEditNotenausgabe, selected.getLitedit(),
						(cbxControl3, val) -> cbxControl3.getLt().equals(val));

				titelgrafikAnzeigen();
			}
		}
		finally
		{
			// optional: Fehler-Logging oder GUI-Aufräumarbeiten
		}
	}

// == TableView StückeLieder Handles =======================
	// ergänzt 2.1.2023
	public void handleTblvwStck_OnMouseRel() throws Exception
	{// Mausklick auf die Gridllist Tableview -- Combobox Zeile auswählen
		try
		{
			if (!oblist_st.isEmpty()) // nur, wenn in tblView eine Zeile vorhanden ist
			{
				updateCombosStckDichter();
				// alles Einlesen, aber nur, wenn tblView einen Inhalt hat
				txtStueckTitel.setText(tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel());
				txtStueckGeaendert.setText(tblvwStuecke.getSelectionModel().getSelectedItem().getStckerfasst());
				String dichAuswahl = tblvwStuecke.getSelectionModel().getSelectedItem().getStckdicht();
				ToolsWinHelper.autoSelectComboBoxValue(cbxStckEditDichter, dichAuswahl, (cbxControl, val) -> cbxControl.getAautor().equals(val));

				List<LiederStueckeThemenModel> list_liestckthe = dbStart().getStueckinfosTh(tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel());
				oblist_stth.setAll(list_liestckthe);
				tblvwStckThemen.getSortOrder().clear(); // verhindert automatische Sortierung durch TableView
				tblvwStckThemen.setItems(oblist_stth);

				List<LiederStueckeWoliModel> list_liestckwoli = dbStart().getStueckinfosWoli(tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel());
				oblist_stwoli.setAll(list_liestckwoli);
				tblvwStckWoli.getSortOrder().clear(); // verhindert automatische Sortierung durch TableView
				tblvwStckWoli.setItems(oblist_stwoli);

				List<LiederStueckeGesbModel> list_liestckgesb = dbStart().getStueckinfosGesb(tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel());
				oblist_stgesb.setAll(list_liestckgesb);
				tblvwStckGesb.getSortOrder().clear(); // verhindert automatische Sortierung durch TableView
				tblvwStckGesb.setItems(oblist_stgesb);

				List<LiederStueckeBibModel> listliestckbib = dbStart().getStueckinfosBib(tblvwStuecke.getSelectionModel().getSelectedItem().getStcktitel());
				oblist_stbib.setAll(listliestckbib);
				tblvwStckBib.getSortOrder().clear(); // verhindert automatische Sortierung durch TableView
				tblvwStckBib.setItems(oblist_stbib);

				// dbController.dbStop();

			}

		}

		finally
		{

		}
	}

//== TableView Notenausgaben Handles =======================
	public void handleTblvwNa_OnMouseRel() throws Exception
	{// Mausklick auf die Gridllist Tableview -- Zeile ausw�hlen und Image
		// anzeigen
		var sel = tblvwEditionen.getSelectionModel().getSelectedItem();

		if (sel == null)
		{
			clearNaEditFields();
			return;
		}
		updateCombosNaIndiv();
		txtNaEditLang.setText(sel.getLt());
		txtNaEditKurz.setText(sel.getKt());
		txtaNaEditBeschr.setText(sel.getBesch());
		txtNaEditBestnr.setText(sel.getBestnr());
		txtNaEditErfasst.setText(sel.geterfasst().substring(0, 16));
		txtNaEditHrsg.setText(sel.getHrsg());
		txtNaEditJahr.setText(sel.getEdjahr());
		txtNaEditSchw.setText(sel.getSchwierig());
		txtNaEditTitelgrafikpfad.setText(sel.getTitelgrafikpfad());
		txtNaIdCheck.setText(String.valueOf(sel.getId()));
		txtNaEditDb.setText(sel.getDbkedit());
		ToolsWinHelper.autoSelectComboBoxValue(
				cbxNaEditVerlag,
				sel.getVerlag(),
				(cbxControl, val) -> cbxControl.getVverlag().equals(val));
		ToolsWinHelper.autoSelectComboBoxValue(
				cbxNaEditArt,
				sel.getEdart(),
				(cbxControl, val) -> cbxControl.getEdart().equals(val));
		titelgrafikAnzeigen();
	}

	private void clearNaEditFields()
	{
		txtNaEditLang.clear();
		txtNaEditKurz.clear();
		txtaNaEditBeschr.clear();
		txtNaEditBestnr.clear();
		txtNaEditErfasst.clear();
		txtNaEditHrsg.clear();
		txtNaEditJahr.clear();
		txtNaEditSchw.clear();
		txtNaEditTitelgrafikpfad.clear();
		txtNaIdCheck.clear();
	}

	// -----------------------------------------------------------------------------------------
	// Cursor auf der Tableview löst MausRel aus...
	public void handleTblvwNa_OnKeyRel() throws Exception
	{
		handleTblvwNa_OnMouseRel();
	}

	public void handleTblvwLit_OnKeyRel() throws Exception
	{
		handleTblvwLit_OnMouseRel();
	}

	public void handleTblvwStck_OnKeyRel() throws Exception
	{
		handleTblvwStck_OnMouseRel();
	}
//############################ Ende Table View Handles #################################	

//==============================================================================
//                MENUELEISTE
//==============================================================================
	@FXML
	public void men01Beenden_OnClick()
	{
		saveFilterToState();
		if (!Msgbox.yesno("Programm beenden", "Möchten Sie das Programm wirklich beenden?"))
		{
			return;
		}
		SceneManager.exitApp();
	}

	@FXML
	public void men01Einstellungen_OnClick() throws IOException
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/views/FrmStartEinstellungen.fxml"));
		Parent root = fxmlLoader.load();

		Stage stModalwindow = new Stage();
		Scene scene = new Scene(root);

		FrmStartEinstellungenController controller = fxmlLoader.getController();
		controller.setStage(stModalwindow);

		stModalwindow.setTitle("Einstellungen bearbeiten...");
		stModalwindow.setResizable(false);
		stModalwindow.setWidth(730);
		stModalwindow.setHeight(340);

		// 🔹 Owner setzen → KEIN extra Taskleisten-Icon mehr
		stModalwindow.initOwner(btnBeenden.getScene().getWindow()); // aus beliebigem Steuerelement holen

		// 🔹 Modalität
		stModalwindow.initModality(Modality.APPLICATION_MODAL);

		// 🔹 Icons aus zentraler Klasse (empfohlen)
		// stModalwindow.getIcons().add(new Image("/icons/javafx/werkzeug.png"));
		stModalwindow.getIcons().setAll(application.AppIcons.getIcons());

		stModalwindow.setScene(scene);

		stModalwindow.setOnCloseRequest(e -> {
			e.consume();
			stModalwindow.close();
		});

		stModalwindow.showAndWait();
	}

	@FXML
	public void men01Importieren_OnClick()
	{
	}

	@FXML
	public void men03WebAllgemein_OnClick()
	{

	}

	@FXML
	public void men03UpdatesPruefen_OnClick()
	{
		String textUpdates = "Programmupdates müssen manuell heruntergeladen werden: \n"
				+ "https://www.pcnd.eu/jpcnd/updates\n\n"
				+ "Für Datenbankupdates wird eine Schaltfläche im Hauptfenster angezeigt "
				+ "und können direkt installiert werden."
				+ "\n\n"
				+ "Weitere Informationen zum Programm: https://www.pcnd.eu/jpcnd/";
		try
		{
			Msgbox.show("Programmupdates ...", textUpdates);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
//		String versionNeu = "";
//		String textUpdates = "";
//		try
//		{
//			ToolsUpdateChecker.checkForUpdates(stage); // Programm
//			ToolsUpdateChecker.checkForDatabaseUpdate(stage); // Datenbank
//			versionNeu = ToolsUpdateChecker.checkForUpdatesUniversell("prog");
//
//			if (!versionNeu.equals(""))
//			{
//				textUpdates = "Neue Programmversion verfügbar: " + versionNeu + "\n"
//						+ "--> siehe Webseite https://www.pcnd.eu/jpcnd/updates\n\n";
//
//			}
//			versionNeu = ToolsUpdateChecker.checkForUpdatesUniversell("db");
//			if (!versionNeu.equals(""))
//			{
//				;
//				textUpdates += "Aktualisierte Datenbank verfügbar: " + versionNeu + "\n\n";
//
//			}
//			if (textUpdates.equals(""))
//			{
//				Msgbox.show("Auf Updates prüfen...", "Sie arbeiten bereits mit der aktuellen Programmversion und mit der aktuellen Datenbank.");
//			}
//			else
//			{
//				Msgbox.showUrl("Auf Updates prüfen...", "Es liegen Updates vor: \n\n" + textUpdates + "");
//			}
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
	}

	@FXML
	public void men03Lizenz_OnClick()
	{
		// showLicenseInfoDialog();
		LicenseManager lm = LicenseManager.getInstance();
		License lic = lm.getCurrentLicense();

		if (lic == null)
		{
			Msgbox.warn("Lizenz", "Keine Lizenz vorhanden");
			return;
		}

		String text = "Lizenz: " + lic.getLicenseKey() + "\n" +
				"E-Mail: " + lic.getEmail() + "\n" +
				// "Gültig bis: " + lic.getValidUntil() + "\n" +
				"Letzter Online-Check: " + lic.getLastOnlineCheck() + "\n" +
				"Status: " + lm.getLicenseStatusText();

		Msgbox.show("Lizenzinformationen", text);
	}

	@FXML
	public void men03HilfeVersion_OnClick() throws Exception
	{
		ProgrammversionenModel version = dbStart().getHoechsteVersion();
		String msg;
		msg = "Progamm-Vers: " + ValuesGlobals.progVersion;
		msg += "\nDatenbank-Vers" + version.getVernr() + " (" + version.getVerdat() + ")\n";
		msg += "\nProgrammpfad: " + ValuesGlobals.progPfad;
		msg += "\nDatenbankdatei: " + ConfigManager.loadDBPath();
		msg += "\nSicherungspfad: " + ConfigManager.loadBackupDirectory();
		msg += "\nTitelgrafikpfad: " + ConfigManager.loadGrafikDirectory();
		msg += "\n\n " + ValuesGlobals.progIconLizenz;
		msg += "\n\nJavaVers JRT:	" + ValuesGlobals.Versionsinfo + "\nPfad JRT: " + System.getProperty("java.home");
		msg += "\nJavaFX: " + System.getProperty("javafx.runtime.version") + "\nPfad JFX-Module:\n" + System.getProperty("java.class.path");

		try
		{
			Msgbox.show("Programminformationen....", msg);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	@FXML
	public void men03WebOnlinehilfe_OnClick()
	{
		String url = "https://www.pcnd.eu/jpcnd/"; // Hilfeseite Notenarchiv
		if (tabregLiteratur.isSelected())
		{
			url = "https://www.pcnd.eu/jpcnd/index.php?aw=31-01-notenarchiv.php";
		}
		else if (tabregNotenausgaben.isSelected())
		{
			url = "https://www.pcnd.eu/jpcnd/index.php?aw=31-01-editionen.php";
		}
		else if (tabregStck.isSelected()) {
			url = "https://www.pcnd.eu/jpcnd/index.php?aw=31-01-liederstuecke.php";
		}

		try
		{
			if (Desktop.isDesktopSupported())
			{
				Desktop.getDesktop().browse(new URI(url));

			}
			else
			{
				System.out.println("Desktop wird nicht unterstützt");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@FXML
	public void men40Programm_OnClick()
	{
		String url = "https://www.pcnd.eu/jpcnd/index.php?aw=20-01-prg.php"; // Update Programm
		try
		{
			if (Desktop.isDesktopSupported())
			{
				Desktop.getDesktop().browse(new URI(url));
			}
			else
			{
				System.out.println("Desktop wird nicht unterstützt");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		// men03WebAllgemein_OnClick();
	}

	@FXML
	public void men40Datenbank_OnClick() throws IOException
	{
		fctUpdaten();
	}

//+++++++++Ende Menüleiste ++++++++++++

//===============================================================================================
// FILTERBEREICH-ComboBoxen init -- erstellt alle Filter-Comboboxenlisten im Hauptformular
//===============================================================================================

	public void initCombosFilterConverters()
	{

		// Stückarten
		cbxFilterStueckart.setConverter(new StringConverter<StueckartlisteModel>()
		{
			@Override
			public String toString(StueckartlisteModel object)
			{
				return object == null ? "" : object.getBez();
			}

			@Override
			public StueckartlisteModel fromString(String string)
			{
				return cbxFilterStueckart.getValue();
			}
		});
		cbxFilterStueckart.valueProperty().addListener((obs, oldval, newval) -> {
		});

		// Thema
		cbxFilterThema.setConverter(new StringConverter<ThemenlisteModel>()
		{
			@Override
			public String toString(ThemenlisteModel object)
			{
				return object == null ? "" : object.getBez();
			}

			@Override
			public ThemenlisteModel fromString(String string)
			{
				return cbxFilterThema.getValue();
			}
		});
		cbxFilterThema.valueProperty().addListener((obs, oldval, newval) -> {
		});

		// Wochenlied
		cbxFilterWochenlied.setConverter(new StringConverter<WochenliedlisteModel>()
		{
			@Override
			public String toString(WochenliedlisteModel object)
			{
				return object == null ? "" : object.getWolibez();
			}

			@Override
			public WochenliedlisteModel fromString(String string)
			{
				return cbxFilterWochenlied.getValue();
			}
		});

		// Bibel
		cbxFilterBibel.setConverter(new StringConverter<BibellisteModel>()
		{
			@Override
			public String toString(BibellisteModel object)
			{
				return object == null ? "" : object.getBuch();
			}

			@Override
			public BibellisteModel fromString(String string)
			{
				return cbxFilterBibel.getValue();
			}
		});

		// Gesangbuch
		cbxFilterGesangbuch.setConverter(new StringConverter<GesangbuchModel>()
		{
			@Override
			public String toString(GesangbuchModel object)
			{
				return object == null ? "" : object.getBez();
			}

			@Override
			public GesangbuchModel fromString(String string)
			{
				return cbxFilterGesangbuch.getValue();
			}
		});
	}

	public void updateCombosFilterItems() throws Exception
	{

		// Stückarten
		List<StueckartlisteModel> listStckArt = dbStart().getStueckartListeAll();
		listStckArt.sort(Comparator.comparing(StueckartlisteModel::getBez, String::compareToIgnoreCase));
		cbxFilterStueckart.setItems(FXCollections.observableArrayList(listStckArt));

		// Thema
		List<ThemenlisteModel> listThema = dbStart().getThemenListeAll();
		listThema.sort(Comparator.comparing(ThemenlisteModel::getBez, String::compareToIgnoreCase));
		cbxFilterThema.setItems(FXCollections.observableArrayList(listThema));

		// Wochenlied
		List<WochenliedlisteModel> listWochenlied = dbStart().getWochenliedlisteListeAll();
		listWochenlied.sort(Comparator.comparing(WochenliedlisteModel::getWolirang, String::compareToIgnoreCase));
		cbxFilterWochenlied.setItems(FXCollections.observableArrayList(listWochenlied));

		// Bibel
		List<BibellisteModel> listBibel = dbStart().getBibelListeAll();
		listBibel.sort(Comparator.comparing(BibellisteModel::getBirang, String::compareToIgnoreCase));
		cbxFilterBibel.setItems(FXCollections.observableArrayList(listBibel));

		// Gesangbuch
		List<GesangbuchModel> listGesangbuch = dbStart().getGesAll();
		listGesangbuch.sort(Comparator.comparing(GesangbuchModel::getBez, String::compareToIgnoreCase));
		cbxFilterGesangbuch.setItems(FXCollections.observableArrayList(listGesangbuch));

		// Notenausgaben
		// -- nur Textfeld

		// Optional: Erste Auswahl oder SelectionModel clear
		cbxFilterStueckart.getSelectionModel().clearSelection();
		cbxFilterThema.getSelectionModel().clearSelection();
		cbxFilterWochenlied.getSelectionModel().clearSelection();
		cbxFilterBibel.getSelectionModel().clearSelection();
		cbxFilterGesangbuch.getSelectionModel().clearSelection();
	}

	// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public void initComboNotenmappeFilterConverter()
	{
		cbxFilterNotenmappe.setConverter(new StringConverter<NotenmappeModel>()
		{
			@Override
			public String toString(NotenmappeModel object)
			{
				return object == null ? "" : object.getBez();
			}

			@Override
			public NotenmappeModel fromString(String string)
			{
				return cbxFilterNotenmappe.getValue();
			}
		});

		cbxFilterNotenmappe.valueProperty().addListener((obs, oldval, newval) -> {
			// Optional: Reaktion bei Auswahlwechsel
			if (newval != null)
			{
				// System.out.println("Notenmappe ausgewählt: " + newval.getBez());
			}
		});
	}

	public void updateComboNotenmappeItems() throws Exception
	{
		List<NotenmappeModel> listNoma = dbStart().getNomaAll();
		listNoma.sort(Comparator.comparing(NotenmappeModel::getBez, String::compareToIgnoreCase));

		cbxFilterNotenmappe.setItems(FXCollections.observableArrayList(listNoma));

		// Optional: Auswahl zurücksetzen
		cbxFilterNotenmappe.getSelectionModel().clearSelection();
	}

//===============================================================================================
// Ende ... FILTERBEREICH-ComboBoxen init --
//===============================================================================================

//===============================================================================================
// Individuelle-ComboBoxen init -- abhängig von angeklickter Zeile in TblView
//===============================================================================================
	// ***********************************
	// Combos für Register Notenausgaben-Edit
	// enthalten: cbxNaEditVerlage, cbxNaEditArt
	// Initial einmal aufrufen
	public void initCombosNaIndivConverters()
	{
		// Verlag
		cbxNaEditVerlag.setConverter(new StringConverter<VerlaglisteModel>()
		{
			@Override
			public String toString(VerlaglisteModel object)
			{
				return object == null ? "" : object.getVverlag();
			}

			@Override
			public VerlaglisteModel fromString(String string)
			{
				return cbxNaEditVerlag.getValue();
			}
		});
		cbxNaEditVerlag.valueProperty().addListener((obs, oldval, newval) -> {
			if (newval != null)
			{
				// System.out.println("Verlag geändert: " + newval.getVverlag());
			}
		});

		// EditionsArt
		cbxNaEditArt.setConverter(new StringConverter<EditionenlisteComboModel>()
		{
			@Override
			public String toString(EditionenlisteComboModel object)
			{
				return object == null ? "" : object.getEdart();
			}

			@Override
			public EditionenlisteComboModel fromString(String string)
			{
				return cbxNaEditArt.getValue();
			}
		});
		cbxNaEditArt.valueProperty().addListener((obs, oldval, newval) -> {
			if (newval != null)
			{
				// System.out.println("EditionsArt geändert: " + newval.getEdart());
			}
		});
	}

	// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

	// ***********************************
	// Combos für Register Literatur-Edit
	// --enthalten: cbxLitEditKomp, cbxLitEditBearb, cbxLitEditNotenausgabe,
	// cbxLitEditTitel
	// -----------------------------------------------------
	// Converter + Listener einmalig setzen
	// -----------------------------------------------------
	public void initCombosLitConverters()
	{
		// Autoren-Komponist
		cbxLitEditKomp.setConverter(new StringConverter<AutorenlisteModel>()
		{
			@Override
			public String toString(AutorenlisteModel object)
			{
				return object == null ? "" : object.getAautor();
			}

			@Override
			public AutorenlisteModel fromString(String string)
			{
				return cbxLitEditKomp.getValue();
			}
		});
		cbxLitEditKomp.valueProperty().addListener((obs, oldval, newval) -> {
		});

		// Autoren-Bearbeiter
		cbxLitEditBearb.setConverter(new StringConverter<AutorenlisteModel>()
		{
			@Override
			public String toString(AutorenlisteModel object)
			{
				return object == null ? "" : object.getAautor();
			}

			@Override
			public AutorenlisteModel fromString(String string)
			{
				return cbxLitEditBearb.getValue();
			}
		});
		cbxLitEditBearb.valueProperty().addListener((obs, oldval, newval) -> {
		});

		// Notenausgaben
		cbxLitEditNotenausgabe.setConverter(new StringConverter<EditionenlisteComboNaModel>()
		{
			@Override
			public String toString(EditionenlisteComboNaModel object)
			{
				return object == null ? "" : object.toString();
			}

			@Override
			public EditionenlisteComboNaModel fromString(String string)
			{
				return cbxLitEditNotenausgabe.getValue();
			}
		});

		// Stückarten
		cbxLitStueckart.setConverter(new StringConverter<StueckartlisteModel>()
		{
			@Override
			public String toString(StueckartlisteModel object)
			{
				return object == null ? "" : object.getBez();
			}

			@Override
			public StueckartlisteModel fromString(String string)
			{
				return cbxLitStueckart.getValue();
			}
		});

		// Lieder/Stücke
		cbxLitEditTitel.setConverter(new StringConverter<LiederStueckeComboModel>()
		{
			@Override
			public String toString(LiederStueckeComboModel object)
			{
				return object == null ? "" : object.getStcktitel();
			}

			@Override
			public LiederStueckeComboModel fromString(String string)
			{
				return cbxLitEditTitel.getValue();
			}
		});
	}

	// -----------------------------------------------------
	// Items laden / aktualisieren – beliebig oft aufrufbar
	// -----------------------------------------------------
	// ----------- NOTENAUSGABEN TAB
	public void updateCombosNaIndiv() throws Exception
	{
		// Verlag
		List<VerlaglisteModel> listverlage = dbStart().getVerlaglisteListeAlle();
		listverlage.sort(Comparator.comparing(VerlaglisteModel::getVverlag, String::compareToIgnoreCase));
		cbxNaEditVerlag.setItems(FXCollections.observableArrayList(listverlage));

		if (!cbxNaEditVerlag.getItems().isEmpty())
		{
			cbxNaEditVerlag.getSelectionModel().selectFirst();
		}
		else
		{
			cbxNaEditVerlag.getSelectionModel().clearSelection();
		}

		// EditionsArt
		List<EditionenlisteComboModel> listedart = dbStart().getEditionenArtGroup();
		listedart.sort(Comparator.comparing(EditionenlisteComboModel::getEdart, String::compareToIgnoreCase));
		cbxNaEditArt.setItems(FXCollections.observableArrayList(listedart));

		if (!cbxNaEditArt.getItems().isEmpty())
		{
			cbxNaEditArt.getSelectionModel().selectFirst();
		}
		else
		{
			cbxNaEditArt.getSelectionModel().clearSelection();
		}

		clearNaEingabefelder();
		cbxNaEditArt.getSelectionModel().clearSelection();
		cbxNaEditVerlag.getSelectionModel().clearSelection();

	}

	// ------- LITERATUR TAB
	public void updateCombosLitItems() throws Exception
	{
		// Autorenliste
		List<AutorenlisteModel> listautoren = dbStart().getAutorenlisteAlle();
		listautoren.sort(Comparator.comparing(AutorenlisteModel::getAautor, String::compareToIgnoreCase));
		ObservableList<AutorenlisteModel> oblistaut = FXCollections.observableArrayList(listautoren);
		cbxLitEditKomp.setItems(oblistaut);
		cbxLitEditBearb.setItems(oblistaut);

		// Notenausgaben
		List<EditionenlisteComboNaModel> listedit = dbStart().getEditionenListeNaCombo();
		ObservableList<EditionenlisteComboNaModel> oblistedit = FXCollections.observableArrayList(listedit);
		cbxLitEditNotenausgabe.setItems(oblistedit);

		// Stückarten
		List<StueckartlisteModel> liststckart = dbStart().getStueckartListeAll();
		liststckart.sort(Comparator.comparing(StueckartlisteModel::getBez, String::compareToIgnoreCase));
		ObservableList<StueckartlisteModel> obliststckart = FXCollections.observableArrayList(liststckart);
		cbxLitStueckart.setItems(obliststckart);

		// Lieder/Stücke
		List<LiederStueckeComboModel> listlscbx = dbStart().getStueckLiedAll();
		listlscbx.sort(Comparator.comparing(LiederStueckeComboModel::getStcktitel, String::compareToIgnoreCase));
		ObservableList<LiederStueckeComboModel> oblistlscbx = FXCollections.observableArrayList(listlscbx);
		cbxLitEditTitel.setItems(oblistlscbx);

		clearLitEingabefelder();
		cbxLitEditBearb.getSelectionModel().clearSelection();
		cbxLitEditKomp.getSelectionModel().clearSelection();
		cbxLitEditNotenausgabe.getSelectionModel().clearSelection();
	}

	// ***********************************
	// Combos für Register Stücke/Lieder-Edit
	// ergänzt 2.1.2023
	// -----------------------------------------------------
	// Converter + Listener einmalig setzen
	// -----------------------------------------------------
	public void initCombosStckDichterConverters()
	{
		cbxStckEditDichter.setConverter(new StringConverter<AutorenlisteModel>()
		{
			@Override
			public String toString(AutorenlisteModel object)
			{
				return object == null ? "" : object.getAautor();
			}

			@Override
			public AutorenlisteModel fromString(String string)
			{
				return cbxStckEditDichter.getValue();
			}
		});

		cbxStckEditDichter.valueProperty().addListener((obs, oldval, newval) -> {
			if (newval != null)
			{
				// System.out.println("Stück-Dichter geändert: " + newval.getAautor());
			}
		});
	}

	// -----------------------------------------------------
	// Items laden / aktualisieren – beliebig oft aufrufbar
	// -----------------------------------------------------
	public void updateCombosStckDichter() throws Exception
	{
		List<AutorenlisteModel> listlit = dbStart().getAutorenlisteAlle();
		listlit.sort(Comparator.comparing(AutorenlisteModel::getAautor, String::compareToIgnoreCase));

		ObservableList<AutorenlisteModel> oblistaut = FXCollections.observableArrayList(listlit);
		cbxStckEditDichter.setItems(oblistaut);

		if (!cbxStckEditDichter.getItems().isEmpty())
		{
			cbxStckEditDichter.getSelectionModel().selectFirst();
		}
		else
		{
			cbxStckEditDichter.getSelectionModel().clearSelection();
		}

//		// bei neuem Datensatz ggf. AccessibleText setzen
//		if (!txtLitStckLiedId.getText().isEmpty() && tblvwStuecke.getSelectionModel().getSelectedItem() != null)
//		{
//			cbxStckEditDichter.setAccessibleText(
//					tblvwStuecke.getSelectionModel().getSelectedItem().getStckdicht());
//		}

	}

//	// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//+++++++++++ Ende ComboBoxen +++++++++++++++++++++++

//===============================================================================
//  Allgemeine Methoden 
//===============================================================================	

	public void titelgrafikAnzeigen()
	{
		// System.out.println("titelgarfikAnzeigen()" + ValuesGlobals.progPfadGrafik);
		int selectedTabindex = 0;
		selectedTabindex = tabpaneStartfenster.getSelectionModel().getSelectedIndex();
		File fileFehlt = new File(ValuesGlobals.progPfadGrafik + ValuesGlobals.progPfadGrafikFehlt);
		switch (selectedTabindex)
		{

		case 0:
			File imgFile0 = new File(ValuesGlobals.progPfadGrafik + txtLitEditTitelgrafikpfad.getText());
			if (imgFile0.isFile())
			{
				Image image0 = new Image(imgFile0.toURI().toString());
				imgLitEditTitelgrafik.setImage(image0);
			}
			else
			{
				if (!tblvwLiteratur.getSelectionModel().isEmpty())
				{
					Image imgfileLeer = new Image(fileFehlt.toURI().toString());
					imgLitEditTitelgrafik.setImage(imgfileLeer);
				}
				else
				{
					imgLitEditTitelgrafik.setImage(null);
				}
			}
			break;
		case 1:
			File imgFile1 = new File(ValuesGlobals.progPfadGrafik + txtNaEditTitelgrafikpfad.getText());

			if (imgFile1.isFile())
			{
				Image image = new Image(imgFile1.toURI().toString());
				imgNaTitelgrafik.setImage(image);
			}
			else
			{
				if (!tblvwEditionen.getSelectionModel().isEmpty())
				{
					Image imgfileLeer = new Image(fileFehlt.toURI().toString());
					imgNaTitelgrafik.setImage(imgfileLeer);
				}
				else
				{
					imgNaTitelgrafik.setImage(null);
				}

			}
			break;
		}

	}

	public void Nomafilter_OnClick() throws Exception
	{
	}
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!	

	///////////////////////////////////////////////////////

	public void chkDsBearbeiten_OnChanged()
	{
		System.out.println(chkDsBearbeiten.isSelected());

		if (chkDsBearbeiten.isSelected() == true)
		{
			lblLitInfozeile.setText("!! Schreibzugriff !!");
			lblLitInfozeile.setTextFill(javafx.scene.paint.Color.web("#c31e1e"));
			btnLitNeu.setDisable(false);
			btnLitLoeschen.setDisable(false);
			btnLitSpeichern.setDisable(false);

			lblNaEditInfozeile.setText("!! Schreibzugriff !!");
			lblNaEditInfozeile.setTextFill(javafx.scene.paint.Color.web("#c31e1e"));
			btnNaNeu.setDisable(false);
			btnNaLoeschen.setDisable(false);
			btnNaSpeichern.setDisable(false);
			btnNaGrafikwahl.setDisable(false);

			lblStckInfozeile.setText("!! Schreibzugriff !!");
			lblStckInfozeile.setTextFill(javafx.scene.paint.Color.web("#c31e1e"));
			btnStckNeu.setDisable(false);
			btnStckLoeschen.setDisable(false);
			btnStckSpeichern.setDisable(false);

		}
		else
		{
			lblLitInfozeile.setText("Lesezugriff...");
			lblLitInfozeile.setTextFill(javafx.scene.paint.Color.web("#03636D"));
			btnLitNeu.setDisable(true);
			btnLitLoeschen.setDisable(true);
			btnLitSpeichern.setDisable(true);

			lblNaEditInfozeile.setText("Lesezugriff...");
			lblNaEditInfozeile.setTextFill(javafx.scene.paint.Color.web("#03636D"));
			btnNaNeu.setDisable(true);
			btnNaLoeschen.setDisable(true);
			btnNaSpeichern.setDisable(true);
			btnNaGrafikwahl.setDisable(true);

			lblStckInfozeile.setText("Lesezugriff...");
			lblStckInfozeile.setTextFill(javafx.scene.paint.Color.web("#03636D"));
			btnStckNeu.setDisable(true);
			btnStckLoeschen.setDisable(true);
			btnStckSpeichern.setDisable(true);

		}

	}

	private String safeIntToString(Integer val)
	{
		if (val != null)
		{
			return val.toString();
		}
		else
		{
			return "";
		}

	}

	private <T> TableColumn<T, ?> getPrimarySortColumn(TableView<T> tableView)
	{
		if (tableView.getSortOrder().isEmpty())
		{
			return null;
		}
		TableColumn<T, ?> col = (TableColumn<T, ?>) tableView.getSortOrder().get(0);
		return col;
	}

	private void cleanupProgress()
	{
		progressPdf.visibleProperty().unbind();
		progressPdf.progressProperty().unbind();
		progressPdf.setVisible(false);
		lblStatusPdf.setVisible(false);
	}

	private void openPdfAsync(File pdfFile)
	{
		new Thread(() -> {
			try
			{
				String os = System.getProperty("os.name").toLowerCase();

				if (os.contains("linux"))
				{
					new ProcessBuilder("xdg-open", pdfFile.getAbsolutePath()).start();
				}
				else if (os.contains("mac"))
				{
					new ProcessBuilder("open", pdfFile.getAbsolutePath()).start();
				}
				else if (java.awt.Desktop.isDesktopSupported())
				{
					java.awt.Desktop.getDesktop().open(pdfFile);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}, "PDF-Open-Thread").start();
	}
// ======================================================================
// Startoverlay 
// ======================================================================

	@FXML
	private StackPane rootPane; // Root-Pane in FXML

	private StackPane overlay;

	public void showOverlay(String message)
	{
		if (overlay != null)
			return; // nur einmal

		ProgressIndicator pi = new ProgressIndicator();
		Label lbl = new Label(message);
		lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

		VBox box = new VBox(10, pi, lbl);
		box.setAlignment(Pos.CENTER);

		overlay = new StackPane();
		overlay.getChildren().add(box);
		overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);"); // halbtransparent
		overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

		rootPane.getChildren().add(overlay);
	}

	public void hideOverlay()
	{
		if (overlay != null)
		{
			rootPane.getChildren().remove(overlay);
			overlay = null;
		}
	}

//===========================================================================================================================
//===========================================================================================================================
//**************************************************
//          DEMOS zum Kopieren!!!!
//==================================================
	@FXML // Doppelklick feststellen
	public void handleTblvwNa_OnMousePressed()
	{ // auf Doppelklick testen
		tblvwEditionen.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2)
			{

				try
				{
					// btnNaBearbeiten_OnClick();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
//=====================================================
//*****************************************************

	}

}