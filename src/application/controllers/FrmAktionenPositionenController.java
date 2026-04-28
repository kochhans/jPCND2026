package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import application.ConfigManager;
import application.FilterState;
import application.ValuesGlobals;
import application.models.AktionenEinzelnPerId;
import application.models.AktionenListeModel;
import application.models.AktionenListePositionenAufgefuehrtModel;
import application.models.AktionenListePositionenModel;
import application.models.LiteraturlisteModel;
import application.models.NotenmappeModel;
import application.models.StueckartlisteModel;
import application.models.ThemenlisteModel;
import application.models.WochenliedlisteModel;
import application.uicomponents.Msgbox;

// .....................................................
public class FrmAktionenPositionenController
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
	@FXML
	private Label lblAktionBeschreibung;
	@FXML
	private Label lblAktionDatum;
	// BUTTONS---------------------
	@FXML
	private Button btnZurueck;
	// ----------------------------------------
	// (2) SplitContainer Links - Positionen
	// ----------------------------------------
	// Choraktionen-Positionen
	@FXML
	private TableView<AktionenListePositionenModel> tblvwAktionPositionen;
	@FXML
	private TableColumn<AktionenListePositionenModel, Integer> tblvwPosCol0Pos;
	@FXML
	private TableColumn<AktionenListePositionenModel, String> tblvwPosCol1Art, tblvwPosCol2Stcktitel,
			tblvwPosCol3Notenausgabe, tblvwPosCol4Nr, tblvwPosCol4S, tblvwPosCol5Stckart,
			tblvwPosCol6Komp, tblvwPosCol7Besetzung, tblvwPosCol8Anmerkungen;
	// TableView identisch, aber in gespeicherten Aktionen
	// Choraktionen-Positionen
	@FXML
	private TableView<AktionenListePositionenModel> tblvwAktionPositionen1;
	@FXML
	private TableColumn<AktionenListePositionenModel, Integer> tblvwPosCol0Pos1;
	@FXML
	private TableColumn<AktionenListePositionenModel, String> tblvwPosCol1Art1, tblvwPosCol2Stcktitel1,
			tblvwPosCol3Notenausgabe1, tblvwPosCol4Nr1, tblvwPosCol4S1, tblvwPosCol5Stckart1,
			tblvwPosCol6Komp1, tblvwPosCol7Besetzung1, tblvwPosCol8Anmerkungen1;
	@FXML
	private TextField txtCaId, txtPosProgpkt, txtPosStckTitel, txtPosNotenausgabe, txtPosKomponist,
			txtPosBearbeiter, txtPosSeite, txtPosNr, txtPosStueckart, txtPosTonart,
			txtPosDauermin, txtPosDauersec, txtPosLitId, txtPosTitelgrafik, txtPosBesetzung;
	@FXML
	private TextArea txtBemerkungen;
	@FXML
	private CheckBox chkProgZwischentext, chkProgQuellePcnd;
	@FXML
	private Label lblZeilebearbeiten;
	@FXML
	private Button btnPosSpeichern, btnPosLoeschen, btnPosHoch,
			btnPosRunter, btnPosLeeren, btnPosLeerenAnhaengen, btnNaGrafikwahl,btnNaGrafikwahlLeer, btnPosgespielt;
	
	@FXML
	FileChooser fileAuswahlbox = new FileChooser();
	@FXML
	private ImageView imgNaTitelgrafik;
	// --- Listen für TableView anlegen
	ObservableList<AktionenListePositionenModel> oblist_aktionenpos = FXCollections.observableArrayList();
	ObservableList<AktionenListePositionenModel> oblist_aktionenpos1 = FXCollections.observableArrayList();
	ObservableList<AktionenListePositionenAufgefuehrtModel> oblist_pos_aufgef = FXCollections.observableArrayList();
	// Tableviewliste für alle Aktionen (rechts Tab aus Aktionen)
	ObservableList<AktionenListeModel> oblist_aktionen = FXCollections.observableArrayList();
	// ----------------------------------------
	// (3) SplitContainer Rechts Filterungen
	// ----------------------------------------
	// Combos
	@FXML
	private ComboBox<StueckartlisteModel> cbxFilterStueckart;
	@FXML
	private ComboBox<ThemenlisteModel> cbxFilterThema;
	@FXML
	private ComboBox<WochenliedlisteModel> cbxFilterWochenlied;
	@FXML
	private ComboBox<NotenmappeModel> cbxFilterNotenmappe;
	@FXML
	private Label lblFilterTab1Anzahl, lblFilterTab0Anzahl;
	@FXML
	private ComboBox<String> cbxFilterAktion, cbxFilterOrt, cbxFilterGruppe;
	// TEXTFELDER -----------------
	@FXML
	private TextField txtFilterTitel, txtFilterEdit, txtFilterKomp, txtFilterDicht, txtFilterEditVerlag;
	@FXML
	private DatePicker dpFilterDatumVon, dpFilterDatumBis;
	// RADIOBUTTONS in Togglegroups ---
	@FXML
	private RadioButton radFilterProben, radFilterAuff, radFilterAlles,
			radFilterProbengespielt, radFilterAuffgespielt, radFilterAllesgespielt;
	@FXML
	private ToggleGroup ogrFilterProbeAuffAllesGespielt;
	@FXML
	private CheckBox chkEditionGespielt, chkStueckartGespielt;
	// BUTTONS---------------------
	@FXML
	private Button btnFiltergespAn, btnFiltergespAus;
	// TABLEVIEWS ========================
	// TableView Literatur mit Spalten
	@FXML
	private TableView<LiteraturlisteModel> tblvwLiteratur;
	@FXML
	private TableColumn<LiteraturlisteModel, String> tblvwColTitel, tblvwColEdit, tblvwColTArt,
			tblvwColKomp, tblvwColBearb, tblvwColBes, tblvwColThema, tblvwColStckArt, tblvwColId;
	@FXML
	private TableColumn<LiteraturlisteModel, Integer> tblvwColN, tblvwColS;
	// Tableview Choraktionen aus gespeicherten Aktionen
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
	private TableColumn<AktionenListeModel, String> tblvwAktionenCol6; // Anwesend
	@FXML
	private TableColumn<AktionenListeModel, Integer> tblvwAktionenCol7; // art
	@FXML
	private TableView<AktionenListePositionenAufgefuehrtModel> tblvwChoraktionenAufgef;
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, LocalDate> tblvwAktionenAufgefCol0; // Datum
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, String> tblvwAktionenAufgefCol1; // Aktion
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, String> tblvwAktionenAufgefCol2; // Na kurz
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, String> tblvwAktionenAufgefCol3; // Nummer
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, String> tblvwAktionenAufgefCol4; // Seite
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, String> tblvwAktionenAufgefCol5; // Stückart
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, String> tblvwAktionenAufgefCol6; // Kopmonist
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, String> tblvwAktionenAufgefCol7; // Ort
	@FXML
	private TableColumn<AktionenListePositionenAufgefuehrtModel, String> tblvwAktionenAufgefCol8; // Gruppe
	@FXML
	private Button btnFilterAn, btnFilterAus;
	@FXML
	private TabPane tabPanePosFilter;
	// Liste für Tableview FilterLiteratur
	ObservableList<LiteraturlisteModel> oblist_lit = FXCollections.observableArrayList();
	// ####################################################################################################
	// ================================================================================================
	// Variablen
	public int posBearbStatus = 0; // 1=bearbeiten aus der Liste; 2=bearbeiten aus Suche; 3=bearbeiten neuer
									// Datensatz; 4= unten anhängen neuer DS
	public int posAktuell = 1; // Position in der Tableviewliste mit Positionen
	private DatabaseControllerAktionen db;

