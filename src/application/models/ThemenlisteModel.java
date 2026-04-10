package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class ThemenlisteModel implements IDOwner
{
	public ThemenlisteModel(String bez, String db, int id)
	{
			super();
			this.bez = bez;
			this.db = db;
			this.id = id;
		}

	String bez, db;
	int id;

	public String bez()
	{
		return bez;
	}
	public void setBez(String bez)
	{
		this.bez = bez;
	}
	public String getBez()
	{
		return bez;
	}
	
	public void setDb(String db)
	{
		this.db = db;
	}
	public String getDb()
	{
		return db;
	}
	
	
	public void setId(int id)
	{
		this.id = id;
	}
	public int getId()
	{
		return id;
	}
//-------------
	
	@Override
	public String toString() {
		return bez;
	}
	@Override
	public Integer getID() {
		
		return null;
	}
	

}
