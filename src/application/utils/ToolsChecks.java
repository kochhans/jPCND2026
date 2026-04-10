package application.utils;
import javafx.scene.control.TextField;

public class ToolsChecks 
{
	public static boolean isInt(TextField steuerelementTxt, String eingabe)
	{//�bergabe eines beliebigen Textfeldes und des Inhalts
		try
		{
			//Inhalt auslesen und in Konsole schreiben
			int zahl=Integer.parseInt(steuerelementTxt.getText());
			System.out.println(zahl);
			//HkAlertBox.display("Eingabe ok!", "Alter:" + alter);			
			return true;
		}
		catch(NumberFormatException e)
		{
			//Bei fehlerhafter Eingabe - Fehler in Konsole schreiben
			//System.out.println("Die Eingabe [" + eingabe + "] ist keine g�ltige Altersangabe");
			//HkAlertBox.display("Fehlerhafte Eingabe!", "Die Eingabe [" + eingabe + "] ist keine g�ltige Zahl");
			return false;			
		}
	}


}
