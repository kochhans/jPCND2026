package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class GesangbuchModel implements IDOwner
{
	public GesangbuchModel(String bez, String kurz, String bem, String db)
	{
			super();
			this.bez = bez;
			this.kurz = kurz;
			this.bem = bem;
			this.db = db;

		}
	String bez, kurz, bem, db;





	public String getBem() {
		return bem;
	}

	public void setBem(String bem) {
		this.bem = bem;
	}

	public String getBez()
	{
		return bez;
	}

	public void setBez(String bez)
	{
		this.bez = bez;
	}

	public String getKurz()
	{
		return kurz;
	}

	public void setKurz(String kurz)
	{
		this.kurz = kurz;
	}	
	
	public String getDb()
	{
		return db;
	}

	public void setDb(String db)
	{
		this.db = db;
	}	
	
	@Override
	public String toString() {
		return bez;
	}

	@Override
	public Integer getID()
	{
		
		return null;
	}
	

}
