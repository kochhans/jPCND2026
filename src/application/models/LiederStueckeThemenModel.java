package application.models;

//F�r eine Combobox mit Editionlang und ID
//Klasse, um die Liste zur Ausgabe zusammenzustellen
//2.1.2023
public class LiederStueckeThemenModel implements IDOwner
{
	public LiederStueckeThemenModel(
			String thema,
			String liedsstueck)
	{
		super();
		this.thema = thema;
		this.liedsstueck = liedsstueck;
	}
	String thema, liedsstueck;
	// --------------------------------------------
	public String getThema()
	{
		return thema;
	}

	public void setThema(String thema)
	{
		this.thema = thema;
	}

	//--------------------------------------------
	public String getLiedstueck()
	{
		return liedsstueck;
	}

	public void setLiedstueck(String liedsstueck)
	{
		this.liedsstueck = liedsstueck;
	}
	//--------------------------------------------
	@Override
	public Integer getID()
	{
		
		return null;
	}

}