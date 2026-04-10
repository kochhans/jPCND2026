package application.models;

//St�ckarten komplettModell
public class StueckartlisteModel
{
	public StueckartlisteModel(String bez, String db, int id)
	{
		super();
		this.bez = bez;
		this.id = id;
		this.db = db;

	}

	String bez;
	String db;
	int id;

	public String bez()
	{
		return bez;
	}

	public int id()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
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

//-------------

	@Override
	public String toString()
	{
		return bez;
	}

}
