package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class ProgrammversionenModel implements IDOwner
{
	public ProgrammversionenModel(String vernr, String verdat)
	{
			super();
			this.vernr = vernr;
			this.verdat = verdat;

		}

	String vernr, verdat;

	public String vernr()
	{
		return vernr;
	}
	public void setVernr(String vernr)
	{
		this.vernr = vernr;
	}
	public String getVernr()
	{
		return vernr;
	}
	
	
	
	public String verdat()
	{
		return verdat;
	}
	public void setVerdat(String verdat)
	{
		this.verdat = verdat;
	}
	public String getVerdat()
	{
		return verdat;
	}
//-------------
	
	@Override
	public String toString() {
		return vernr;
	}
	@Override
	public Integer getID() {
		
		return null;
	}
	

}
