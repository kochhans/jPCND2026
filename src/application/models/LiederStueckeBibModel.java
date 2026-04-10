package application.models;

//F�r eine Combobox mit Editionlang und ID
public class LiederStueckeBibModel
{
	public LiederStueckeBibModel(
			String liedsstueck,
			String bib, 
			String versangabe
			)
	{
			super();
			this.bib = bib;
			this.liedsstueck = liedsstueck;
			this.versangabe=versangabe;

		}
	String bib, liedsstueck, versangabe;


	public String getBib() {
		return bib;
	}

	public void setBib(String bib) {
		this.bib = bib;
	}

	public String getLiedstueck()
	{
		return liedsstueck;
	}

	public void setLiedstueck(String liedsstueck)
	{
		this.liedsstueck = liedsstueck;
	}

	
	public String getVersangabe()
	{
		return versangabe;
	}

	public void setVersangabe(String versangabe)
	{
		this.versangabe = versangabe;
	}


	
	public Integer getID()
	{
		
		return null;
	}


}
