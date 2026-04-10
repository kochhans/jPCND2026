package application.controllers;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import application.ValuesGlobals;
import application.models.EditionenlisteKompaktModel;
import application.models.NotenmappeInhaltModel;
import application.models.NotenmappeModel;
import application.models.VerlaglisteModel;
import application.uicomponents.Msgbox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class FrmNotenmappenEditController
{
	@FXML
	private AnchorPane rootPane;
	// Labels
	@FXML
	private Label lblHaupttitel;
	@FXML
	private Label lblNotenmappenInfozeile;
	@FXML
	private Label lblNotenmappenInfozeile1;
	//

	// Filter Radio Group in ToggleGroups
	// --
	@FXML
	private ToggleGroup toggleGrpDb1;
	@FXML
	private RadioButton radAdb;
	@FXML
	private RadioButton radPdb;
	@FXML
	private RadioButton radAdbPdb;
	// TableViews und Columns
	// --
	@FXML
	private TableView<NotenmappeModel> tblvwNm;
	@FXML
	private TableColumn<NotenmappeModel, String> tblvwColNmbez;
	@FXML
	private TableColumn<NotenmappeModel, String> tblvwColNmbem;
	@FXML
	private TableColumn<NotenmappeModel, String> tblvwColNmId;

	@FXML
	private TableView<NotenmappeInhaltModel> tblvwNmInhalt;
	@FXML
	private TableColumn<NotenmappeInhaltModel, String> tblvwColNmInhEdition;
	@FXML
	private TableColumn<NotenmappeInhaltModel, String> tblvwColNmInhAnz;
	@FXML
	private TableColumn<NotenmappeInhaltModel, String> tblvwColNmInhLo1;
	@FXML
	private TableColumn<NotenmappeInhaltModel, String> tblvwColNmInhLo2;
	@FXML
	private TableColumn<NotenmappeInhaltModel, String> tblvwColNmInhLo3;
	@FXML
	private TableColumn<NotenmappeInhaltModel, String> tblvwColNmInhBem;
	@FXML
	private TableColumn<NotenmappeInhaltModel, String> tblvwColInhaltId;
	@FXML
	private TableColumn<NotenmappeInhaltModel, String> tblvwColInhaltGrafik;

	@FXML
	private TableView<EditionenlisteKompaktModel> tblvwNmEditAuswahl;
	@FXML
	private TableColumn<EditionenlisteKompaktModel, String> tblvwColNmAuswahlEditon;
	@FXML
	private TableColumn<EditionenlisteKompaktModel, String> tblvwColNmAuswahlVerlag;
	@FXML
	private TableColumn<EditionenlisteKompaktModel, String> tblvwColNmAuswahlId;

	// Textfelder zum Editieren
	@FXML
	private TextField txtNm;
	@FXML
	private TextField txtNmBemerkung;
	@FXML
	private TextField txtNotenmappeIdCheck;
	@FXML
	private TextField txtNmInhaltEditAnzahl;
	@FXML
	private TextField txtNmInhaltEditLo2;
	@FXML
	private TextField txtNmInhaltEditLo1;
	@FXML
	private TextField txtNmInhaltEditLo3;
	@FXML
	private TextField txtNmInhaltEditBem;
	@FXML
	private TextField txtNmInhaltEdition;

	// Textfelder zum Filtern
	@FXML
	private TextField txtFilterNomaEdition;
	@FXML
	private TextField txtFilterNomaVerlag;

	// Buttons
	@FXML
	private Button btnNomaNeu;
	@FXML
	private Button btnNomaSpeichern;
	@FXML
	private Button btnNomaLoeschen;
	@FXML
	private Button btnNomaEditWeg;
	@FXML
	private Button btnNomaEditHinzu;
	@FXML
	private Button btnNomaInhaltSpeichern;
	@FXML
	private Button btnNomaEditHinzuFilterAus;
	@FXML
	private Button btnNomaEditHinzuFilter;
	@FXML
	private Button btnNomaZurueck;

	// Comboboxen
	@FXML
	private ComboBox<VerlaglisteModel> cbxNaEditVerlag;

	// Image
	@FXML
	private ImageView imgNomaGrafik;

	@FXML
	private ImageView imgNomaGrafik1;

	// Observable Lists
	ObservableList<NotenmappeModel> oblistnoma = FXCollections.observableArrayList();
	ObservableList<NotenmappeInhaltModel> oblistnomainhalt = FXCollections.observableArrayList();
	ObservableList<EditionenlisteKompaktModel> oblistnomaeditliste = FXCollections.observableArrayList();
	// ==============================================================================================
	String dbauswahl = "";
	String filteredition = "";
	String filterverlag = "";
	@SuppressWarnings("unused")
	private Stage stage;

	private DatabaseControllerPcndNotenmappen db;
	public void setDb(DatabaseControllerPcndNotenmappen db)
	{
	    this.db = db;
	}
	
	public void initialize() throws Exception
	{
		//leer lassen!!!!
	}

	
	// init(Stage stage)--  wird vom Startcontroller aufgerufen
	public void init(Stage stage) throws Exception
	{
		this.stage = stage;
		//this.db = new DatabaseControllerPcndNotenmappen();
		// this.db.initConnection(); // DB-Verbindung aufbauen kommt aus DB-Manager.getConnection
		//ab hier alles sicher
		//	initCombos();
		this.setUIDataNotenmappenliste();//<--- hier liegt der Fehler 
		
		this.setUIDataNotenmappenlisteInhalt();
		this.setUIDataEditliste();

		lblNotenmappenInfozeile1.setText("");
		tblvwNm.getColumns().forEach(col -> col.setReorderable(false));
		tblvwNmEditAuswahl.getColumns().forEach(col -> col.setReorderable(false));
		tblvwNmInhalt.getColumns().forEach(col -> col.setReorderable(false));
	}

	@FXML
	public void btnNomaZurueck_OnClick(ActionEvent event)
	{
		this.stage.close();
//		Stage stage = (Stage) ((Node) event.getSource())
//				.getScene()
//				.getWindow();
//		stage.close();
	}

	// #########################################################################################
	// #########################################################################################

	// ######### Notenmappen -- Liste links oben ###########
	// ## enthält die Bezeichnungen der Notenmappen und Bemerkungen
	private void setUIDataNotenmappenliste() throws Exception
	{

		//alt: DatabaseControllerPcndNotenmappen nomacon = new DatabaseControllerPcndNotenmappen();
		//alt: List<NotenmappeModel> listnoma = nomacon.getNomaListeAll();
		List<NotenmappeModel> listnoma = db.getNomaListeAll();
		oblistnoma.clear();
		oblistnoma.addAll(listnoma);

		tblvwColNmbez.setCellValueFactory(new PropertyValueFactory<NotenmappeModel, String>("bez"));
		tblvwColNmbem.setCellValueFactory(new PropertyValueFactory<NotenmappeModel, String>("bem"));
		tblvwColNmId.setCellValueFactory(new PropertyValueFactory<NotenmappeModel, String>("id"));

		tblvwNm.setItems(oblistnoma);

		tblvwNm.getSortOrder().add(tblvwColNmbez);// sortieren nach erster Spalte

	}

	public void tblvwNoma_OnMouseRel() throws Exception
	{
		oblistnomainhalt.clear();
		String filternoma;
		//DatabaseControllerPcndNotenmappen pdbcon1 = new DatabaseControllerPcndNotenmappen();
		filternoma = tblvwNm.getSelectionModel().getSelectedItem().getBez();
		List<NotenmappeInhaltModel> listinhaltpdb = db.getNomaListeInhaltAll(filternoma);
		listinhaltpdb.forEach((item) -> oblistnomainhalt.add(item));
		tblvwNmInhalt.getSortOrder().add(tblvwColNmInhEdition);
		txtNotenmappeIdCheck.setText(Integer.toString(tblvwNm.getSelectionModel().getSelectedItem().getId()));
		txtNm.setText(tblvwNm.getSelectionModel().getSelectedItem().getBez());
		txtNmBemerkung.setText(tblvwNm.getSelectionModel().getSelectedItem().getBem());
		lblNotenmappenInfozeile1.setText("Notenmappe ist ausgewählt");
	}

	// Button Handlings
	public void btnNomaNeu_OnClick() throws Exception
	{
		txtNm.setText("");
		txtNmBemerkung.setText("");
		txtNotenmappeIdCheck.setText("");
		oblistnomainhalt.clear();
		tblvwNm.getSelectionModel().clearSelection();
		tblvwNmInhalt.getSelectionModel().clearSelection();
		NotenmappeninhaltLeeren();
		lblNotenmappenInfozeile1.setText("Notenmappe wird neu angelegt...");
		// String a = new
		// SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		// txtNm.setText("Notenmappe " + a);
		txtNm.requestFocus();
	}

	public void btnNomaSpeichern_OnClick() throws Exception
	{
		int sqlErrNr = 0;
		String eingabenomaid = txtNotenmappeIdCheck.getText();
		String eingabenomabez = txtNm.getText();
		String eingabenomabem = txtNmBemerkung.getText();
		//String eingabenomaid = txtNotenmappeIdCheck.getText();
		if (eingabenomabez.equals("") || eingabenomabez.equals(null))
		{
			Msgbox.warn("Notenmappe speichern", "Bitte geben Sie eine Bezeichnung für die Notenmappe an!");
			txtNm.requestFocus();
			return;
		}
		

		//DatabaseControllerPcndNotenmappen pdbcon = new DatabaseControllerPcndNotenmappen();

		try
		{
			if (txtNotenmappeIdCheck.getText().equals(""))
			{// neuer Datensatz speichern
				db.setNomaListeNeu(eingabenomabez, eingabenomabem);
			}
			else
			{// bestehender Datensatz ändern
				Integer eingabenomaidint = Integer.parseInt(eingabenomaid);
				db.setNomaListeEdit(eingabenomabez, eingabenomabem, eingabenomaidint); // .setNomaListeEdit(eingabenomabez, eingabenomabem, eingabenomaid);
			}
		}
		catch (SQLException e)
		{
			sqlErrNr = ((SQLException) e).getErrorCode();
			switch (sqlErrNr)
			{
			case 1:
				break;
			case 19:
				Msgbox.warn("Notenmappe speichern... ", "Speichern nicht möglich!\nDie Notenmappenbezeichnung" + eingabenomabez + "\nexistiert schon."
						+ " \n\nBitte eine andere Bezeichnung verwenden! ");

				txtNm.requestFocus();
				break;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		setUIDataNotenmappenliste();
		setUIDataNotenmappenlisteInhalt();
		btnNomaNeu_OnClick();
		// listpdb.forEach((item) -> oblistnoma.add(item));
		// tblvwNm.getSortOrder().add(tblvwColNmbez);
	}

	public void btnNomaLoeschen_OnClick() throws Exception
	{
		int sqlErrNr = 0;
		String sqlErrText = "";
		String eingabenomabez = txtNm.getText();
		int eingabenomaid = Integer.parseInt(txtNotenmappeIdCheck.getText());
		if (eingabenomabez.equals(""))
		{
			Msgbox.warn("Notenmappe entfernen...", "Bitte eine Notenmappe zum Löschen auswählen!");
		}
		//DatabaseControllerPcndNotenmappen pdbcon = new DatabaseControllerPcndNotenmappen();
		try
		{
			if (txtNotenmappeIdCheck.getText().equals(""))
			{
				return;
			}
			else
			{
				db.setNomaListeDelete(eingabenomaid);
				txtNm.setText("");
				txtNmBemerkung.setText("");
				txtNotenmappeIdCheck.setText("");
				oblistnoma.clear();
				setUIDataNotenmappenliste();
				btnNomaNeu_OnClick();
			}
		}
		catch (SQLException e)
		{
			sqlErrNr = ((SQLException) e).getErrorCode();
			sqlErrText = ((SQLException) e).getMessage();
			System.out.println("Löschen: " + sqlErrNr + "\n" + sqlErrText);
			switch (sqlErrNr)
			{
			case 1:
				break;
			case 19:
				Msgbox.warn( "Noten mappe löschen...", "Löschen nicht möglich!\nDie Notenmappe\n--  " + eingabenomabez + "  --\n "
						+ "enthält noch mindestens eine Notenausgabe.\n\n "
						+ "Bitte komplett leeren...");
				txtNm.requestFocus();
				break;
			default:
				System.out.println("Löschen: " + sqlErrNr + "\n" + sqlErrText);
				break;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ++++++++++ Notenmappen-Inhalt -- Liste links unten +++++++++++++++
	// enthält die Zuweisungen von Editionen zu Notenmappen n:m

	private void setUIDataNotenmappenlisteInhalt() throws Exception
	{
		tblvwColInhaltId.setCellValueFactory(new PropertyValueFactory<NotenmappeInhaltModel, String>("id"));
		tblvwColNmInhAnz.setCellValueFactory(new PropertyValueFactory<NotenmappeInhaltModel, String>("nomaanzahl"));
		tblvwColNmInhBem.setCellValueFactory(new PropertyValueFactory<NotenmappeInhaltModel, String>("nomabem"));
		tblvwColNmInhLo1.setCellValueFactory(new PropertyValueFactory<NotenmappeInhaltModel, String>("nomalag1"));
		tblvwColNmInhLo2.setCellValueFactory(new PropertyValueFactory<NotenmappeInhaltModel, String>("nomalag2"));
		tblvwColNmInhLo3.setCellValueFactory(new PropertyValueFactory<NotenmappeInhaltModel, String>("nomalag3"));
		tblvwColNmInhEdition.setCellValueFactory(new PropertyValueFactory<NotenmappeInhaltModel, String>("edit"));
		tblvwColInhaltGrafik.setCellValueFactory(new PropertyValueFactory<NotenmappeInhaltModel, String>("titelgrafik"));
		tblvwNmInhalt.setItems(oblistnomainhalt);
		tblvwNmInhalt.getSortOrder().add(tblvwColNmInhEdition);
		tblvwNmInhalt.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

	}

	public void tblvwNomaInhalt_OnMouseRel() throws Exception
	{
		if ((oblistnomainhalt.isEmpty()) || (tblvwNmInhalt.getSelectionModel().getSelectedCells().isEmpty()))// nur, wenn in tblView eine Zeile vorhanden
		{
			return;
		}
		else
		{
			txtNmInhaltEditBem.setText(tblvwNmInhalt.getSelectionModel().getSelectedItem().getNomabem());
			txtNmInhaltEditLo1.setText(tblvwNmInhalt.getSelectionModel().getSelectedItem().getNomalag1());
			txtNmInhaltEditLo2.setText(tblvwNmInhalt.getSelectionModel().getSelectedItem().getNomalag2());
			txtNmInhaltEditLo3.setText(tblvwNmInhalt.getSelectionModel().getSelectedItem().getNomalag3());
			txtNmInhaltEdition.setText(tblvwNmInhalt.getSelectionModel().getSelectedItem().getEdit());
			txtNmInhaltEditAnzahl.setText(Integer.toString(tblvwNmInhalt.getSelectionModel().getSelectedItem().getNomaanzahl()));
			titelgrafikanzeigen1();
		}
	}

	public void NotenmappeninhaltLeeren()
	{
		txtNmBemerkung.setText("");
		txtNotenmappeIdCheck.setText("");
		txtNmInhaltEditAnzahl.setText("");
		txtNmInhaltEditBem.setText("");
		txtNmInhaltEdition.setText("");
		txtNmInhaltEditLo1.setText("");
		txtNmInhaltEditLo2.setText("");
		txtNmInhaltEditLo3.setText("");
		imgNomaGrafik1.setImage(null);
	}

	// ######## Editionenauswahl -- Liste rechts ##############
	private void setUIDataEditliste() throws Exception // verbessert 01.2026
	{
		String filterna = txtFilterNomaEdition.getText();
		String filterverlag = txtFilterNomaVerlag.getText();
		//DatabaseControllerPcndNotenmappen dbController = new DatabaseControllerPcndNotenmappen();
		// DatabaseControllerPcndNotenmappen dbController = new
		// DatabaseControllerPcndNotenmappen();
		List<EditionenlisteKompaktModel> listedit = db.getNomaEditionenliste(filterna, filterverlag);// .getNomaEditionenliste(); //.getEditionenListeFilterNoma(filterna,
																												// filterverlag);

		oblistnomaeditliste.clear();
		oblistnomaeditliste.addAll(listedit);

		// 1️⃣ CellValueFactories (IMMER zuerst)
		tblvwColNmAuswahlEditon.setCellValueFactory(
				new PropertyValueFactory<>("lt"));
		tblvwColNmAuswahlVerlag.setCellValueFactory(
				new PropertyValueFactory<>("verlag"));

		// 2️⃣ Items genau einmal setzen
		tblvwNmEditAuswahl.setItems(oblistnomaeditliste);

		// 3️⃣ Auswahlmodus
		tblvwNmEditAuswahl.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);

		// 4️⃣ Sortierung (nach setItems!)
		tblvwNmEditAuswahl.getSortOrder().clear();
		tblvwNmEditAuswahl.getSortOrder().add(tblvwColNmAuswahlEditon);
	}

	public void tblvwNmEditAuswahl_OnMouseRel() // 01.2026
	{
		titelgrafikanzeigen();
	}

	public void btnNomaEditHinzuFilter_OnClick() throws Exception // 01.2026
	{
		setUIDataEditliste();
	}

	public void btnNomaEditHinzuFilterAus_OnClick() throws Exception // 01.2026
	{
		txtFilterNomaEdition.setText("");
		txtFilterNomaVerlag.setText("");
		setUIDataEditliste();
	}

	// ## Notenmappeninhalt ändern ###################################
	@SuppressWarnings("unused")
	@FXML
	public void btnNomaEditWeg_OnClick() throws Exception
	{
		int sqlErrNr = 0;
		Integer nmid;
		String nminhaltzeileid = "";
		//DatabaseControllerPcndNotenmappen con = new DatabaseControllerPcndNotenmappen();
		if (tblvwNmInhalt.getSelectionModel().isEmpty())
		{
			// nichts markiert oder Liste leer
			Msgbox.show("Edition aus der Notenmappe entfrenen ...", "Zum Entfernen von Editionen bitte eine oder mehrere Zeilen makieren!");
		}
		else
		{
			if (tblvwNmInhalt.getSelectionModel().getSelectedItems().size() > 1)
			{ // Mehrfachauswahl auslesen
				ObservableList<NotenmappeInhaltModel> mehrfachauswahl = tblvwNmInhalt.getSelectionModel().getSelectedItems();
				for (NotenmappeInhaltModel mFa : mehrfachauswahl)
				{
					nmid = mFa.getId();
					nminhaltzeileid = mFa.getNomabez();
					db.setNomaInhaltLoeschen(nmid);

				}
			}
			else
			{
				nmid = tblvwNmInhalt.getSelectionModel().getSelectedItem().getId();
				try
				{
					db.setNomaInhaltLoeschen(nmid);
					NotenmappeninhaltLeeren();
				}
				catch (SQLException e)
				{
					sqlErrNr = ((SQLException) e).getErrorCode();
					switch (sqlErrNr)
					{
					case 1:
						break;
					}
				}
				finally
				{

				}
			}
			
			NotenmappeninhaltLeeren();
			tblvwNoma_OnMouseRel();
		}
	}

	public void btnNomaEditHinzu_OnClick() throws Exception
	{
		if (txtNotenmappeIdCheck.getText().isEmpty())
		{
			Msgbox.show("Editionen hinuzufügen...", "Bitte eine Notenmappe zum Befüllen \n von Editionen aktivieren!");

			return;
		}
		if (tblvwNmEditAuswahl.getSelectionModel().getSelectedItem() == null)
		{
			Msgbox.show("Editonenen hinzufügen...", "Bitte eine Edition (Notenausgabe) zum Hinzufügen in \n die Notenmappe auswählen!");

			return;
		}
		else
		{
			int sqlErrNr = 0;
			String eingabenomabez = txtNm.getText();
			String eingabeedithinzu = tblvwNmEditAuswahl.getSelectionModel().getSelectedItem().getLt();
			String titelgrafik = tblvwNmEditAuswahl.getSelectionModel().getSelectedItem().getTpfad();
			//DatabaseControllerPcndNotenmappen pdbcon = new DatabaseControllerPcndNotenmappen();
			if (tblvwNmEditAuswahl.getSelectionModel().getSelectedItems().size() > 1)
			{ // Mehrfachauswahl auslesen
				ObservableList<EditionenlisteKompaktModel> mehrfachauswahl = tblvwNmEditAuswahl.getSelectionModel().getSelectedItems();
				int anzUnique = 0;
				String naUnique = "";
				for (EditionenlisteKompaktModel mFa : mehrfachauswahl)
				{
					try
					{
						eingabeedithinzu = mFa.getLt();
						titelgrafik = mFa.getTpfad();
						db.setNomaInhaltHinzu(eingabenomabez, eingabeedithinzu, titelgrafik);
					}
					catch (SQLException e)
					{
						sqlErrNr = ((SQLException) e).getErrorCode();
						System.out.println(sqlErrNr);
						switch (sqlErrNr)
						{
						case 19:
							anzUnique++;
							naUnique += " " + eingabeedithinzu + "\n";

							break;

						default:
							System.out.println(sqlErrNr);

						}
					}
				}
				if (anzUnique > 0)
				{
					Msgbox.warn("Editionen schon zugewiesen", "Insgesamt waren bereits " + anzUnique + " Notenausgaben\n schon in der Notenmappe vorhanden!\n"
							+ naUnique);
				}
			}
			else // Einzelausgabe
			{
				try
				{
					db.setNomaInhaltHinzu(eingabenomabez, eingabeedithinzu, titelgrafik);
				}
				catch (SQLException e)
				{
					sqlErrNr = ((SQLException) e).getErrorCode();
					System.out.println(sqlErrNr);
					switch (sqlErrNr)
					{
					case 19:
						Msgbox.show("Notenausgabe schon vorhanden...", "Diese Notenausgabe ist schon in der Notenmappe vorhanden!");
						break;

					default:
						System.out.println(sqlErrNr);
					}
				}
				finally
				{

				}
			}

			tblvwNoma_OnMouseRel();
			tblvwNmEditAuswahl.getSelectionModel().clearSelection();
			imgNomaGrafik.setImage(null);
			
		}
	}

	public void btnNomaInhaltSpeichern_OnClick() throws Exception
	{
		if (tblvwNmInhalt.getSelectionModel().getSelectedCells().isEmpty())
		{
			Msgbox.warn("Notenmappenihalt speichern...", "Bitte eine Zeile zum Ändern auswählen!");

			return;
		}
		int sqlErrNr = 0;
		Integer nminhid;
		nminhid = tblvwNmInhalt.getSelectionModel().getSelectedItem().getId();
		String nminhanz = "0";
		String nminhbem = "";
		String nminhlo1 = "", nminhlo2 = "", nminhlo3 = "";
		nminhanz = txtNmInhaltEditAnzahl.getText();
		nminhbem = txtNmInhaltEditBem.getText();
		if (txtNmInhaltEditBem.getText() == null)
		{
			nminhbem = "";
		}
		nminhlo1 = txtNmInhaltEditLo1.getText();
		nminhlo2 = txtNmInhaltEditLo2.getText();
		nminhlo3 = txtNmInhaltEditLo3.getText();
		//DatabaseControllerPcndNotenmappen pdbcon = new DatabaseControllerPcndNotenmappen();
		try
		{
			if (nminhid == null)
			{// neuer Datensatz speichern
				return;
			}
			else
			{// bestehender Datensatz �ndern
				db.setNomaInhaltEdit(nminhid, nminhanz, nminhbem, nminhlo1, nminhlo2, nminhlo3);
			}
		}
		catch (SQLException e)
		{
			sqlErrNr = ((SQLException) e).getErrorCode();
			switch (sqlErrNr)
			{
			case 1:
				break;
			case 19:
				Msgbox.warn("Speichern nicht möglich", "Die Notenmappe " + "XX" + "\nexistiert schon."
						+ " \n\nBitte eine andere Bezeichnung verwenden! ");

				txtNm.requestFocus();
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		tblvwNoma_OnMouseRel();
		txtNmInhaltEditAnzahl.setText("");
		txtNmInhaltEditBem.setText("");
		txtNmInhaltEditLo1.setText("");
		txtNmInhaltEditLo2.setText("");
		txtNmInhaltEditLo3.setText("");
		txtNmInhaltEdition.setText("");
		tblvwNmInhalt.getSelectionModel().select(-1);
		imgNomaGrafik1.setImage(null);
	}

	public void titelgrafikanzeigen()
	{
		try
		{
			File imgFile0 = new File(ValuesGlobals.progPfadGrafik + tblvwNmEditAuswahl.getSelectionModel().getSelectedItem().getTpfad());
			File fileFehlt = new File(ValuesGlobals.progPfadGrafik + ValuesGlobals.progPfadGrafikFehlt);
			if (imgFile0.isFile())
			{
				Image image0 = new Image(imgFile0.toURI().toString());
				imgNomaGrafik.setImage(image0);
			}
			else
			{
				Image imgfileLeer = new Image(fileFehlt.toURI().toString());
				imgNomaGrafik.setImage(imgfileLeer);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	public void titelgrafikanzeigen1()
	{
		File imgFile1 = new File(ValuesGlobals.progPfadGrafik + tblvwNmInhalt.getSelectionModel().getSelectedItem().getTitelgrafik());
		File fileFehlt2 = new File(ValuesGlobals.progPfadGrafik + ValuesGlobals.progPfadGrafikFehlt);
		if (imgFile1.isFile())
		{
			Image image1 = new Image(imgFile1.toURI().toString());
			imgNomaGrafik1.setImage(image1);
		}
		else
		{
			Image imgfileLeer2 = new Image(fileFehlt2.toURI().toString());
			imgNomaGrafik1.setImage(imgfileLeer2);
		}
	}

}
