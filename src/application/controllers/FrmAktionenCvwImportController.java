package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import application.models.CvwPersonenModel;

// .....................................................
public class FrmAktionenCvwImportController
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

	// Liste für Tableview FilterLiteratur
	// Tableview Choraktionen aus gespeicherten Aktionen
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
	ObservableList<CvwPersonenModel> oblist_cvwpersonen = FXCollections.observableArrayList();

	// ####################################################################################################

	// ================================================================================================
	// Variablen

	@FXML
	private void handleImport()
	{
		// Gefilterte Daten nehmen
		// und via mainDb in normale DB schreiben
	}

	// -----------------------------------------------------------------
	@FXML
	public void initialize()
	{
		System.out.println("FXML initialize (nur 1x)");
		initTableCvwPersonenImport();
		// Listener für Auswahländerung
	}

	@FXML
	public void onShow(int caid) throws Exception
	{// Hier die Scene-spezifische Initialisierung

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
}
