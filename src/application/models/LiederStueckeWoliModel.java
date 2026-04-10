package application.models;

//F�r eine Combobox mit Editionlang und ID
public class LiederStueckeWoliModel
{
	public LiederStueckeWoliModel(
			String woli, 
			String liedsstueck,
			int id
			)
	{
			super();
			this.woli = woli;
			this.liedsstueck = liedsstueck;
			this.id = id;


		}
	String woli, liedsstueck;
	int id;


	public String getWoli() {
		return woli;
	}

	public void setWoli(String woli) {
		this.woli = woli;
	}

	public String getLiedstueck()
	{
		return liedsstueck;
	}

	public void setLiedstueck(String liedsstueck)
	{
		this.liedsstueck = liedsstueck;
	}

	public Integer getId()
	{
		return id;
	}


}
