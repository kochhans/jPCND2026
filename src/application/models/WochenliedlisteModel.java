package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class WochenliedlisteModel implements IDOwner
{
	public WochenliedlisteModel(String wolibez, String wolirang, String wolidb, Integer woliid)
	{
			super();
			this.wolibez = wolibez;
			this.wolirang = wolirang;
			this.woliid = woliid;
			this.wolidb = wolidb;

		}
	String wolibez, wolirang, wolidb ;
	int woliid;

	public String getWolibez()
	{
		return wolibez;
	}
	public void setWolibez(String wolibez)
	{
		this.wolibez = wolibez;
	}

	public String getWolirang()
	{
		return wolirang;
	}
	public void setWolirang(String wolirang)
	{
		this.wolirang = wolirang;
	}
	
	public String getWolidb()
	{
		return wolidb;
	}
	public void setWolisb(String wolidb)
	{
		this.wolidb = wolidb;
	}	
	
	
	
	
	
	
	@Override
	public String toString() {
		return wolibez;
	}

	public Integer getWoliid()
	{
		return woliid;
	}
	public void setWoliid(Integer woliid)
	{
		this.woliid = woliid;
	}
	@Override
	public Integer getID()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
