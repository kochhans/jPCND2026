package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class AutorenlisteModel implements IDOwner
{
	public AutorenlisteModel(String aautor, String anname, String avname, String agjahr, String atjahr, String asonst, String adb, String id)
	{
		super();
		this.aautor= aautor;
		this.anname = anname;
		this.avname = avname;
		this.agjahr = agjahr;
		this.atjahr = atjahr;
		this.asonst = asonst;
		this.adb = adb;
		this.id = id;
		

	}



	String aautor;
	String anname;
	String avname;
	String agjahr;
	String atjahr;
	String asonst;
	String adb;
	String id;
	

//-------------


	public String getAautor() {
		return aautor;
	}
	public void setAautor(String aautor) {
		this.aautor = aautor;
	}
	public String getAnname()
	{
		return anname;
	}


	public void setAnname(String anname)
	{
		this.anname = anname;
	}

	public String getAvname()
	{
		return avname;
	}

	public void setAvname(String avname)
	{
		this.avname = avname;
	}

	public String getAgjahr()
	{
		return agjahr;
	}

	public void setAgjahr(String agjahr)
	{
		this.agjahr = agjahr;
	}

	public String getAtjahr()
	{
		return atjahr;
	}

	public void setAtjahr(String atjahr)
	{
		this.atjahr = atjahr;
	}

	public String getAsonst()
	{
		return asonst;
	}

	public void setAsonst(String asonst)
	{
		this.asonst = asonst;
	}
	
	public String getAdb()
	{
		return adb;
	}

	public void setAdb(String adb)
	{
		this.adb = adb;
	}	
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}	
	
	
	
	
	
	
	@Override
	public String toString()
	{
		return anname + ", " + avname;
	}
	@Override
	public Integer getID() {
		// TODO Auto-generated method stub
		return null;
	}


}