//******************************************************************************************************
	public void setDbControllerAktionen(DatabaseControllerAktionen db)
	{
		this.db = db;
	}

	// ----
	@FXML
	public void initialize()
	{
		System.out.println("FXML initialize (nur 1x)");
		// Listener für Auswahländerung
		ogrFilterProbeAuffAllesGespielt.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
			if (newToggle != null)
			{
				RadioButton selected = (RadioButton) newToggle;
				System.out.println("Neue Auswahl: " + selected.getText());
				handleOgrFilterGespielt_onChanged(selected);
			}
		});
		btnPosHoch.setDisable(false);
		btnPosRunter.setDisable(false);
		btnPosLeeren.setDisable(true);
		btnPosLoeschen.setDisable(true);
		btnNaGrafikwahl.setDisable(true);

	}

	@FXML
	public void onShow(int caid) throws Exception
	{// Hier die Scene-spezifische Initialisierung
		if (db == null)
		{
			throw new IllegalStateException("DB-Controller wurde nicht gesetzt!");
		}
		// init Combos ------------------
		// Converter für Combos einmalig setzen
		initCombosFilterConverters();
		// Combos füllen
		updateCombosFilterItems();
		// --- Bereiche-------------------
		// init Kopf
		anzeigenAktioneninfo(caid);
		txtCaId.setText(String.valueOf(caid));
		// init linker Splitter oben
		initTableviewsAktionenPositionen();
		anzeigenTabelleAktionenPositionen(caid);
		initEingabefelder(); // Eingabefelder für AktionenPositionen
		// init rechter Spliter Tab 0 (aus Literatur)
		initTblvwLiteraturliste();
		initTableviewChorAktionenAusAufgef();
////TODO
		// init rechter Spliter Tab 1 (aus gespeicherten Aktionen)
		setTableviewChorAktionenAusGesp(); // Tabelle oben
		// Tabelle unten (benötigt Verzögerung)
		Platform.runLater(() -> {
			try
			{
				setTableviewAktionenPositionenAusGesp();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
		// Sperren aller Eingabefelder
		posAlleSperren(0);
		initFilterfelderGespAktionen();
		filternAktionen();
		restoreFilterAktionenPositionen();

		// System.out.println("tblvwAktionPositionen1 = " + tblvwAktionPositionen1);
		// System.out.println("Controller Instanz: " + this);
		// alle Tableviews Spalten unverschiebbar machen
		tblvwAktionPositionen.getColumns().forEach(col -> col.setReorderable(false));
		tblvwAktionPositionen1.getColumns().forEach(col -> col.setReorderable(false));
		tblvwChoraktionen.getColumns().forEach(col -> col.setReorderable(false));
		tblvwChoraktionenAufgef.getColumns().forEach(col -> col.setReorderable(false));

	}

	@FXML
	public void initEingabefelder()
	{
		txtCaId.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("\\d*"))
			{
				return change;
			}
			return null;
		}));
		txtPosDauermin.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("\\d*"))
			{
				return change;
			}
			return null;
		}));
		txtPosDauersec.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("\\d*"))
			{
				return change;
			}
			return null;
		}));
		lblZeilebearbeitenSetzen(0);
	}

	private void initFilterfelderGespAktionen() throws Exception
	{// Filter für Register aus Aktionen
		dpFilterDatumBis.setValue(null);
		// dpFilterDatumVon.setValue(null);
		dpFilterDatumVon.setValue(
				LocalDate.now()
						.minusYears(10)
						.withDayOfYear(1));
		cbxFilterAktion.setValue(null);
		cbxFilterGruppe.setValue(null);
		cbxFilterOrt.setValue(null);
		radFilterAlles.setSelected(true);
	}

