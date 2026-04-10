package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class ThemenlisteFilterModel 
{
	public ThemenlisteFilterModel(String bez)
	{
			super();
			this.bez = bez;

		}

	
	private String bez ;
	//----------------------------------------

	public void setBez(String bez)
	{
		this.bez = bez;
	}
	public String getBez()
	{
		return bez;
	}
	//----------------------------------------
	
	@Override
	public String toString() {
		return bez;
	}
	//----------------------------------------	
	
//	public String bez()
//	{
//		return bez;
//	}
	//----------------------------------------	

	
}


