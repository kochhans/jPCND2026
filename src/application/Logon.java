package application;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Logon  extends Stage
{
	//public LoginModel loginModel = new LoginModel();
	String gedrueckterButton="";
	protected final GridPane myLogonGridPane = new GridPane();
	

	public Logon(Stage stLetzte) throws Exception
	{
	
		anzeigen(stLetzte);
	}
	//----- gedrueckte Schaltfl�che... ---------


	private void anzeigen(Stage stLetzte) throws Exception
	{
	//Stage aendern
		this.initOwner(stLetzte);
		this.initModality(Modality.APPLICATION_MODAL);
		this.centerOnScreen();
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setTitle("Anmelden...");		
		
	//GridPane anlegen mit Abstandsangaben f�r die Elemente
		GridPane layoutGridPane = new GridPane();
		layoutGridPane.setPadding(new Insets(10,10,10,10));
		layoutGridPane.setVgap(8);
		layoutGridPane.setHgap(10);
		
		//--Steuerelemente ins Grid setzen (Element,Spalte,Zeile)
		Label usernameLabel = new Label("Benutzerkennung");
		usernameLabel.setMinWidth(100);
		GridPane.setConstraints(usernameLabel, 0, 0); 
	
		TextField usernameTextfeld = new TextField("....");
		usernameTextfeld.setMinWidth(100);
		GridPane.setConstraints(usernameTextfeld, 1, 0);	
	
		Label passwortLabel = new Label("Passwort");
		GridPane.setConstraints(passwortLabel, 0, 2);
	
		PasswordField pwPasswort = new PasswordField();
		pwPasswort.setMinWidth(100);
		pwPasswort.setPromptText("Passwort eingeben"); 
		GridPane.setConstraints(pwPasswort, 1, 2);		
	
		Button abbrechenButton = new Button("Abbrechen");
		abbrechenButton.setMinWidth(250);		
		GridPane.setConstraints(abbrechenButton, 1, 4);	
		abbrechenButton.setOnAction(e -> 
		{
			this.gedrueckterButton=abbrechenButton.getText();
			this.close();
		});		

		Button loginButton = new Button("Login...");
		GridPane.setConstraints(loginButton, 1, 3);	
		loginButton.setMinWidth(250);			
		loginButton.setOnAction(e -> 
		{
			this.gedrueckterButton=loginButton.getText();
			this.close();
		});

		//GridPane-Layout vervollst�ndigen und Scene zuweisen
		layoutGridPane.getChildren().addAll(usernameLabel,usernameTextfeld, 
				passwortLabel,pwPasswort,loginButton, abbrechenButton);
		
		Scene scene = new Scene(layoutGridPane, 420, 150);
		this.setScene(scene);

		 
	}
	
	public String setButton()
	{
		String ergBtn=gedrueckterButton;
		return ergBtn;

	}
	

	
}