//######################################################################################
	// =================================
	// Steuerelemente im Kopfbereich
	// =================================
	@FXML
	private void btnZurueck_OnClick(ActionEvent event) throws Exception
	{
		speichereFilterAktionenPositionen();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // aktuelle Stage rausfinden
		stage.close(); // stage schließen
	}

	public void registerHandling()
	{
	}

	// ==================================================================================
	// Steuerelemente im linken Bereich (Positionenliste der Aktion)
	// ==================================================================================
	private void initTableviewsAktionenPositionen() throws Exception // (1) definieren
	{
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
		// ---------------- Nummer Seite ----------------
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
		// ---------------- Lade Daten aus DB ----------------
		// anzeigenTabelleAktionenPositionen(0);
	}

	private void setTableviewChorAktionenAusGesp() // (1) definieren
	{
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).
		// TABLEVIEWES mit Spalten -----
		lblFilterTab1Anzahl.setText("Aktivitätensuche - Filter nicht aktiv");
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
		tblvwAktionenCol6.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCaverantwortlich()));
		// ---------------- Art Probe/Auff ----------------
		tblvwAktionenCol7.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCaauftrittstermin()));
		lblFilterTab1Anzahl.setText("Aktivitätensuche - Filter nicht aktiv");
		// ---------------- Lade Daten aus DB ----------------
		try
		{
			List<AktionenListeModel> daten = db.getAktionenListeAll();
			tblvwChoraktionen.getItems().setAll(daten);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

//@FXML
	private void setTableviewAktionenPositionenAusGesp() throws Exception // (1) definieren
	{
		// ---------------- PosNr ----------------
		tblvwPosCol0Pos1.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCapoPos()));
		tblvwPosCol0Pos1.setStyle("-fx-alignment: CENTER;");
		// ---------------- Bemerkungen zur Zeile ----------------
		tblvwPosCol1Art1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoSonstiges() != null ? cellData.getValue().getCapoSonstiges() : ""));
		// ---------------- Stcktitel ----------------
		tblvwPosCol2Stcktitel1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoStcktitel() != null ? cellData.getValue().getCapoStcktitel() : ""));
		// ---------------- Notenausgabe ----------------
		tblvwPosCol3Notenausgabe1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoEdition() != null ? cellData.getValue().getCapoEdition() : ""));
		// ---------------- Nummer Seite ----------------
		tblvwPosCol4Nr1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoNr() != null ? cellData.getValue().getCapoNr() : ""));
		tblvwPosCol4Nr1.setStyle("-fx-alignment: CENTER;");
		// ---------------- Nummer Seite ----------------
		tblvwPosCol4S1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoSeite() != null ? cellData.getValue().getCapoSeite() : ""));
		tblvwPosCol4S1.setStyle("-fx-alignment: CENTER;");
		// ---------------- Stueckart ----------------
		tblvwPosCol5Stckart1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoArt() != null ? cellData.getValue().getCapoArt() : ""));
		// ---------------- Komponist ----------------
		tblvwPosCol6Komp1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoKomponist() != null ? cellData.getValue().getCapoKomponist() : ""));
		// ---------------- Besetzung ----------------
		tblvwPosCol7Besetzung1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoBesetzung() != null ? cellData.getValue().getCapoBesetzung() : ""));
		// ---------------- Anmerkungen ----------------
		tblvwPosCol8Anmerkungen1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCapoBem() != null ? cellData.getValue().getCapoBem() : ""));
		// ---------------- Lade Daten aus DB ----------------
		// anzeigenTabelleAktionenPositionen(0);
	}

	public void anzeigenTabelleAktionenPositionen(int caid) throws Exception // (2) anzeigen nach Speichern
	{
		System.out.println("aktuelle Aktion " + caid);
		// linke Tableview (alle)
		oblist_aktionenpos.clear();
		List<AktionenListePositionenModel> listaktionen = db.getAktionenPositionenListeAll(caid);
		oblist_aktionenpos.addAll(listaktionen);
		tblvwAktionPositionen.setFixedCellSize(26);
		tblvwAktionPositionen.getSortOrder().clear(); // Sortierung in SQL ohne Steuerelemts Sortierung !! vor SetItems
		tblvwAktionPositionen.setItems(oblist_aktionenpos);
	}

	@FXML
	public void anzeigenAktioneninfo(int caid)
	{
		// System.out.println("anzeigtenAktioneninfo -- aktuelle Aktion " + caid);
		AktionenEinzelnPerId aktion = db.getAktionPerId(caid);
		String aktionInfo = "";
		if (aktion != null)
		{
			aktionInfo += aktion.getCadatum() + " | ";
			if (aktion.getCatreffpunkt() != null)
			{
				aktionInfo += aktion.getCatreffpunkt().toString() + " | ";
			}
			// System.out.println("anzeigtenAktioneninfo -- aktuelle Aktion " + aktionInfo);
			// aktionInfo="";
			aktionInfo += aktion.getCabeschreibung() + " | ";
			aktionInfo += aktion.getCagruppe() + " | ";
			aktionInfo += aktion.getCaaktionsort() + " | ";
			lblAktionDatum.setText(aktionInfo);
			// System.out.println("anzeigtenAktioneninfo -- aktuelle Aktion " + aktionInfo);
		}
	}

	// -----------------------------
	// Buttons für Positionen
	// ------------------------------
	// POSITION SPEICHERN
	@FXML
	private void btnPosSpeichern_OnKlick(ActionEvent event) throws Exception
	{
		int capoid = 0;
		int capopos = 0;
		int capozwischentext = 0;
		int capoquellepcnd = 0;
		int capolitid = 0;
		int capodauermin = 0;
		int capodauersec = 0;
		String capozeilentyp = "";
		// Validierungen
		if (txtPosStckTitel.getText().equals(""))
		{
			Msgbox.warn("Speichern abgebrochen", "Bitte geben Sie einen Stücktitel bzw. einen Zwischentext ein");
			Platform.runLater(txtPosStckTitel::requestFocus);
			return;
		}
		// --- alles aus den Feldern einlesen
		String capotitelgrafik = txtPosTitelgrafik.getText();
		String capoprgpkt = txtPosProgpkt.getText();// PrgPkt
		String capostcktitel = txtPosStckTitel.getText();
		String caponotenausgabe = txtPosNotenausgabe.getText();
		String capokomponist = txtPosKomponist.getText();
		String capobearbeiter = txtPosBearbeiter.getText();
		String caposeite = txtPosSeite.getText();
		String caponummer = txtPosNr.getText();
		String capostckart = txtPosStueckart.getText();
		String capotonart = txtPosTonart.getText();
		String capobesetzung = txtPosBesetzung.getText();
		int capocaid = Integer.parseInt(txtCaId.getText());
		String text = txtPosDauermin.getText();
		if (text != null && !text.trim().isEmpty())
		{
			capodauermin = Integer.parseInt(text);
		}
		text = txtPosDauermin.getText();
		if (text != null && !text.trim().isEmpty())
		{
			capodauermin = Integer.parseInt(text);
		}
		if (chkProgZwischentext.isSelected() == true)
		{
			capozwischentext = 1;
		}
		if (chkProgQuellePcnd.isSelected() == true)
		{
			capoquellepcnd = 1;
		}
		String capobemerkung = txtBemerkungen.getText();
		capolitid = Integer.parseInt(txtPosLitId.getText());
		// -- selektierten Index der Tableview holen (für Verschieben und EInfügen an
		// korrekter Stelle)
		AktionenListePositionenModel selected = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		int index = tblvwAktionPositionen.getSelectionModel().getSelectedIndex();

		if (selected != null)
		{// updaten einer Zeile
			capoid = selected.getCapoId(); // wird zum Updaten benötigt
			capopos = selected.getCapoPos();
			capozeilentyp = selected.getCapoZeilentyp();
		}
		else
		{
			int anzahlZeilen = tblvwAktionPositionen.getItems().size();

			// neue Positionszeile bei leerer Liste
			capopos = anzahlZeilen + 1;
		}
		// Save direkt mit LocalDate und LocalTime
		if (posBearbStatus == 3 || posBearbStatus == 2) // einfügen
		{
			db.verschiebeAlleAbPosition(capocaid, capopos); // zuerst verschieben, dann einfügen mit alter Pos
			// neuer Datensatz in Postiionen
			db.saveAktionPosition(
					0, capopos, capobemerkung, capocaid, capostcktitel,
					caponotenausgabe, capostckart, capodauermin, capodauersec,
					capoprgpkt, caponummer, caposeite, capobesetzung,
					capotonart, capokomponist, capobearbeiter, capoquellepcnd, capozwischentext,
					capolitid, capozeilentyp, capotitelgrafik, true);
			posAktuell = capopos;
		}

		if (posBearbStatus == 4)
		{ // neuen Datensatz unten anhängen

			db.saveAktionPosition(
					0, capopos, capobemerkung, capocaid, capostcktitel,
					caponotenausgabe, capostckart, capodauermin, capodauersec,
					capoprgpkt, caponummer, caposeite, capobesetzung,
					capotonart, capokomponist, capobearbeiter, capoquellepcnd, capozwischentext,
					capolitid, capozeilentyp, capotitelgrafik, true);
			posAktuell = 0;
		}
		else if (posBearbStatus == 1)
		{
			// Position bearbeiten
			db.saveAktionPosition(
					capoid, capopos, capobemerkung, capocaid, capostcktitel,
					caponotenausgabe, capostckart, capodauermin, capodauersec,
					capoprgpkt, caponummer, caposeite, capobesetzung,
					capotonart, capokomponist, capobearbeiter, capoquellepcnd, capozwischentext,
					capolitid, capozeilentyp, capotitelgrafik, false);
			posAktuell = capopos;
		}
		else
		{
		}
		anzeigenTabelleAktionenPositionen(capocaid);
		Platform.runLater(() -> {
			posTextfelderLeeren(0);
			tblvwAktionPositionen.getSelectionModel().select(index);
			tblvwAktionPositionen.scrollTo(index);
		});

		if (chkProgZwischentext.isSelected())
		{
			lblZeilebearbeitenSetzen(5);
		}
		else
		{
			lblZeilebearbeitenSetzen(4);
		}

		if (posBearbStatus != 4)
		{
			// nicht unten angefügt als neuer DS, dann immer neuerDatensatz an Cursor
			posBearbStatus = 3;

			if (chkProgZwischentext.isSelected())
			{ // ...als Zwischentext
				lblZeilebearbeitenSetzen(5);
			}
			else
			{ // ..als Literatur
				lblZeilebearbeitenSetzen(4);
			}
		}
		else
		{
			// nicht unten angefügt als neuer DS, dann immer neuerDatensatz an Cursor
			posBearbStatus = 4;

			if (chkProgZwischentext.isSelected())
			{ // ...als Zwischentext
				lblZeilebearbeitenSetzen(7);
			}
			else
			{ // ..als Literatur
				lblZeilebearbeitenSetzen(6);
			}
		}
		txtPosStckTitel.requestFocus();

	}

	// POSITION LOESCHEN
	@FXML
	private void btnPosLoeschen_OnClick(ActionEvent event) throws Exception
	{
		AktionenListePositionenModel selectedPos = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		if (selectedPos == null)
		{
			Msgbox.show("Zeile löschen...", "Bitte eine Zeile zum Löschen markieren!");
			return;
		}
		posAktuell = tblvwAktionPositionen.getSelectionModel().getSelectedItem().getCapoPos(); // aktuelle Position sichern
		int index = tblvwAktionPositionen.getSelectionModel().getSelectedIndex();
		// int posnrAktuell = selectedPos.getCapoPos();
		int capo_caid = selectedPos.getCapoCaId();
		int markierterIndex = tblvwAktionPositionen.getSelectionModel().getSelectedIndex();
		db.deleteAndRenumber(selectedPos.getCapoId(), selectedPos.getCapoPos(), selectedPos.getCapoCaId());
		// 3️⃣ Tabelle neu laden
		anzeigenTabelleAktionenPositionen(capo_caid);
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

//		// 1. Datensatz löschen
//		db.deletePosition(selected.getCapoId());
//
//		// 2. Rest nach oben schieben
//		db.verschiebeAlleNachLoeschen(caId, pos);
		Platform.runLater(() -> {
			posTextfelderLeeren(0);
			tblvwAktionPositionen.getSelectionModel().select(index);
			tblvwAktionPositionen.scrollTo(index);
		});

		if (posAktuell == 0)
		{
			lblZeilebearbeitenSetzen(0);
		}
		else
		{
			if (chkProgZwischentext.isSelected())
			{
				lblZeilebearbeitenSetzen(5);
			}
			else
			{
				lblZeilebearbeitenSetzen(4);
			}

		}

		posBearbStatus = 3;
		txtPosStckTitel.requestFocus();
	}

	// POSITION LEEREN (NEUER DS)
	@FXML
	private void btnPosLeeren_OnClick(ActionEvent event) throws Exception
	{
		AktionenListePositionenModel selected = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			posAktuell = 1;
		}
		else
		{
			posAktuell = tblvwAktionPositionen.getSelectionModel().getSelectedItem().getCapoPos(); // aktuelle Position sichern
		}
		chkProgZwischentext.setSelected(false);
		posTextfelderLeeren(0);
		posFelderSperren();
		if (chkProgZwischentext.isSelected())
		{
			lblZeilebearbeitenSetzen(1);
		}
		else
		{
			lblZeilebearbeitenSetzen(4);
		}
		posBearbStatus = 3;
		tblvwLiteratur.getSelectionModel().clearSelection();
		btnNaGrafikwahl.setDisable(true);
		txtPosStckTitel.requestFocus();
		// tblvwAktionPositionen.getSelectionModel().clearSelection();
	}

	// POSITION LEEREN und unten anhängen (NEUER DS)
	@FXML
	private void btnPosLeerenAnhaengen_OnClick(ActionEvent event) throws Exception
	{
		tblvwAktionPositionen.getSelectionModel().clearSelection();

//		Platform.runLater(() -> {
//			tblvwAktionPositionen.getSelectionModel().clearSelection();
//			tblvwAktionPositionen.getFocusModel().focus(-1);
//		});
		System.out.println(tblvwLiteratur.getSelectionModel().getSelectedItem());

		posTextfelderLeeren(0);
		posFelderSperren();
		if (chkProgZwischentext.isSelected())
		{
			lblZeilebearbeitenSetzen(7);
		}
		else
		{
			lblZeilebearbeitenSetzen(6);
		}
		posBearbStatus = 4;
		btnPosLeeren.setDisable(true);
		btnNaGrafikwahl.setDisable(true);
		txtPosStckTitel.requestFocus();

	}

	// POSITION SCHIEBEN AB
	@FXML
	private void btnPosRunter_OnClick(ActionEvent event) throws Exception
	{
		AktionenListePositionenModel selected = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			posAktuell = 1;
		}
		else
		{
			posAktuell = tblvwAktionPositionen.getSelectionModel().getSelectedItem().getCapoPos(); // aktuelle Position sichern
		}
		schiebenPositon(1); // beim reinen Schieben die zweite Variable 0 setzen
	}

	// POSITION SCHIEBEN AUF
	@FXML
	private void btnPosHoch_OnClick(ActionEvent event) throws Exception
	{
		AktionenListePositionenModel selected = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			posAktuell = 1;
		}
		else
		{
			posAktuell = tblvwAktionPositionen.getSelectionModel().getSelectedItem().getCapoPos(); // aktuelle Position sichern
		}
		schiebenPositon(2);// beim reinen Schieben die zweite Variable 0 setzen
	}

	// SCHIEBEN FUNKTION
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
			Msgbox.show("Verschieben von Zeilen...", "Bitte markieren Sie die Zeile, die verschoben werden soll!");
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
			// btnPosRunter.setDisable(false);
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
			// btnPosHoch.setDisable(false);
		}
		anzeigenTabelleAktionenPositionen(Integer.parseInt(txtCaId.getText()));
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
		posAktuell = neuerIndex;
		// tblvwAktionPositionen.scrollTo(neuerIndex);
		if (chkProgZwischentext.isSelected())
		{
			lblZeilebearbeitenSetzen(1);
		}
		else
		{
			lblZeilebearbeitenSetzen(2);
		}
		posTextfelderLeeren(0);
		posBearbStatus = 3;// bearbeiten nach Schieben

	}

	// POS EINFÜGEN UND SCHIEBEN
	public void einfuegenAnMarkierterPosition() throws Exception
	{
		int markierterIndex = tblvwAktionPositionen.getSelectionModel().getSelectedIndex();
		int caId = tblvwAktionPositionen.getSelectionModel().getSelectedItem().getCapoCaId();
		if (markierterIndex < 0)
		{
			Msgbox.show("Einfügen...", "Bitte markieren Sie eine Zeile!");
			return;
		}
		AktionenListePositionenModel selected = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		int neuePosition = selected.getCapoPos();
		// 1. Alles nach unten verschieben
		db.verschiebeAlleAbPosition(caId, neuePosition);
		// 2. Neuen Datensatz einfügen
		// db.insertNeuePosition (caId, neuePosition);
		// 3. Tabelle neu laden
		anzeigenTabelleAktionenPositionen(caId);
	}

	// POS LÖSCHEN
	public void btnPosEntf_OnClick() throws Exception
	{
		posAktuell = tblvwAktionPositionen.getSelectionModel().getSelectedItem().getCapoPos(); // aktuelle Position sichern
		AktionenListePositionenModel selectedPos = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		if (selectedPos == null)
		{
			Msgbox.show("Zeile löschen...", "Bitte eine Zeile zum Löschen markieren!");
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
		anzeigenTabelleAktionenPositionen(Integer.parseInt(txtCaId.getText()));
		// 4️⃣ Selektion setzen
		ObservableList<AktionenListePositionenModel> items = tblvwAktionPositionen.getItems();
		if (!items.isEmpty())
		{
			if (markierterIndex >= items.size())
			{
				markierterIndex = items.size() - 1;
			}
			tblvwAktionPositionen.getSelectionModel().select(markierterIndex);
			// tblvwAktionPositionen.scrollTo(markierterIndex);
		}
	}

	// POSITION LEEREN und unten anhängen (NEUER DS)
	@FXML
	private void btnPosgespielt_OnClick(ActionEvent event) throws Exception
	{
		Msgbox.show("Schon gespielt ...", "Dieses Stück wurde schon gespielt ...");

	}

	@FXML // Titelgrafikpfad auswählen...
	public void btnNaGrafikwahl_OnClick() throws Exception
	{
		//Msgbox.show("Grafik für die Notenausgabe", "Diese Grafik wird direkt der Aktionsliste-Position zugeordnet\n"
		//		+ "Es können bei Bedarf pro Position eigenständige Grafiken zugeordnet werden.\n Eine Synchronisation mit den Datensätzen des Notenarchiv (Notenausgaben) erfolgt nicht!");
		grafikwahlBox();
	}
	
	public void btnNaGrafikwahlLeer_OnClick() throws Exception
	{
		if(Msgbox.yesno("Grafik entfernen ...", "Soll die Titelgrafik der Notenausgabe von der Listenposition entfernt werden?\n"
				+ "Diese Aktion wird erst nach Speichern der Listenposition wirksam!\n\n"
				+ "Hinweis: Die Zuordnung Titelgrafik-Notenausgabe bleibt in der Literaturliste weiterhin bestehen!")==false) {
			return;
		}
		//grafikwahlBox();
		txtPosTitelgrafik.setText("");
		titelgrafikAnzeigen();
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
			File selectedFile = fileAuswahlbox.showOpenDialog(null);
			if (selectedFile == null)
			{
				return;
			}
			pathFile = selectedFile.toString();
			System.out.println(pathFile.substring(ValuesGlobals.progPfadGrafik.length(), selectedFile.toString().length()));
			pathFile = (pathFile.substring(ValuesGlobals.progPfadGrafik.length(), selectedFile.toString().length()));
			if (txtPosTitelgrafik.getLength() > 0)
			{
				if (Msgbox.yesno("Grafikpfad neu setzen", "Soll der bestehende Pfad zur Grafik überschrieben werden?") == true)
				{
					txtPosTitelgrafik.setText(pathFile);
				}
			}
			else
			{
				txtPosTitelgrafik.setText(pathFile);
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

	// ZWISCHENTEXT UMSCHALTUNG
	@FXML
	public void chkProgZwischentext_OnMouseClicked()
	{
		if (chkProgZwischentext.isSelected())
		{
			if (posBearbStatus == 1)
			{
				if (Msgbox.yesno("Programm-Zwischentext", "Der Eintrag wird umgewandelt in einen Programm-Zwischentext\n Der Stücktitel wird als Beschreibungsfeld verwendet\n"
						+ "wollen Sie den bestehenden Eintrag umwandeln?") == false)
				{
					chkProgZwischentext.setSelected(false);
					return;
				}
			}
			posFelderSperren();
			if (posBearbStatus == 2)
			{
				// neuer Datensatz
				lblZeilebearbeitenSetzen(5);
			}
			else if (posBearbStatus == 1)
			{
				// bearbeiten aus Liste und umwandlen in Zwischentext
				lblZeilebearbeitenSetzen(1);
				imgNaTitelgrafik.setImage(null);
				txtPosNotenausgabe.setText("");
			}
			else
			{
				lblZeilebearbeitenSetzen(5);
				imgNaTitelgrafik.setImage(null);
				txtPosNotenausgabe.setText("");

			}
		}
		else
		{
			if (posBearbStatus == 1)
			{
				if (Msgbox.yesno("Programm-Zwischentext", "Der Eintrag wird umgewandelt in eine Literaturposition!\n"
						+ "Wollen Sie den bestehenden Eintrag umwandeln?") == false)
				{
					chkProgZwischentext.setSelected(false);
					return;
				}
			}
			lblZeilebearbeitenSetzen(1);
			posFelderSperren();

		}
	}

	// LABEL Bearbeitungszustand anzeigen
	public void lblZeilebearbeitenSetzen(int vorgang)
	{
		switch (vorgang)
		{
		case 0:
			lblZeilebearbeiten.setText("Bitte wählen ... [Titel aus den Listen oder manuell neu eingeben]");
			lblZeilebearbeiten.setStyle("-fx-text-fill: black;");
			break;
		case 1:
			lblZeilebearbeiten.setText("Zwischentext an Zeile " + posAktuell + " bearbeiten ...");
			lblZeilebearbeiten.setStyle("-fx-text-fill: #C75A51;");
			break;
		case 2:
			lblZeilebearbeiten.setText("Zeile verschoben - neuer Eintrag vor Zeile " + (posAktuell + 1) + "");
			lblZeilebearbeiten.setStyle("-fx-text-fill: #808080;");
			break;
		case 3:
			lblZeilebearbeiten.setText("Literatureintrag vor Zeile " + posAktuell + " hinzufügen ...");
			lblZeilebearbeiten.setStyle("-fx-text-fill: #507F80;");
			break;
		case 4:
			lblZeilebearbeiten.setText("Neuen Eintrag vor Zeile " + posAktuell + " hinzufügen ...");
			lblZeilebearbeiten.setStyle("-fx-text-fill: #3580BB ;");
			break;
		case 5:
			lblZeilebearbeiten.setText("Neuen Zwischeneintrag vor Zeile " + posAktuell + " hinzufügen ...");
			lblZeilebearbeiten.setStyle("-fx-text-fill: #C75A51;");
			break;
		case 6:
			lblZeilebearbeiten.setText("Neuen Eintrag ans Ende anzufügen ...");
			lblZeilebearbeiten.setStyle("-fx-text-fill: #3580BB ;");
			break;
		case 7:
			lblZeilebearbeiten.setText("Neuen Zwischeneintrag ans Ende anzufügen ...");
			lblZeilebearbeiten.setStyle("-fx-text-fill: #C75A51;");
			break;

		default:
			break;
		}
	}

	// POSITION EIngabefelder leeren
	public void posTextfelderLeeren(int woher)
	{
		txtPosProgpkt.setText("");
		txtPosStckTitel.setText("");
		txtPosNotenausgabe.setText("");
		txtPosSeite.setText("");
		txtPosNr.setText("");
		txtPosTonart.setText("");
		txtPosKomponist.setText("");
		txtPosBearbeiter.setText("");
		txtPosStueckart.setText("");
		txtPosDauermin.setText(null);
		txtPosDauersec.setText(null);
		txtBemerkungen.setText("");
		txtPosBesetzung.setText("");
		txtPosTitelgrafik.setText("");
		txtPosLitId.setText("0");
		imgNaTitelgrafik.setImage(null);
	}

	// POSITION EIngabefelder alle sperren
	public void posAlleSperren(int woher)
	{
		txtPosBearbeiter.setDisable(true);
		txtPosBearbeiter.setText(null);
		txtPosKomponist.setDisable(true);
		txtPosKomponist.setText(null);
		txtPosStueckart.setDisable(true);
		txtPosStueckart.setText(null);
		txtPosSeite.setDisable(true);
		txtPosSeite.setText(null);
		txtPosNr.setDisable(true);
		txtPosNr.setText(null);
		txtPosTonart.setDisable(true);
		txtPosTonart.setText(null);
		txtPosDauermin.setDisable(true);
		txtPosDauermin.setText(null);
		txtPosDauersec.setDisable(true);
		txtPosDauersec.setText(null);
		txtBemerkungen.setDisable(true);
		txtBemerkungen.setText(null);
		txtPosBesetzung.setDisable(true);
		txtPosBesetzung.setText(null);
		txtPosStckTitel.setDisable(true);
		txtPosStckTitel.setText(null);
		txtPosProgpkt.setDisable(true);
		txtPosProgpkt.setText(null);
		txtPosNotenausgabe.setDisable(true);
		txtPosNotenausgabe.setText(null);
		chkProgQuellePcnd.setSelected(false);
		chkProgQuellePcnd.setDisable(true);
		chkProgZwischentext.setDisable(true);
		chkProgZwischentext.setSelected(false);
	}

	// POSITION EIngabefelder sperren je nach Eingabetyp
	public void posFelderSperren()
	{
		// wenn Zwischentext - dann PCND-spezisfische EIngabefelder leeren und sperren
		chkProgZwischentext.setDisable(false);
		if (chkProgZwischentext.isSelected())
		{
			txtPosProgpkt.setDisable(false);
			txtPosStckTitel.setDisable(false);
			txtBemerkungen.setDisable(false);
			
			txtPosNotenausgabe.setDisable(true);
			txtPosNotenausgabe.setText(null);
			txtPosSeite.setDisable(true);
			txtPosSeite.setText(null);
			txtPosNr.setDisable(true);
			txtPosNr.setText(null);
			txtPosTonart.setDisable(true);
			txtPosTonart.setText(null);
			txtPosKomponist.setDisable(true);
			txtPosKomponist.setText(null);
			txtPosBearbeiter.setDisable(true);
			txtPosBearbeiter.setText(null);
			txtPosStueckart.setDisable(true);
			txtPosStueckart.setText(null);
			txtPosBesetzung.setDisable(true);
			txtPosBesetzung.setText(null);
			txtPosTitelgrafik.setText(null);

			chkProgQuellePcnd.setSelected(false);
			chkProgQuellePcnd.setDisable(true);
			txtPosStckTitel.requestFocus();
		}
		else
		{
			txtPosProgpkt.setDisable(false);
			txtPosStckTitel.setDisable(false);
			// txtPosNotenausgabe.setDisable(false);
			txtPosSeite.setDisable(false);
			txtPosNr.setDisable(false);
			txtPosTonart.setDisable(false);
			txtPosKomponist.setDisable(false);
			txtPosBearbeiter.setDisable(false);
			txtPosStueckart.setDisable(false);
			txtPosDauermin.setDisable(false);
			txtPosDauersec.setDisable(false);
			txtPosBesetzung.setDisable(false);
			txtBemerkungen.setDisable(false);
		}
		if (chkProgQuellePcnd.isSelected() == true && chkProgZwischentext.isSelected() == false)
		{
			txtPosNotenausgabe.setDisable(false);
		}
		else if (chkProgQuellePcnd.isSelected() == false && chkProgZwischentext.isSelected() == false)
		{
			txtPosNotenausgabe.setDisable(false);
		}
		else
		{
			txtPosNotenausgabe.setDisable(true);
		}

	}

	// Tableview Zeile wählen und Inhalte in Textfelder schreiben
	@FXML
	void handleTblvwAktionPositionen_onmouse_clicked()
	{
		AktionenListePositionenModel selected = tblvwAktionPositionen.getSelectionModel().getSelectedItem();
		if (selected == null)
		{
			return;
		}
		int capopos = selected.getCapoPos();
		String capoprgpkt = selected.getCapoSonstiges();// PrgPkt
		String capostcktitel = selected.getCapoStcktitel();
		String caponotenausgabe = selected.getCapoEdition();
		String capokomponist = selected.getCapoKomponist();
		String capobearbeiter = selected.getCapoBearbeiter();
		String caposeite = selected.getCapoSeite();
		String caponummer = selected.getCapoNr();
		String capostckart = selected.getCapoArt();
		String capotonart = selected.getCapoTonart();
		int capodauermin = selected.getCapoDauermin();
		int capodauersec = selected.getCapoDauersec();
		int capozwischentext = selected.getCapoZwischentext();
		int capoquellepcnd = selected.isCapoLiteratur();
		int capoposlitid = selected.getCapoLitId();
		String capotitelgrafik = selected.getCapoTitelbild();
		String capobemerkung = selected.getCapoBem();
		String capobesetzung = selected.getCapoBesetzung();
		txtPosProgpkt.setText(capoprgpkt);
		txtPosStckTitel.setText(capostcktitel);
		txtPosNotenausgabe.setText(caponotenausgabe);
		txtPosKomponist.setText(capokomponist);
		txtPosBearbeiter.setText(capobearbeiter);
		txtPosSeite.setText(caposeite);
		txtPosNr.setText(caponummer);
		txtPosStueckart.setText(capostckart);
		txtPosTonart.setText(capotonart);
		txtPosDauermin.setText(Integer.toString(capodauermin));
		txtPosDauersec.setText(Integer.toString(capodauersec));
		txtPosLitId.setText(Integer.toString(capoposlitid));
		txtPosTitelgrafik.setText(capotitelgrafik);
		txtPosBesetzung.setText(capobesetzung);
		lblZeilebearbeiten.setText("Zeile " + capopos + " - bearbeiten");
		lblZeilebearbeiten.setStyle("-fx-text-fill: #A81C1E");
		posBearbStatus = 1; // Zeile aus Liste bearbeiten
		posAktuell = capopos;
		txtBemerkungen.setText(capobemerkung);
		if (capozwischentext == 0)
		{
			chkProgZwischentext.setSelected(false);
		}
		else
		{
			chkProgZwischentext.setSelected(true);
		}
		if (capoquellepcnd == 0)
		{
			chkProgQuellePcnd.setSelected(false);
		}
		else
		{
			chkProgQuellePcnd.setSelected(true);
		}
		titelgrafikAnzeigen();
		posFelderSperren();
		btnPosLeeren.setDisable(false);
		btnPosLoeschen.setDisable(false);
		btnNaGrafikwahl.setDisable(false);
		// tblvwLiteratur.getSelectionModel().clearSelection();
//
//		// -------------------- Tabs aktivieren --------------------
//
//		tabDrucken.setDisable(false);
	}

// #################* INIT- HILFSFUNKTIONEN *###########################
	// ===============================================================================================
	// FILTERBEREICH-ComboBoxen init -- erstellt alle Filter-Comboboxenlisten
	// ===============================================================================================
	public void initCombosFilterConverters() throws SQLException
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
		List<AktionenListeModel> baseList = db.getAktionenListeAll();
		// init(welcheCombobox, welche Listes, welches Modell::getter)
		initComboBox(cbxFilterAktion, baseList, AktionenListeModel::getCaakttyp, false);
		initComboBox(cbxFilterOrt, baseList, AktionenListeModel::getCaaktionsort, false);
		initComboBox(cbxFilterGruppe, baseList, AktionenListeModel::getCagruppe, false);
	}

	public void updateCombosFilterItems() throws Exception
	{
		// Stückarten
		List<StueckartlisteModel> listStckArt = db.getStueckartListeAll();
		listStckArt.sort(Comparator.comparing(StueckartlisteModel::getBez, String::compareToIgnoreCase));
		cbxFilterStueckart.setItems(FXCollections.observableArrayList(listStckArt));
		// Thema
		List<ThemenlisteModel> listThema = db.getThemenListeAll();
		listThema.sort(Comparator.comparing(ThemenlisteModel::getBez, String::compareToIgnoreCase));
		cbxFilterThema.setItems(FXCollections.observableArrayList(listThema));
		// Wochenlied
		List<WochenliedlisteModel> listWochenlied = db.getWochenliedlisteListeAll();
		listWochenlied.sort(Comparator.comparing(WochenliedlisteModel::getWolirang, String::compareToIgnoreCase));
		cbxFilterWochenlied.setItems(FXCollections.observableArrayList(listWochenlied));
		List<NotenmappeModel> listNoma = db.getNomaAll();
		listNoma.sort(Comparator.comparing(NotenmappeModel::getBez, String::compareToIgnoreCase));
		cbxFilterNotenmappe.setItems(FXCollections.observableArrayList(listNoma));
		// Optional: Auswahl zurücksetzen
		cbxFilterNotenmappe.getSelectionModel().clearSelection();
		// Optional: Erste Auswahl oder SelectionModel clear
		cbxFilterStueckart.getSelectionModel().clearSelection();
		cbxFilterThema.getSelectionModel().clearSelection();
		cbxFilterWochenlied.getSelectionModel().clearSelection();
		//// TODO 20.20.2026
		// Filter Register gespeicherte Aktionen
	}

	// ===============================================================================================
	// Tableviews init
	// ===============================================================================================
	private void initTblvwLiteraturliste()
	{
		lblFilterTab0Anzahl.setText("Kein Filter aus Literaturdaten aktiv ...");
		tblvwColId.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("id"));
		// tblvwColErfasst.setCellValueFactory(new
		// PropertyValueFactory<LiteraturlisteModel, String>("literfasst"));
		tblvwColTitel.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("littitel"));
		tblvwColEdit.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litedit"));
		// tblvwColDb.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel,
		// String>("dbkennung"));
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
		// tblvwColLitGrafik.setCellValueFactory(new
		// PropertyValueFactory<LiteraturlisteModel, String>("litgrafik"));
		tblvwColTArt.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("littonart"));
		// tblvwColWoli.setCellValueFactory(new
		// PropertyValueFactory<LiteraturlisteModel, String>("litwoli"));
		tblvwColThema.setCellValueFactory(new PropertyValueFactory<LiteraturlisteModel, String>("litthema"));
		// tblvwColNotenmappe.setCellValueFactory(new
		// PropertyValueFactory<LiteraturlisteModel, String>("litnoma"));
		tblvwLiteratur.setItems(oblist_lit);
	}

	private void initTableviewChorAktionenAusAufgef() // (1) definieren
	{
		// ✅ Wichtig: Die Strings in PropertyValueFactory müssen exakt den Getter-Namen
		// !!ohne get!! entsprechen (lsbibez → getLsbibez()).
		// TABLEVIEWES mit Spalten -----
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		// DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		// ---------------- Datum ----------------
		tblvwAktionenAufgefCol0.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCadatum()));
		tblvwAktionenAufgefCol0.setCellFactory(col -> new TableCell<AktionenListePositionenAufgefuehrtModel, LocalDate>()
		{
			@Override
			protected void updateItem(LocalDate item, boolean empty)
			{
				super.updateItem(item, empty);
				setText(empty || item == null ? "" : item.format(dateFormatter));
			}
		});
		// ---------------- Aktion ----------------
		tblvwAktionenAufgefCol1.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaakttyp() != null ? cellData.getValue().getCaakttyp() : ""));
		// ---------------- NA kurz ----------------
		tblvwAktionenAufgefCol2.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaakttyp() != null ? cellData.getValue().getCapoEdition() : ""));
		// ---------------- Nr ----------------
		tblvwAktionenAufgefCol3.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaakttyp() != null ? cellData.getValue().getCapoNr() : ""));
		// ---------------- S ----------------
		tblvwAktionenAufgefCol4.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaakttyp() != null ? cellData.getValue().getCapoSeite() : ""));
		// ---------------- Stückart ----------------
		tblvwAktionenAufgefCol5.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaakttyp() != null ? cellData.getValue().getCapoArt() : ""));
		// ---------------- Komponist ----------------
		tblvwAktionenAufgefCol6.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaakttyp() != null ? cellData.getValue().getCapoKomponist() : ""));
		// ---------------- Ort ----------------
		tblvwAktionenAufgefCol7.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCaaktionsort() != null ? cellData.getValue().getCaaktionsort() : ""));
		// ---------------- Gruppe ----------------
		tblvwAktionenAufgefCol8.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getCagruppe() != null ? cellData.getValue().getCagruppe() : ""));
		tblvwChoraktionenAufgef.setItems(oblist_pos_aufgef);
		// ---------------- Art Probe/Auff ----------------
		// tblvwAktionenCol9.setCellValueFactory(cellData -> new
		// ReadOnlyObjectWrapper<>(cellData.getValue().getCaauftrittstermin()));
	}

	private void anzeigenTableviewChorAktionenAusAufgef() // (1) definieren
	{
		String titel = tblvwLiteratur.getSelectionModel().getSelectedItem().getLittitel();
		String edition = tblvwLiteratur.getSelectionModel().getSelectedItem().getLitedit();
		String stueckart = tblvwLiteratur.getSelectionModel().getSelectedItem().getLitstueckart();
		int filterAllesProbeAuff = 0; // 0 = Probe, 1= Aufführung // alles = 9
		if (chkEditionGespielt.isSelected() == false)
		{
			edition = "%";
		}
		if (chkStueckartGespielt.isSelected() == false)
		{
			stueckart = "%";
		}
		if (radFilterAllesgespielt.isSelected() == true)
		{
			filterAllesProbeAuff = 9;
		}
		if (radFilterProbengespielt.isSelected() == true)
		{
			filterAllesProbeAuff = 0;
		}
		if (radFilterAuffgespielt.isSelected() == true)
		{
			filterAllesProbeAuff = 1;
		}
		// ---------------- Lade Daten aus DB ----------------
		try
		{
//			List<AktionenListePositionenAufgefuehrtModel> oblist_pos_aufgef = db.getAktionenPositionenAufgefuehrt(titel, edition,stueckart);
//			tblvwChoraktionenAufgef.getItems().setAll(oblist_pos_aufgef);
			oblist_pos_aufgef.setAll(db.getAktionenPositionenAufgefuehrt(titel, edition, stueckart, filterAllesProbeAuff));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	void clearFilterfelder()throws Exception
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
		cbxFilterNotenmappe.getSelectionModel().clearSelection();
		cbxFilterNotenmappe.getEditor().setText("");
		//oblist_lit.clear();
		oblist_pos_aufgef.clear();
		filternAktionen();
		//lblFilterTab0Anzahl.setText("Kein Filter aus Literaturdaten aktiv ...");
	}

	void clearFilterfelderAktionen() throws Exception
	{
		//dpFilterDatumBis.setValue(null);
//		dpFilterDatumVon.setValue(
//				LocalDate.now()
//						.minusYears(10)
//						.withDayOfYear(1));
		//dpFilterDatumVon.setValue(null);
		cbxFilterAktion.setValue(null);
		cbxFilterAktion.getEditor().setText("");
		cbxFilterGruppe.setValue(null);
		cbxFilterGruppe.getEditor().setText("");
		cbxFilterOrt.setValue(null);
		cbxFilterOrt.getEditor().setText("");		
		radFilterAlles.setSelected(true);
		filternAktionen();
		oblist_aktionenpos1.clear();
		//oblist_pos_aufgef.clear();
	}

	private void speichereFilterAktionenPositionen()
	{
		// Register aus gespeicherten Aktivitäten
		ConfigManager.saveFilterAktionPosDatumvon(dpFilterDatumVon.getEditor().getText());
		ConfigManager.saveFilterAktionPosDatumbis(dpFilterDatumBis.getEditor().getText());
		ConfigManager.saveFilterAktionPos(cbxFilterAktion.getEditor().getText());
		ConfigManager.saveFilterAktionPosOrt(cbxFilterOrt.getEditor().getText());
		ConfigManager.saveFilterAktionPosGruppe(cbxFilterGruppe.getEditor().getText());
		// Register aus Literaturdaten (synchron mit Notenarchiv)
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

	}

	public void restoreFilterAktionenPositionen() throws Exception
	{ // Filterwerte wieder holen aus Prop

		dpFilterDatumVon.getEditor().setText(ConfigManager.loadFilterAktionPosDatumvon());
		dpFilterDatumBis.getEditor().setText(ConfigManager.loadFilterAktionPosDatumbis());
		cbxFilterAktion.getEditor().setText(ConfigManager.loadFilterAktionPos());
		cbxFilterOrt.getEditor().setText(ConfigManager.loadFilterAktionPosOrt());
		cbxFilterGruppe.getEditor().setText(ConfigManager.loadFilterAktionPosGruppe());

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

		filtern(0);

		filternAktionen();

		Platform.runLater(dpFilterDatumVon::requestFocus);
		Platform.runLater(dpFilterDatumBis::requestFocus);
		Platform.runLater(btnFilterAn::requestFocus);

	}

	// ===============================================================================================
	// Tableview in Tab0 LITERATUR - Daten in Textfelder übergeben
	// ===============================================================================================
	@FXML // Tabelle Literatur gefiltert
	void handleTblvwLiteratur_OnMouseClicked()
	{
		holenLiteratur(1);
	}

	@FXML // Tabelle Literatur schon gespielt
	void handletblvwAktionenlisteGespielt_onmouse_clicked()
	{
		holenLiteratur(2);
	}

	@FXML // Tabelle Literatur einer gespeicherten Aktion
	void handleTblvwAktionPositionen1_onmouse_clicked()
	{
		holenLiteratur(3);
	}

	void holenLiteratur(int woher)
	{
		String capoprgpkt = "";
		String capostcktitel = "";
		String caponotenausgabe = "";
		String capokomponist = "";
		String capobearbeiter = "";
		String caposeite = "";
		String caponummer = "";
		String capostckart = "";
		String capotonart = "";
		String capobesetzung = "";
		int capoposlitid = 0;
		String capotitelgrafik = "";
		int capodauermin = 0;
		int capodauersec = 0;
		String capobemerkung = "";
		int capozwischentext = 0;
		int capoquellepcnd = 1;
		if (woher == 1)
		{
			LiteraturlisteModel selected = tblvwLiteratur.getSelectionModel().getSelectedItem();
			if (selected == null)
			{
				return;
			}
			capostcktitel = selected.getLittitel();
			caponotenausgabe = selected.getLitedit();
			capokomponist = selected.getLitkomp();
			capobearbeiter = selected.getLitbearb();
			caposeite = selected.getLitseite();
			caponummer = selected.getLitnr();
			capostckart = selected.getLitstueckart();
			capotonart = selected.getLittonart();
			capobesetzung = selected.getLitbesetzung();
			capoposlitid = selected.getId();
			capotitelgrafik = selected.getLitgrafik();
			capodauermin = 0;
			capodauersec = 0;
			if (selected.getLitmin() != null)
			{
				capodauermin = selected.getLitmin();
			}
			if (selected.getLitsec() != null)
			{
				capodauersec = selected.getLitsec();
			}
			capozwischentext = 0;
			capoquellepcnd = 1;
			capobemerkung = "";
		}
		else if (woher == 2)
		{
			AktionenListePositionenAufgefuehrtModel selected = tblvwChoraktionenAufgef.getSelectionModel().getSelectedItem();
			if (selected == null)
			{
				return;
			}
			capostcktitel = selected.getCapoStcktitel();
			caponotenausgabe = selected.getCapoEdition();
			capokomponist = selected.getCapoKomponist();
			capobearbeiter = selected.getCapoBearbeiter();
			caposeite = selected.getCapoSeite();
			caponummer = selected.getCapoNr();
			capostckart = selected.getCapoArt();
			capotonart = selected.getCapoTonart();
			capobesetzung = selected.getCapoBesetzung();
			capoposlitid = selected.getCapoLitId(); //
			capotitelgrafik = selected.getCapoTitelbild();
			capodauermin = 0;
			capodauersec = 0;
			if (selected.getCapoDauermin() != 0)
			{
				capodauermin = selected.getCapoDauermin();
			}
			if (selected.getCapoDauersec() != 0)
			{
				capodauersec = selected.getCapoDauersec();
			}
			capozwischentext = selected.getCapoZwischentext();
			capoquellepcnd = selected.getCapoLitId();// isCapoLiteratur();
			capobemerkung = selected.getCapoBem();
			// capobemerkung = "";
		}
		else if (woher == 3)
		{
			AktionenListePositionenModel selected = tblvwAktionPositionen1.getSelectionModel().getSelectedItem();
			if (selected == null)
			{
				return;
			}
			capoprgpkt = selected.getCapoSonstiges();
			capostcktitel = selected.getCapoStcktitel();
			caponotenausgabe = selected.getCapoEdition();
			capokomponist = selected.getCapoKomponist();
			capobearbeiter = selected.getCapoBearbeiter();
			caposeite = selected.getCapoSeite();
			caponummer = selected.getCapoNr();
			capostckart = selected.getCapoArt();
			capotonart = selected.getCapoTonart();
			capobesetzung = selected.getCapoBesetzung();
			capoposlitid = selected.getCapoLitId(); //
			capotitelgrafik = selected.getCapoTitelbild();
			capodauermin = 0;
			capodauersec = 0;
			if (selected.getCapoDauermin() != 0)
			{
				capodauermin = selected.getCapoDauermin();
			}
			if (selected.getCapoDauersec() != 0)
			{
				capodauersec = selected.getCapoDauersec();
			}
			capozwischentext = selected.getCapoZwischentext();
			capoquellepcnd = selected.getCapoLitId();// isCapoLiteratur();
			capobemerkung = selected.getCapoBem();
		}
		else
		{
			Msgbox.show("Fehler im Ablauf", "Übernahme von Zeilen gescheitert");
			return;
		}
		txtPosProgpkt.setText(capoprgpkt);
		txtPosStckTitel.setText(capostcktitel);
		txtPosNotenausgabe.setText(caponotenausgabe);
		txtPosKomponist.setText(capokomponist);
		txtPosBearbeiter.setText(capobearbeiter);
		txtPosSeite.setText(caposeite);
		txtPosNr.setText(caponummer);
		txtPosStueckart.setText(capostckart);
		txtPosTonart.setText(capotonart);
		txtPosDauermin.setText(Integer.toString(capodauermin));
		txtPosDauersec.setText(Integer.toString(capodauersec));
		txtPosLitId.setText(Integer.toString(capoposlitid));
		txtPosTitelgrafik.setText(capotitelgrafik);
		txtPosBesetzung.setText(capobesetzung);
		txtBemerkungen.setText("");
		lblZeilebearbeitenSetzen(3);
		posBearbStatus = 2; // Suche aus Literatur bereitstellen und evt. zur Liste hinzufügen (wichtig fürs
							// Speichern)
		txtBemerkungen.setText(capobemerkung);
		if (capozwischentext == 0)
		{
			chkProgZwischentext.setSelected(false);
		}
		else
		{
			chkProgZwischentext.setSelected(true);
		}
		if (capoquellepcnd == 0)
		{
			chkProgQuellePcnd.setSelected(false);
		}
		else
		{
			chkProgQuellePcnd.setSelected(true);
		}
		posFelderSperren();
		titelgrafikAnzeigen();
		posBearbStatus = 2;
		if (woher == 1)
		{// nur wenn oben geklickt wurde, darf die untere Tabelle neu aufgebaut werden
			anzeigenTableviewChorAktionenAusAufgef();
		}
		else {
				
		}
		// tblvwAktionPositionen.getSelectionModel().clearSelection();
	}

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
		// Zentrale Reset-Logik: Alle Tabellen & Eingabefelder vorbereiten
	}

	// ==================================================
	// Button-Handling rechte Seite Tab 0 Literaturfilter
	// ==================================================
	@FXML // Button Filtern...
	public void btnFilterAn_OnClick(ActionEvent event) throws Exception
	{
		int tabAktiv = 0;
		tabAktiv = tabPanePosFilter.getSelectionModel().getSelectedIndex();
		System.out.println(tabAktiv);
		if (tabAktiv == 0)
		{
			filtern(0);
			
			
		}
		else if (tabAktiv == 1)
		{
			
			filternAktionen();
			
		}
	}

	@FXML // Button Filter aus
	public void btnFilterAus_OnClick(ActionEvent event) throws Exception
	{
		int tabAktiv = 0;
		tabAktiv = tabPanePosFilter.getSelectionModel().getSelectedIndex();
		System.out.println(tabAktiv);
		if (tabAktiv == 0)
		{
			clearFilterfelder();
			filtern(0);
			//oblist_.clear();
		}
		else if (tabAktiv == 1)
		{
			clearFilterfelderAktionen();
			filternAktionen();
			


		}

//		try
//		{
//			clearFilterfelder();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}

	// =========================================================================================================
	// Filtervorgang
	// =========================================================================================================
	private void filtern(int filterLitId) throws Exception
	{
		System.out.println("filtern()");
		String filterlittitel = "";
		String filterlitedit = "";
		String filterlitkomp = "";
		String filterlitstueckart = "";
		String filterlitthema = "";
		String filterlitwoli = "";
		String filterlitnoma = "";
		String filternaverlag = "";
		String filterstckdicht = "";
		// setButtons();
		try
		{
			filterlittitel = txtFilterTitel.getText();
			filterlitedit = txtFilterEdit.getText();
			filterlitkomp = txtFilterKomp.getText();
			filterlitstueckart = cbxFilterStueckart.getEditor().getText();
			filterstckdicht = txtFilterDicht.getText();
			filternaverlag = txtFilterEditVerlag.getText();
			// Filtervariablen für Literatur
			filterlitthema = cbxFilterThema.getEditor().getText();
			filterlitwoli = cbxFilterWochenlied.getEditor().getText();
			filterlitnoma = cbxFilterNotenmappe.getEditor().getText();
			oblist_lit.clear();
			oblist_pos_aufgef.clear();
			List<LiteraturlisteModel> listadb = db.getLiteraturListeFilter(
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
			lblFilterTab0Anzahl.setText(oblist_lit.size() + " Literatureinträge gefiltert");
			// gefiltert (maximal " + ValuesGlobals.filtermax + ")");
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
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		// Finally - Bereich
		finally
		{
		}
	}

	public void titelgrafikAnzeigen()
	{
		//File fileFehlt = new File(ValuesGlobals.progPfadGrafik + ValuesGlobals.progPfadGrafikFehlt);
		File imgFile0 = new File(ValuesGlobals.progPfadGrafik + txtPosTitelgrafik.getText());
		if (imgFile0.isFile())
		{
			Image image0 = new Image(imgFile0.toURI().toString());
			imgNaTitelgrafik.setImage(image0);
		}
		else
		{
			imgNaTitelgrafik.setImage(null);
//			if (!tblvwLiteratur.getSelectionModel().isEmpty())
//			{
//				//Image imgfileLeer = new Image(fileFehlt.toURI().toString());
//				//imgNaTitelgrafik.setImage(imgfileLeer);
//			}
//			else
//			{
//				imgNaTitelgrafik.setImage(null);
//
//			}
		}
	}

	// ============================================================
	// Button-Handling rechte Seite Tab 1 - gespeicherte Aktionen
	// ===========================================================
	@FXML // Button Filtern...
	public void btnFiltergespAn_OnClick(ActionEvent event) throws Exception
	{
		filternAktionen();
	}

	@FXML // Button Filter aus
	public void btnFiltergespAus_OnClick(ActionEvent event)
	{
		try
		{
			clearFilterfelderAktionen();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	public void handleChkEditionGespielt_onAction()
	{
		anzeigenTableviewChorAktionenAusAufgef();
	}

	@FXML
	public void handleChkStueckartGespielt_onAction()
	{
		anzeigenTableviewChorAktionenAusAufgef();
	}

	@FXML
	public void handleOgrFilterGespielt_onChanged(RadioButton selected)
	{
		anzeigenTableviewChorAktionenAusAufgef();
//		switch (selected.getText())
//		{
//		case "alles":
//			System.out.println("Aktion für Option 1");
//			anzeigenTableviewChorAktionenAusAufgef();
//			break;
//
//		case "Auff.":
//			System.out.println("Aktion für Option 2");
//			break;
//		case "Prb":
//			System.out.println("Aktion für Option 2");
//			break;
//		}	
	}

	public void filternAktionen() throws Exception
	{
		oblist_aktionen.clear();
		oblist_aktionenpos1.clear();
		tblvwLiteratur.getSelectionModel().clearSelection();
		// Filterwerte direkt aus DatePicker
		LocalDate filterDatumVon = dpFilterDatumVon.getValue();
		LocalDate filterDatumBis = dpFilterDatumBis.getValue();
		String filterAktion = cbxFilterAktion.getEditor().getText();
		String filterAktionOrt = cbxFilterOrt.getEditor().getText();
		String filterAktionGruppe = cbxFilterGruppe.getEditor().getText();
		String filterAktionBeschreibung = "";
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
				filterAktionGruppe, filterArt, filterAktionBeschreibung);
		// ObservableList auffüllen
		oblist_aktionen.addAll(listaktionen);
		// TableView aktualisieren
		tblvwChoraktionen.getSortOrder().clear();
		tblvwChoraktionen.setItems(oblist_aktionen);
		lblFilterTab1Anzahl.setText(oblist_aktionen.size() + " gespeicherte Aktivitäten gefunden");
		// lblFilterTab1Anzahl.setText(oblist_aktionen + " Literatureinträge");
		// leerenTabelleAktionenPositionen();
	}

	@FXML
	void handletblvwAktionenliste_onmouse_clicked()
	{
		AktionenListeModel selected = tblvwChoraktionen.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		try
		{
			// tblvwAktionPositionen
			anzeigenTabelleAktionenPositionen1();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void anzeigenTabelleAktionenPositionen1() throws Exception // (2) anzeigen nach Speichern
	{
		int aktuelleAktion = 0;
		aktuelleAktion = tblvwChoraktionen.getSelectionModel().getSelectedItem().getCaid();
		// System.out.println("aktuelle Aktion " + aktuelleAktion);
		// Tableview rechts in Tab1 unten
		oblist_aktionenpos1.clear();
		List<AktionenListePositionenModel> listaktionen1 = db.getAktionenPositionenListeAll(aktuelleAktion);
		oblist_aktionenpos1.addAll(listaktionen1);
		tblvwAktionPositionen1.setFixedCellSize(26);
		tblvwAktionPositionen1.getSortOrder().clear(); // Sortierung in SQL ohne Steuerelemts Sortierung !! vor SetItems
		tblvwAktionPositionen1.setItems(oblist_aktionenpos1);
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
		if (autoopen == true)
		{
			installAutoOpenOnFocus(combo);
		}
	}

	// Combo klappt runter, wenn man mit der Tab-Taste draufgeht
	private void installAutoOpenOnFocus(ComboBox<?> combo)
	{
		combo.focusedProperty().addListener((obs, oldF, newF) -> {
			if (newF)
			{
				Platform.runLater(combo::show);
			}
		});
	}
}