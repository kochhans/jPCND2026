package application.models;

//F�r eine Combobox mit Editionlang und ID
public class LiederStueckeGesbModel
{
	public LiederStueckeGesbModel(
			String gesb, 
			String nummer,			
			Integer nrsort,
			String liedstueck,
			Integer id
			)
	{
			super();
			this.gesb = gesb;
			this.nummer=nummer;
			this.nrsort=nrsort;
			this.liedstueck = liedstueck;
			this.id = id;



		}
	String gesb, liedstueck, nummer;
	Integer nrsort, id;


	public String getGesb() {
		return gesb;
	}

	public void setGesb(String gesb) {
		this.gesb = gesb;
	}

	
	
	public String getLiedstueck()
	{
		return liedstueck;
	}

	public void setLiedstueck(String liedstueck)
	{
		this.liedstueck = liedstueck;
	}
	
	
	public String getNummer()
	{
		return nummer;
	}

	public void setNummer(String nummer)
	{
		this.nummer = nummer;
	}
	
	
	public Integer getNrsort()
	{
		return nrsort;
	}

	public void setNrSort(Integer nrsort)
	{
		this.nrsort = nrsort;
	}

	public Integer getId()
	{
		return id;
	}


}
